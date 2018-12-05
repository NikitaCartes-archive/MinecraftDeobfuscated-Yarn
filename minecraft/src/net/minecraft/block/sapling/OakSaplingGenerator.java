package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import net.minecraft.world.gen.feature.TreeFeature;

public class OakSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected TreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return (TreeFeature<DefaultFeatureConfig>)(random.nextInt(10) == 0
			? new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, true)
			: new OakTreeFeature(DefaultFeatureConfig::deserialize, true));
	}
}
