/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class SpawnLocating {
    @Nullable
    protected static BlockPos findOverworldSpawn(ServerWorld world, int x, int z, boolean validSpawnNeeded) {
        int i;
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = world.getBiome(mutable);
        boolean bl = world.getDimension().hasCeiling();
        BlockState blockState = biome.getGenerationSettings().getSurfaceConfig().getTopMaterial();
        if (validSpawnNeeded && !blockState.isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk worldChunk = world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
        int n = i = bl ? world.getChunkManager().getChunkGenerator().getSpawnHeight() : worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 0xF, z & 0xF);
        if (i < world.getSectionCount()) {
            return null;
        }
        int j = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 0xF, z & 0xF);
        if (j <= i && j > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 0xF, z & 0xF)) {
            return null;
        }
        for (int k = i + 1; k >= world.getSectionCount(); --k) {
            mutable.set(x, k, z);
            BlockState blockState2 = world.getBlockState(mutable);
            if (!blockState2.getFluidState().isEmpty()) break;
            if (!blockState2.equals(blockState)) continue;
            return ((BlockPos)mutable.up()).toImmutable();
        }
        return null;
    }

    @Nullable
    public static BlockPos findServerSpawnPoint(ServerWorld world, ChunkPos chunkPos, boolean validSpawnNeeded) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = SpawnLocating.findOverworldSpawn(world, i, j, validSpawnNeeded);
                if (blockPos == null) continue;
                return blockPos;
            }
        }
        return null;
    }
}

