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
	private BlockLayeredBufferBuilder bufferBuilder;
	private ChunkRenderData renderData;
	private ChunkRenderTask.Stage stage = ChunkRenderTask.Stage.field_4422;
	private boolean cancelled;

	public ChunkRenderTask(ChunkRenderer chunkRenderer, ChunkRenderTask.Mode mode, double d, @Nullable ChunkRendererRegion chunkRendererRegion) {
		this.chunkRenderer = chunkRenderer;
		this.mode = mode;
		this.squaredCameraDistance = d;
		this.region = chunkRendererRegion;
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

	public void setRenderData(ChunkRenderData chunkRenderData) {
		this.renderData = chunkRenderData;
	}

	public BlockLayeredBufferBuilder getBufferBuilders() {
		return this.bufferBuilder;
	}

	public void setBufferBuilders(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		this.bufferBuilder = blockLayeredBufferBuilder;
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
			if (this.mode == ChunkRenderTask.Mode.field_4426 && this.stage != ChunkRenderTask.Stage.field_4423) {
				this.chunkRenderer.scheduleRebuild(false);
			}

			this.cancelled = true;
			this.stage = ChunkRenderTask.Stage.field_4423;

			for (Runnable runnable : this.completionActions) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void addCompletionAction(Runnable runnable) {
		this.lock.lock();

		try {
			this.completionActions.add(runnable);
			if (this.cancelled) {
				runnable.run();
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

	public int method_3601(ChunkRenderTask chunkRenderTask) {
		return Doubles.compare(this.squaredCameraDistance, chunkRenderTask.squaredCameraDistance);
	}

	public double getSquaredCameraDistance() {
		return this.squaredCameraDistance;
	}

	@Environment(EnvType.CLIENT)
	public static enum Mode {
		field_4426,
		field_4427;
	}

	@Environment(EnvType.CLIENT)
	public static enum Stage {
		field_4422,
		field_4424,
		field_4421,
		field_4423;
	}
}
