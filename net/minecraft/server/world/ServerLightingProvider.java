/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerLightingProvider
extends LightingProvider
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TaskExecutor<Runnable> processor;
    private final ObjectList<Pair<Stage, Runnable>> pendingTasks = new ObjectArrayList<Pair<Stage, Runnable>>();
    private final ThreadedAnvilChunkStorage chunkStorage;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor;
    private volatile int taskBatchSize = 5;
    private final AtomicBoolean ticking = new AtomicBoolean();

    public ServerLightingProvider(ChunkProvider chunkProvider, ThreadedAnvilChunkStorage chunkStorage, boolean hasBlockLight, TaskExecutor<Runnable> processor, MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor) {
        super(chunkProvider, true, hasBlockLight);
        this.chunkStorage = chunkStorage;
        this.executor = executor;
        this.processor = processor;
    }

    @Override
    public void close() {
    }

    @Override
    public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void addLightSource(BlockPos pos, int level) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPos pos) {
        BlockPos blockPos = pos.toImmutable();
        this.enqueue(pos.getX() >> 4, pos.getZ() >> 4, Stage.POST_UPDATE, Util.debugRunnable(() -> super.checkBlock(blockPos), () -> "checkBlock " + blockPos));
    }

    protected void updateChunkStatus(ChunkPos pos) {
        this.enqueue(pos.x, pos.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            int i;
            super.setRetainData(pos, false);
            super.setLightEnabled(pos, false);
            for (i = -1; i < 17; ++i) {
                super.queueData(LightType.BLOCK, ChunkSectionPos.from(pos, i), null);
                super.queueData(LightType.SKY, ChunkSectionPos.from(pos, i), null);
            }
            for (i = 0; i < 16; ++i) {
                super.updateSectionStatus(ChunkSectionPos.from(pos, i), true);
            }
        }, () -> "updateChunkStatus " + pos + " " + true));
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.updateSectionStatus(pos, status), () -> "updateSectionStatus " + pos + " " + status));
    }

    @Override
    public void setLightEnabled(ChunkPos pos, boolean lightEnabled) {
        this.enqueue(pos.x, pos.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setLightEnabled(pos, lightEnabled), () -> "enableLight " + pos + " " + lightEnabled));
    }

    @Override
    public void queueData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles) {
        this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.queueData(lightType, pos, nibbles), () -> "queueData " + pos));
    }

    private void enqueue(int x, int z, Stage stage, Runnable task) {
        this.enqueue(x, z, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
    }

    private void enqueue(int x, int z, IntSupplier completedLevelSupplier, Stage stage, Runnable task) {
        this.executor.send(ChunkTaskPrioritySystem.createMessage(() -> {
            this.pendingTasks.add(Pair.of(stage, task));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(x, z), completedLevelSupplier));
    }

    @Override
    public void setRetainData(ChunkPos pos, boolean retainData) {
        this.enqueue(pos.x, pos.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setRetainData(pos, retainData), () -> "retainData " + pos));
    }

    public CompletableFuture<Chunk> light(Chunk chunk, boolean excludeBlocks) {
        ChunkPos chunkPos = chunk.getPos();
        chunk.setLightOn(false);
        this.enqueue(chunkPos.x, chunkPos.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            ChunkSection[] chunkSections = chunk.getSectionArray();
            for (int i = 0; i < 16; ++i) {
                ChunkSection chunkSection = chunkSections[i];
                if (ChunkSection.isEmpty(chunkSection)) continue;
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), false);
            }
            super.setLightEnabled(chunkPos, true);
            if (!excludeBlocks) {
                chunk.getLightSourcesStream().forEach(blockPos -> super.addLightSource((BlockPos)blockPos, chunk.getLuminance((BlockPos)blockPos)));
            }
            this.chunkStorage.releaseLightTicket(chunkPos);
        }, () -> "lightChunk " + chunkPos + " " + excludeBlocks));
        return CompletableFuture.supplyAsync(() -> {
            chunk.setLightOn(true);
            super.setRetainData(chunkPos, false);
            return chunk;
        }, runnable -> this.enqueue(chunkPos.x, chunkPos.z, Stage.POST_UPDATE, runnable));
    }

    public void tick() {
        if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.ticking.compareAndSet(false, true)) {
            this.processor.send(() -> {
                this.runTasks();
                this.ticking.set(false);
            });
        }
    }

    private void runTasks() {
        Pair pair;
        int j;
        int i = Math.min(this.pendingTasks.size(), this.taskBatchSize);
        Iterator objectListIterator = this.pendingTasks.iterator();
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() != Stage.PRE_UPDATE) continue;
            ((Runnable)pair.getSecond()).run();
        }
        objectListIterator.back(j);
        super.doLightUpdates(Integer.MAX_VALUE, true, true);
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() == Stage.POST_UPDATE) {
                ((Runnable)pair.getSecond()).run();
            }
            objectListIterator.remove();
        }
    }

    public void setTaskBatchSize(int taskBatchSize) {
        this.taskBatchSize = taskBatchSize;
    }

    static enum Stage {
        PRE_UPDATE,
        POST_UPDATE;

    }
}

