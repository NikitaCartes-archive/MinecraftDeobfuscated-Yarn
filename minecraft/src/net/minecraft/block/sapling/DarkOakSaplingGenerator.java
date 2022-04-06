package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(AbstractRandom random, boolean bees) {
		return null;
	}

	@Nullable
	@Override
	protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getLargeTreeFeature(AbstractRandom random) {
		return TreeConfiguredFeatures.DARK_OAK;
	}
}
