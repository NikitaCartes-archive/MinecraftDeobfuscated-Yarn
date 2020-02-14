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
import net.minecraft.class_4831;
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
	private final Map<MemoryModuleType<?>, Optional<? extends class_4831<?>>> memories = Maps.<MemoryModuleType<?>, Optional<? extends class_4831<?>>>newHashMap();
	private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.<SensorType<? extends Sensor<? super E>>, Sensor<? super E>>newLinkedHashMap();
	private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
	private Schedule schedule = Schedule.EMPTY;
	private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories = Maps.<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>>newHashMap();
	private final Map<Activity, Set<MemoryModuleType<?>>> field_22282 = Maps.<Activity, Set<MemoryModuleType<?>>>newHashMap();
	private Set<Activity> coreActivities = Sets.<Activity>newHashSet();
	private final Set<Activity> possibleActivities = Sets.<Activity>newHashSet();
	private Activity defaultActivity = Activity.IDLE;
	private long activityStartTime = -9999L;

	public <T> Brain(Collection<MemoryModuleType<?>> collection, Collection<SensorType<? extends Sensor<? super E>>> sensors, Dynamic<T> dynamic) {
		collection.forEach(memoryModuleType -> {
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

		for (Entry<Dynamic<T>, Dynamic<T>> entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
			this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic<T>)entry.getValue());
		}
	}

	public boolean hasMemoryModule(MemoryModuleType<?> memoryModuleType) {
		return this.isMemoryInState(memoryModuleType, MemoryModuleState.VALUE_PRESENT);
	}

	private <T, U> void readMemory(MemoryModuleType<U> memoryModuleType, Dynamic<T> dynamic) {
		class_4831<U> lv = new class_4831((Function<Dynamic<?>, T>)memoryModuleType.getFactory().orElseThrow(RuntimeException::new), dynamic);
		this.method_24535(memoryModuleType, Optional.of(lv));
	}

	public <U> void forget(MemoryModuleType<U> memoryModuleType) {
		this.setMemory(memoryModuleType, Optional.empty());
	}

	public <U> void putMemory(MemoryModuleType<U> memoryModuleType, @Nullable U value) {
		this.setMemory(memoryModuleType, Optional.ofNullable(value));
	}

	public <U> void method_24525(MemoryModuleType<U> memoryModuleType, U object, long l, long m) {
		long n = l + m;
		this.method_24535(memoryModuleType, Optional.of(class_4831.method_24636(object, n)));
	}

	public <U> void setMemory(MemoryModuleType<U> memoryModuleType, Optional<U> value) {
		this.method_24535(memoryModuleType, value.map(class_4831::method_24635));
	}

	private <U> void method_24535(MemoryModuleType<U> memoryModuleType, Optional<? extends class_4831<?>> optional) {
		if (this.memories.containsKey(memoryModuleType)) {
			if (optional.isPresent() && this.isEmptyCollection(((class_4831)optional.get()).method_24637())) {
				this.forget(memoryModuleType);
			} else {
				this.memories.put(memoryModuleType, optional);
			}
		}
	}

	public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> memoryModuleType) {
		return ((Optional)this.memories.get(memoryModuleType)).map(class_4831::method_24637);
	}

	public boolean isMemoryInState(MemoryModuleType<?> memoryModuleType, MemoryModuleState state) {
		Optional<? extends class_4831<?>> optional = (Optional<? extends class_4831<?>>)this.memories.get(memoryModuleType);
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

	public void setCoreActivities(Set<Activity> set) {
		this.coreActivities = set;
	}

	@Deprecated
	public Stream<Task<? super E>> streamRunningTasks() {
		return this.tasks.values().stream().flatMap(map -> map.values().stream()).flatMap(Collection::stream).filter(task -> task.getStatus() == Task.Status.RUNNING);
	}

	public void method_24536() {
		this.resetPossibleActivities(this.defaultActivity);
	}

	public Optional<Activity> method_24538() {
		return this.possibleActivities.stream().filter(activity -> !this.coreActivities.contains(activity)).findFirst();
	}

	public void method_24526(Activity activity) {
		if (this.canDoActivity(activity)) {
			this.resetPossibleActivities(activity);
		} else {
			this.method_24536();
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
			.map(this.field_22282::get)
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

	public void method_24531(List<Activity> list) {
		list.stream().filter(this::canDoActivity).findFirst().ifPresent(this::resetPossibleActivities);
	}

	public void setDefaultActivity(Activity activity) {
		this.defaultActivity = activity;
	}

	public void setTaskList(Activity activity, int i, ImmutableList<? extends Task<? super E>> immutableList) {
		this.setTaskList(activity, this.method_24524(i, immutableList));
	}

	public void method_24527(Activity activity, int i, ImmutableList<? extends Task<? super E>> immutableList, MemoryModuleType<?> memoryModuleType) {
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set = ImmutableSet.of(Pair.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		Set<MemoryModuleType<?>> set2 = ImmutableSet.of(memoryModuleType);
		this.method_24530(activity, this.method_24524(i, immutableList), set, set2);
	}

	public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList) {
		this.method_24530(activity, immutableList, ImmutableSet.of(), Sets.<MemoryModuleType<?>>newHashSet());
	}

	public void method_24529(
		Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set
	) {
		this.method_24530(activity, immutableList, set, Sets.<MemoryModuleType<?>>newHashSet());
	}

	private void method_24530(
		Activity activity,
		ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList,
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set,
		Set<MemoryModuleType<?>> set2
	) {
		this.requiredActivityMemories.put(activity, set);
		if (!set2.isEmpty()) {
			this.field_22282.put(activity, set2);
		}

		immutableList.forEach(
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
		this.memories.forEach((memoryModuleType, optional) -> optional.ifPresent(arg -> {
				Optional var10000 = (Optional)brain.memories.put(memoryModuleType, Optional.of(arg));
			}));
		return brain;
	}

	public void tick(ServerWorld serverWorld, E livingEntity) {
		this.method_24533(serverWorld);
		this.updateSensors(serverWorld, livingEntity);
		this.startTasks(serverWorld, livingEntity);
		this.updateTasks(serverWorld, livingEntity);
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
							((class_4831)((Optional)entry.getValue()).get()).serialize(ops)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return ops.createMap(ImmutableMap.of(ops.createString("memories"), object));
	}

	private void updateSensors(ServerWorld serverWorld, E livingEntity) {
		this.sensors.values().forEach(sensor -> sensor.canSense(serverWorld, livingEntity));
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

	private void method_24533(ServerWorld serverWorld) {
		this.memories.forEach((memoryModuleType, optional) -> {
			if (optional.isPresent() && ((class_4831)optional.get()).method_24634(serverWorld.getTime())) {
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

	private ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> method_24524(int i, ImmutableList<? extends Task<? super E>> immutableList) {
		MutableInt mutableInt = new MutableInt(i);
		return (ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>>)immutableList.stream()
			.map(task -> Pair.of(mutableInt.incrementAndGet(), task))
			.collect(ImmutableList.toImmutableList());
	}
}
