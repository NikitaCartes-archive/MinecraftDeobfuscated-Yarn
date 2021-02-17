package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class NetherForestVegetationFeature extends Feature<BlockPileFeatureConfig> {
	public NetherForestVegetationFeature(Codec<BlockPileFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<BlockPileFeatureConfig> context) {
		return generate(context.getWorld(), context.getRandom(), context.getOrigin(), context.getConfig(), 8, 4);
	}

	public static boolean generate(WorldAccess world, Random random, BlockPos pos, BlockPileFeatureConfig config, int i, int j) {
		BlockState blockState = world.getBlockState(pos.down());
		if (!blockState.isIn(BlockTags.NYLIUM)) {
			return false;
		} else {
			int k = pos.getY();
			if (k >= world.getBottomY() + 1 && k + 1 < world.getTopY()) {
				int l = 0;

				for (int m = 0; m < i * i; m++) {
					BlockPos blockPos = pos.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
					BlockState blockState2 = config.stateProvider.getBlockState(random, blockPos);
					if (world.isAir(blockPos) && blockPos.getY() > world.getBottomY() && blockState2.canPlaceAt(world, blockPos)) {
						world.setBlockState(blockPos, blockState2, 2);
						l++;
					}
				}

				return l > 0;
			} else {
				return false;
			}
		}
	}
}
