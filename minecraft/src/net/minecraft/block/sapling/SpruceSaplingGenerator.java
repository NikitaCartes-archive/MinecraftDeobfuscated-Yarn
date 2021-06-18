package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random random, boolean bees) {
		return ConfiguredFeatures.SPRUCE;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> getLargeTreeFeature(Random random) {
		return random.nextBoolean() ? ConfiguredFeatures.MEGA_SPRUCE : ConfiguredFeatures.MEGA_PINE;
	}
}
