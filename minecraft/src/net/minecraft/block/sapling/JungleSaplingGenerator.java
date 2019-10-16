package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random) {
		return new OakTreeFeature(NormalTreeFeatureConfig::method_23426).configure(DefaultBiomeFeatures.field_21183);
	}

	@Nullable
	@Override
	protected ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
		return Feature.MEGA_JUNGLE_TREE.configure(DefaultBiomeFeatures.field_21200);
	}
}
