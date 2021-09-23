package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
		return null;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<?, ?> getLargeTreeFeature(Random random) {
		return ConfiguredFeatures.DARK_OAK;
	}
}
