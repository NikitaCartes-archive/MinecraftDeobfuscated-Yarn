package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class AzaleaSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return TreeConfiguredFeatures.AZALEA_TREE;
	}
}
