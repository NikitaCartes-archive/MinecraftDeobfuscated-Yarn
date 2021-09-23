package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
		return ConfiguredFeatures.SPRUCE;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
		return random.nextBoolean() ? ConfiguredFeatures.MEGA_SPRUCE : ConfiguredFeatures.MEGA_PINE;
	}
}
