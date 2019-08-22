/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class QueueingWorldGenerationProgressListener
implements WorldGenerationProgressListener {
    private final WorldGenerationProgressListener progressListener;
    private final MailboxProcessor<Runnable> queue;

    public QueueingWorldGenerationProgressListener(WorldGenerationProgressListener worldGenerationProgressListener, Executor executor) {
        this.progressListener = worldGenerationProgressListener;
        this.queue = MailboxProcessor.create(executor, "progressListener");
    }

    @Override
    public void start(ChunkPos chunkPos) {
        this.queue.send(() -> this.progressListener.start(chunkPos));
    }

    @Override
    public void setChunkStatus(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
        this.queue.send(() -> this.progressListener.setChunkStatus(chunkPos, chunkStatus));
    }

    @Override
    public void stop() {
        this.queue.send(this.progressListener::stop);
    }
}

