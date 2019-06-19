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
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderWorker implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkBatcher batcher;
	private final BlockLayeredBufferBuilder bufferBuilders;
	private boolean running = true;

	public ChunkRenderWorker(ChunkBatcher chunkBatcher) {
		this(chunkBatcher, null);
	}

	public ChunkRenderWorker(ChunkBatcher chunkBatcher, @Nullable BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		this.batcher = chunkBatcher;
		this.bufferBuilders = blockLayeredBufferBuilder;
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
				MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().populateCrashReport(crashReport));
				return;
			}
		}
	}

	void runTask(ChunkRenderTask chunkRenderTask) throws InterruptedException {
		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.field_4422)) {
				return;
			}

			if (!chunkRenderTask.getChunkRenderer().shouldBuild()) {
				chunkRenderTask.cancel();
				return;
			}

			chunkRenderTask.setStage(ChunkRenderTask.Stage.field_4424);
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		final BlockLayeredBufferBuilder blockLayeredBufferBuilder = this.getBufferBuilders();
		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.field_4424)) {
				this.freeRenderTask(blockLayeredBufferBuilder);
				return;
			}
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		chunkRenderTask.setBufferBuilders(blockLayeredBufferBuilder);
		Vec3d vec3d = this.batcher.getCameraPosition();
		float f = (float)vec3d.x;
		float g = (float)vec3d.y;
		float h = (float)vec3d.z;
		ChunkRenderTask.Mode mode = chunkRenderTask.getMode();
		if (mode == ChunkRenderTask.Mode.field_4426) {
			chunkRenderTask.getChunkRenderer().rebuildChunk(f, g, h, chunkRenderTask);
		} else if (mode == ChunkRenderTask.Mode.field_4427) {
			chunkRenderTask.getChunkRenderer().resortTransparency(f, g, h, chunkRenderTask);
		}

		chunkRenderTask.getLock().lock();

		try {
			if (!method_20719(chunkRenderTask, ChunkRenderTask.Stage.field_4424)) {
				this.freeRenderTask(blockLayeredBufferBuilder);
				return;
			}

			chunkRenderTask.setStage(ChunkRenderTask.Stage.field_4421);
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		final ChunkRenderData chunkRenderData = chunkRenderTask.getRenderData();
		ArrayList list = Lists.newArrayList();
		if (mode == ChunkRenderTask.Mode.field_4426) {
			for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
				if (chunkRenderData.isBufferInitialized(blockRenderLayer)) {
					list.add(
						this.batcher
							.upload(
								blockRenderLayer,
								chunkRenderTask.getBufferBuilders().get(blockRenderLayer),
								chunkRenderTask.getChunkRenderer(),
								chunkRenderData,
								chunkRenderTask.getSquaredCameraDistance()
							)
					);
				}
			}
		} else if (mode == ChunkRenderTask.Mode.field_4427) {
			list.add(
				this.batcher
					.upload(
						BlockRenderLayer.field_9179,
						chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.field_9179),
						chunkRenderTask.getChunkRenderer(),
						chunkRenderData,
						chunkRenderTask.getSquaredCameraDistance()
					)
			);
		}

		ListenableFuture<List<Void>> listenableFuture = Futures.allAsList(list);
		chunkRenderTask.addCompletionAction(() -> listenableFuture.cancel(false));
		Futures.addCallback(listenableFuture, new FutureCallback<List<Void>>() {
			public void method_3617(@Nullable List<Void> list) {
				ChunkRenderWorker.this.freeRenderTask(blockLayeredBufferBuilder);
				chunkRenderTask.getLock().lock();

				label32: {
					try {
						if (ChunkRenderWorker.method_20719(chunkRenderTask, ChunkRenderTask.Stage.field_4421)) {
							chunkRenderTask.setStage(ChunkRenderTask.Stage.field_4423);
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
				ChunkRenderWorker.this.freeRenderTask(blockLayeredBufferBuilder);
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

	private BlockLayeredBufferBuilder getBufferBuilders() throws InterruptedException {
		return this.bufferBuilders != null ? this.bufferBuilders : this.batcher.getNextAvailableBuffer();
	}

	private void freeRenderTask(BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		if (blockLayeredBufferBuilder != this.bufferBuilders) {
			this.batcher.addAvailableBuffer(blockLayeredBufferBuilder);
		}
	}

	public void stop() {
		this.running = false;
	}
}
