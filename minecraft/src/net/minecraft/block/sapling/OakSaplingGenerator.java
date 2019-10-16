package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public class OakSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random) {
		return random.nextInt(10) == 0
			? Feature.FANCY_TREE.configure(DefaultBiomeFeatures.field_21190)
			: Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.field_21126);
	}
}
