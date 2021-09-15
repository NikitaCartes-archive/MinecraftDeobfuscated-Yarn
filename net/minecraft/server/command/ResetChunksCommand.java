/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Unit;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResetChunksCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("resetchunks").requires(source -> source.hasPermissionLevel(2))).executes(context -> ResetChunksCommand.executeResetChunks((ServerCommandSource)context.getSource(), 0))).then(CommandManager.argument("range", IntegerArgumentType.integer(0, 5)).executes(context -> ResetChunksCommand.executeResetChunks((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "range")))));
    }

    private static int executeResetChunks(ServerCommandSource source, int radius) {
        ServerWorld serverWorld = source.getWorld();
        ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
        serverChunkManager.threadedAnvilChunkStorage.method_37904();
        Vec3d vec3d = source.getPosition();
        ChunkPos chunkPos = new ChunkPos(new BlockPos(vec3d));
        for (int i = chunkPos.z - radius; i <= chunkPos.z + radius; ++i) {
            for (int j = chunkPos.x - radius; j <= chunkPos.x + radius; ++j) {
                ChunkPos chunkPos2 = new ChunkPos(j, i);
                for (BlockPos blockPos : BlockPos.iterate(chunkPos2.getStartX(), serverWorld.getBottomY(), chunkPos2.getStartZ(), chunkPos2.getEndX(), serverWorld.getTopY() - 1, chunkPos2.getEndZ())) {
                    serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
                }
            }
        }
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "worldgen-resetchunks");
        long l = System.currentTimeMillis();
        int k = (radius * 2 + 1) * (radius * 2 + 1);
        for (ChunkStatus chunkStatus : ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES)) {
            long m = System.currentTimeMillis();
            CompletionStage<Unit> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);
            for (int n = chunkPos.z - radius; n <= chunkPos.z + radius; ++n) {
                for (int o = chunkPos.x - radius; o <= chunkPos.x + radius; ++o) {
                    ChunkPos chunkPos3 = new ChunkPos(o, n);
                    ArrayList<Chunk> list = Lists.newArrayList();
                    int p = Math.max(1, chunkStatus.getTaskMargin());
                    for (int q = chunkPos3.z - p; q <= chunkPos3.z + p; ++q) {
                        for (int r = chunkPos3.x - p; r <= chunkPos3.x + p; ++r) {
                            Chunk chunk = serverChunkManager.getChunk(r, q, chunkStatus.getPrevious(), true);
                            Chunk chunk2 = chunk instanceof ReadOnlyChunk ? new ReadOnlyChunk(((ReadOnlyChunk)chunk).getWrappedChunk(), true) : (chunk instanceof WorldChunk ? new ReadOnlyChunk((WorldChunk)chunk, true) : chunk);
                            list.add(chunk2);
                        }
                    }
                    completableFuture = completableFuture.thenComposeAsync(unit -> chunkStatus.runGenerationTask(taskExecutor::send, serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getStructureManager(), serverChunkManager.getLightingProvider(), chunk -> {
                        throw new UnsupportedOperationException("Not creating full chunks here");
                    }, list, true).thenApply(either -> {
                        if (chunkStatus == ChunkStatus.NOISE) {
                            either.left().ifPresent(chunk -> Heightmap.populateHeightmaps(chunk, ChunkStatus.POST_CARVER_HEIGHTMAPS));
                        }
                        return Unit.INSTANCE;
                    }), taskExecutor::send);
                }
            }
            source.getServer().runTasks(() -> completableFuture.isDone());
            LOGGER.debug(chunkStatus.getId() + " took " + (System.currentTimeMillis() - m) + " ms");
        }
        long l2 = System.currentTimeMillis();
        for (int t = chunkPos.z - radius; t <= chunkPos.z + radius; ++t) {
            for (int u = chunkPos.x - radius; u <= chunkPos.x + radius; ++u) {
                ChunkPos chunkPos4 = new ChunkPos(u, t);
                for (BlockPos blockPos2 : BlockPos.iterate(chunkPos4.getStartX(), serverWorld.getBottomY(), chunkPos4.getStartZ(), chunkPos4.getEndX(), serverWorld.getTopY() - 1, chunkPos4.getEndZ())) {
                    serverChunkManager.markForUpdate(blockPos2);
                }
            }
        }
        LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - l2) + " ms");
        long m = System.currentTimeMillis() - l;
        source.sendFeedback(new LiteralText(String.format("%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", k, m, k, Float.valueOf((float)m / (float)k))), true);
        return 1;
    }
}

