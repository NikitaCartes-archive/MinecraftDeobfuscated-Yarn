package net.minecraft.block.sapling;

import java.util.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class BirchSaplingGenerator extends SaplingGenerator {
	@Override
	protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
		return bees ? ConfiguredFeatures.BIRCH_BEES_005 : ConfiguredFeatures.BIRCH;
	}
}
