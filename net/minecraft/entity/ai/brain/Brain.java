/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
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
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Brain<E extends LivingEntity> {
    static final Logger LOGGER = LogManager.getLogger();
    private final Supplier<Codec<Brain<E>>> codecSupplier;
    private static final int ACTIVITY_REFRESH_COOLDOWN = 20;
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

    public static <E extends LivingEntity> Profile<E> createProfile(Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
        return new Profile(memoryModules, sensors);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> createBrainCodec(final Collection<? extends MemoryModuleType<?>> memoryModules, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
        final MutableObject mutableObject = new MutableObject();
        mutableObject.setValue(new MapCodec<Brain<E>>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return memoryModules.stream().flatMap((? super T memoryModuleType) -> Util.stream(memoryModuleType.getCodec().map((? super T codec) -> Registry.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)memoryModuleType)))).map((? super T identifier) -> dynamicOps.createString(identifier.toString()));
            }

            @Override
            public <T> DataResult<Brain<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                MutableObject mutableObject2 = new MutableObject(DataResult.success(ImmutableList.builder()));
                mapLike.entries().forEach(pair -> {
                    DataResult dataResult = Registry.MEMORY_MODULE_TYPE.parse(dynamicOps, (MemoryModuleType<?>)pair.getFirst());
                    DataResult dataResult2 = dataResult.flatMap((? super R memoryModuleType) -> this.method_28320((MemoryModuleType)memoryModuleType, dynamicOps, (Object)pair.getSecond()));
                    mutableObject2.setValue(((DataResult)mutableObject2.getValue()).apply2(ImmutableList.Builder::add, dataResult2));
                });
                ImmutableList immutableList = mutableObject2.getValue().resultOrPartial(LOGGER::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
                return DataResult.success(new Brain(memoryModules, sensors, immutableList, mutableObject::getValue));
            }

            private <T, U> DataResult<MemoryEntry<U>> method_28320(MemoryModuleType<U> memoryModuleType, DynamicOps<T> dynamicOps, T object) {
                return memoryModuleType.getCodec().map(DataResult::success).orElseGet(() -> DataResult.error("No codec for memory: " + memoryModuleType)).flatMap((? super R codec) -> codec.parse(dynamicOps, object)).map((? super R memory) -> new MemoryEntry(memoryModuleType, Optional.of(memory)));
            }

            @Override
            public <T> RecordBuilder<T> encode(Brain<E> brain, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                brain.streamMemories().forEach(memoryEntry -> memoryEntry.serialize(dynamicOps, recordBuilder));
                return recordBuilder;
            }

            @Override
            public /* synthetic */ RecordBuilder encode(Object object, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
                return this.encode((Brain)object, dynamicOps, recordBuilder);
            }
        }.fieldOf("memories").codec());
        return (Codec)mutableObject.getValue();
    }

    public Brain(Collection<? extends MemoryModuleType<?>> memories, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors, ImmutableList<MemoryEntry<?>> memoryEntries, Supplier<Codec<Brain<E>>> codecSupplier) {
        this.codecSupplier = codecSupplier;
        for (MemoryModuleType<?> memoryModuleType : memories) {
            this.memories.put(memoryModuleType, Optional.empty());
        }
        for (SensorType sensorType : sensors) {
            this.sensors.put(sensorType, (Sensor<E>)sensorType.create());
        }
        for (Sensor sensor : this.sensors.values()) {
            for (MemoryModuleType<?> memoryModuleType2 : sensor.getOutputMemoryModules()) {
                this.memories.put(memoryModuleType2, Optional.empty());
            }
        }
        for (MemoryEntry memoryEntry : memoryEntries) {
            memoryEntry.apply(this);
        }
    }

    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return this.codecSupplier.get().encodeStart(ops, this);
    }

    Stream<MemoryEntry<?>> streamMemories() {
        return this.memories.entrySet().stream().map(entry -> MemoryEntry.of((MemoryModuleType)entry.getKey(), (Optional)entry.getValue()));
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

    public <U> void remember(MemoryModuleType<U> type, U value, long startTime) {
        this.setMemory(type, Optional.of(Memory.timed(value, startTime)));
    }

    public <U> void remember(MemoryModuleType<U> type, Optional<? extends U> value) {
        this.setMemory(type, value.map(Memory::permanent));
    }

    <U> void setMemory(MemoryModuleType<U> type, Optional<? extends Memory<?>> memory) {
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

    public <U> long getMemory(MemoryModuleType<U> type) {
        Optional<Memory<?>> optional = this.memories.get(type);
        return optional.map(Memory::getExpiry).orElse(0L);
    }

    @Deprecated
    @Debug
    public Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> getMemories() {
        return this.memories;
    }

    public <U> boolean hasMemoryModuleWithValue(MemoryModuleType<U> type, U value) {
        if (!this.hasMemoryModule(type)) {
            return false;
        }
        return this.getOptionalMemory(type).filter(object2 -> object2.equals(value)).isPresent();
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
    @Debug
    public Set<Activity> getPossibleActivities() {
        return this.possibleActivities;
    }

    @Deprecated
    @Debug
    public List<Task<? super E>> getRunningTasks() {
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

    public void doExclusively(Activity activity) {
        if (this.canDoActivity(activity)) {
            this.resetPossibleActivities(activity);
        } else {
            this.resetPossibleActivities();
        }
    }

    private void resetPossibleActivities(Activity except) {
        if (this.hasActivity(except)) {
            return;
        }
        this.forgetIrrelevantMemories(except);
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        this.possibleActivities.add(except);
    }

    private void forgetIrrelevantMemories(Activity except) {
        for (Activity activity : this.possibleActivities) {
            Set<MemoryModuleType<?>> set;
            if (activity == except || (set = this.forgettingActivityMemories.get(activity)) == null) continue;
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
                this.doExclusively(activity);
            }
        }
    }

    public void resetPossibleActivities(List<Activity> activities) {
        for (Activity activity : activities) {
            if (!this.canDoActivity(activity)) continue;
            this.resetPossibleActivities(activity);
            break;
        }
    }

    public void setDefaultActivity(Activity activity) {
        this.defaultActivity = activity;
    }

    public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> list) {
        this.setTaskList(activity, this.indexTaskList(begin, list));
    }

    public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryType) {
        ImmutableSet<Pair<MemoryModuleType<?>, MemoryModuleState>> set = ImmutableSet.of(Pair.of(memoryType, MemoryModuleState.VALUE_PRESENT));
        ImmutableSet<MemoryModuleType<?>> set2 = ImmutableSet.of(memoryType);
        this.setTaskList(activity, this.indexTaskList(begin, tasks), set, set2);
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks) {
        this.setTaskList(activity, indexedTasks, ImmutableSet.of(), Sets.newHashSet());
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories) {
        this.setTaskList(activity, indexedTasks, requiredMemories, Sets.newHashSet());
    }

    public void setTaskList(Activity activity2, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories, Set<MemoryModuleType<?>> forgettingMemories) {
        this.requiredActivityMemories.put(activity2, requiredMemories);
        if (!forgettingMemories.isEmpty()) {
            this.forgettingActivityMemories.put(activity2, forgettingMemories);
        }
        for (Pair pair : indexedTasks) {
            this.tasks.computeIfAbsent((Integer)pair.getFirst(), integer -> Maps.newHashMap()).computeIfAbsent(activity2, activity -> Sets.newLinkedHashSet()).add((Task)pair.getSecond());
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
        Brain<E> brain = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codecSupplier);
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            if (!entry.getValue().isPresent()) continue;
            brain.memories.put(memoryModuleType, entry.getValue());
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
        for (Sensor<E> sensor : this.sensors.values()) {
            sensor.tick(world, entity);
        }
    }

    private void tickMemories() {
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            if (!entry.getValue().isPresent()) continue;
            Memory<?> memory = entry.getValue().get();
            memory.tick();
            if (!memory.isExpired()) continue;
            this.forget(entry.getKey());
        }
    }

    public void stopAllTasks(ServerWorld world, E entity) {
        long l = ((LivingEntity)entity).world.getTime();
        for (Task<E> task : this.getRunningTasks()) {
            task.stop(world, entity, l);
        }
    }

    private void startTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Map.Entry<Activity, Set<Task<E>>> entry : map.entrySet()) {
                Activity activity = entry.getKey();
                if (!this.possibleActivities.contains(activity)) continue;
                Set<Task<E>> set = entry.getValue();
                for (Task<E> task : set) {
                    if (task.getStatus() != Task.Status.STOPPED) continue;
                    task.tryStarting(world, entity, l);
                }
            }
        }
    }

    private void updateTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for (Task<E> task : this.getRunningTasks()) {
            task.tick(world, entity, l);
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
     * @param begin the beginning of the index of tasks, exclusive
     */
    ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> tasks) {
        int i = begin;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Task task : tasks) {
            builder.add(Pair.of(i++, task));
        }
        return builder.build();
    }

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
            return this.codec.parse(data).resultOrPartial(LOGGER::error).orElseGet(() -> new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> this.codec));
        }
    }

    static final class MemoryEntry<U> {
        private final MemoryModuleType<U> type;
        private final Optional<? extends Memory<U>> data;

        static <U> MemoryEntry<U> of(MemoryModuleType<U> type, Optional<? extends Memory<?>> data) {
            return new MemoryEntry<U>(type, data);
        }

        MemoryEntry(MemoryModuleType<U> type, Optional<? extends Memory<U>> data) {
            this.type = type;
            this.data = data;
        }

        void apply(Brain<?> brain) {
            brain.setMemory(this.type, this.data);
        }

        public <T> void serialize(DynamicOps<T> ops, RecordBuilder<T> builder) {
            this.type.getCodec().ifPresent(codec -> this.data.ifPresent(memory -> builder.add(Registry.MEMORY_MODULE_TYPE.encodeStart(ops, this.type), codec.encodeStart(ops, memory))));
        }
    }
}

