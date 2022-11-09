package net.minecraft.entity.ai.brain;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.annotation.Debug;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

/**
 * A brain is associated with each living entity.
 * 
 * <p>A brain has {@linkplain #memories memories}, {@linkplain #sensors sensors},
 * and {@linkplain #tasks tasks}. In general, the brain can use sensors to refresh
 * the memories over time, and the memories can be shared by different tasks,
 * which can reduce duplicate calculation. For instance, instead of having each
 * task scan for the player, the memories can hold information about nearby player,
 * and the task can choose to run or not accordingly.
 * 
 * <p>To construct a brain, you need to specify the memory (module) types and
 * sensors present in a brain, and then you can add individual tasks.
 * 
 * @see LivingEntity#brain
 */
public class Brain<E extends LivingEntity> {
	static final Logger LOGGER = LogUtils.getLogger();
	private final Supplier<Codec<Brain<E>>> codecSupplier;
	private static final int ACTIVITY_REFRESH_COOLDOWN = 20;
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

	public static <E extends LivingEntity> Brain.Profile<E> createProfile(
		Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors
	) {
		return new Brain.Profile<>(memoryModules, sensors);
	}

	public static <E extends LivingEntity> Codec<Brain<E>> createBrainCodec(
		Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors
	) {
		final MutableObject<Codec<Brain<E>>> mutableObject = new MutableObject<>();
		mutableObject.setValue(
			(new MapCodec<Brain<E>>() {
					@Override
					public <T> Stream<T> keys(DynamicOps<T> ops) {
						return memoryModules.stream()
							.flatMap(memoryType -> memoryType.getCodec().map(codec -> Registries.MEMORY_MODULE_TYPE.getId(memoryType)).stream())
							.map(id -> ops.createString(id.toString()));
					}

					@Override
					public <T> DataResult<Brain<E>> decode(DynamicOps<T> ops, MapLike<T> map) {
						MutableObject<DataResult<Builder<Brain.MemoryEntry<?>>>> mutableObject = new MutableObject<>(DataResult.success(ImmutableList.builder()));
						map.entries().forEach(pair -> {
							DataResult<MemoryModuleType<?>> dataResult = Registries.MEMORY_MODULE_TYPE.getCodec().parse(ops, (T)pair.getFirst());
							DataResult<? extends Brain.MemoryEntry<?>> dataResult2 = dataResult.flatMap(memoryType -> this.parse(memoryType, ops, (T)pair.getSecond()));
							mutableObject.setValue(mutableObject.getValue().apply2(Builder::add, dataResult2));
						});
						ImmutableList<Brain.MemoryEntry<?>> immutableList = (ImmutableList<Brain.MemoryEntry<?>>)mutableObject.getValue()
							.resultOrPartial(Brain.LOGGER::error)
							.map(Builder::build)
							.orElseGet(ImmutableList::of);
						return DataResult.success(new Brain<>(memoryModules, sensors, immutableList, mutableObject::getValue));
					}

					private <T, U> DataResult<Brain.MemoryEntry<U>> parse(MemoryModuleType<U> memoryType, DynamicOps<T> ops, T value) {
						return ((DataResult)memoryType.getCodec().map(DataResult::success).orElseGet(() -> DataResult.error("No codec for memory: " + memoryType)))
							.flatMap(codec -> codec.parse(ops, value))
							.map(data -> new Brain.MemoryEntry<>(memoryType, Optional.of(data)));
					}

					public <T> RecordBuilder<T> encode(Brain<E> brain, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
						brain.streamMemories().forEach(entry -> entry.serialize(dynamicOps, recordBuilder));
						return recordBuilder;
					}
				})
				.fieldOf("memories")
				.codec()
		);
		return mutableObject.getValue();
	}

	public Brain(
		Collection<? extends MemoryModuleType<?>> memories,
		Collection<? extends SensorType<? extends Sensor<? super E>>> sensors,
		ImmutableList<Brain.MemoryEntry<?>> memoryEntries,
		Supplier<Codec<Brain<E>>> codecSupplier
	) {
		this.codecSupplier = codecSupplier;

		for (MemoryModuleType<?> memoryModuleType : memories) {
			this.memories.put(memoryModuleType, Optional.empty());
		}

		for (SensorType<? extends Sensor<? super E>> sensorType : sensors) {
			this.sensors.put(sensorType, sensorType.create());
		}

		for (Sensor<? super E> sensor : this.sensors.values()) {
			for (MemoryModuleType<?> memoryModuleType2 : sensor.getOutputMemoryModules()) {
				this.memories.put(memoryModuleType2, Optional.empty());
			}
		}

		for (Brain.MemoryEntry<?> memoryEntry : memoryEntries) {
			memoryEntry.apply(this);
		}
	}

