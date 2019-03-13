package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.SafeWorldView;

@Environment(EnvType.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
	private final ChunkRenderer field_4418;
	private final ReentrantLock lock = new ReentrantLock();
	private final List<Runnable> runnables = Lists.<Runnable>newArrayList();
	private final ChunkRenderTask.Mode mode;
	private final double distanceToPlayerSquared;
	@Nullable
	private SafeWorldView field_4414;
	private BlockLayeredBufferBuilder bufferBuilder;
	private ChunkRenderData field_4417;
	private ChunkRenderTask.Stage stage = ChunkRenderTask.Stage.field_4422;
	private boolean cancelled;

	public ChunkRenderTask(ChunkRenderer chunkRenderer, ChunkRenderTask.Mode mode, double d, @Nullable SafeWorldView safeWorldView) {
		this.field_4418 = chunkRenderer;
		this.mode = mode;
		this.distanceToPlayerSquared = d;
		this.field_4414 = safeWorldView;
	}

	public ChunkRenderTask.Stage getStage() {
		return this.stage;
	}

	public ChunkRenderer method_3608() {
		return this.field_4418;
	}

	@Nullable
	public SafeWorldView method_3606() {
		SafeWorldView safeWorldView = this.field_4414;
		this.field_4414 = null;
		return safeWorldView;
	}

	public ChunkRenderData method_3609() {
		return this.field_4417;
	}

	public void method_3598(ChunkRenderData chunkRenderData) {
		this.field_4417 = chunkRenderData;
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
				this.field_4418.scheduleRender(false);
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

	public int method_3601(ChunkRenderTask chunkRenderTask) {
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
