package net.minecraft.server.network;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

public class SpawnLocating {
	@Nullable
	protected static BlockPos findOverworldSpawn(ServerWorld world, int x, int z, boolean validSpawnNeeded) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
		Biome biome = world.getBiome(mutable);
		boolean bl = world.getDimension().hasCeiling();
		BlockState blockState = biome.getGenerationSettings().getSurfaceConfig().getTopMaterial();
		if (validSpawnNeeded && !blockState.getBlock().isIn(BlockTags.field_15478)) {
			return null;
		} else {
			WorldChunk worldChunk = world.method_8497(x >> 4, z >> 4);
			int i = bl ? world.method_14178().getChunkGenerator().getSpawnHeight() : worldChunk.sampleHeightmap(Heightmap.Type.field_13197, x & 15, z & 15);
			if (i < 0) {
				return null;
			} else {
				int j = worldChunk.sampleHeightmap(Heightmap.Type.field_13202, x & 15, z & 15);
				if (j <= i && j > worldChunk.sampleHeightmap(Heightmap.Type.field_13200, x & 15, z & 15)) {
					return null;
				} else {
					for (int k = i + 1; k >= 0; k--) {
						mutable.set(x, k, z);
						BlockState blockState2 = world.getBlockState(mutable);
						if (!blockState2.getFluidState().isEmpty()) {
							break;
						}

						if (blockState2.equals(blockState)) {
							return mutable.up().toImmutable();
						}
					}

					return null;
				}
			}
		}
	}

	@Nullable
	public static BlockPos findServerSpawnPoint(ServerWorld world, ChunkPos chunkPos, boolean validSpawnNeeded) {
		for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); i++) {
			for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); j++) {
				BlockPos blockPos = findOverworldSpawn(world, i, j, validSpawnNeeded);
				if (blockPos != null) {
					return blockPos;
				}
			}
		}

		return null;
	}
}
