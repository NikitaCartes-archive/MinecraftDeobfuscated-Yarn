package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.registry.Registry;

public class Brain<E extends LivingEntity> implements DynamicSerializable {
	private final Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = Maps.<MemoryModuleType<?>, Optional<? extends Memory<?>>>newHashMap();
	private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.<SensorType<? extends Sensor<? super E>>, Sensor<? super E>>newLinkedHashMap();
	private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
	private Schedule schedule = Schedule.EMPTY;
	private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories = Maps.<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>>newHashMap();
	/**
	 * The map from activities to the memories to forget after the activity is
	 * completed.
	 */
	private final Map<Activity, Set<MemoryModuleType<?>>> forgettingActivityMemories = Maps.<Activity, Set<MemoryModuleType<?>>>newHashMap();
	private Set<Activity> coreActivities = Sets.<Activity>newHashSet();
	private final Set<Activity> possibleActivities = Sets.<Activity>newHashSet();
	private Activity defaultActivity = Activity.IDLE;
	private long activityStartTime = -9999L;

	public <T> Brain(Collection<MemoryModuleType<?>> memoryTypes, Collection<SensorType<? extends Sensor<? super E>>> collection, Dynamic<T> dynamic) {
		for (MemoryModuleType<?> memoryModuleType : memoryTypes) {
			this.memories.put(memoryModuleType, Optional.empty());
		}

		for (SensorType<? extends Sensor<? super E>> sensorType : collection) {
			this.sensors.put(sensorType, sensorType.create());
		}

		for (Sensor<? super E> sensor : this.sensors.values()) {
			for (MemoryModuleType<?> memoryModuleType2 : sensor.getOutputMemoryModules()) {
				this.memories.put(memoryModuleType2, Optional.empty());
			}
		}

		for (Entry<Dynamic<T>, Dynamic<T>> entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
			this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic<T>)entry.getValue());
		}
	}

	public boolean hasMemoryModule(MemoryModuleType<?> type) {
		return this.isMemoryInState(type, MemoryModuleState.VALUE_PRESENT);
	}

	private <T, U> void readMemory(MemoryModuleType<U> type, Dynamic<T> data) {
		Memory<U> memory = new Memory((Function<Dynamic<?>, T>)type.getFactory().orElseThrow(RuntimeException::new), data);
		this.setMemory(type, Optional.of(memory));
	}

	public <U> void forget(MemoryModuleType<U> type) {
		this.remember(type, Optional.empty());
	}

	public <U> void remember(MemoryModuleType<U> type, @Nullable U value) {
		this.remember(type, Optional.ofNullable(value));
	}

	public <U> void remember(MemoryModuleType<U> type, U value, long startTime) {
		this.setMemory(type, Optional.of(Memory.timed(value, startTime)));
	}

	public <U> void remember(MemoryModuleType<U> type, Optional<? extends U> value) {
		this.setMemory(type, value.map(Memory::permanent));
	}

	private <U> void setMemory(MemoryModuleType<U> type, Optional<? extends Memory<?>> memory) {
		if (this.memories.containsKey(type)) {
			if (memory.isPresent() && this.isEmptyCollection(((Memory)memory.get()).getValue())) {
				this.forget(type);
			} else {
				this.memories.put(type, memory);
			}
		}
	}

	public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> type) {
		return ((Optional)this.memories.get(type)).map(Memory::getValue);
	}

	public boolean isMemoryInState(MemoryModuleType<?> type, MemoryModuleState state) {
		Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)this.memories.get(type);
		return optional == null
			? false
			: state == MemoryModuleState.REGISTERED
				|| state == MemoryModuleState.VALUE_PRESENT && optional.isPresent()
				|| state == MemoryModuleState.VALUE_ABSENT && !optional.isPresent();
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void setCoreActivities(Set<Activity> coreActivities) {
		this.coreActivities = coreActivities;
	}

	@Deprecated
	public List<Task<? super E>> method_27074() {
		List<Task<? super E>> list = new ObjectArrayList<>();

		for (Map<Activity, Set<Task<? super E>>> map : this.tasks.values()) {
			for (Set<Task<? super E>> set : map.values()) {
				for (Task<? super E> task : set) {
					if (task.getStatus() == Task.Status.RUNNING) {
						list.add(task);
					}
				}
			}
		}

		return list;
	}

	public void resetPossibleActivities() {
		this.resetPossibleActivities(this.defaultActivity);
	}

	public Optional<Activity> getFirstPossibleNonCoreActivity() {
		for (Activity activity : this.possibleActivities) {
			if (!this.coreActivities.contains(activity)) {
				return Optional.of(activity);
			}
		}

		return Optional.empty();
	}

	public void method_24526(Activity activity) {
		if (this.canDoActivity(activity)) {
			this.resetPossibleActivities(activity);
		} else {
			this.resetPossibleActivities();
		}
	}

	private void resetPossibleActivities(Activity activity) {
		if (!this.hasActivity(activity)) {
			this.method_24537(activity);
			this.possibleActivities.clear();
			this.possibleActivities.addAll(this.coreActivities);
			this.possibleActivities.add(activity);
		}
	}

	private void method_24537(Activity activity) {
		for (Activity activity2 : this.possibleActivities) {
			if (activity2 != activity) {
				Set<MemoryModuleType<?>> set = (Set<MemoryModuleType<?>>)this.forgettingActivityMemories.get(activity2);
				if (set != null) {
					for (MemoryModuleType<?> memoryModuleType : set) {
						this.forget(memoryModuleType);
					}
				}
			}
		}
	}

	public void refreshActivities(long timeOfDay, long time) {
		if (time - this.activityStartTime > 20L) {
			this.activityStartTime = time;
			Activity activity = this.getSchedule().getActivityForTime((int)(timeOfDay % 24000L));
			if (!this.possibleActivities.contains(activity)) {
				this.method_24526(activity);
			}
		}
	}

	public void resetPossibleActivities(List<Activity> list) {
		for (Activity activity : list) {
			if (this.canDoActivity(activity)) {
				this.resetPossibleActivities(activity);
				break;
			}
		}
	}

	public void setDefaultActivity(Activity activity) {
		this.defaultActivity = activity;
	}

	public void setTaskList(Activity activity, int i, ImmutableList<? extends Task<? super E>> immutableList) {
		this.setTaskList(activity, this.indexTaskList(i, immutableList));
	}

	public void setTaskList(Activity activity, int i, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryModuleType) {
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set = ImmutableSet.of(Pair.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		Set<MemoryModuleType<?>> set2 = ImmutableSet.of(memoryModuleType);
		this.setTaskList(activity, this.indexTaskList(i, tasks), set, set2);
	}

	public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks) {
		this.setTaskList(activity, indexedTasks, ImmutableSet.of(), Sets.<MemoryModuleType<?>>newHashSet());
	}

	public void setTaskList(
		Activity activity,
		ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks,
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories
	) {
		this.setTaskList(activity, indexedTasks, requiredMemories, Sets.<MemoryModuleType<?>>newHashSet());
	}

	private void setTaskList(
		Activity activity,
		ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks,
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories,
		Set<MemoryModuleType<?>> forgettingMemories
	) {
		this.requiredActivityMemories.put(activity, requiredMemories);
		if (!forgettingMemories.isEmpty()) {
			this.forgettingActivityMemories.put(activity, forgettingMemories);
		}

		for (Pair<Integer, ? extends Task<? super E>> pair : indexedTasks) {
			((Set)((Map)this.tasks.computeIfAbsent(pair.getFirst(), integer -> Maps.newHashMap())).computeIfAbsent(activity, activityx -> Sets.newLinkedHashSet()))
				.add(pair.getSecond());
		}
	}

	public boolean hasActivity(Activity activity) {
		return this.possibleActivities.contains(activity);
	}

	public Brain<E> copy() {
		Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));

		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			if (((Optional)entry.getValue()).isPresent()) {
				brain.memories.put(memoryModuleType, entry.getValue());
			}
		}

		return brain;
	}

	public void tick(ServerWorld serverWorld, E entity) {
		this.method_27075();
		this.method_27073(serverWorld, entity);
		this.startTasks(serverWorld, entity);
		this.updateTasks(serverWorld, entity);
	}

	private void method_27073(ServerWorld serverWorld, E livingEntity) {
		for (Sensor<? super E> sensor : this.sensors.values()) {
			sensor.canSense(serverWorld, livingEntity);
		}
	}

	private void method_27075() {
		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
			if (((Optional)entry.getValue()).isPresent()) {
				Memory<?> memory = (Memory<?>)((Optional)entry.getValue()).get();
				memory.method_24913();
				if (memory.isExpired()) {
					this.forget((MemoryModuleType)entry.getKey());
				}
			}
		}
	}

	public void stopAllTasks(ServerWorld serverWorld, E livingEntity) {
		long l = livingEntity.world.getTime();

		for (Task<? super E> task : this.method_27074()) {
			task.stop(serverWorld, livingEntity, l);
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();

		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			if (((Optional)entry.getValue()).isPresent() && memoryModuleType.getFactory().isPresent()) {
				Memory<?> memory = (Memory<?>)((Optional)entry.getValue()).get();
				T object = ops.createString(Registry.MEMORY_MODULE_TYPE.getId(memoryModuleType).toString());
				T object2 = memory.serialize(ops);
				builder.put(object, object2);
			}
		}

		return ops.createMap(ImmutableMap.of(ops.createString("memories"), ops.createMap(builder.build())));
	}

	private void startTasks(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();

		for (Map<Activity, Set<Task<? super E>>> map : this.tasks.values()) {
			for (Entry<Activity, Set<Task<? super E>>> entry : map.entrySet()) {
				Activity activity = (Activity)entry.getKey();
				if (this.possibleActivities.contains(activity)) {
					for (Task<? super E> task : (Set)entry.getValue()) {
						if (task.getStatus() == Task.Status.STOPPED) {
							task.tryStarting(serverWorld, livingEntity, l);
						}
					}
				}
			}
		}
	}

	private void updateTasks(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();

		for (Task<? super E> task : this.method_27074()) {
			task.tick(serverWorld, livingEntity, l);
		}
	}

	private boolean canDoActivity(Activity activity) {
		if (!this.requiredActivityMemories.containsKey(activity)) {
			return false;
		} else {
			for (Pair<MemoryModuleType<?>, MemoryModuleState> pair : (Set)this.requiredActivityMemories.get(activity)) {
				MemoryModuleType<?> memoryModuleType = pair.getFirst();
				MemoryModuleState memoryModuleState = pair.getSecond();
				if (!this.isMemoryInState(memoryModuleType, memoryModuleState)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean isEmptyCollection(Object value) {
		return value instanceof Collection && ((Collection)value).isEmpty();
	}

	/**
	 * @param begin The beginning of the index of tasks, exclusive
	 */
	ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> immutableList) {
		int i = begin;
		com.google.common.collect.ImmutableList.Builder<Pair<Integer, ? extends Task<? super E>>> builder = ImmutableList.builder();

		for (Task<? super E> task : immutableList) {
			builder.add(Pair.of(i++, task));
		}

		return builder.build();
	}
}
