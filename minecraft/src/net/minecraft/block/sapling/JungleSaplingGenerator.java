package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		return new AbstractTreeFeature(TreeFeatureConfig::deserialize).configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG);
	}

	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
		return Feature.TREE.configure(DefaultBiomeFeatures.MEGA_JUNGLE_TREE_CONFIG);
	}
}
