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
        int i = chunkPos.z - radius;
        int j = chunkPos.z + radius;
        int k = chunkPos.x - radius;
        int l = chunkPos.x + radius;
        for (int m = i; m <= j; ++m) {
            for (int n = k; n <= l; ++n) {
                ChunkPos chunkPos2 = new ChunkPos(n, m);
                WorldChunk worldChunk = serverChunkManager.getWorldChunk(n, m, false);
                if (worldChunk == null) continue;
                for (BlockPos blockPos : BlockPos.iterate(chunkPos2.getStartX(), serverWorld.getBottomY(), chunkPos2.getStartZ(), chunkPos2.getEndX(), serverWorld.getTopY() - 1, chunkPos2.getEndZ())) {
                    serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
                }
            }
        }
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(Util.getMainWorkerExecutor(), "worldgen-resetchunks");
        long o = System.currentTimeMillis();
        int p = (radius * 2 + 1) * (radius * 2 + 1);
        for (ChunkStatus chunkStatus : ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.LIQUID_CARVERS, ChunkStatus.FEATURES)) {
            long q = System.currentTimeMillis();
            CompletionStage<Unit> completableFuture = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, taskExecutor::send);
            for (int r = chunkPos.z - radius; r <= chunkPos.z + radius; ++r) {
                for (int s = chunkPos.x - radius; s <= chunkPos.x + radius; ++s) {
                    ChunkPos chunkPos3 = new ChunkPos(s, r);
                    WorldChunk worldChunk2 = serverChunkManager.getWorldChunk(s, r, false);
                    if (worldChunk2 == null) continue;
                    ArrayList<Chunk> list = Lists.newArrayList();
                    int t = Math.max(1, chunkStatus.getTaskMargin());
                    for (int u = chunkPos3.z - t; u <= chunkPos3.z + t; ++u) {
                        for (int v = chunkPos3.x - t; v <= chunkPos3.x + t; ++v) {
                            Chunk chunk = serverChunkManager.getChunk(v, u, chunkStatus.getPrevious(), true);
                            Chunk chunk2 = chunk instanceof ReadOnlyChunk ? new ReadOnlyChunk(((ReadOnlyChunk)chunk).getWrappedChunk(), true) : (chunk instanceof WorldChunk ? new ReadOnlyChunk((WorldChunk)chunk, true) : chunk);
                            list.add(chunk2);
                        }
                    }
                    completableFuture = completableFuture.thenComposeAsync(unit -> chunkStatus.runGenerationTask(taskExecutor::send, serverWorld, serverChunkManager.getChunkGenerator(), serverWorld.getStructureManager(), serverChunkManager.getLightingProvider(), chunk -> {
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
            LOGGER.debug(chunkStatus.getId() + " took " + (System.currentTimeMillis() - q) + " ms");
        }
        long w = System.currentTimeMillis();
        for (int x = chunkPos.z - radius; x <= chunkPos.z + radius; ++x) {
            for (int y = chunkPos.x - radius; y <= chunkPos.x + radius; ++y) {
                ChunkPos chunkPos4 = new ChunkPos(y, x);
                WorldChunk worldChunk3 = serverChunkManager.getWorldChunk(y, x, false);
                if (worldChunk3 == null) continue;
                for (BlockPos blockPos2 : BlockPos.iterate(chunkPos4.getStartX(), serverWorld.getBottomY(), chunkPos4.getStartZ(), chunkPos4.getEndX(), serverWorld.getTopY() - 1, chunkPos4.getEndZ())) {
                    serverChunkManager.markForUpdate(blockPos2);
                }
            }
        }
        LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - w) + " ms");
        long q = System.currentTimeMillis() - o;
        source.sendFeedback(new LiteralText(String.format("%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", p, q, p, Float.valueOf((float)q / (float)p))), true);
        return 1;
    }
}