	public <T> DataResult<T> encode(DynamicOps<T> ops) {
		return ((Codec)this.codecSupplier.get()).encodeStart(ops, this);
	}

	Stream<Brain.MemoryEntry<?>> streamMemories() {
		return this.memories
			.entrySet()
			.stream()
			.map(entry -> Brain.MemoryEntry.of((MemoryModuleType)entry.getKey(), (Optional<? extends Memory<?>>)entry.getValue()));
	}

	public boolean hasMemoryModule(MemoryModuleType<?> type) {
		return this.isMemoryInState(type, MemoryModuleState.VALUE_PRESENT);
	}

	public <U> void forget(MemoryModuleType<U> type) {
		this.remember(type, Optional.empty());
	}

	public <U> void remember(MemoryModuleType<U> type, @Nullable U value) {
		this.remember(type, Optional.ofNullable(value));
	}

	public <U> void remember(MemoryModuleType<U> type, U value, long expiry) {
		this.setMemory(type, Optional.of(Memory.timed(value, expiry)));
	}

	public <U> void remember(MemoryModuleType<U> type, Optional<? extends U> value) {
		this.setMemory(type, value.map(Memory::permanent));
	}

	<U> void setMemory(MemoryModuleType<U> type, Optional<? extends Memory<?>> memory) {
		if (this.memories.containsKey(type)) {
			if (memory.isPresent() && this.isEmptyCollection(((Memory)memory.get()).getValue())) {
				this.forget(type);
			} else {
				this.memories.put(type, memory);
			}
		}
	}

