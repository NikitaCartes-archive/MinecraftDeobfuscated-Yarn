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
	private final int bufferCount;
	private final List<Thread> workerThreads = Lists.<Thread>newArrayList();
	private final List<ChunkRenderWorker> workers = Lists.<ChunkRenderWorker>newArrayList();
	private final PriorityBlockingQueue<ChunkRenderTask> pendingChunks = Queues.newPriorityBlockingQueue();
	private final BlockingQueue<BlockLayeredBufferBuilder> availableBuffers;
	private final BufferRenderer displayListBufferRenderer = new BufferRenderer();
	private final GlBufferRenderer vboBufferRenderer = new GlBufferRenderer();
	private final Queue<ChunkBatcher.ChunkUploadTask> pendingUploads = Queues.<ChunkBatcher.ChunkUploadTask>newPriorityQueue();
	private final ChunkRenderWorker clientThreadWorker;
	private Vec3d cameraPosition = Vec3d.ZERO;

	public ChunkBatcher(boolean bl) {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 10485760 - 1);
		int j = Runtime.getRuntime().availableProcessors();
		int k = bl ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k * 3, i));
		this.clientThreadWorker = new ChunkRenderWorker(this, new BlockLayeredBufferBuilder());
		List<BlockLayeredBufferBuilder> list = Lists.<BlockLayeredBufferBuilder>newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new BlockLayeredBufferBuilder());
			}
		} catch (OutOfMemoryError var11) {
			LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
			int n = list.size() * 2 / 3;

			for (int o = 0; o < n; o++) {
				list.remove(list.size() - 1);
			}

			System.gc();
		}

		this.bufferCount = list.size();
		this.availableBuffers = Queues.<BlockLayeredBufferBuilder>newArrayBlockingQueue(this.bufferCount);
		this.availableBuffers.addAll(list);
		int m = Math.min(k, this.bufferCount);
		if (m > 1) {
			for (int n = 0; n < m; n++) {
				ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
				Thread thread = THREAD_FACTORY.newThread(chunkRenderWorker);
				thread.start();
				this.workers.add(chunkRenderWorker);
				this.workerThreads.add(thread);
			}
		}
	}

	public String getDebugString() {
		return this.workerThreads.isEmpty()
			? String.format("pC: %03d, single-threaded", this.pendingChunks.size())
			: String.format("pC: %03d, pU: %1d, aB: %1d", this.pendingChunks.size(), this.pendingUploads.size(), this.availableBuffers.size());
	}

	public void setCameraPosition(Vec3d vec3d) {
		this.cameraPosition = vec3d;
	}

	public Vec3d getCameraPosition() {
		return this.cameraPosition;
	}

	public boolean runTasksSync(long l) {
		boolean bl = false;

		boolean bl2;
		do {
			bl2 = false;
			if (this.workerThreads.isEmpty()) {
				ChunkRenderTask chunkRenderTask = (ChunkRenderTask)this.pendingChunks.poll();
				if (chunkRenderTask != null) {
					try {
						this.clientThreadWorker.runTask(chunkRenderTask);
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

	public boolean rebuild(ChunkRenderer chunkRenderer) {
		chunkRenderer.getLock().lock();

		boolean var4;
		try {
			ChunkRenderTask chunkRenderTask = chunkRenderer.startRebuild();
			chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
			boolean bl = this.pendingChunks.offer(chunkRenderTask);
			if (!bl) {
				chunkRenderTask.cancel();
			}

			var4 = bl;
		} finally {
			chunkRenderer.getLock().unlock();
		}

		return var4;
	}

	public boolean rebuildSync(ChunkRenderer chunkRenderer) {
		chunkRenderer.getLock().lock();

		boolean var3;
		try {
			ChunkRenderTask chunkRenderTask = chunkRenderer.startRebuild();

			try {
				this.clientThreadWorker.runTask(chunkRenderTask);
			} catch (InterruptedException var7) {
			}

			var3 = true;
		} finally {
			chunkRenderer.getLock().unlock();
		}

		return var3;
	}

	public void reset() {
		this.clear();
		List<BlockLayeredBufferBuilder> list = Lists.<BlockLayeredBufferBuilder>newArrayList();

		while (list.size() != this.bufferCount) {
			this.runTasksSync(Long.MAX_VALUE);

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

	public boolean resortTransparency(ChunkRenderer chunkRenderer) {
		chunkRenderer.getLock().lock();

		boolean var3;
		try {
			ChunkRenderTask chunkRenderTask = chunkRenderer.startResortTransparency();
			if (chunkRenderTask == null) {
				return true;
			}

			chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
			var3 = this.pendingChunks.offer(chunkRenderTask);
		} finally {
			chunkRenderer.getLock().unlock();
		}

		return var3;
	}

	public ListenableFuture<Object> upload(
		BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d
	) {
		if (MinecraftClient.getInstance().isOnThread()) {
			if (GLX.useVbo()) {
				this.uploadVbo(bufferBuilder, chunkRenderer.getGlBuffer(blockRenderLayer.ordinal()));
			} else {
				this.uploadDisplayList(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).method_3639(blockRenderLayer, chunkRenderData));
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(
				() -> this.upload(blockRenderLayer, bufferBuilder, chunkRenderer, chunkRenderData, d), null
			);
			synchronized (this.pendingUploads) {
				this.pendingUploads.add(new ChunkBatcher.ChunkUploadTask(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void uploadDisplayList(BufferBuilder bufferBuilder, int i) {
		GlStateManager.newList(i, 4864);
		this.displayListBufferRenderer.draw(bufferBuilder);
		GlStateManager.endList();
	}

	private void uploadVbo(BufferBuilder bufferBuilder, GlBuffer glBuffer) {
		this.vboBufferRenderer.setGlBuffer(glBuffer);
		this.vboBufferRenderer.draw(bufferBuilder);
	}

	public void clear() {
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

	public void stop() {
		this.clear();

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
