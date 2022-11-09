package net.minecraft.block.sapling;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return TreeConfiguredFeatures.JUNGLE_TREE_NO_VINE;
	}

	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getLargeTreeFeature(Random random) {
		return TreeConfiguredFeatures.MEGA_JUNGLE_TREE;
	}
}
