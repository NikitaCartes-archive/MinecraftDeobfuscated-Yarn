package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.TreeFeature;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return null;
	}

	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
		return new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, true);
	}
}
