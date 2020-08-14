package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ForestRockFeature extends Feature<SingleStateFeatureConfig> {
	public ForestRockFeature(Codec<SingleStateFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SingleStateFeatureConfig singleStateFeatureConfig
	) {
		while (blockPos.getY() > 3) {
			if (!structureWorldAccess.isAir(blockPos.down())) {
				Block block = structureWorldAccess.getBlockState(blockPos.down()).getBlock();
				if (isSoil(block) || isStone(block)) {
					break;
				}
			}

			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= 3) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				int j = random.nextInt(2);
				int k = random.nextInt(2);
				int l = random.nextInt(2);
				float f = (float)(j + k + l) * 0.333F + 0.5F;

				for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-j, -k, -l), blockPos.add(j, k, l))) {
					if (blockPos2.getSquaredDistance(blockPos) <= (double)(f * f)) {
						structureWorldAccess.setBlockState(blockPos2, singleStateFeatureConfig.state, 4);
					}
				}

				blockPos = blockPos.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
			}

			return true;
		}
	}
}
