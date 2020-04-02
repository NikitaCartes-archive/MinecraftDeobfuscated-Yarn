package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<? extends TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		return new OakTreeFeature(BranchedTreeFeatureConfig::deserialize).configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG);
	}

	@Nullable
	@Override
	protected ConfiguredFeature<? extends TreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
		return Feature.MEGA_JUNGLE_TREE.configure(DefaultBiomeFeatures.MEGA_JUNGLE_TREE_CONFIG);
	}
}
