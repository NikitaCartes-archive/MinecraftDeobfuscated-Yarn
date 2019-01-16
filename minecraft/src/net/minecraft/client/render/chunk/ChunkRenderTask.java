package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_853;

@Environment(EnvType.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
	private final ChunkRenderer chunkRenderer;
	private final ReentrantLock lock = new ReentrantLock();
	private final List<Runnable> runnables = Lists.<Runnable>newArrayList();
	private final ChunkRenderTask.Mode mode;
	private final double distanceToPlayerSquared;
	@Nullable
	private class_853 field_4414;
	private BlockLayeredBufferBuilder bufferBuilder;
	private ChunkRenderData renderData;
	private ChunkRenderTask.Stage stage = ChunkRenderTask.Stage.field_4422;
	private boolean cancelled;

	public ChunkRenderTask(ChunkRenderer chunkRenderer, ChunkRenderTask.Mode mode, double d, @Nullable class_853 arg) {
		this.chunkRenderer = chunkRenderer;
		this.mode = mode;
		this.distanceToPlayerSquared = d;
		this.field_4414 = arg;
	}

	public ChunkRenderTask.Stage getStage() {
		return this.stage;
	}

	public ChunkRenderer getChunkRenderer() {
		return this.chunkRenderer;
	}

	@Nullable
	public class_853 method_3606() {
		class_853 lv = this.field_4414;
		this.field_4414 = null;
		return lv;
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
			this.field_4414 = null;
			if (this.mode == ChunkRenderTask.Mode.field_4426 && this.stage != ChunkRenderTask.Stage.field_4423) {
				this.chunkRenderer.scheduleRender(false);
			}

			this.cancelled = true;
			this.stage = ChunkRenderTask.Stage.field_4423;

			for (Runnable runnable : this.runnables) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void add(Runnable runnable) {
		this.lock.lock();

		try {
			this.runnables.add(runnable);
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

	public int compareTo(ChunkRenderTask chunkRenderTask) {
		return Doubles.compare(this.distanceToPlayerSquared, chunkRenderTask.distanceToPlayerSquared);
	}

	public double getDistanceToPlayerSquared() {
		return this.distanceToPlayerSquared;
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
