package net.minecraft.server;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(EnvType.CLIENT)
public class QueueingWorldGenerationProgressListener implements WorldGenerationProgressListener {
	private final WorldGenerationProgressListener progressListener;
	private final TaskExecutor<Runnable> queue;
	private volatile boolean field_29184;

	private QueueingWorldGenerationProgressListener(WorldGenerationProgressListener progressListener, Executor executor) {
		this.progressListener = progressListener;
		this.queue = TaskExecutor.create(executor, "progressListener");
	}

	public static QueueingWorldGenerationProgressListener method_34228(WorldGenerationProgressListener worldGenerationProgressListener, Executor executor) {
		QueueingWorldGenerationProgressListener queueingWorldGenerationProgressListener = new QueueingWorldGenerationProgressListener(
			worldGenerationProgressListener, executor
		);
		queueingWorldGenerationProgressListener.start();
		return queueingWorldGenerationProgressListener;
	}

	@Override
	public void start(ChunkPos spawnPos) {
		if (this.field_29184) {
			this.queue.send(() -> this.progressListener.start(spawnPos));
		}
	}

	@Override
	public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
		if (this.field_29184) {
			this.queue.send(() -> this.progressListener.setChunkStatus(pos, status));
		}
	}

	@Override
	public void start() {
		if (!this.field_29184) {
			this.field_29184 = true;
			this.queue.send(this.progressListener::start);
		}
	}

	@Override
	public void stop() {
		if (this.field_29184) {
			this.field_29184 = false;
			this.queue.send(this.progressListener::stop);
		}
	}
}
