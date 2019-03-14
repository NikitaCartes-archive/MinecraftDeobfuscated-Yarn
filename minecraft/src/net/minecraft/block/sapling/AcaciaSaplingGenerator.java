package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.SavannaTreeFeature;

public class AcaciaSaplingGenerator extends SaplingGenerator {
	@Nullable
	@Override
	protected AbstractTreeFeature<DefaultFeatureConfig> createTreeFeature(Random random) {
		return new SavannaTreeFeature(DefaultFeatureConfig::deserialize, true);
	}
}
