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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Brain<E extends LivingEntity> implements DynamicSerializable {
	private final Map<MemoryModuleType<?>, Optional<?>> memories = Maps.<MemoryModuleType<?>, Optional<?>>newHashMap();
	private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.<SensorType<? extends Sensor<? super E>>, Sensor<? super E>>newLinkedHashMap();
	private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
	private Schedule schedule = Schedule.EMPTY;
	private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> field_18326 = Maps.<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>>newHashMap();
	private Set<Activity> field_18327 = Sets.<Activity>newHashSet();
	private final Set<Activity> possibleActivities = Sets.<Activity>newHashSet();
	private Activity activity = Activity.field_18595;
	private long field_18853 = 0L;

	public <T> Brain(Collection<MemoryModuleType<?>> collection, Collection<SensorType<? extends Sensor<? super E>>> collection2, Dynamic<T> dynamic) {
		collection.forEach(memoryModuleType -> {
			Optional var10000 = (Optional)this.memories.put(memoryModuleType, Optional.empty());
		});
		collection2.forEach(sensorType -> {
			Sensor var10000 = (Sensor)this.sensors.put(sensorType, sensorType.method_19102());
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
		return this.isMemoryInState(memoryModuleType, MemoryModuleState.field_18456);
	}

	private <T, U> void readMemory(MemoryModuleType<U> memoryModuleType, Dynamic<T> dynamic) {
		this.putMemory(memoryModuleType, (U)((Function)memoryModuleType.getFactory().orElseThrow(RuntimeException::new)).apply(dynamic));
	}

	public void forget(MemoryModuleType<?> memoryModuleType) {
		this.setMemory(memoryModuleType, Optional.empty());
	}

	public <U> void putMemory(MemoryModuleType<U> memoryModuleType, @Nullable U object) {
		this.setMemory(memoryModuleType, Optional.ofNullable(object));
	}

	public <U> void setMemory(MemoryModuleType<U> memoryModuleType, Optional<U> optional) {
		if (this.memories.containsKey(memoryModuleType)) {
			this.memories.put(memoryModuleType, optional);
		}
	}

	public <U> Optional<U> getMemory(MemoryModuleType<U> memoryModuleType) {
		Optional<?> optional = (Optional<?>)this.memories.get(memoryModuleType);
		if (optional == null) {
			throw new IllegalStateException("No memory of type " + memoryModuleType.getId() + " found");
		} else {
			return (Optional<U>)optional;
		}
	}

	public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> memoryModuleType) {
		return (Optional<U>)this.memories.getOrDefault(memoryModuleType, Optional.empty());
	}

	public boolean isMemoryInState(MemoryModuleType<?> memoryModuleType, MemoryModuleState memoryModuleState) {
		Optional<?> optional = (Optional<?>)this.memories.get(memoryModuleType);
		return optional == null
			? false
			: memoryModuleState == MemoryModuleState.field_18458
				|| memoryModuleState == MemoryModuleState.field_18456 && optional.isPresent()
				|| memoryModuleState == MemoryModuleState.field_18457 && !optional.isPresent();
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void method_18890(Set<Activity> set) {
		this.field_18327 = set;
	}

	@Deprecated
	public Stream<Task<? super E>> method_18899() {
		return this.tasks
			.values()
			.stream()
			.flatMap(map -> map.values().stream())
			.flatMap(Collection::stream)
			.filter(task -> task.getStatus() == Task.Status.field_18338);
	}

	public void method_18880(Activity activity) {
		this.possibleActivities.clear();
		this.possibleActivities.addAll(this.field_18327);
		boolean bl = this.field_18326.keySet().contains(activity) && this.method_18874(activity);
		this.possibleActivities.add(bl ? activity : this.activity);
	}

	public void doActivity(long l, long m) {
		if (m - this.field_18853 > 20L) {
			this.field_18853 = m;
			Activity activity = this.getSchedule().getActivityForTime((int)(l % 24000L));
			if (!this.possibleActivities.contains(activity)) {
				this.method_18880(activity);
			}
		}
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setTaskList(Activity activity, ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList) {
		this.setTaskList(activity, immutableList, ImmutableSet.of());
	}

	public void setTaskList(
		Activity activity, ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set
	) {
		this.field_18326.put(activity, set);
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
		this.memories.forEach((memoryModuleType, optional) -> optional.ifPresent(object -> {
				Optional var10000 = (Optional)brain.memories.put(memoryModuleType, Optional.of(object));
			}));
		return brain;
	}

	public void method_19542(ServerWorld serverWorld, E livingEntity) {
		this.method_19544(serverWorld, livingEntity);
		this.tick(serverWorld, livingEntity);
		this.method_19545(serverWorld, livingEntity);
	}

	public void stopAllTasks(ServerWorld serverWorld, E livingEntity) {
		long l = livingEntity.world.getTime();
		this.method_18899().forEach(task -> task.stop(serverWorld, livingEntity, l));
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			(Map<T, T>)this.memories
				.entrySet()
				.stream()
				.filter(entry -> ((MemoryModuleType)entry.getKey()).getFactory().isPresent() && ((Optional)entry.getValue()).isPresent())
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((MemoryModuleType)entry.getKey()).getId().toString()),
							((DynamicSerializable)((Optional)entry.getValue()).get()).serialize(dynamicOps)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("memories"), object));
	}

	private void method_19544(ServerWorld serverWorld, E livingEntity) {
		this.sensors.values().stream().filter(sensor -> sensor.canSense(serverWorld, livingEntity)).forEach(sensor -> sensor.sense(serverWorld, livingEntity));
	}

	private void tick(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();
		this.tasks
			.values()
			.stream()
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> this.possibleActivities.contains(entry.getKey()))
			.map(Entry::getValue)
			.flatMap(Collection::stream)
			.filter(task -> task.getStatus() == Task.Status.field_18337)
			.forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
	}

	private void method_19545(ServerWorld serverWorld, E livingEntity) {
		long l = serverWorld.getTime();
		this.method_18899().forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	private boolean method_18874(Activity activity) {
		return ((Set)this.field_18326.get(activity)).stream().allMatch(pair -> {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)pair.getFirst();
			MemoryModuleState memoryModuleState = (MemoryModuleState)pair.getSecond();
			return this.isMemoryInState(memoryModuleType, memoryModuleState);
		});
	}
}
