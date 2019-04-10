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
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.gl.GlBufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
	private final PriorityBlockingQueue<ChunkRenderTask> pendingChunks = Queues.newPriorityBlockingQueue();
	private final BlockingQueue<BlockLayeredBufferBuilder> availableBuffers;
	private final BufferRenderer bufferRenderer = new BufferRenderer();
	private final GlBufferRenderer field_4441 = new GlBufferRenderer();
	private final Queue<ChunkBatcher.ChunkUploadTask> pendingUploads = Queues.<ChunkBatcher.ChunkUploadTask>newPriorityQueue();
	private final ChunkRenderWorker activeWorker;
	private Vec3d field_18766 = Vec3d.ZERO;

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

		this.availableBuffers = Queues.<BlockLayeredBufferBuilder>newArrayBlockingQueue(this.field_4442);

		for (int k = 0; k < this.field_4442; k++) {
			this.availableBuffers.add(new BlockLayeredBufferBuilder());
		}

		this.activeWorker = new ChunkRenderWorker(this, new BlockLayeredBufferBuilder());
	}

	public String getDebugString() {
		return this.workerThreads.isEmpty()
			? String.format("pC: %03d, single-threaded", this.pendingChunks.size())
			: String.format("pC: %03d, pU: %1d, aB: %1d", this.pendingChunks.size(), this.pendingUploads.size(), this.availableBuffers.size());
	}

	public void method_19419(Vec3d vec3d) {
		this.field_18766 = vec3d;
	}

	public Vec3d method_19420() {
		return this.field_18766;
	}

	public boolean method_3631(long l) {
		boolean bl = false;

		boolean bl2;
		do {
			bl2 = false;
			if (this.workerThreads.isEmpty()) {
				ChunkRenderTask chunkRenderTask = (ChunkRenderTask)this.pendingChunks.poll();
				if (chunkRenderTask != null) {
					try {
						this.activeWorker.runTask(chunkRenderTask);
						bl2 = true;
					} catch (InterruptedException var8) {
						LOGGER.warn("Skipped task due to interrupt");
					}
				}
			}

			synchronized (this.pendingUploads) {
				if (!this.pendingUploads.isEmpty()) {
					((ChunkBatcher.ChunkUploadTask)this.pendingUploads.poll()).task.run();
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
			ChunkRenderTask chunkRenderTask = chunkRenderer.method_3674();
			chunkRenderTask.add(() -> this.pendingChunks.remove(chunkRenderTask));
			boolean bl = this.pendingChunks.offer(chunkRenderTask);
			if (!bl) {
				chunkRenderTask.cancel();
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
			ChunkRenderTask chunkRenderTask = chunkRenderer.method_3674();

			try {
				this.activeWorker.runTask(chunkRenderTask);
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
		List<BlockLayeredBufferBuilder> list = Lists.<BlockLayeredBufferBuilder>newArrayList();

		while (list.size() != this.field_4442) {
			this.method_3631(Long.MAX_VALUE);

			try {
				list.add(this.getNextAvailableBuffer());
			} catch (InterruptedException var3) {
			}
		}

		this.availableBuffers.addAll(list);
	}

	public void addAvailableBuffer(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		this.availableBuffers.add(blockLayeredBufferBuilder);
	}

	public BlockLayeredBufferBuilder getNextAvailableBuffer() throws InterruptedException {
		return (BlockLayeredBufferBuilder)this.availableBuffers.take();
	}

	public ChunkRenderTask getNextChunkRenderDataTask() throws InterruptedException {
		return (ChunkRenderTask)this.pendingChunks.take();
	}

	public boolean method_3620(ChunkRenderer chunkRenderer) {
		chunkRenderer.getChunkRenderLock().lock();

		boolean var3;
		try {
			ChunkRenderTask chunkRenderTask = chunkRenderer.getResortTransparencyTask();
			if (chunkRenderTask == null) {
				return true;
			}

			chunkRenderTask.add(() -> this.pendingChunks.remove(chunkRenderTask));
			var3 = this.pendingChunks.offer(chunkRenderTask);
		} finally {
			chunkRenderer.getChunkRenderLock().unlock();
		}

		return var3;
	}

	public ListenableFuture<Object> method_3635(
		BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d
	) {
		if (MinecraftClient.getInstance().isOnThread()) {
			if (GLX.useVbo()) {
				this.method_3621(bufferBuilder, chunkRenderer.getGlBuffer(blockRenderLayer.ordinal()));
			} else {
				this.method_3623(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).method_3639(blockRenderLayer, chunkRenderData));
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(
				() -> this.method_3635(blockRenderLayer, bufferBuilder, chunkRenderer, chunkRenderData, d), null
			);
			synchronized (this.pendingUploads) {
				this.pendingUploads.add(new ChunkBatcher.ChunkUploadTask(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void method_3623(BufferBuilder bufferBuilder, int i) {
		GlStateManager.newList(i, 4864);
		this.bufferRenderer.draw(bufferBuilder);
		GlStateManager.endList();
	}

	private void method_3621(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
		this.field_4441.setGlBuffer(glBuffer);
		this.field_4441.draw(bufferBuilder);
	}

	public void method_3633() {
		while (!this.pendingChunks.isEmpty()) {
			ChunkRenderTask chunkRenderTask = (ChunkRenderTask)this.pendingChunks.poll();
			if (chunkRenderTask != null) {
				chunkRenderTask.cancel();
			}
		}
	}

	public boolean isEmpty() {
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
	class ChunkUploadTask implements Comparable<ChunkBatcher.ChunkUploadTask> {
		private final ListenableFutureTask<Object> task;
		private final double priority;

		public ChunkUploadTask(ListenableFutureTask<Object> listenableFutureTask, double d) {
			this.task = listenableFutureTask;
			this.priority = d;
		}

		public int method_3638(ChunkBatcher.ChunkUploadTask chunkUploadTask) {
			return Doubles.compare(this.priority, chunkUploadTask.priority);
		}
	}
}
