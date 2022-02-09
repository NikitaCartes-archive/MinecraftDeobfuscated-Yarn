package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public class ConfiguredFeatures {
	public static RegistryEntry<? extends ConfiguredFeature<?, ?>> getDefaultConfiguredFeature() {
		List<RegistryEntry<? extends ConfiguredFeature<?, ?>>> list = List.of(
			OceanConfiguredFeatures.KELP,
			UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL,
			EndConfiguredFeatures.CHORUS_PLANT,
			MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD,
			NetherConfiguredFeatures.BASALT_BLOBS,
			OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE,
			PileConfiguredFeatures.PILE_HAY,
			TreeConfiguredFeatures.AZALEA_TREE,
			VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA
		);
		return Util.getRandom(list, new Random());
	}

	private static BlockPredicate createBlockPredicate(List<Block> validGround) {
		BlockPredicate blockPredicate;
		if (!validGround.isEmpty()) {
			blockPredicate = BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.matchingBlocks(validGround, new BlockPos(0, -1, 0)));
		} else {
			blockPredicate = BlockPredicate.IS_AIR;
		}

		return blockPredicate;
	}

	public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(int tries, RegistryEntry<PlacedFeature> feature) {
		return new RandomPatchFeatureConfig(tries, 7, 3, feature);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(
		F feature, FC featureConfig, List<Block> list, int i
	) {
		return createRandomPatchFeatureConfig(i, PlacedFeatures.createEntry(feature, featureConfig, createBlockPredicate(list)));
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(
		F feature, FC featureConfig, List<Block> list
	) {
		return createRandomPatchFeatureConfig(feature, featureConfig, list, 96);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(F feature, FC featureConfig) {
		return createRandomPatchFeatureConfig(feature, featureConfig, List.of(), 96);
	}

	public static RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> method_40364(String string, Feature<DefaultFeatureConfig> feature) {
		return register(string, feature, FeatureConfig.DEFAULT);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC featureConfig) {
		return BuiltinRegistries.method_40360(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, featureConfig));
	}
}
