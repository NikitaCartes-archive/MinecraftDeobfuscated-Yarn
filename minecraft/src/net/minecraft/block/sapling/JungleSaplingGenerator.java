package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Override
	protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
		return ConfiguredFeatures.JUNGLE_TREE_NO_VINE;
	}

	@Override
	protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
		return ConfiguredFeatures.MEGA_JUNGLE_TREE;
	}
}
