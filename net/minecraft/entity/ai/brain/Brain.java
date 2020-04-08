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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class Brain<E extends LivingEntity>
implements DynamicSerializable {
    private final Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories = Maps.newHashMap();
    /**
     * The map from activities to the memories to forget after the activity is
     * completed.
     */
    private final Map<Activity, Set<MemoryModuleType<?>>> forgettingActivityMemories = Maps.newHashMap();
    private Set<Activity> coreActivities = Sets.newHashSet();
    private final Set<Activity> possibleActivities = Sets.newHashSet();
    private Activity defaultActivity = Activity.IDLE;
    private long activityStartTime = -9999L;

    public <T> Brain(Collection<MemoryModuleType<?>> memoryTypes, Collection<SensorType<? extends Sensor<? super E>>> collection, Dynamic<T> dynamic) {
        for (MemoryModuleType<?> memoryModuleType : memoryTypes) {
            this.memories.put(memoryModuleType, Optional.empty());
        }
        for (SensorType sensorType : collection) {
            this.sensors.put(sensorType, (Sensor<E>)sensorType.create());
        }
        for (Sensor sensor : this.sensors.values()) {
            for (MemoryModuleType<?> memoryModuleType2 : sensor.getOutputMemoryModules()) {
                this.memories.put(memoryModuleType2, Optional.empty());
            }
        }
        for (Map.Entry entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
            this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic)entry.getValue());
        }
    }

    public boolean hasMemoryModule(MemoryModuleType<?> type) {
        return this.isMemoryInState(type, MemoryModuleState.VALUE_PRESENT);
    }

    private <T, U> void readMemory(MemoryModuleType<U> type, Dynamic<T> data) {
        Memory<U> memory = new Memory<U>(type.getFactory().orElseThrow(RuntimeException::new), data);
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
            if (memory.isPresent() && this.isEmptyCollection(memory.get().getValue())) {
                this.forget(type);
            } else {
                this.memories.put(type, memory);
            }
        }
    }

    public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> type) {
        return this.memories.get(type).map(Memory::getValue);
    }

    public boolean isMemoryInState(MemoryModuleType<?> type, MemoryModuleState state) {
        Optional<Memory<?>> optional = this.memories.get(type);
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

    public void setCoreActivities(Set<Activity> coreActivities) {
        this.coreActivities = coreActivities;
    }

    @Deprecated
    public List<Task<? super E>> method_27074() {
        ObjectArrayList<Task<Task<E>>> list = new ObjectArrayList<Task<Task<E>>>();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Set<Task<E>> set : map.values()) {
                for (Task<E> task : set) {
                    if (task.getStatus() != Task.Status.RUNNING) continue;
                    list.add(task);
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
            if (this.coreActivities.contains(activity)) continue;
            return Optional.of(activity);
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
        if (this.hasActivity(activity)) {
            return;
        }
        this.method_24537(activity);
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        this.possibleActivities.add(activity);
    }

    private void method_24537(Activity activity) {
        for (Activity activity2 : this.possibleActivities) {
            Set<MemoryModuleType<?>> set;
            if (activity2 == activity || (set = this.forgettingActivityMemories.get(activity2)) == null) continue;
            for (MemoryModuleType<?> memoryModuleType : set) {
                this.forget(memoryModuleType);
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
            if (!this.canDoActivity(activity)) continue;
            this.resetPossibleActivities(activity);
            break;
        }
    }

    public void setDefaultActivity(Activity activity) {
        this.defaultActivity = activity;
    }

    public void setTaskList(Activity activity, int i, ImmutableList<? extends Task<? super E>> immutableList) {
        this.setTaskList(activity, this.indexTaskList(i, immutableList));
    }

    public void setTaskList(Activity activity, int i, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryModuleType) {
        ImmutableSet<Pair<MemoryModuleType<?>, MemoryModuleState>> set = ImmutableSet.of(Pair.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT));
        ImmutableSet<MemoryModuleType<?>> set2 = ImmutableSet.of(memoryModuleType);
        this.setTaskList(activity, this.indexTaskList(i, tasks), set, set2);
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks) {
        this.setTaskList(activity, indexedTasks, ImmutableSet.of(), Sets.newHashSet());
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories) {
        this.setTaskList(activity, indexedTasks, requiredMemories, Sets.newHashSet());
    }

    private void setTaskList(Activity activity2, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories, Set<MemoryModuleType<?>> forgettingMemories) {
        this.requiredActivityMemories.put(activity2, requiredMemories);
        if (!forgettingMemories.isEmpty()) {
            this.forgettingActivityMemories.put(activity2, forgettingMemories);
        }
        for (Pair pair : indexedTasks) {
            this.tasks.computeIfAbsent((Integer)pair.getFirst(), (Function<Integer, Map<Activity, Set<Task<E>>>>)((Function<Integer, Map>)integer -> Maps.newHashMap())).computeIfAbsent(activity2, activity -> Sets.newLinkedHashSet()).add(pair.getSecond());
        }
    }

    public boolean hasActivity(Activity activity) {
        return this.possibleActivities.contains(activity);
    }

    public Brain<E> copy() {
        Brain<E> brain = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<CompoundTag>(NbtOps.INSTANCE, new CompoundTag()));
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            if (!entry.getValue().isPresent()) continue;
            brain.memories.put(memoryModuleType, entry.getValue());
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
        for (Sensor<E> sensor : this.sensors.values()) {
            sensor.canSense(serverWorld, livingEntity);
        }
    }

    private void method_27075() {
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            if (!entry.getValue().isPresent()) continue;
            Memory<?> memory = entry.getValue().get();
            memory.method_24913();
            if (!memory.isExpired()) continue;
            this.forget(entry.getKey());
        }
    }

    public void stopAllTasks(ServerWorld serverWorld, E livingEntity) {
        long l = ((LivingEntity)livingEntity).world.getTime();
        for (Task<E> task : this.method_27074()) {
            task.stop(serverWorld, livingEntity, l);
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, ?> builder = ImmutableMap.builder();
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            if (!entry.getValue().isPresent() || !memoryModuleType.getFactory().isPresent()) continue;
            Memory<?> memory = entry.getValue().get();
            T object = ops.createString(Registry.MEMORY_MODULE_TYPE.getId(memoryModuleType).toString());
            Object object2 = memory.serialize(ops);
            builder.put(object, object2);
        }
        return ops.createMap(ImmutableMap.of(ops.createString("memories"), ops.createMap(builder.build())));
    }

    private void startTasks(ServerWorld serverWorld, E livingEntity) {
        long l = serverWorld.getTime();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Map.Entry<Activity, Set<Task<E>>> entry : map.entrySet()) {
                Activity activity = entry.getKey();
                if (!this.possibleActivities.contains(activity)) continue;
                Set<Task<E>> set = entry.getValue();
                for (Task<E> task : set) {
                    if (task.getStatus() != Task.Status.STOPPED) continue;
                    task.tryStarting(serverWorld, livingEntity, l);
                }
            }
        }
    }

    private void updateTasks(ServerWorld serverWorld, E livingEntity) {
        long l = serverWorld.getTime();
        for (Task<E> task : this.method_27074()) {
            task.tick(serverWorld, livingEntity, l);
        }
    }

    private boolean canDoActivity(Activity activity) {
        if (!this.requiredActivityMemories.containsKey(activity)) {
            return false;
        }
        for (Pair<MemoryModuleType<?>, MemoryModuleState> pair : this.requiredActivityMemories.get(activity)) {
            MemoryModuleState memoryModuleState;
            MemoryModuleType<?> memoryModuleType = pair.getFirst();
            if (this.isMemoryInState(memoryModuleType, memoryModuleState = pair.getSecond())) continue;
            return false;
        }
        return true;
    }

    private boolean isEmptyCollection(Object value) {
        return value instanceof Collection && ((Collection)value).isEmpty();
    }

    /**
     * @param begin The beginning of the index of tasks, exclusive
     */
    ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> immutableList) {
        int i = begin;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Task task : immutableList) {
            builder.add(Pair.of(i++, task));
        }
        return builder.build();
    }
}

