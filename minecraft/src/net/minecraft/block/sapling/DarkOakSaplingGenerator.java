package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<NormalTreeFeatureConfig, ?> createTreeFeature(Random random) {
		return null;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<MegaTreeFeatureConfig, ?> createLargeTreeFeature(Random random) {
		return Feature.DARK_OAK_TREE.configure(DefaultBiomeFeatures.field_21197);
	}
}
