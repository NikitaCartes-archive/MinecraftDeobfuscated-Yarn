/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;

public class ServerTickScheduler<T>
implements TickScheduler<T> {
    protected final Predicate<T> invalidObjPredicate;
    protected final Function<T, Identifier> idToName;
    protected final Function<Identifier, T> nameToId;
    protected final Set<ScheduledTick<T>> ticksScheduled = Sets.newHashSet();
    protected final TreeSet<ScheduledTick<T>> ticksScheduledOrdered = new TreeSet();
    private final ServerWorld world;
    private final List<ScheduledTick<T>> ticksCurrent = Lists.newArrayList();
    private final Consumer<ScheduledTick<T>> tickConsumer;

    public ServerTickScheduler(ServerWorld serverWorld, Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, Consumer<ScheduledTick<T>> consumer) {
        this.invalidObjPredicate = predicate;
        this.idToName = function;
        this.nameToId = function2;
        this.world = serverWorld;
        this.tickConsumer = consumer;
    }

    public void tick() {
        ScheduledTick<T> scheduledTick;
        int i = this.ticksScheduledOrdered.size();
        if (i != this.ticksScheduled.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (i > 65536) {
            i = 65536;
        }
        this.world.getProfiler().push("cleaning");
        for (int j = 0; j < i; ++j) {
            scheduledTick = this.ticksScheduledOrdered.first();
            if (scheduledTick.time > this.world.getTime()) break;
            this.ticksScheduledOrdered.remove(scheduledTick);
            this.ticksScheduled.remove(scheduledTick);
            this.ticksCurrent.add(scheduledTick);
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("ticking");
        Iterator<ScheduledTick<T>> iterator = this.ticksCurrent.iterator();
        while (iterator.hasNext()) {
            scheduledTick = iterator.next();
            iterator.remove();
            if (this.world.isBlockLoaded(scheduledTick.pos)) {
                try {
                    this.tickConsumer.accept(scheduledTick);
                    continue;
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Exception while ticking");
                    CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
                    CrashReportSection.addBlockInfo(crashReportSection, scheduledTick.pos, null);
                    throw new CrashException(crashReport);
                }
            }
            this.schedule(scheduledTick.pos, scheduledTick.getObject(), 0);
        }
        this.world.getProfiler().pop();
        this.ticksCurrent.clear();
    }

    @Override
    public boolean isTicking(BlockPos blockPos, T object) {
        return this.ticksCurrent.contains(new ScheduledTick<T>(blockPos, object));
    }

    @Override
    public void method_20470(Stream<ScheduledTick<T>> stream) {
        stream.forEach(this::method_20468);
    }

    public List<ScheduledTick<T>> getScheduledTicksInChunk(boolean bl, ChunkPos chunkPos) {
        int i = (chunkPos.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (chunkPos.z << 4) - 2;
        int l = k + 16 + 2;
        return this.getScheduledTicks(new MutableIntBoundingBox(i, 0, k, j, 256, l), bl);
    }

    public List<ScheduledTick<T>> getScheduledTicks(MutableIntBoundingBox mutableIntBoundingBox, boolean bl) {
        ArrayList<ScheduledTick<T>> list = null;
        for (int i = 0; i < 2; ++i) {
            Iterator<ScheduledTick<T>> iterator = i == 0 ? this.ticksScheduledOrdered.iterator() : this.ticksCurrent.iterator();
            while (iterator.hasNext()) {
                ScheduledTick<T> scheduledTick = iterator.next();
                BlockPos blockPos = scheduledTick.pos;
                if (blockPos.getX() < mutableIntBoundingBox.minX || blockPos.getX() >= mutableIntBoundingBox.maxX || blockPos.getZ() < mutableIntBoundingBox.minZ || blockPos.getZ() >= mutableIntBoundingBox.maxZ) continue;
                if (bl) {
                    if (i == 0) {
                        this.ticksScheduled.remove(scheduledTick);
                    }
                    iterator.remove();
                }
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(scheduledTick);
            }
        }
        return list == null ? Collections.emptyList() : list;
    }

    public void copyScheduledTicks(MutableIntBoundingBox mutableIntBoundingBox, BlockPos blockPos) {
        List<ScheduledTick<T>> list = this.getScheduledTicks(mutableIntBoundingBox, false);
        for (ScheduledTick<T> scheduledTick : list) {
            if (!mutableIntBoundingBox.contains(scheduledTick.pos)) continue;
            BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
            this.scheduleTick(blockPos2, scheduledTick.getObject(), (int)(scheduledTick.time - this.world.getLevelProperties().getTime()), scheduledTick.priority);
        }
    }

    public ListTag toTag(ChunkPos chunkPos) {
        List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(false, chunkPos);
        return ServerTickScheduler.serializeScheduledTicks(this.idToName, list, this.world.getTime());
    }

    public static <T> ListTag serializeScheduledTicks(Function<T, Identifier> function, Iterable<ScheduledTick<T>> iterable, long l) {
        ListTag listTag = new ListTag();
        for (ScheduledTick<T> scheduledTick : iterable) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("i", function.apply(scheduledTick.getObject()).toString());
            compoundTag.putInt("x", scheduledTick.pos.getX());
            compoundTag.putInt("y", scheduledTick.pos.getY());
            compoundTag.putInt("z", scheduledTick.pos.getZ());
            compoundTag.putInt("t", (int)(scheduledTick.time - l));
            compoundTag.putInt("p", scheduledTick.priority.getPriorityIndex());
            listTag.add(compoundTag);
        }
        return listTag;
    }

    public void fromTag(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundTag(i);
            T object = this.nameToId.apply(new Identifier(compoundTag.getString("i")));
            if (object == null) continue;
            this.scheduleTick(new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z")), object, compoundTag.getInt("t"), TaskPriority.getByIndex(compoundTag.getInt("p")));
        }
    }

    @Override
    public boolean isScheduled(BlockPos blockPos, T object) {
        return this.ticksScheduled.contains(new ScheduledTick<T>(blockPos, object));
    }

    @Override
    public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
        if (this.invalidObjPredicate.test(object)) {
            return;
        }
        this.scheduleTickUnchecked(blockPos, object, i, taskPriority);
    }

    protected void scheduleTick(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
        if (!this.invalidObjPredicate.test(object)) {
            this.scheduleTickUnchecked(blockPos, object, i, taskPriority);
        }
    }

    private void scheduleTickUnchecked(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
        ScheduledTick<T> scheduledTick = new ScheduledTick<T>(blockPos, object, (long)i + this.world.getTime(), taskPriority);
        this.method_20468(scheduledTick);
    }

    private void method_20468(ScheduledTick<T> scheduledTick) {
        if (!this.ticksScheduled.contains(scheduledTick)) {
            this.ticksScheduled.add(scheduledTick);
            this.ticksScheduledOrdered.add(scheduledTick);
        }
    }
}

