package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return null;
	}

	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
		return new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, true);
	}
}
