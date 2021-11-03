package net.minecraft.server.network;

import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;

public class SpawnLocating {
	@Nullable
	protected static BlockPos findOverworldSpawn(ServerWorld world, int x, int z) {
		boolean bl = world.getDimension().hasCeiling();
		WorldChunk worldChunk = world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
		int i = bl ? world.getChunkManager().getChunkGenerator().getSpawnHeight(world) : worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
		if (i < world.getBottomY()) {
			return null;
		} else {
			int j = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
			if (j <= i && j > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
				return null;
			} else {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = i + 1; k >= world.getBottomY(); k--) {
					mutable.set(x, k, z);
					BlockState blockState = world.getBlockState(mutable);
					if (!blockState.getFluidState().isEmpty()) {
						break;
					}

					if (Block.isFaceFullSquare(blockState.getCollisionShape(world, mutable), Direction.UP)) {
						return mutable.up().toImmutable();
					}
				}

				return null;
			}
		}
	}

	@Nullable
	public static BlockPos findServerSpawnPoint(ServerWorld world, ChunkPos chunkPos) {
		if (SharedConstants.method_37896(chunkPos)) {
			return null;
		} else {
			for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); i++) {
				for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); j++) {
					BlockPos blockPos = findOverworldSpawn(world, i, j);
					if (blockPos != null) {
						return blockPos;
					}
				}
			}

			return null;
		}
	}
}
