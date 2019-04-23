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
import net.minecraft.util.Actor;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLightingProvider
extends LightingProvider
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MailboxProcessor<Runnable> processor;
    private final ObjectList<Pair<class_3901, Runnable>> pendingTasks = new ObjectArrayList<Pair<class_3901, Runnable>>();
    private final ThreadedAnvilChunkStorage chunkStorage;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor;
    private volatile int taskBatchSize = 5;
    private final AtomicBoolean field_18812 = new AtomicBoolean();

    public ServerLightingProvider(ChunkProvider chunkProvider, ThreadedAnvilChunkStorage threadedAnvilChunkStorage, boolean bl, MailboxProcessor<Runnable> mailboxProcessor, Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> actor) {
        super(chunkProvider, true, bl);
        this.chunkStorage = threadedAnvilChunkStorage;
        this.actor = actor;
        this.processor = mailboxProcessor;
    }

    @Override
    public void close() {
    }

    @Override
    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }

    @Override
    public void method_15560(BlockPos blockPos, int i) {
        throw new UnsupportedOperationException("Ran authomatically on a different thread!");
    }

    @Override
    public void enqueueLightUpdate(BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.toImmutable();
        this.enqueue(blockPos.getX() >> 4, blockPos.getZ() >> 4, class_3901.POST_UPDATE, SystemUtil.debugRunnable(() -> super.enqueueLightUpdate(blockPos2), () -> "checkBlock " + blockPos2));
    }

    protected void method_20386(ChunkPos chunkPos) {
        this.enqueue(chunkPos.x, chunkPos.z, () -> 0, class_3901.PRE_UPDATE, SystemUtil.debugRunnable(() -> {
            for (int i = 0; i < 16; ++i) {
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), true);
            }
        }, () -> "updateChunkStatus " + chunkPos + " " + true));
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
        this.enqueue(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkZ(), () -> 0, class_3901.PRE_UPDATE, SystemUtil.debugRunnable(() -> super.updateSectionStatus(chunkSectionPos, bl), () -> "updateSectionStatus " + chunkSectionPos + " " + bl));
    }

    @Override
    public void suppressLight(ChunkPos chunkPos, boolean bl) {
        this.enqueue(chunkPos.x, chunkPos.z, class_3901.PRE_UPDATE, SystemUtil.debugRunnable(() -> super.suppressLight(chunkPos, bl), () -> "enableLight " + chunkPos + " " + bl));
    }

    @Override
    public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, ChunkNibbleArray chunkNibbleArray) {
        this.enqueue(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkZ(), class_3901.PRE_UPDATE, SystemUtil.debugRunnable(() -> super.queueData(lightType, chunkSectionPos, chunkNibbleArray), () -> "queueData " + chunkSectionPos));
    }

    private void enqueue(int i, int j, class_3901 arg, Runnable runnable) {
        this.enqueue(i, j, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(i, j)), arg, runnable);
    }

    private void enqueue(int i, int j, IntSupplier intSupplier, class_3901 arg, Runnable runnable) {
        this.actor.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> {
            this.pendingTasks.add(Pair.of(arg, runnable));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(i, j), intSupplier));
    }

    public CompletableFuture<Chunk> light(Chunk chunk, boolean bl) {
        ChunkPos chunkPos = chunk.getPos();
        this.enqueue(chunkPos.x, chunkPos.z, class_3901.PRE_UPDATE, SystemUtil.debugRunnable(() -> {
            ChunkSection[] chunkSections = chunk.getSectionArray();
            for (int i = 0; i < 16; ++i) {
                ChunkSection chunkSection = chunkSections[i];
                if (ChunkSection.isEmpty(chunkSection)) continue;
                super.updateSectionStatus(ChunkSectionPos.from(chunkPos, i), false);
            }
            super.suppressLight(chunkPos, true);
            if (!bl) {
                chunk.getLightSourcesStream().forEach(blockPos -> super.method_15560((BlockPos)blockPos, chunk.getLuminance((BlockPos)blockPos)));
            }
            chunk.setLightOn(true);
            this.chunkStorage.method_20441(chunkPos);
        }, () -> "lightChunk " + chunkPos + " " + bl));
        return CompletableFuture.supplyAsync(() -> chunk, runnable -> this.enqueue(chunkPos.x, chunkPos.z, class_3901.POST_UPDATE, runnable));
    }

    public void tick() {
        if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.field_18812.compareAndSet(false, true)) {
            this.processor.send(() -> {
                this.runTasks();
                this.field_18812.set(false);
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
            if (pair.getFirst() != class_3901.PRE_UPDATE) continue;
            ((Runnable)pair.getSecond()).run();
        }
        objectListIterator.back(j);
        super.doLightUpdates(Integer.MAX_VALUE, true, true);
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            pair = (Pair)objectListIterator.next();
            if (pair.getFirst() == class_3901.POST_UPDATE) {
                ((Runnable)pair.getSecond()).run();
            }
            objectListIterator.remove();
        }
    }

    public void setTaskBatchSize(int i) {
        this.taskBatchSize = i;
    }

    static enum class_3901 {
        PRE_UPDATE,
        POST_UPDATE;

    }
}

