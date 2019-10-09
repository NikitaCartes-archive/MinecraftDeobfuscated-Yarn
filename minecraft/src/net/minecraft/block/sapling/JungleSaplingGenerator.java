package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4636;
import net.minecraft.class_4640;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OakTreeFeature;

public class JungleSaplingGenerator extends LargeTreeSaplingGenerator {
	@Nullable
	@Override
	protected ConfiguredFeature<class_4640, ?> createTreeFeature(Random random) {
		return new OakTreeFeature(class_4640::method_23426).method_23397(DefaultBiomeFeatures.field_21183);
	}

	@Nullable
	@Override
	protected ConfiguredFeature<class_4636, ?> createLargeTreeFeature(Random random) {
		return Feature.MEGA_JUNGLE_TREE.method_23397(DefaultBiomeFeatures.field_21200);
	}
}
