package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class AcaciaSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<? extends TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		return Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG);
	}
}
