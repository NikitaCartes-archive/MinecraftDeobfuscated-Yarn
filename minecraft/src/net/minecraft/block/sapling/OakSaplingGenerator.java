package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class OakSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		if (random.nextInt(10) == 0) {
			return bl ? ConfiguredFeatures.field_26097 : ConfiguredFeatures.field_26044;
		} else {
			return bl ? ConfiguredFeatures.field_26054 : ConfiguredFeatures.field_26036;
		}
	}
}
