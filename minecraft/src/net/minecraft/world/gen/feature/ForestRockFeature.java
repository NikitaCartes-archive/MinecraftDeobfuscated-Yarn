package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ForestRockFeature extends Feature<SingleStateFeatureConfig> {
	public ForestRockFeature(Codec<SingleStateFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SingleStateFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();

		SingleStateFeatureConfig singleStateFeatureConfig;
		for (singleStateFeatureConfig = context.getConfig(); blockPos.getY() > structureWorldAccess.getBottomY() + 3; blockPos = blockPos.down()) {
			if (!structureWorldAccess.isAir(blockPos.down())) {
				BlockState blockState = structureWorldAccess.getBlockState(blockPos.down());
				if (isSoil(blockState) || isStone(blockState)) {
					break;
				}
			}
		}

		if (blockPos.getY() <= structureWorldAccess.getBottomY() + 3) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				int j = random.nextInt(2);
				int k = random.nextInt(2);
				int l = random.nextInt(2);
				float f = (float)(j + k + l) * 0.333F + 0.5F;

				for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-j, -k, -l), blockPos.add(j, k, l))) {
					if (blockPos2.getSquaredDistance(blockPos) <= (double)(f * f)) {
						structureWorldAccess.setBlockState(blockPos2, singleStateFeatureConfig.state, Block.NOTIFY_ALL);
					}
				}

				blockPos = blockPos.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
			}

			return true;
		}
	}
}
