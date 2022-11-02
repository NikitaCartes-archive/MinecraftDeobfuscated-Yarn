package net.minecraft.block.sapling;

import javax.annotation.Nullable;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public class MangroveSaplingGenerator extends SaplingGenerator {
	private final float tallChance;

	public MangroveSaplingGenerator(float tallChance) {
		this.tallChance = tallChance;
	}

	@Nullable
	@Override
	protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
		return random.nextFloat() < this.tallChance ? TreeConfiguredFeatures.TALL_MANGROVE : TreeConfiguredFeatures.MANGROVE;
	}
}
