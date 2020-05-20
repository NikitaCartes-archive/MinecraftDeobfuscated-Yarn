package net.minecraft.server.network;

import java.util.Random;
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
	private static BlockPos findOverworldSpawn(ServerWorld world, int x, int z, boolean validSpawnNeeded) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
		Biome biome = world.getBiome(mutable);
		BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
		if (validSpawnNeeded && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
			return null;
		} else {
			WorldChunk worldChunk = world.getChunk(x >> 4, z >> 4);
			int i = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
			if (i < 0) {
				return null;
			} else if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)
				)
			 {
				return null;
			} else {
				for (int j = i + 1; j >= 0; j--) {
					mutable.set(x, j, z);
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

	@Nullable
	private static BlockPos findEndSpawn(ServerWorld world, long seed, int x, int z) {
		ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
		Random random = new Random(seed);
		BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
		return world.getTopNonAirState(blockPos).getMaterial().blocksMovement() ? blockPos : null;
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

	@Nullable
	protected static BlockPos findPlayerSpawn(ServerWorld world, BlockPos pos, int negativeOffset, int xOffset, int zOffset) {
		if (world.getDimension().isOverworld()) {
			return findOverworldSpawn(world, pos.getX() + xOffset - negativeOffset, pos.getZ() + zOffset - negativeOffset, false);
		} else {
			return world.getDimension().isEnd()
				? findEndSpawn(world, world.getSeed(), pos.getX() + xOffset - negativeOffset, pos.getZ() + zOffset - negativeOffset)
				: null;
		}
	}
}
