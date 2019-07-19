package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
	private final ChunkRenderer chunkRenderer;
	private final ReentrantLock lock = new ReentrantLock();
	private final List<Runnable> completionActions = Lists.<Runnable>newArrayList();
	private final ChunkRenderTask.Mode mode;
	private final double squaredCameraDistance;
	@Nullable
	private ChunkRendererRegion region;
	private BlockBufferBuilderStorage bufferBuilder;
	private ChunkRenderData renderData;
	private ChunkRenderTask.Stage stage = ChunkRenderTask.Stage.PENDING;
	private boolean cancelled;

	public ChunkRenderTask(ChunkRenderer chunkRenderer, ChunkRenderTask.Mode mode, double squaredCameraDistance, @Nullable ChunkRendererRegion region) {
		this.chunkRenderer = chunkRenderer;
		this.mode = mode;
		this.squaredCameraDistance = squaredCameraDistance;
		this.region = region;
	}

	public ChunkRenderTask.Stage getStage() {
		return this.stage;
	}

	public ChunkRenderer getChunkRenderer() {
		return this.chunkRenderer;
	}

	@Nullable
	public ChunkRendererRegion takeRegion() {
		ChunkRendererRegion chunkRendererRegion = this.region;
		this.region = null;
		return chunkRendererRegion;
	}

	public ChunkRenderData getRenderData() {
		return this.renderData;
	}

	public void setRenderData(ChunkRenderData renderData) {
		this.renderData = renderData;
	}

	public BlockBufferBuilderStorage getBufferBuilders() {
		return this.bufferBuilder;
	}

	public void setBufferBuilders(BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.bufferBuilder = blockBufferBuilderStorage;
	}

	public void setStage(ChunkRenderTask.Stage stage) {
		this.lock.lock();

		try {
			this.stage = stage;
		} finally {
			this.lock.unlock();
		}
	}

	public void cancel() {
		this.lock.lock();

		try {
			this.region = null;
			if (this.mode == ChunkRenderTask.Mode.REBUILD_CHUNK && this.stage != ChunkRenderTask.Stage.DONE) {
				this.chunkRenderer.scheduleRebuild(false);
			}

			this.cancelled = true;
			this.stage = ChunkRenderTask.Stage.DONE;

			for (Runnable runnable : this.completionActions) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void addCompletionAction(Runnable action) {
		this.lock.lock();

		try {
			this.completionActions.add(action);
			if (this.cancelled) {
				action.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public ReentrantLock getLock() {
		return this.lock;
	}

	public ChunkRenderTask.Mode getMode() {
		return this.mode;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public int compareTo(ChunkRenderTask chunkRenderTask) {
		return Doubles.compare(this.squaredCameraDistance, chunkRenderTask.squaredCameraDistance);
	}

	public double getSquaredCameraDistance() {
		return this.squaredCameraDistance;
	}

	@Environment(EnvType.CLIENT)
	public static enum Mode {
		REBUILD_CHUNK,
		RESORT_TRANSPARENCY;
	}

	@Environment(EnvType.CLIENT)
	public static enum Stage {
		PENDING,
		COMPILING,
		UPLOADING,
		DONE;
	}
}
