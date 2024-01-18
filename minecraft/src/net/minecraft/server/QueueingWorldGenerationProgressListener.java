package net.minecraft.server;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;

public class QueueingWorldGenerationProgressListener implements WorldGenerationProgressListener {
	private final WorldGenerationProgressListener progressListener;
	private final TaskExecutor<Runnable> queue;
	private boolean running;

	private QueueingWorldGenerationProgressListener(WorldGenerationProgressListener progressListener, Executor executor) {
		this.progressListener = progressListener;
		this.queue = TaskExecutor.create(executor, "progressListener");
	}

	public static QueueingWorldGenerationProgressListener create(WorldGenerationProgressListener progressListener, Executor executor) {
		QueueingWorldGenerationProgressListener queueingWorldGenerationProgressListener = new QueueingWorldGenerationProgressListener(progressListener, executor);
		queueingWorldGenerationProgressListener.start();
		return queueingWorldGenerationProgressListener;
	}

	@Override
	public void start(ChunkPos spawnPos) {
		this.queue.send(() -> this.progressListener.start(spawnPos));
	}

	@Override
	public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {
		if (this.running) {
			this.queue.send(() -> this.progressListener.setChunkStatus(pos, status));
		}
	}

	@Override
	public void start() {
		this.running = true;
		this.queue.send(this.progressListener::start);
	}

	@Override
	public void stop() {
		this.running = false;
		this.queue.send(this.progressListener::stop);
	}
}
