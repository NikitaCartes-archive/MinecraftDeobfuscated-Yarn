package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BirchSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		return Feature.TREE.configure(bl ? DefaultBiomeFeatures.BIRCH_TREE_WITH_MORE_BEEHIVES_CONFIG : DefaultBiomeFeatures.BIRCH_TREE_CONFIG);
	}
}
