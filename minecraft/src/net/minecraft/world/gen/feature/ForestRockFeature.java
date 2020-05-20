package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ForestRockFeature extends Feature<BoulderFeatureConfig> {
	public ForestRockFeature(Codec<BoulderFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		BoulderFeatureConfig boulderFeatureConfig
	) {
		while (blockPos.getY() > 3) {
			if (!serverWorldAccess.isAir(blockPos.down())) {
				Block block = serverWorldAccess.getBlockState(blockPos.down()).getBlock();
				if (isDirt(block) || isStone(block)) {
					break;
				}
			}

			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= 3) {
			return false;
		} else {
			int i = boulderFeatureConfig.startRadius;

			for (int j = 0; i >= 0 && j < 3; j++) {
				int k = i + random.nextInt(2);
				int l = i + random.nextInt(2);
				int m = i + random.nextInt(2);
				float f = (float)(k + l + m) * 0.333F + 0.5F;

				for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-k, -l, -m), blockPos.add(k, l, m))) {
					if (blockPos2.getSquaredDistance(blockPos) <= (double)(f * f)) {
						serverWorldAccess.setBlockState(blockPos2, boulderFeatureConfig.state, 4);
					}
				}

				blockPos = blockPos.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
			}

			return true;
		}
	}
}
