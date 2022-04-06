package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
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
		return Util.getRandom(list, AbstractRandom.createAtomic());
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
		F feature, FC config, List<Block> predicateBlocks, int tries
	) {
		return createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(feature, config, createBlockPredicate(predicateBlocks)));
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(
		F feature, FC config, List<Block> predicateBlocks
	) {
		return createRandomPatchFeatureConfig(feature, config, predicateBlocks, 96);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RandomPatchFeatureConfig createRandomPatchFeatureConfig(F feature, FC config) {
		return createRandomPatchFeatureConfig(feature, config, List.of(), 96);
	}

	public static RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> register(String id, Feature<DefaultFeatureConfig> feature) {
		return register(id, feature, FeatureConfig.DEFAULT);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
		return BuiltinRegistries.addCasted(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, config));
	}
}
