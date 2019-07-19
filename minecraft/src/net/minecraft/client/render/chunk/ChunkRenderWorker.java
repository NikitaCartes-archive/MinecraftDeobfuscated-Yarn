package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderWorker implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkBuilder batcher;
	private final BlockBufferBuilderStorage bufferBuilders;
	private boolean running = true;

	public ChunkRenderWorker(ChunkBuilder chunkBuilder) {
		this(chunkBuilder, null);
	}

	public ChunkRenderWorker(ChunkBuilder batcher, @Nullable BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.batcher = batcher;
		this.bufferBuilders = blockBufferBuilderStorage;
	}

	public void run() {
		while (this.running) {
			try {
				this.runTask(this.batcher.getNextChunkRenderDataTask());
			} catch (InterruptedException var3) {
				LOGGER.debug("Stopping chunk worker due to interrupt");
				return;
			} catch (Throwable var4) {
				CrashReport crashReport = CrashReport.create(var4, "Batching chunks");
				MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().addDetailsToCrashReport(crashReport));
				return;
			}
		}
	}

	void runTask(ChunkRenderTask chunkRenderTask) throws InterruptedException {
		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.PENDING)) {
				return;
			}

			if (!chunkRenderTask.getChunkRenderer().shouldBuild()) {
				chunkRenderTask.cancel();
				return;
			}

			chunkRenderTask.setStage(ChunkRenderTask.Stage.COMPILING);
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		final BlockBufferBuilderStorage blockBufferBuilderStorage = this.getBufferBuilders();
		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.COMPILING)) {
				this.freeRenderTask(blockBufferBuilderStorage);
				return;
			}
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		chunkRenderTask.setBufferBuilders(blockBufferBuilderStorage);
		Vec3d vec3d = this.batcher.getCameraPosition();
		float f = (float)vec3d.x;
		float g = (float)vec3d.y;
		float h = (float)vec3d.z;
		ChunkRenderTask.Mode mode = chunkRenderTask.getMode();
		if (mode == ChunkRenderTask.Mode.REBUILD_CHUNK) {
			chunkRenderTask.getChunkRenderer().rebuildChunk(f, g, h, chunkRenderTask);
		} else if (mode == ChunkRenderTask.Mode.RESORT_TRANSPARENCY) {
			chunkRenderTask.getChunkRenderer().resortTransparency(f, g, h, chunkRenderTask);
		}

		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.COMPILING)) {
				this.freeRenderTask(blockBufferBuilderStorage);
				return;
			}

			chunkRenderTask.setStage(ChunkRenderTask.Stage.UPLOADING);
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		final ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
		ArrayList list = Lists.newArrayList();
		if (mode == ChunkRenderTask.Mode.REBUILD_CHUNK) {
			for (RenderLayer renderLayer : RenderLayer.values()) {
				if (chunkRenderData.isBufferInitialized(renderLayer)) {
					list.add(
						this.batcher
							.upload(
								renderLayer,
								chunkRenderTask.getBufferBuilders().get(renderLayer),
								chunkRenderTask.getChunkRenderer(),
								chunkRenderData,
								chunkRenderTask.getSquaredCameraDistance()
							)
					);
				}
			}
		} else if (mode == ChunkRenderTask.Mode.RESORT_TRANSPARENCY) {
			list.add(
				this.batcher
					.upload(
						RenderLayer.TRANSLUCENT,
						chunkRenderTask.getBufferBuilders().get(RenderLayer.TRANSLUCENT),
						chunkRenderTask.getChunkRenderer(),
						chunkRenderData,
						chunkRenderTask.getSquaredCameraDistance()
					)
			);
		}

		ListenableFuture<List<Void>> listenableFuture = Futures.allAsList(list);
		chunkRenderTask.addCompletionAction(() -> listenableFuture.cancel(false));
		Futures.addCallback(listenableFuture, new FutureCallback<List<Void>>() {
			public void onSuccess(@Nullable List<Void> list) {
				ChunkRenderWorker.this.freeRenderTask(blockBufferBuilderStorage);
				chunkRenderTask.getLock().lock();

				label32: {
					try {
						if (ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.UPLOADING)) {
							chunkRenderTask.setStage(ChunkRenderTask.Stage.DONE);
							break label32;
						}
					} finally {
						chunkRenderTask.getLock().unlock();
					}

					return;
				}

				chunkRenderTask.getChunkRenderer().setData(chunkRenderData);
			}

			@Override
			public void onFailure(Throwable throwable) {
				ChunkRenderWorker.this.freeRenderTask(blockBufferBuilderStorage);
				if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
					MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
				}
			}
		});
	}

	private static boolean method_20719(ChunkRenderTask chunkRenderTask, ChunkRenderTask.Stage stage) {
		if (chunkRenderTask.getStage() != stage) {
			if (!chunkRenderTask.isCancelled()) {
				LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", chunkRenderTask.getStage(), stage);
			}

			return false;
		} else {
			return true;
		}
	}

	private BlockBufferBuilderStorage getBufferBuilders() throws InterruptedException {
		return this.bufferBuilders != null ? this.bufferBuilders : this.batcher.getNextAvailableBuffer();
	}

	private void freeRenderTask(BlockBufferBuilderStorage blockBufferBuilderStorage) {
		if (blockBufferBuilderStorage != this.bufferBuilders) {
			this.batcher.addAvailableBuffer(blockBufferBuilderStorage);
		}
	}

	public void stop() {
		this.running = false;
	}
}
