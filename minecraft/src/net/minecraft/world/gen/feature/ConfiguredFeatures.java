package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public class ConfiguredFeatures {
	public static ConfiguredFeature<?, ?> getDefaultConfiguredFeature() {
		ConfiguredFeature<?, ?>[] configuredFeatures = new ConfiguredFeature[]{
			OceanConfiguredFeatures.KELP,
			UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL,
			EndConfiguredFeatures.CHORUS_PLANT,
			MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD,
			NetherConfiguredFeatures.BASALT_BLOBS,
			OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE,
			PileConfiguredFeatures.PILE_HAY,
			TreeConfiguredFeatures.AZALEA_TREE,
			VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA
		};
		return Util.getRandom(configuredFeatures, new Random());
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

	public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(int tries, PlacedFeature feature) {
		return new RandomPatchFeatureConfig(tries, 7, 3, () -> feature);
	}

	public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature, List<Block> validGround, int tries) {
		return createRandomPatchFeatureConfig(tries, feature.withBlockPredicateFilter(createBlockPredicate(validGround)));
	}

	public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature, List<Block> validGround) {
		return createRandomPatchFeatureConfig(feature, validGround, 96);
	}

	public static RandomPatchFeatureConfig createRandomPatchFeatureConfig(ConfiguredFeature<?, ?> feature) {
		return createRandomPatchFeatureConfig(feature, List.of(), 96);
	}

	public static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
	}
}
