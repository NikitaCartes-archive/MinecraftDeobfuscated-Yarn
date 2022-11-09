package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return null;
	}

	@Nullable
	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getLargeTreeFeature(Random random) {
		return TreeConfiguredFeatures.DARK_OAK;
	}
}
