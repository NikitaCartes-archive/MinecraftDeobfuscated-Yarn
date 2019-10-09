package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4636;
import net.minecraft.class_4640;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class DarkOakSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<class_4640, ?> createTreeFeature(Random random) {
		return null;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<class_4636, ?> createLargeTreeFeature(Random random) {
		return Feature.DARK_OAK_TREE.method_23397(DefaultBiomeFeatures.field_21197);
	}
}
