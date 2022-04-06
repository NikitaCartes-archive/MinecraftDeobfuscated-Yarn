package net.minecraft.block.sapling;

import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(AbstractRandom random, boolean bees) {
		return TreeConfiguredFeatures.SPRUCE;
	}

	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getLargeTreeFeature(AbstractRandom random) {
		return random.nextBoolean() ? TreeConfiguredFeatures.MEGA_SPRUCE : TreeConfiguredFeatures.MEGA_PINE;
	}
}
