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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_294;
import net.minecraft.class_848;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexBufferRenderer;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkBatcher {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
		.setNameFormat("Chunk Batcher %d")
		.setDaemon(true)
		.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER))
		.build();
	private final int field_4442;
	private final List<Thread> workerThreads = Lists.<Thread>newArrayList();
	private final List<ChunkRenderWorker> workers = Lists.<ChunkRenderWorker>newArrayList();
	private final PriorityBlockingQueue<ChunkRenderDataTask> pendingChunks = Queues.newPriorityBlockingQueue();
	private final BlockingQueue<ChunkVertexBuffer> availableBuffers;
	private final VertexBufferRenderer field_4437 = new VertexBufferRenderer();
	private final class_294 field_4441 = new class_294();
	private final Queue<ChunkBatcher.class_847> pendingUploads = Queues.<ChunkBatcher.class_847>newPriorityQueue();
	private final ChunkRenderWorker activeWorker;

	public ChunkBatcher() {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
		int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
		this.field_4442 = MathHelper.clamp(j * 10, 1, i);
		if (j > 1) {
			for (int k = 0; k < j; k++) {
				ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
				Thread thread = THREAD_FACTORY.newThread(chunkRenderWorker);
				thread.start();
				this.workers.add(chunkRenderWorker);
				this.workerThreads.add(thread);
			}
		}

		this.availableBuffers = Queues.<ChunkVertexBuffer>newArrayBlockingQueue(this.field_4442);

		for (int k = 0; k < this.field_4442; k++) {
			this.availableBuffers.add(new ChunkVertexBuffer());
		}

		this.activeWorker = new ChunkRenderWorker(this, new ChunkVertexBuffer());
	}

	public String getDebugString() {
		return this.workerThreads.isEmpty()
			? String.format("pC: %03d, single-threaded", this.pendingChunks.size())
			: String.format("pC: %03d, pU: %1d, aB: %1d", this.pendingChunks.size(), this.pendingUploads.size(), this.availableBuffers.size());
	}

	public boolean method_3631(long l) {
		boolean bl = false;

		boolean bl2;
		do {
			bl2 = false;
			if (this.workerThreads.isEmpty()) {
				ChunkRenderDataTask chunkRenderDataTask = (ChunkRenderDataTask)this.pendingChunks.poll();
				if (chunkRenderDataTask != null) {
					try {
						this.activeWorker.runTask(chunkRenderDataTask);
						bl2 = true;
					} catch (InterruptedException var8) {
						LOGGER.warn("Skipped task due to interrupt");
					}
				}
			}

			synchronized (this.pendingUploads) {
				if (!this.pendingUploads.isEmpty()) {
					((ChunkBatcher.class_847)this.pendingUploads.poll()).field_4446.run();
					bl2 = true;
					bl = true;
				}
			}
		} while (l != 0L && bl2 && l >= SystemUtil.getMeasuringTimeNano());

		return bl;
	}

	public boolean method_3624(ChunkRenderer chunkRenderer) {
		chunkRenderer.getChunkRenderLock().lock();

		boolean var4;
		try {
			ChunkRenderDataTask chunkRenderDataTask = chunkRenderer.method_3674();
			chunkRenderDataTask.method_3597(() -> this.pendingChunks.remove(chunkRenderDataTask));
			boolean bl = this.pendingChunks.offer(chunkRenderDataTask);
			if (!bl) {
				chunkRenderDataTask.method_3596();
			}

			var4 = bl;
		} finally {
			chunkRenderer.getChunkRenderLock().unlock();
		}

		return var4;
	}

	public boolean method_3627(ChunkRenderer chunkRenderer) {
		chunkRenderer.getChunkRenderLock().lock();

		boolean var3;
		try {
			ChunkRenderDataTask chunkRenderDataTask = chunkRenderer.method_3674();

			try {
				this.activeWorker.runTask(chunkRenderDataTask);
			} catch (InterruptedException var7) {
			}

			var3 = true;
		} finally {
			chunkRenderer.getChunkRenderLock().unlock();
		}

		return var3;
	}

	public void method_3632() {
		this.method_3633();
		List<ChunkVertexBuffer> list = Lists.<ChunkVertexBuffer>newArrayList();

		while (list.size() != this.field_4442) {
			this.method_3631(Long.MAX_VALUE);

			try {
				list.add(this.getNextAvailableBuffer());
			} catch (InterruptedException var3) {
			}
		}

		this.availableBuffers.addAll(list);
	}

	public void addAvailableBuffer(ChunkVertexBuffer chunkVertexBuffer) {
		this.availableBuffers.add(chunkVertexBuffer);
	}

	public ChunkVertexBuffer getNextAvailableBuffer() throws InterruptedException {
		return (ChunkVertexBuffer)this.availableBuffers.take();
	}

	public ChunkRenderDataTask getNextChunkRenderDataTask() throws InterruptedException {
		return (ChunkRenderDataTask)this.pendingChunks.take();
	}

	public boolean method_3620(ChunkRenderer chunkRenderer) {
		chunkRenderer.getChunkRenderLock().lock();

		boolean var3;
		try {
			ChunkRenderDataTask chunkRenderDataTask = chunkRenderer.method_3669();
			if (chunkRenderDataTask == null) {
				return true;
			}

			chunkRenderDataTask.method_3597(() -> this.pendingChunks.remove(chunkRenderDataTask));
			var3 = this.pendingChunks.offer(chunkRenderDataTask);
		} finally {
			chunkRenderer.getChunkRenderLock().unlock();
		}

		return var3;
	}

	public ListenableFuture<Object> method_3635(
		BlockRenderLayer blockRenderLayer, VertexBuffer vertexBuffer, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d
	) {
		if (MinecraftClient.getInstance().isMainThread()) {
			if (GLX.useVbo()) {
				this.method_3621(vertexBuffer, chunkRenderer.method_3656(blockRenderLayer.ordinal()));
			} else {
				this.method_3623(vertexBuffer, ((class_848)chunkRenderer).method_3639(blockRenderLayer, chunkRenderData), chunkRenderer);
			}

			vertexBuffer.setOffset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(
				() -> this.method_3635(blockRenderLayer, vertexBuffer, chunkRenderer, chunkRenderData, d), null
			);
			synchronized (this.pendingUploads) {
				this.pendingUploads.add(new ChunkBatcher.class_847(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void method_3623(VertexBuffer vertexBuffer, int i, ChunkRenderer chunkRenderer) {
		GlStateManager.newList(i, 4864);
		GlStateManager.pushMatrix();
		chunkRenderer.method_3664();
		this.field_4437.draw(vertexBuffer);
		GlStateManager.popMatrix();
		GlStateManager.endList();
	}

	private void method_3621(VertexBuffer vertexBuffer, GlBuffer glBuffer) {
		this.field_4441.method_1372(glBuffer);
		this.field_4441.draw(vertexBuffer);
	}

	public void method_3633() {
		while (!this.pendingChunks.isEmpty()) {
			ChunkRenderDataTask chunkRenderDataTask = (ChunkRenderDataTask)this.pendingChunks.poll();
			if (chunkRenderDataTask != null) {
				chunkRenderDataTask.method_3596();
			}
		}
	}

	public boolean method_3630() {
		return this.pendingChunks.isEmpty() && this.pendingUploads.isEmpty();
	}

	public void method_3619() {
		this.method_3633();

		for (ChunkRenderWorker chunkRenderWorker : this.workers) {
			chunkRenderWorker.stop();
		}

		for (Thread thread : this.workerThreads) {
			try {
				thread.interrupt();
				thread.join();
			} catch (InterruptedException var4) {
				LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)var4);
			}
		}

		this.availableBuffers.clear();
	}

	@Environment(EnvType.CLIENT)
	class class_847 implements Comparable<ChunkBatcher.class_847> {
		private final ListenableFutureTask<Object> field_4446;
		private final double field_4447;

		public class_847(ListenableFutureTask<Object> listenableFutureTask, double d) {
			this.field_4446 = listenableFutureTask;
			this.field_4447 = d;
		}

		public int method_3638(ChunkBatcher.class_847 arg) {
			return Doubles.compare(this.field_4447, arg.field_4447);
		}
	}
}
