package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.TreeFeature;

public class BirchSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return new BirchTreeFeature(DefaultFeatureConfig::deserialize, true, false);
	}
}
