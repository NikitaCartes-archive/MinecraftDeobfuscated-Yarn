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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderWorker implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkBatcher batcher;
	private final ChunkVertexBuffer field_4428;
	private boolean running = true;

	public ChunkRenderWorker(ChunkBatcher chunkBatcher) {
		this(chunkBatcher, null);
	}

	public ChunkRenderWorker(ChunkBatcher chunkBatcher, @Nullable ChunkVertexBuffer chunkVertexBuffer) {
		this.batcher = chunkBatcher;
		this.field_4428 = chunkVertexBuffer;
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

	protected void runTask(ChunkRenderDataTask chunkRenderDataTask) throws InterruptedException {
		chunkRenderDataTask.getLock().lock();

		try {
			if (chunkRenderDataTask.getStage() != ChunkRenderDataTask.Stage.INIT) {
				if (!chunkRenderDataTask.method_3595()) {
					LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", chunkRenderDataTask.getStage());
				}

				return;
			}

			if (!chunkRenderDataTask.getChunkRenderer().method_3673()) {
				chunkRenderDataTask.method_3596();
				return;
			}

			chunkRenderDataTask.setStage(ChunkRenderDataTask.Stage.field_4424);
		} finally {
			chunkRenderDataTask.getLock().unlock();
		}

		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		if (entity == null) {
			chunkRenderDataTask.method_3596();
		} else {
			chunkRenderDataTask.setVertexBuffer(this.method_3613());
			Vec3d vec3d = class_295.method_1379(entity, 1.0);
			float f = (float)vec3d.x;
			float g = (float)vec3d.y;
			float h = (float)vec3d.z;
			ChunkRenderDataTask.Mode mode = chunkRenderDataTask.getMode();
			if (mode == ChunkRenderDataTask.Mode.field_4426) {
				chunkRenderDataTask.getChunkRenderer().method_3652(f, g, h, chunkRenderDataTask);
			} else if (mode == ChunkRenderDataTask.Mode.field_4427) {
				chunkRenderDataTask.getChunkRenderer().method_3657(f, g, h, chunkRenderDataTask);
			}

			chunkRenderDataTask.getLock().lock();

			try {
				if (chunkRenderDataTask.getStage() != ChunkRenderDataTask.Stage.field_4424) {
					if (!chunkRenderDataTask.method_3595()) {
						LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", chunkRenderDataTask.getStage());
					}

					this.method_3610(chunkRenderDataTask);
					return;
				}

				chunkRenderDataTask.setStage(ChunkRenderDataTask.Stage.field_4421);
			} finally {
				chunkRenderDataTask.getLock().unlock();
			}

			final ChunkRenderData chunkRenderData = chunkRenderDataTask.getRenderData();
			ArrayList list = Lists.newArrayList();
			if (mode == ChunkRenderDataTask.Mode.field_4426) {
				for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
					if (chunkRenderData.method_3649(blockRenderLayer)) {
						list.add(
							this.batcher
								.method_3635(
									blockRenderLayer,
									chunkRenderDataTask.getVertexBuffer().getVertexBuffer(blockRenderLayer),
									chunkRenderDataTask.getChunkRenderer(),
									chunkRenderData,
									chunkRenderDataTask.getDistanceToPlayerSquared()
								)
						);
					}
				}
			} else if (mode == ChunkRenderDataTask.Mode.field_4427) {
				list.add(
					this.batcher
						.method_3635(
							BlockRenderLayer.TRANSLUCENT,
							chunkRenderDataTask.getVertexBuffer().getVertexBuffer(BlockRenderLayer.TRANSLUCENT),
							chunkRenderDataTask.getChunkRenderer(),
							chunkRenderData,
							chunkRenderDataTask.getDistanceToPlayerSquared()
						)
				);
			}

			ListenableFuture<List<Object>> listenableFuture = Futures.allAsList(list);
			chunkRenderDataTask.method_3597(() -> listenableFuture.cancel(false));
			Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
				public void method_3617(@Nullable List<Object> list) {
					ChunkRenderWorker.this.method_3610(chunkRenderDataTask);
					chunkRenderDataTask.getLock().lock();

					label43: {
						try {
							if (chunkRenderDataTask.getStage() == ChunkRenderDataTask.Stage.field_4421) {
								chunkRenderDataTask.setStage(ChunkRenderDataTask.Stage.field_4423);
								break label43;
							}

							if (!chunkRenderDataTask.method_3595()) {
								ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", chunkRenderDataTask.getStage());
							}
						} finally {
							chunkRenderDataTask.getLock().unlock();
						}

						return;
					}

					chunkRenderDataTask.getChunkRenderer().method_3665(chunkRenderData);
				}

				@Override
				public void onFailure(Throwable throwable) {
					ChunkRenderWorker.this.method_3610(chunkRenderDataTask);
					if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
						MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
					}
				}
			});
		}
	}

	private ChunkVertexBuffer method_3613() throws InterruptedException {
		return this.field_4428 != null ? this.field_4428 : this.batcher.getNextAvailableBuffer();
	}

	private void method_3610(ChunkRenderDataTask chunkRenderDataTask) {
		if (this.field_4428 == null) {
			this.batcher.addAvailableBuffer(chunkRenderDataTask.getVertexBuffer());
		}
	}

	public void stop() {
		this.running = false;
	}
}
