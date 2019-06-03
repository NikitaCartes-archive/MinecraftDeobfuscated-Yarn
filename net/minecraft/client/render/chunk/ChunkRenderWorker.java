/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderWorker
implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkBatcher batcher;
    private final BlockLayeredBufferBuilder bufferBuilders;
    private boolean running = true;

    public ChunkRenderWorker(ChunkBatcher chunkBatcher) {
        this(chunkBatcher, null);
    }

    public ChunkRenderWorker(ChunkBatcher chunkBatcher, @Nullable BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        this.batcher = chunkBatcher;
        this.bufferBuilders = blockLayeredBufferBuilder;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                this.runTask(this.batcher.getNextChunkRenderDataTask());
            } catch (InterruptedException interruptedException) {
                LOGGER.debug("Stopping chunk worker due to interrupt");
                return;
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
                MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().populateCrashReport(crashReport));
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void runTask(final ChunkRenderTask chunkRenderTask) throws InterruptedException {
        chunkRenderTask.getLock().lock();
        try {
            if (!ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.PENDING)) {
                return;
            }
            if (!chunkRenderTask.getChunkRenderer().shouldBuild()) {
                chunkRenderTask.cancel();
                return;
            }
            chunkRenderTask.setStage(ChunkRenderTask.Stage.COMPILING);
        } finally {
            chunkRenderTask.getLock().unlock();
        }
        final BlockLayeredBufferBuilder blockLayeredBufferBuilder = this.getBufferBuilders();
        chunkRenderTask.getLock().lock();
        try {
            if (!ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.COMPILING)) {
                this.freeRenderTask(blockLayeredBufferBuilder);
                return;
            }
        } finally {
            chunkRenderTask.getLock().unlock();
        }
        chunkRenderTask.setBufferBuilders(blockLayeredBufferBuilder);
        Vec3d vec3d = this.batcher.getCameraPosition();
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        ChunkRenderTask.Mode mode = chunkRenderTask.getMode();
        if (mode == ChunkRenderTask.Mode.REBUILD_CHUNK) {
            chunkRenderTask.getChunkRenderer().rebuildChunk(f, g, h, chunkRenderTask);
        } else if (mode == ChunkRenderTask.Mode.RESORT_TRANSPARENCY) {
            chunkRenderTask.getChunkRenderer().resortTransparency(f, g, h, chunkRenderTask);
        }
        chunkRenderTask.getLock().lock();
        try {
            if (!ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.COMPILING)) {
                this.freeRenderTask(blockLayeredBufferBuilder);
                return;
            }
            chunkRenderTask.setStage(ChunkRenderTask.Stage.UPLOADING);
        } finally {
            chunkRenderTask.getLock().unlock();
        }
        final ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
        ArrayList<ListenableFuture<Void>> list = Lists.newArrayList();
        if (mode == ChunkRenderTask.Mode.REBUILD_CHUNK) {
            for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
                if (!chunkRenderData.isBufferInitialized(blockRenderLayer)) continue;
                list.add(this.batcher.upload(blockRenderLayer, chunkRenderTask.getBufferBuilders().get(blockRenderLayer), chunkRenderTask.getChunkRenderer(), chunkRenderData, chunkRenderTask.getSquaredCameraDistance()));
            }
        } else if (mode == ChunkRenderTask.Mode.RESORT_TRANSPARENCY) {
            list.add(this.batcher.upload(BlockRenderLayer.TRANSLUCENT, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), chunkRenderTask.getChunkRenderer(), chunkRenderData, chunkRenderTask.getSquaredCameraDistance()));
        }
        ListenableFuture listenableFuture = Futures.allAsList(list);
        chunkRenderTask.addCompletionAction(() -> listenableFuture.cancel(false));
        Futures.addCallback(listenableFuture, new FutureCallback<List<Void>>(){

            public void method_3617(@Nullable List<Void> list) {
                ChunkRenderWorker.this.freeRenderTask(blockLayeredBufferBuilder);
                chunkRenderTask.getLock().lock();
                try {
                    if (!ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.UPLOADING)) {
                        return;
                    }
                    chunkRenderTask.setStage(ChunkRenderTask.Stage.DONE);
                } finally {
                    chunkRenderTask.getLock().unlock();
                }
                chunkRenderTask.getChunkRenderer().setData(chunkRenderData);
            }

            @Override
            public void onFailure(Throwable throwable) {
                ChunkRenderWorker.this.freeRenderTask(blockLayeredBufferBuilder);
                if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                    MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                }
            }

            @Override
            public /* synthetic */ void onSuccess(@Nullable Object object) {
                this.method_3617((List)object);
            }
        });
    }

    private static boolean method_20719(ChunkRenderTask chunkRenderTask, ChunkRenderTask.Stage stage) {
        if (chunkRenderTask.getStage() != stage) {
            if (!chunkRenderTask.isCancelled()) {
                LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", (Object)chunkRenderTask.getStage(), (Object)stage);
            }
            return false;
        }
        return true;
    }

    private BlockLayeredBufferBuilder getBufferBuilders() throws InterruptedException {
        return this.bufferBuilders != null ? this.bufferBuilders : this.batcher.getNextAvailableBuffer();
    }

    private void freeRenderTask(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
        if (blockLayeredBufferBuilder != this.bufferBuilders) {
            this.batcher.addAvailableBuffer(blockLayeredBufferBuilder);
        }
    }

    public void stop() {
        this.running = false;
    }
}

