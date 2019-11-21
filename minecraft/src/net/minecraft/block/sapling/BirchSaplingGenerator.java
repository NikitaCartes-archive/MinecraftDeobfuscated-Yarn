package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class BirchSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random) {
		return Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.BIRCH_TREE_CONFIG);
	}
}
