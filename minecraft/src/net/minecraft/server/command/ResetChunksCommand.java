package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import org.slf4j.Logger;

public class ResetChunksCommand {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("resetchunks")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> executeResetChunks(context.getSource(), 0, true))
				.then(
					CommandManager.argument("range", IntegerArgumentType.integer(0, 5))
						.executes(context -> executeResetChunks(context.getSource(), IntegerArgumentType.getInteger(context, "range"), true))
						.then(
							CommandManager.argument("skipOldChunks", BoolArgumentType.bool())
								.executes(
									context -> executeResetChunks(
											context.getSource(), IntegerArgumentType.getInteger(context, "range"), BoolArgumentType.getBool(context, "skipOldChunks")
										)
								)
						)
				)
		);
	}

	private static int executeResetChunks(ServerCommandSource source, int radius, boolean skipOldChunks) {
		ServerWorld serverWorld = source.getWorld();
		ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
		serverChunkManager.threadedAnvilChunkStorage.verifyChunkGenerator();
		Vec3d vec3d = source.getPosition();
		ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(vec3d));
		int i = chunkPos.z - radius;
		int j = chunkPos.z + radius;
		int k = chunkPos.x - radius;
		int l = chunkPos.x + radius;

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				ChunkPos chunkPos2 = new ChunkPos(n, m);
				WorldChunk worldChunk = serverChunkManager.getWorldChunk(n, m, false);
				if (worldChunk != null && (!skipOldChunks || !worldChunk.usesOldNoise())) {
					for (BlockPos blockPos : BlockPos.iterate(
						chunkPos2.getStartX(), serverWorld.getBottomY(), chunkPos2.getStartZ(), chunkPos2.getEndX(), serverWorld.getTopY() - 1, chunkPos2.getEndZ()
					)) {
						serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
					}
				}
			}
		}

		TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "worldgen-resetchunks");
		long o = System.currentTimeMillis();
		int p = (radius * 2 + 1) * (radius * 2 + 1);

		for (ChunkStatus chunkStatus : ImmutableList.of(
			ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.FEATURES, ChunkStatus.INITIALIZE_LIGHT
		)) {
			long q = System.currentTimeMillis();
			CompletableFuture<Unit> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);

			for (int r = chunkPos.z - radius; r <= chunkPos.z + radius; r++) {
				for (int s = chunkPos.x - radius; s <= chunkPos.x + radius; s++) {
					ChunkPos chunkPos3 = new ChunkPos(s, r);
					WorldChunk worldChunk2 = serverChunkManager.getWorldChunk(s, r, false);
					if (worldChunk2 != null && (!skipOldChunks || !worldChunk2.usesOldNoise())) {
						List<Chunk> list = Lists.<Chunk>newArrayList();
						int t = Math.max(1, chunkStatus.getTaskMargin());

						for (int u = chunkPos3.z - t; u <= chunkPos3.z + t; u++) {
							for (int v = chunkPos3.x - t; v <= chunkPos3.x + t; v++) {
								Chunk chunk = serverChunkManager.getChunk(v, u, chunkStatus.getPrevious(), true);
								Chunk chunk2;
								if (chunk instanceof WrapperProtoChunk) {
									chunk2 = new WrapperProtoChunk(((WrapperProtoChunk)chunk).getWrappedChunk(), true);
								} else if (chunk instanceof WorldChunk) {
									chunk2 = new WrapperProtoChunk((WorldChunk)chunk, true);
								} else {
									chunk2 = chunk;
								}

								list.add(chunk2);
							}
						}

						completableFuture = completableFuture.thenComposeAsync(
							unit -> chunkStatus.runGenerationTask(
										taskExecutor::send,
										serverWorld,
										serverChunkManager.getChunkGenerator(),
										serverWorld.getStructureTemplateManager(),
										serverChunkManager.getLightingProvider(),
										chunkx -> {
											throw new UnsupportedOperationException("Not creating full chunks here");
										},
										list
									)
									.thenApply(either -> {
										if (chunkStatus == ChunkStatus.NOISE) {
											either.left().ifPresent(chunkx -> Heightmap.populateHeightmaps(chunkx, ChunkStatus.POST_CARVER_HEIGHTMAPS));
										}

										return Unit.INSTANCE;
									}),
							taskExecutor::send
						);
					}
				}
			}

			source.getServer().runTasks(completableFuture::isDone);
			LOGGER.debug(chunkStatus + " took " + (System.currentTimeMillis() - q) + " ms");
		}

		long w = System.currentTimeMillis();

		for (int x = chunkPos.z - radius; x <= chunkPos.z + radius; x++) {
			for (int y = chunkPos.x - radius; y <= chunkPos.x + radius; y++) {
				ChunkPos chunkPos4 = new ChunkPos(y, x);
				WorldChunk worldChunk3 = serverChunkManager.getWorldChunk(y, x, false);
				if (worldChunk3 != null && (!skipOldChunks || !worldChunk3.usesOldNoise())) {
					for (BlockPos blockPos2 : BlockPos.iterate(
						chunkPos4.getStartX(), serverWorld.getBottomY(), chunkPos4.getStartZ(), chunkPos4.getEndX(), serverWorld.getTopY() - 1, chunkPos4.getEndZ()
					)) {
						serverChunkManager.markForUpdate(blockPos2);
					}
				}
			}
		}

		LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - w) + " ms");
		long q = System.currentTimeMillis() - o;
		source.sendFeedback(
			() -> Text.literal(
					String.format(Locale.ROOT, "%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", p, q, p, (float)q / (float)p)
				),
			true
		);
		return 1;
	}
}
