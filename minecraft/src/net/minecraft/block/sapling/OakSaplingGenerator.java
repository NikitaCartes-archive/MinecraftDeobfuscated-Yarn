package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class OakSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<BranchedTreeFeatureConfig, ?> createTreeFeature(Random random) {
		return random.nextInt(10) == 0
			? Feature.FANCY_TREE.configure(DefaultBiomeFeatures.FANCY_TREE_CONFIG)
			: Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG);
	}
}
