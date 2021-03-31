package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GrowingPlantFeature extends Feature<GrowingPlantFeatureConfig> {
	public GrowingPlantFeature(Codec<GrowingPlantFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<GrowingPlantFeatureConfig> context) {
		WorldAccess worldAccess = context.getWorld();
		GrowingPlantFeatureConfig growingPlantFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		int i = ((IntProvider)growingPlantFeatureConfig.heightDistribution.getDataOrEmpty(random).orElseThrow(IllegalStateException::new)).get(random);
		BlockPos.Mutable mutable = context.getOrigin().mutableCopy();
		BlockPos.Mutable mutable2 = mutable.mutableCopy().move(growingPlantFeatureConfig.direction);
		BlockState blockState = worldAccess.getBlockState(mutable);

		for (int j = 1; j <= i; j++) {
			BlockState blockState2 = blockState;
			blockState = worldAccess.getBlockState(mutable2);
			if (blockState2.isAir() || growingPlantFeatureConfig.allowWater && blockState2.getFluidState().isIn(FluidTags.WATER)) {
				if (j == i || !blockState.isAir()) {
					worldAccess.setBlockState(mutable, growingPlantFeatureConfig.headProvider.getBlockState(random, mutable), Block.NOTIFY_LISTENERS);
					break;
				}

				worldAccess.setBlockState(mutable, growingPlantFeatureConfig.bodyProvider.getBlockState(random, mutable), Block.NOTIFY_LISTENERS);
			}

			mutable2.move(growingPlantFeatureConfig.direction);
			mutable.move(growingPlantFeatureConfig.direction);
		}

		return true;
	}
}
