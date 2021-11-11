package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Override
	protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
		return TreeConfiguredFeatures.JUNGLE_TREE_NO_VINE;
	}

	@Override
	protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
		return TreeConfiguredFeatures.MEGA_JUNGLE_TREE;
	}
}
