package net.minecraft.block.sapling;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class AcaciaSaplingGenerator extends SaplingGenerator {
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return TreeConfiguredFeatures.ACACIA;
	}
}
