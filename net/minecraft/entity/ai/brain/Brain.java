/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class Brain<E extends LivingEntity>
implements DynamicSerializable {
    private final Map<MemoryModuleType<?>, Optional<?>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories = Maps.newHashMap();
    private Set<Activity> coreActivities = Sets.newHashSet();
    private final Set<Activity> possibleActivities = Sets.newHashSet();
    private Activity defaultActivity = Activity.IDLE;
    private long activityStartTime = -9999L;

    public <T> Brain(Collection<MemoryModuleType<?>> collection, Collection<SensorType<? extends Sensor<? super E>>> sensors, Dynamic<T> dynamic) {
        collection.forEach(memoryModuleType -> this.memories.put((MemoryModuleType<?>)memoryModuleType, Optional.empty()));
        sensors.forEach(sensorType -> this.sensors.put((SensorType<Sensor<E>>)sensorType, (Sensor<E>)sensorType.create()));
        this.sensors.values().forEach(sensor -> {
            for (MemoryModuleType<?> memoryModuleType : sensor.getOutputMemoryModules()) {
                this.memories.put(memoryModuleType, Optional.empty());
            }
        });
        for (Map.Entry entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
            this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic)entry.getValue());
        }
    }

    public boolean hasMemoryModule(MemoryModuleType<?> memoryModuleType) {
        return this.isMemoryInState(memoryModuleType, MemoryModuleState.VALUE_PRESENT);
    }

    private <T, U> void readMemory(MemoryModuleType<U> memoryModuleType, Dynamic<T> dynamic) {
        this.putMemory(memoryModuleType, memoryModuleType.getFactory().orElseThrow(RuntimeException::new).apply(dynamic));
    }

    public <U> void forget(MemoryModuleType<U> memoryModuleType) {
        this.setMemory(memoryModuleType, Optional.empty());
    }

    public <U> void putMemory(MemoryModuleType<U> memoryModuleType, @Nullable U value) {
        this.setMemory(memoryModuleType, Optional.ofNullable(value));
    }

    public <U> void setMemory(MemoryModuleType<U> memoryModuleType, Optional<U> value) {
        if (this.memories.containsKey(memoryModuleType)) {
            if (value.isPresent() && this.isEmptyCollection(value.get())) {
                this.forget(memoryModuleType);
            } else {
                this.memories.put(memoryModuleType, value);
            }
        }
    }

    public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> memoryModuleType) {
        return this.memories.get(memoryModuleType);
    }

    public boolean isMemoryInState(MemoryModuleType<?> memoryModuleType, MemoryModuleState state) {
        Optional<?> optional = this.memories.get(memoryModuleType);
        if (optional == null) {
            return false;
        }
        return state == MemoryModuleState.REGISTERED || state == MemoryModuleState.VALUE_PRESENT && optional.isPresent() || state == MemoryModuleState.VALUE_ABSENT && !optional.isPresent();
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

    public void resetPossibleActivities(Activity activity) {
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        boolean bl = this.requiredActivityMemories.keySet().contains(activity) && this.canDoActivity(activity);
        this.possibleActivities.add(bl ? activity : this.defaultActivity);
    }

    public void refreshActivities(long timeOfDay, long time) {
        if (time - this.activityStartTime > 20L) {
            this.activityStartTime = time;
            Activity activity = this.getSchedule().getActivityForTime((int)(timeOfDay % 24000L));
            if (!this.possibleActivities.contains(activity)) {
                this.resetPossibleActivities(activity);
            }
        }
    }

    public void setDefaultActivity(Activity activity) {
        this.defaultActivity = activity;
    }

    public void setTaskList(Activity activity, ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList) {
        this.setTaskList(activity, immutableList, ImmutableSet.of());
    }

    public void setTaskList(Activity activity, ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set) {
        this.requiredActivityMemories.put(activity, set);
        immutableList.forEach((Consumer<Pair<Integer, Task<Pair>>>)((Consumer<Pair>)pair -> this.tasks.computeIfAbsent((Integer)pair.getFirst(), (Function<Integer, Map<Activity, Set<Task<E>>>>)((Function<Integer, Map>)integer -> Maps.newHashMap())).computeIfAbsent(activity, activity -> Sets.newLinkedHashSet()).add(pair.getSecond())));
    }

    public boolean hasActivity(Activity activity) {
        return this.possibleActivities.contains(activity);
    }

    public Brain<E> copy() {
        Brain<E> brain = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<CompoundTag>(NbtOps.INSTANCE, new CompoundTag()));
        this.memories.forEach((memoryModuleType, optional) -> optional.ifPresent(object -> brain.memories.put((MemoryModuleType<?>)memoryModuleType, Optional.of(object))));
        return brain;
    }

    public void tick(ServerWorld serverWorld, E livingEntity) {
        this.updateSensors(serverWorld, livingEntity);
        this.startTasks(serverWorld, livingEntity);
        this.updateTasks(serverWorld, livingEntity);
    }

    public void stopAllTasks(ServerWorld serverWorld, E livingEntity) {
        long l = ((LivingEntity)livingEntity).world.getTime();
        this.streamRunningTasks().forEach(task -> task.stop(serverWorld, livingEntity, l));
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        Object object = ops.createMap(this.memories.entrySet().stream().filter(entry -> ((MemoryModuleType)entry.getKey()).getFactory().isPresent() && ((Optional)entry.getValue()).isPresent()).map(entry -> Pair.of(ops.createString(Registry.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)entry.getKey()).toString()), ((DynamicSerializable)((Optional)entry.getValue()).get()).serialize(ops))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (T)ops.createMap(ImmutableMap.of(ops.createString("memories"), object));
    }

    private void updateSensors(ServerWorld serverWorld, E livingEntity) {
        this.sensors.values().forEach(sensor -> sensor.canSense(serverWorld, livingEntity));
    }

    private void startTasks(ServerWorld serverWorld, E livingEntity) {
        long l = serverWorld.getTime();
        this.tasks.values().stream().flatMap(map -> map.entrySet().stream()).filter(entry -> this.possibleActivities.contains(entry.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).filter(task -> task.getStatus() == Task.Status.STOPPED).forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
    }

    private void updateTasks(ServerWorld serverWorld, E livingEntity) {
        long l = serverWorld.getTime();
        this.streamRunningTasks().forEach(task -> task.tick(serverWorld, livingEntity, l));
    }

    private boolean canDoActivity(Activity activity) {
        return this.requiredActivityMemories.get(activity).stream().allMatch(pair -> {
            MemoryModuleType memoryModuleType = (MemoryModuleType)pair.getFirst();
            MemoryModuleState memoryModuleState = (MemoryModuleState)((Object)((Object)pair.getSecond()));
            return this.isMemoryInState(memoryModuleType, memoryModuleState);
        });
    }

    private boolean isEmptyCollection(Object value) {
        return value instanceof Collection && ((Collection)value).isEmpty();
    }
}

