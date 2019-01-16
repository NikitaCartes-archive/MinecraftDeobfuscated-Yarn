package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaPineTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class SpruceSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return new SpruceTreeFeature(DefaultFeatureConfig::deserialize, true);
	}

	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
		return new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, random.nextBoolean());
	}
}
