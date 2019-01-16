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
import net.minecraft.class_295;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderWorker implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkBatcher batcher;
	private final BlockLayeredBufferBuilder field_4428;
	private boolean running = true;

	public ChunkRenderWorker(ChunkBatcher chunkBatcher) {
		this(chunkBatcher, null);
	}

	public ChunkRenderWorker(ChunkBatcher chunkBatcher, @Nullable BlockLayeredBufferBuilder blockLayeredBufferBuilder) {
		this.batcher = chunkBatcher;
		this.field_4428 = blockLayeredBufferBuilder;
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

	protected void runTask(ChunkRenderTask chunkRenderTask) throws InterruptedException {
		chunkRenderTask.getLock().lock();

		try {
			if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.field_4422) {
				if (!chunkRenderTask.isCancelled()) {
					LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", chunkRenderTask.getStage());
				}

				return;
			}

			if (!chunkRenderTask.getChunkRenderer().method_3673()) {
				chunkRenderTask.cancel();
				return;
			}

			chunkRenderTask.setStage(ChunkRenderTask.Stage.field_4424);
		} finally {
			chunkRenderTask.getLock().unlock();
		}

		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		if (entity == null) {
			chunkRenderTask.cancel();
		} else {
			chunkRenderTask.setBufferBuilders(this.method_3613());
			Vec3d vec3d = class_295.method_1379(entity, 1.0);
			float f = (float)vec3d.x;
			float g = (float)vec3d.y;
			float h = (float)vec3d.z;
			ChunkRenderTask.Mode mode = chunkRenderTask.getMode();
			if (mode == ChunkRenderTask.Mode.field_4426) {
				chunkRenderTask.getChunkRenderer().method_3652(f, g, h, chunkRenderTask);
			} else if (mode == ChunkRenderTask.Mode.field_4427) {
				chunkRenderTask.getChunkRenderer().method_3657(f, g, h, chunkRenderTask);
			}

			chunkRenderTask.getLock().lock();

			try {
				if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.field_4424) {
					if (!chunkRenderTask.isCancelled()) {
						LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", chunkRenderTask.getStage());
					}

					this.method_3610(chunkRenderTask);
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
								.method_3635(
									blockRenderLayer,
									chunkRenderTask.getBufferBuilders().get(blockRenderLayer),
									chunkRenderTask.getChunkRenderer(),
									chunkRenderData,
									chunkRenderTask.getDistanceToPlayerSquared()
								)
						);
					}
				}
			} else if (mode == ChunkRenderTask.Mode.field_4427) {
				list.add(
					this.batcher
						.method_3635(
							BlockRenderLayer.TRANSLUCENT,
							chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT),
							chunkRenderTask.getChunkRenderer(),
							chunkRenderData,
							chunkRenderTask.getDistanceToPlayerSquared()
						)
				);
			}

			ListenableFuture<List<Object>> listenableFuture = Futures.allAsList(list);
			chunkRenderTask.add(() -> listenableFuture.cancel(false));
			Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
				public void method_3617(@Nullable List<Object> list) {
					ChunkRenderWorker.this.method_3610(chunkRenderTask);
					chunkRenderTask.getLock().lock();

					label43: {
						try {
							if (chunkRenderTask.getStage() == ChunkRenderTask.Stage.field_4421) {
								chunkRenderTask.setStage(ChunkRenderTask.Stage.field_4423);
								break label43;
							}

							if (!chunkRenderTask.isCancelled()) {
								ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", chunkRenderTask.getStage());
							}
						} finally {
							chunkRenderTask.getLock().unlock();
						}

						return;
					}

					chunkRenderTask.getChunkRenderer().method_3665(chunkRenderData);
				}

				@Override
				public void onFailure(Throwable throwable) {
					ChunkRenderWorker.this.method_3610(chunkRenderTask);
					if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
						MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
					}
				}
			});
		}
	}

	private BlockLayeredBufferBuilder method_3613() throws InterruptedException {
		return this.field_4428 != null ? this.field_4428 : this.batcher.getNextAvailableBuffer();
	}

	private void method_3610(ChunkRenderTask chunkRenderTask) {
		if (this.field_4428 == null) {
			this.batcher.addAvailableBuffer(chunkRenderTask.getBufferBuilders());
		}
	}

	public void stop() {
		this.running = false;
	}
}
