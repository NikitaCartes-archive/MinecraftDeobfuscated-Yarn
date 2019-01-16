package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaJungleTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return new OakTreeFeature(
			DefaultFeatureConfig::deserialize, true, 4 + random.nextInt(7), Blocks.field_10306.getDefaultState(), Blocks.field_10335.getDefaultState(), false
		);
	}

	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createLargeTreeFeature(Random random) {
		return new MegaJungleTreeFeature(DefaultFeatureConfig::deserialize, true, 10, 20, Blocks.field_10306.getDefaultState(), Blocks.field_10335.getDefaultState());
	}
}
