package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class OakSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return (AbstractTreeFeature<DefaultFeatureConfig>)(random.nextInt(10) == 0
			? new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, true)
			: new OakTreeFeature(DefaultFeatureConfig::deserialize, true));
	}
}
