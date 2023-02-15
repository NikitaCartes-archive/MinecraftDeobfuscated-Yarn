package net.minecraft.block.sapling;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class CherrySaplingGenerator extends SaplingGenerator {
	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return bees ? TreeConfiguredFeatures.CHERRY_BEES_005 : TreeConfiguredFeatures.CHERRY;
	}
}
