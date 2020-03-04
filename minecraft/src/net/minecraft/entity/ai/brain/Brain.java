package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableInt;

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

	public <T> Brain(Collection<MemoryModuleType<?>> memoryTypes, Collection<SensorType<? extends Sensor<? super E>>> sensors, Dynamic<T> data) {
		memoryTypes.forEach(memoryModuleType -> {
			Optional var10000 = (Optional)this.memories.put(memoryModuleType, Optional.empty());
		});
		sensors.forEach(sensorType -> {
			Sensor var10000 = (Sensor)this.sensors.put(sensorType, sensorType.create());
		});
		this.sensors.values().forEach(sensor -> {
			for (MemoryModuleType<?> memoryModuleType : sensor.getOutputMemoryModules()) {
				this.memories.put(memoryModuleType, Optional.empty());
			}
		});

		for (Entry<Dynamic<T>, Dynamic<T>> entry : data.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
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

	public <U> void remember(MemoryModuleType<U> type, Optional<U> value) {
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
	public Stream<Task<? super E>> streamRunningTasks() {
		return this.tasks.values().stream().flatMap(map -> map.values().stream()).flatMap(Collection::stream).filter(task -> task.getStatus() == Task.Status.RUNNING);
	}

	public void resetPossibleActivities() {
		this.resetPossibleActivities(this.defaultActivity);
	}

	public Optional<Activity> getFirstPossibleNonCoreActivity() {
		return this.possibleActivities.stream().filter(activity -> !this.coreActivities.contains(activity)).findFirst();
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
		this.possibleActivities
			.stream()
			.filter(activity2 -> activity2 != activity)
			.map(this.forgettingActivityMemories::get)
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.forEach(this::forget);
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
		list.stream().filter(this::canDoActivity).findFirst().ifPresent(this::resetPossibleActivities);
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

		indexedTasks.forEach(
			pair -> ((Set)((Map)this.tasks.computeIfAbsent(pair.getFirst(), integer -> Maps.newHashMap()))
						.computeIfAbsent(activity, activityxx -> Sets.newLinkedHashSet()))
					.add(pair.getSecond())
		);
	}

	public boolean hasActivity(Activity activity) {
		return this.possibleActivities.contains(activity);
	}

	public Brain<E> copy() {
		Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
		this.memories.forEach((memoryModuleType, optional) -> optional.ifPresent(memory -> {
				Optional var10000 = (Optional)brain.memories.put(memoryModuleType, Optional.of(memory));
			}));
		return brain;
	}

	public void tick(ServerWorld serverWorld, E entity) {
		this.memories.forEach(this::method_24912);
		this.sensors.values().forEach(sensor -> sensor.canSense(serverWorld, entity));
		this.startTasks(serverWorld, entity);
		this.updateTasks(serverWorld, entity);
	}

	public void stopAllTasks(ServerWorld serverWorld, E livingEntity) {
		long l = livingEntity.world.getTime();
		this.streamRunningTasks().forEach(task -> task.stop(serverWorld, livingEntity, l));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T object = ops.createMap(
			(Map<T, T>)this.memories
				.entrySet()
				.stream()
				.filter(entry -> ((MemoryModuleType)entry.getKey()).getFactory().isPresent() && ((Optional)entry.getValue()).isPresent())
				.map(
					entry -> Pair.of(
							ops.createString(Registry.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)entry.getKey()).toString()),
							((Memory)((Optional)entry.getValue()).get()).serialize(ops)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return ops.createMap(ImmutableMap.of(ops.createString("memories"), object));
	}

	private void startTasks(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();
		this.tasks
			.values()
			.stream()
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> this.possibleActivities.contains(entry.getKey()))
			.map(Entry::getValue)
			.flatMap(Collection::stream)
			.filter(task -> task.getStatus() == Task.Status.STOPPED)
			.forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
	}

	private void updateTasks(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();
		this.streamRunningTasks().forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	private void method_24912(MemoryModuleType<?> memoryModuleType, Optional<? extends Memory<?>> optional) {
		optional.ifPresent(memory -> {
			memory.method_24913();
			if (memory.isExpired()) {
				this.forget(memoryModuleType);
			}
		});
	}

	private boolean canDoActivity(Activity activity) {
		return this.requiredActivityMemories.containsKey(activity) && ((Set)this.requiredActivityMemories.get(activity)).stream().allMatch(pair -> {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)pair.getFirst();
			MemoryModuleState memoryModuleState = (MemoryModuleState)pair.getSecond();
			return this.isMemoryInState(memoryModuleType, memoryModuleState);
		});
	}

	private boolean isEmptyCollection(Object value) {
		return value instanceof Collection && ((Collection)value).isEmpty();
	}

	/**
	 * @param begin The beginning of the index of tasks, exclusive
	 */
	private ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> immutableList) {
		MutableInt mutableInt = new MutableInt(begin);
		return (ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>>)immutableList.stream()
			.map(task -> Pair.of(mutableInt.incrementAndGet(), task))
			.collect(ImmutableList.toImmutableList());
	}
}
