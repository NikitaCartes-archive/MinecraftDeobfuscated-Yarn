package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.RegeneratingChunk;
import net.minecraft.world.chunk.WorldChunk;

public class ResetChunksCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("resetchunks")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("radius", IntegerArgumentType.integer(0, 8))
						.executes(context -> executeResetChunks(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))
				)
		);
	}

	private static int executeResetChunks(ServerCommandSource source, int radius) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
		Vec3d vec3d = source.getPosition();
		VanillaTerrainParameters.init();
		ChunkPos chunkPos = new ChunkPos(MathHelper.floor(vec3d.getX() / 16.0), MathHelper.floor(vec3d.getZ() / 16.0));

		for (int i = chunkPos.z - radius; i <= chunkPos.z + radius; i++) {
			for (int j = chunkPos.x - radius; j <= chunkPos.x + radius; j++) {
				ChunkPos chunkPos2 = new ChunkPos(j, i);

				for (BlockPos blockPos : BlockPos.iterate(
					chunkPos2.getStartX(), serverWorld.getBottomY(), chunkPos2.getStartZ(), chunkPos2.getEndX(), serverWorld.getTopY() - 1, chunkPos2.getEndZ()
				)) {
					serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
				}
			}
		}

		TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "worldgen-resetchunks");
		long l = System.currentTimeMillis();
		int k = (radius * 2 + 1) * (radius * 2 + 1);

		for (ChunkStatus chunkStatus : ImmutableList.of(
			ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES
		)) {
			long m = System.currentTimeMillis();
			CompletableFuture<Unit> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);

			for (int n = chunkPos.z - radius; n <= chunkPos.z + radius; n++) {
				for (int o = chunkPos.x - radius; o <= chunkPos.x + radius; o++) {
					ChunkPos chunkPos3 = new ChunkPos(o, n);
					List<Chunk> list = Lists.<Chunk>newArrayList();

					for (int p = chunkPos3.z - 8; p <= chunkPos3.z + 8; p++) {
						for (int q = chunkPos3.x - 8; q <= chunkPos3.x + 8; q++) {
							Chunk chunk = serverChunkManager.getChunk(q, p, chunkStatus.getPrevious(), true);
							Chunk chunk2;
							if (chunk instanceof ReadOnlyChunk) {
								chunk2 = new RegeneratingChunk(((ReadOnlyChunk)chunk).getWrappedChunk());
							} else if (chunk instanceof WorldChunk) {
								chunk2 = new RegeneratingChunk((WorldChunk)chunk);
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
									serverWorld.getChunkManager().getChunkGenerator(),
									serverWorld.getStructureManager(),
									serverChunkManager.getLightingProvider(),
									chunkx -> {
										throw new UnsupportedOperationException("Not creating full chunks here");
									},
									list,
									true
								)
								.thenApply(either -> Unit.INSTANCE),
						taskExecutor::send
					);
				}
			}

			source.getServer().runTasks(completableFuture::isDone);
			System.out.println(chunkStatus.getId() + " took " + (System.currentTimeMillis() - m) + " ms");
		}

		long r = System.currentTimeMillis();

		for (int s = chunkPos.z - radius; s <= chunkPos.z + radius; s++) {
			for (int t = chunkPos.x - radius; t <= chunkPos.x + radius; t++) {
				ChunkPos chunkPos4 = new ChunkPos(t, s);

				for (BlockPos blockPos2 : BlockPos.iterate(
					chunkPos4.getStartX(), serverWorld.getBottomY(), chunkPos4.getStartZ(), chunkPos4.getEndX(), serverWorld.getTopY() - 1, chunkPos4.getEndZ()
				)) {
					serverChunkManager.markForUpdate(blockPos2);
				}
			}
		}

		System.out.println("blockChanged took " + (System.currentTimeMillis() - r) + " ms");
		long m = System.currentTimeMillis() - l;
		source.sendFeedback(new LiteralText("Chunks have been reset. This took " + m + " ms for " + k + " chunks, or " + m / (long)k + " ms/chunk"), true);
		return 1;
	}
}
