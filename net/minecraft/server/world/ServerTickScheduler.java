/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickScheduler;
import org.jetbrains.annotations.Nullable;

public class ServerTickScheduler<T>
implements TickScheduler<T> {
    protected final Predicate<T> invalidObjPredicate;
    private final Function<T, Identifier> idToName;
    private final Function<Identifier, T> nameToId;
    private final Set<ScheduledTick<T>> scheduledTickActions = Sets.newHashSet();
    private final TreeSet<ScheduledTick<T>> scheduledTickActionsInOrder = Sets.newTreeSet(ScheduledTick.getComparator());
    private final ServerWorld world;
    private final Queue<ScheduledTick<T>> currentTickActions = Queues.newArrayDeque();
    private final List<ScheduledTick<T>> consumedTickActions = Lists.newArrayList();
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
        int i = this.scheduledTickActionsInOrder.size();
        if (i != this.scheduledTickActions.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (i > 65536) {
            i = 65536;
        }
        ServerChunkManager serverChunkManager = this.world.method_14178();
        Iterator<ScheduledTick<T>> iterator = this.scheduledTickActionsInOrder.iterator();
        this.world.getProfiler().push("cleaning");
        while (i > 0 && iterator.hasNext()) {
            scheduledTick = iterator.next();
            if (scheduledTick.time > this.world.getTime()) break;
            if (!serverChunkManager.shouldTickBlock(scheduledTick.pos)) continue;
            iterator.remove();
            this.scheduledTickActions.remove(scheduledTick);
            this.currentTickActions.add(scheduledTick);
            --i;
        }
        this.world.getProfiler().swap("ticking");
        while ((scheduledTick = this.currentTickActions.poll()) != null) {
            if (serverChunkManager.shouldTickBlock(scheduledTick.pos)) {
                try {
                    this.consumedTickActions.add(scheduledTick);
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
        this.consumedTickActions.clear();
        this.currentTickActions.clear();
    }

    @Override
    public boolean isTicking(BlockPos blockPos, T object) {
        return this.currentTickActions.contains(new ScheduledTick<T>(blockPos, object));
    }

    @Override
    public void scheduleAll(Stream<ScheduledTick<T>> stream) {
        stream.forEach(this::addScheduledTick);
    }

    public List<ScheduledTick<T>> getScheduledTicksInChunk(ChunkPos chunkPos, boolean bl, boolean bl2) {
        int i = (chunkPos.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (chunkPos.z << 4) - 2;
        int l = k + 16 + 2;
        return this.getScheduledTicks(new BlockBox(i, 0, k, j, 256, l), bl, bl2);
    }

    public List<ScheduledTick<T>> getScheduledTicks(BlockBox blockBox, boolean bl, boolean bl2) {
        List<ScheduledTick<T>> list = this.transferTicksInBounds(null, this.scheduledTickActionsInOrder, blockBox, bl);
        if (bl && list != null) {
            this.scheduledTickActions.removeAll(list);
        }
        list = this.transferTicksInBounds(list, this.currentTickActions, blockBox, bl);
        if (!bl2) {
            list = this.transferTicksInBounds(list, this.consumedTickActions, blockBox, bl);
        }
        return list == null ? Collections.emptyList() : list;
    }

    @Nullable
    private List<ScheduledTick<T>> transferTicksInBounds(@Nullable List<ScheduledTick<T>> list, Collection<ScheduledTick<T>> collection, BlockBox blockBox, boolean bl) {
        Iterator<ScheduledTick<T>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            ScheduledTick<T> scheduledTick = iterator.next();
            BlockPos blockPos = scheduledTick.pos;
            if (blockPos.getX() < blockBox.minX || blockPos.getX() >= blockBox.maxX || blockPos.getZ() < blockBox.minZ || blockPos.getZ() >= blockBox.maxZ) continue;
            if (bl) {
                iterator.remove();
            }
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(scheduledTick);
        }
        return list;
    }

    public void copyScheduledTicks(BlockBox blockBox, BlockPos blockPos) {
        List<ScheduledTick<T>> list = this.getScheduledTicks(blockBox, false, false);
        for (ScheduledTick<T> scheduledTick : list) {
            if (!blockBox.contains(scheduledTick.pos)) continue;
            BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
            T object = scheduledTick.getObject();
            this.addScheduledTick(new ScheduledTick<T>(blockPos2, object, scheduledTick.time, scheduledTick.priority));
        }
    }

    public ListTag toTag(ChunkPos chunkPos) {
        List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(chunkPos, false, true);
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

    @Override
    public boolean isScheduled(BlockPos blockPos, T object) {
        return this.scheduledTickActions.contains(new ScheduledTick<T>(blockPos, object));
    }

    @Override
    public void schedule(BlockPos blockPos, T object, int i, TaskPriority taskPriority) {
        if (!this.invalidObjPredicate.test(object)) {
            this.addScheduledTick(new ScheduledTick<T>(blockPos, object, (long)i + this.world.getTime(), taskPriority));
        }
    }

    private void addScheduledTick(ScheduledTick<T> scheduledTick) {
        if (!this.scheduledTickActions.contains(scheduledTick)) {
            this.scheduledTickActions.add(scheduledTick);
            this.scheduledTickActionsInOrder.add(scheduledTick);
        }
    }

    public int method_20825() {
        return this.scheduledTickActions.size();
    }
}

