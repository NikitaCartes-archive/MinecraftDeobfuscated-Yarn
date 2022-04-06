package net.minecraft.block.sapling;

import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class AcaciaSaplingGenerator extends SaplingGenerator {
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(AbstractRandom random, boolean bees) {
		return TreeConfiguredFeatures.ACACIA;
	}
}
