/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.gl.GlBufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderWorker;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkBatcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build();
    private final int bufferCount;
    private final List<Thread> workerThreads = Lists.newArrayList();
    private final List<ChunkRenderWorker> workers = Lists.newArrayList();
    private final PriorityBlockingQueue<ChunkRenderTask> pendingChunks = Queues.newPriorityBlockingQueue();
    private final BlockingQueue<BlockLayeredBufferBuilder> availableBuffers;
    private final BufferRenderer displayListBufferRenderer = new BufferRenderer();
    private final GlBufferRenderer vboBufferRenderer = new GlBufferRenderer();
    private final Queue<ChunkUploadTask> pendingUploads = Queues.newPriorityQueue();
    private final ChunkRenderWorker clientThreadWorker;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBatcher() {
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 0xA00000);
        int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
        int k = MathHelper.clamp(j * 10, 1, i);
        if (j > 1) {
            for (int l = 0; l < j; ++l) {
                ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
                Thread thread = THREAD_FACTORY.newThread(chunkRenderWorker);
                thread.start();
                this.workers.add(chunkRenderWorker);
                this.workerThreads.add(thread);
            }
        }
        ArrayList<BlockLayeredBufferBuilder> list = Lists.newArrayListWithExpectedSize(k);
        try {
            list.add(new BlockLayeredBufferBuilder());
        } catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.error("Allocated only {}/{} buffers", (Object)list.size(), (Object)k);
            list.remove(list.size() - 1);
            System.gc();
        }
        this.bufferCount = list.size();
        this.availableBuffers = Queues.newArrayBlockingQueue(this.bufferCount);
        this.availableBuffers.addAll(list);
        this.clientThreadWorker = new ChunkRenderWorker(this, new BlockLayeredBufferBuilder());
    }

    public String getDebugString() {
        if (this.workerThreads.isEmpty()) {
            return String.format("pC: %03d, single-threaded", this.pendingChunks.size());
        }
        return String.format("pC: %03d, pU: %1d, aB: %1d", this.pendingChunks.size(), this.pendingUploads.size(), this.availableBuffers.size());
    }

    public void setCameraPosition(Vec3d vec3d) {
        this.cameraPosition = vec3d;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean runTasksSync(long l) {
        boolean bl2;
        boolean bl = false;
        do {
            ChunkRenderTask chunkRenderTask;
            bl2 = false;
            if (this.workerThreads.isEmpty() && (chunkRenderTask = this.pendingChunks.poll()) != null) {
                try {
                    this.clientThreadWorker.runTask(chunkRenderTask);
                    bl2 = true;
                } catch (InterruptedException interruptedException) {
                    LOGGER.warn("Skipped task due to interrupt");
                }
            }
            Queue<ChunkUploadTask> queue = this.pendingUploads;
            synchronized (queue) {
                if (!this.pendingUploads.isEmpty()) {
                    this.pendingUploads.poll().task.run();
                    bl2 = true;
                    bl = true;
                }
            }
        } while (l != 0L && bl2 && l >= SystemUtil.getMeasuringTimeNano());
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rebuild(ChunkRenderer chunkRenderer) {
        chunkRenderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = chunkRenderer.startRebuild();
            chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
            boolean bl = this.pendingChunks.offer(chunkRenderTask);
            if (!bl) {
                chunkRenderTask.cancel();
            }
            boolean bl2 = bl;
            return bl2;
        } finally {
            chunkRenderer.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rebuildSync(ChunkRenderer chunkRenderer) {
        chunkRenderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = chunkRenderer.startRebuild();
            try {
                this.clientThreadWorker.runTask(chunkRenderTask);
            } catch (InterruptedException interruptedException) {
                // empty catch block
            }
            boolean bl = true;
            return bl;
        } finally {
            chunkRenderer.getLock().unlock();
        }
    }

    public void reset() {
        this.clear();
        ArrayList<BlockLayeredBufferBuilder> list = Lists.newArrayList();
        while (list.size() != this.bufferCount) {
            this.runTasksSync(Long.MAX_VALUE);
            try {
                list.add(this.getNextAvailableBuffer());
            } catch (InterruptedException interruptedException) {}
        }
        this.availableBuffers.addAll(list);
    }

    public void addAvailableBuffer(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        this.availableBuffers.add(blockLayeredBufferBuilder);
    }

    public BlockLayeredBufferBuilder getNextAvailableBuffer() throws InterruptedException {
        return this.availableBuffers.take();
    }

    public ChunkRenderTask getNextChunkRenderDataTask() throws InterruptedException {
        return this.pendingChunks.take();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean resortTransparency(ChunkRenderer chunkRenderer) {
        chunkRenderer.getLock().lock();
        try {
            ChunkRenderTask chunkRenderTask = chunkRenderer.startResortTransparency();
            if (chunkRenderTask != null) {
                chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
                boolean bl = this.pendingChunks.offer(chunkRenderTask);
                return bl;
            }
            boolean bl = true;
            return bl;
        } finally {
            chunkRenderer.getLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListenableFuture<Object> upload(BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d) {
        if (MinecraftClient.getInstance().isOnThread()) {
            if (GLX.useVbo()) {
                this.uploadVbo(bufferBuilder, chunkRenderer.getGlBuffer(blockRenderLayer.ordinal()));
            } else {
                this.uploadDisplayList(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).method_3639(blockRenderLayer, chunkRenderData));
            }
            bufferBuilder.setOffset(0.0, 0.0, 0.0);
            return Futures.immediateFuture(null);
        }
        ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(() -> this.upload(blockRenderLayer, bufferBuilder, chunkRenderer, chunkRenderData, d), null);
        Queue<ChunkUploadTask> queue = this.pendingUploads;
        synchronized (queue) {
            this.pendingUploads.add(new ChunkUploadTask(listenableFutureTask, d));
        }
        return listenableFutureTask;
    }

    private void uploadDisplayList(BufferBuilder bufferBuilder, int i) {
        GlStateManager.newList(i, 4864);
        this.displayListBufferRenderer.draw(bufferBuilder);
        GlStateManager.endList();
    }

    private void uploadVbo(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
        this.vboBufferRenderer.setGlBuffer(glBuffer);
        this.vboBufferRenderer.draw(bufferBuilder);
    }

    public void clear() {
        while (!this.pendingChunks.isEmpty()) {
            ChunkRenderTask chunkRenderTask = this.pendingChunks.poll();
            if (chunkRenderTask == null) continue;
            chunkRenderTask.cancel();
        }
    }

    public boolean isEmpty() {
        return this.pendingChunks.isEmpty() && this.pendingUploads.isEmpty();
    }

    public void stop() {
        this.clear();
        for (ChunkRenderWorker chunkRenderWorker : this.workers) {
            chunkRenderWorker.stop();
        }
        for (Thread thread : this.workerThreads) {
            try {
                thread.interrupt();
                thread.join();
            } catch (InterruptedException interruptedException) {
                LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)interruptedException);
            }
        }
        this.availableBuffers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    class ChunkUploadTask
    implements Comparable<ChunkUploadTask> {
        private final ListenableFutureTask<Object> task;
        private final double priority;

        public ChunkUploadTask(ListenableFutureTask<Object> listenableFutureTask, double d) {
            this.task = listenableFutureTask;
            this.priority = d;
        }

        public int method_3638(ChunkUploadTask chunkUploadTask) {
            return Doubles.compare(this.priority, chunkUploadTask.priority);
        }

        @Override
        public /* synthetic */ int compareTo(Object object) {
            return this.method_3638((ChunkUploadTask)object);
        }
    }
}

