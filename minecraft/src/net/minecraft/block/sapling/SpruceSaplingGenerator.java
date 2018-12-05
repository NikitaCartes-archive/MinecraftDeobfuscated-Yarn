package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaPineTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import net.minecraft.world.gen.feature.TreeFeature;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return new SpruceTreeFeature(DefaultFeatureConfig::deserialize, true);
	}

	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
		return new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, random.nextBoolean());
	}
}
