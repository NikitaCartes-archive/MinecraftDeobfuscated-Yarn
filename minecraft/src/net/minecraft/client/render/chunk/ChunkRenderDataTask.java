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
public class ChunkRenderDataTask implements Comparable<ChunkRenderDataTask> {
	private final ChunkRenderer chunkRenderer;
	private final ReentrantLock lock = new ReentrantLock();
	private final List<Runnable> runnables = Lists.<Runnable>newArrayList();
	private final ChunkRenderDataTask.Mode mode;
	private final double distanceToPlayerSquared;
	@Nullable
	private class_853 field_4414;
	private ChunkVertexBuffer vertexBuffer;
	private ChunkRenderData renderData;
	private ChunkRenderDataTask.Stage stage = ChunkRenderDataTask.Stage.INIT;
	private boolean field_4410;

	public ChunkRenderDataTask(ChunkRenderer chunkRenderer, ChunkRenderDataTask.Mode mode, double d, @Nullable class_853 arg) {
		this.chunkRenderer = chunkRenderer;
		this.mode = mode;
		this.distanceToPlayerSquared = d;
		this.field_4414 = arg;
	}

	public ChunkRenderDataTask.Stage getStage() {
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

	public ChunkVertexBuffer getVertexBuffer() {
		return this.vertexBuffer;
	}

	public void setVertexBuffer(ChunkVertexBuffer chunkVertexBuffer) {
		this.vertexBuffer = chunkVertexBuffer;
	}

	public void setStage(ChunkRenderDataTask.Stage stage) {
		this.lock.lock();

		try {
			this.stage = stage;
		} finally {
			this.lock.unlock();
		}
	}

	public void method_3596() {
		this.lock.lock();

		try {
			this.field_4414 = null;
			if (this.mode == ChunkRenderDataTask.Mode.field_4426 && this.stage != ChunkRenderDataTask.Stage.field_4423) {
				this.chunkRenderer.markRenderUpdate(false);
			}

			this.field_4410 = true;
			this.stage = ChunkRenderDataTask.Stage.field_4423;

			for (Runnable runnable : this.runnables) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void method_3597(Runnable runnable) {
		this.lock.lock();

		try {
			this.runnables.add(runnable);
			if (this.field_4410) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public ReentrantLock getLock() {
		return this.lock;
	}

	public ChunkRenderDataTask.Mode getMode() {
		return this.mode;
	}

	public boolean method_3595() {
		return this.field_4410;
	}

	public int compareTo(ChunkRenderDataTask chunkRenderDataTask) {
		return Doubles.compare(this.distanceToPlayerSquared, chunkRenderDataTask.distanceToPlayerSquared);
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
		INIT,
		field_4424,
		field_4421,
		field_4423;
	}
}