	public <U> Optional<U> getOptionalRegisteredMemory(MemoryModuleType<U> type) {
		Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)this.memories.get(type);
		if (optional == null) {
			throw new IllegalStateException("Unregistered memory fetched: " + type);
		} else {
			return optional.map(Memory::getValue);
		}
	}

	@Nullable
	public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> type) {
		Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)this.memories.get(type);
		return optional == null ? null : optional.map(Memory::getValue);
	}

	public <U> long getMemoryExpiry(MemoryModuleType<U> type) {
		Optional<? extends Memory<?>> optional = (Optional<? extends Memory<?>>)this.memories.get(type);
		return (Long)optional.map(Memory::getExpiry).orElse(0L);
	}

	@Deprecated
	@Debug
	public Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> getMemories() {
		return this.memories;
	}

	public <U> boolean hasMemoryModuleWithValue(MemoryModuleType<U> type, U value) {
		return !this.hasMemoryModule(type) ? false : this.getOptionalRegisteredMemory(type).filter(memoryValue -> memoryValue.equals(value)).isPresent();
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
	@Debug
	public Set<Activity> getPossibleActivities() {
		return this.possibleActivities;
	}

	@Deprecated
	@Debug
	public List<Task<? super E>> getRunningTasks() {
		List<Task<? super E>> list = new ObjectArrayList<>();

		for (Map<Activity, Set<Task<? super E>>> map : this.tasks.values()) {
			for (Set<Task<? super E>> set : map.values()) {
				for (Task<? super E> task : set) {
					if (task.getStatus() == MultiTickTask.Status.RUNNING) {
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

	public void doExclusively(Activity activity) {
		if (this.canDoActivity(activity)) {
			this.resetPossibleActivities(activity);
		} else {
			this.resetPossibleActivities();
		}
	}

	private void resetPossibleActivities(Activity except) {
		if (!this.hasActivity(except)) {
			this.forgetIrrelevantMemories(except);
			this.possibleActivities.clear();
			this.possibleActivities.addAll(this.coreActivities);
			this.possibleActivities.add(except);
		}
	}

	private void forgetIrrelevantMemories(Activity except) {
		for (Activity activity : this.possibleActivities) {
			if (activity != except) {
				Set<MemoryModuleType<?>> set = (Set<MemoryModuleType<?>>)this.forgettingActivityMemories.get(activity);
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
				this.doExclusively(activity);
			}
		}
	}

	public void resetPossibleActivities(List<Activity> activities) {
		for (Activity activity : activities) {
			if (this.canDoActivity(activity)) {
				this.resetPossibleActivities(activity);
				break;
			}
		}
	}

	public void setDefaultActivity(Activity activity) {
		this.defaultActivity = activity;
	}

	public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> list) {
		this.setTaskList(activity, this.indexTaskList(begin, list));
	}

	public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryType) {
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set = ImmutableSet.of(Pair.of(memoryType, MemoryModuleState.VALUE_PRESENT));
		Set<MemoryModuleType<?>> set2 = ImmutableSet.of(memoryType);
		this.setTaskList(activity, this.indexTaskList(begin, tasks), set, set2);
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

	public void setTaskList(
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
			((Set)((Map)this.tasks.computeIfAbsent(pair.getFirst(), index -> Maps.newHashMap())).computeIfAbsent(activity, activity2 -> Sets.newLinkedHashSet()))
				.add(pair.getSecond());
		}
	}

	@VisibleForTesting
	public void clear() {
		this.tasks.clear();
	}

	public boolean hasActivity(Activity activity) {
		return this.possibleActivities.contains(activity);
	}

	public Brain<E> copy() {
		Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codecSupplier);

		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			if (((Optional)entry.getValue()).isPresent()) {
				brain.memories.put(memoryModuleType, (Optional)entry.getValue());
			}
		}

		return brain;
	}

	public void tick(ServerWorld world, E entity) {
		this.tickMemories();
		this.tickSensors(world, entity);
		this.startTasks(world, entity);
		this.updateTasks(world, entity);
	}

	private void tickSensors(ServerWorld world, E entity) {
		for (Sensor<? super E> sensor : this.sensors.values()) {
			sensor.tick(world, entity);
		}
	}

	private void tickMemories() {
		for (Entry<MemoryModuleType<?>, Optional<? extends Memory<?>>> entry : this.memories.entrySet()) {
			if (((Optional)entry.getValue()).isPresent()) {
				Memory<?> memory = (Memory<?>)((Optional)entry.getValue()).get();
				if (memory.isExpired()) {
					this.forget((MemoryModuleType)entry.getKey());
				}

				memory.tick();
			}
		}
	}

	public void stopAllTasks(ServerWorld world, E entity) {
		long l = entity.world.getTime();

		for (Task<? super E> task : this.getRunningTasks()) {
			task.stop(world, entity, l);
		}
	}

	private void startTasks(ServerWorld world, E entity) {
		long l = world.getTime();

		for (Map<Activity, Set<Task<? super E>>> map : this.tasks.values()) {
			for (Entry<Activity, Set<Task<? super E>>> entry : map.entrySet()) {
				Activity activity = (Activity)entry.getKey();
				if (this.possibleActivities.contains(activity)) {
					for (Task<? super E> task : (Set)entry.getValue()) {
						if (task.getStatus() == MultiTickTask.Status.STOPPED) {
							task.tryStarting(world, entity, l);
						}
					}
				}
			}
		}
	}

	private void updateTasks(ServerWorld world, E entity) {
		long l = world.getTime();

		for (Task<? super E> task : this.getRunningTasks()) {
			task.tick(world, entity, l);
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
	 * @param begin the beginning of the index of tasks, exclusive
	 */
	ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> tasks) {
		int i = begin;
		Builder<Pair<Integer, ? extends Task<? super E>>> builder = ImmutableList.builder();

		for (Task<? super E> task : tasks) {
			builder.add(Pair.of(i++, task));
		}

		return builder.build();
	}

	static final class MemoryEntry<U> {
		private final MemoryModuleType<U> type;
		private final Optional<? extends Memory<U>> data;

		static <U> Brain.MemoryEntry<U> of(MemoryModuleType<U> type, Optional<? extends Memory<?>> data) {
			return new Brain.MemoryEntry<>(type, (Optional<? extends Memory<U>>)data);
		}

		MemoryEntry(MemoryModuleType<U> type, Optional<? extends Memory<U>> data) {
			this.type = type;
			this.data = data;
		}

		void apply(Brain<?> brain) {
			brain.setMemory(this.type, this.data);
		}

		public <T> void serialize(DynamicOps<T> ops, RecordBuilder<T> builder) {
			this.type
				.getCodec()
				.ifPresent(
					codec -> this.data.ifPresent(data -> builder.add(Registries.MEMORY_MODULE_TYPE.getCodec().encodeStart(ops, this.type), codec.encodeStart(ops, data)))
				);
		}
	}

	/**
	 * A simple profile of a brain. Indicates what types of memory modules and
	 * sensors a brain can have.
	 */
	public static final class Profile<E extends LivingEntity> {
		private final Collection<? extends MemoryModuleType<?>> memoryModules;
		private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors;
		private final Codec<Brain<E>> codec;

		Profile(Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
			this.memoryModules = memoryModules;
			this.sensors = sensors;
			this.codec = Brain.createBrainCodec(memoryModules, sensors);
		}

		public Brain<E> deserialize(Dynamic<?> data) {
			return (Brain<E>)this.codec
				.parse(data)
				.resultOrPartial(Brain.LOGGER::error)
				.orElseGet(() -> new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> this.codec));
		}
	}
}
