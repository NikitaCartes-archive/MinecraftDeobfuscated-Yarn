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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlBufferRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkBuilder {
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
	private final BlockingQueue<BlockBufferBuilderStorage> availableBuffers;
	private final BufferRenderer displayListBufferRenderer = new BufferRenderer();
	private final GlBufferRenderer vboBufferRenderer = new GlBufferRenderer();
	private final Queue<ChunkBuilder.ChunkUploadTask> uploadQueue = Queues.<ChunkBuilder.ChunkUploadTask>newPriorityQueue();
	private final ChunkRenderWorker clientThreadWorker;
	private Vec3d cameraPosition = Vec3d.ZERO;

	public ChunkBuilder(boolean bl) {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 10485760 - 1);
		int j = Runtime.getRuntime().availableProcessors();
		int k = bl ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k * 2, i));
		this.clientThreadWorker = new ChunkRenderWorker(this, new BlockBufferBuilderStorage());
		List<BlockBufferBuilderStorage> list = Lists.<BlockBufferBuilderStorage>newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new BlockBufferBuilderStorage());
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
		this.availableBuffers = Queues.<BlockBufferBuilderStorage>newArrayBlockingQueue(this.bufferCount);
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
			: String.format("pC: %03d, pU: %02d, aB: %02d", this.pendingChunks.size(), this.uploadQueue.size(), this.availableBuffers.size());
	}

	public void setCameraPosition(Vec3d cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public Vec3d getCameraPosition() {
		return this.cameraPosition;
	}

	public boolean runTasksSync(long endTime) {
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
					} catch (InterruptedException var9) {
						LOGGER.warn("Skipped task due to interrupt");
					}
				}
			}

			int i = 0;
			synchronized (this.uploadQueue) {
				while (i < 10) {
					ChunkBuilder.ChunkUploadTask chunkUploadTask = (ChunkBuilder.ChunkUploadTask)this.uploadQueue.poll();
					if (chunkUploadTask == null) {
						break;
					}

					if (!chunkUploadTask.task.isDone()) {
						chunkUploadTask.task.run();
						bl2 = true;
						bl = true;
						i++;
					}
				}
			}
		} while (endTime != 0L && bl2 && endTime >= Util.getMeasuringTimeNano());

		return bl;
	}

	public boolean rebuild(ChunkRenderer renderer) {
		renderer.getLock().lock();

		boolean var4;
		try {
			ChunkRenderTask chunkRenderTask = renderer.startRebuild();
			chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
			boolean bl = this.pendingChunks.offer(chunkRenderTask);
			if (!bl) {
				chunkRenderTask.cancel();
			}

			var4 = bl;
		} finally {
			renderer.getLock().unlock();
		}

		return var4;
	}

	public boolean rebuildSync(ChunkRenderer renderer) {
		renderer.getLock().lock();

		boolean var3;
		try {
			ChunkRenderTask chunkRenderTask = renderer.startRebuild();

			try {
				this.clientThreadWorker.runTask(chunkRenderTask);
			} catch (InterruptedException var7) {
			}

			var3 = true;
		} finally {
			renderer.getLock().unlock();
		}

		return var3;
	}

	public void reset() {
		this.clear();
		List<BlockBufferBuilderStorage> list = Lists.<BlockBufferBuilderStorage>newArrayList();

		while (list.size() != this.bufferCount) {
			this.runTasksSync(Long.MAX_VALUE);

			try {
				list.add(this.getNextAvailableBuffer());
			} catch (InterruptedException var3) {
			}
		}

		this.availableBuffers.addAll(list);
	}

	public void addAvailableBuffer(BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.availableBuffers.add(blockBufferBuilderStorage);
	}

	public BlockBufferBuilderStorage getNextAvailableBuffer() throws InterruptedException {
		return (BlockBufferBuilderStorage)this.availableBuffers.take();
	}

	public ChunkRenderTask getNextChunkRenderDataTask() throws InterruptedException {
		return (ChunkRenderTask)this.pendingChunks.take();
	}

	public boolean resortTransparency(ChunkRenderer renderer) {
		renderer.getLock().lock();

		boolean var3;
		try {
			ChunkRenderTask chunkRenderTask = renderer.startResortTransparency();
			if (chunkRenderTask == null) {
				return true;
			}

			chunkRenderTask.addCompletionAction(() -> this.pendingChunks.remove(chunkRenderTask));
			var3 = this.pendingChunks.offer(chunkRenderTask);
		} finally {
			renderer.getLock().unlock();
		}

		return var3;
	}

	public ListenableFuture<Void> upload(RenderLayer layer, BufferBuilder bufferBuilder, ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, double d) {
		if (MinecraftClient.getInstance().isOnThread()) {
			if (GLX.useVbo()) {
				this.uploadVbo(bufferBuilder, chunkRenderer.getGlBuffer(layer.ordinal()));
			} else {
				this.uploadDisplayList(bufferBuilder, ((DisplayListChunkRenderer)chunkRenderer).method_3639(layer, chunkRenderData));
			}

			bufferBuilder.setOffset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Void> listenableFutureTask = ListenableFutureTask.create(
				() -> this.upload(layer, bufferBuilder, chunkRenderer, chunkRenderData, d), null
			);
			synchronized (this.uploadQueue) {
				this.uploadQueue.add(new ChunkBuilder.ChunkUploadTask(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void uploadDisplayList(BufferBuilder bufferBuilder, int index) {
		GlStateManager.newList(index, 4864);
		this.displayListBufferRenderer.draw(bufferBuilder);
		GlStateManager.endList();
	}

	private void uploadVbo(BufferBuilder bufferBuilder, VertexBuffer glBuffer) {
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
		return this.pendingChunks.isEmpty() && this.uploadQueue.isEmpty();
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
	class ChunkUploadTask implements Comparable<ChunkBuilder.ChunkUploadTask> {
		private final ListenableFutureTask<Void> task;
		private final double priority;

		public ChunkUploadTask(ListenableFutureTask<Void> priority, double d) {
			this.task = priority;
			this.priority = d;
		}

		public int compareTo(ChunkBuilder.ChunkUploadTask chunkUploadTask) {
			return Doubles.compare(this.priority, chunkUploadTask.priority);
		}
	}
}
