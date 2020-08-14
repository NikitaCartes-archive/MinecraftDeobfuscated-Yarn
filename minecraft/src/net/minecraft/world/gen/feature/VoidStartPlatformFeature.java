package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class VoidStartPlatformFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockPos START_BLOCK = new BlockPos(8, 3, 8);
	private static final ChunkPos START_CHUNK = new ChunkPos(START_BLOCK);

	public VoidStartPlatformFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	private static int getDistance(int x1, int z1, int x2, int z2) {
		return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (getDistance(chunkPos.x, chunkPos.z, START_CHUNK.x, START_CHUNK.z) > 1) {
			return true;
		} else {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = chunkPos.getStartZ(); i <= chunkPos.getEndZ(); i++) {
				for (int j = chunkPos.getStartX(); j <= chunkPos.getEndX(); j++) {
					if (getDistance(START_BLOCK.getX(), START_BLOCK.getZ(), j, i) <= 16) {
						mutable.set(j, START_BLOCK.getY(), i);
						if (mutable.equals(START_BLOCK)) {
							structureWorldAccess.setBlockState(mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
						} else {
							structureWorldAccess.setBlockState(mutable, Blocks.STONE.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		}
	}
}
