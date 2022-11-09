package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class PileConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> PILE_HAY = ConfiguredFeatures.of("pile_hay");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PILE_MELON = ConfiguredFeatures.of("pile_melon");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PILE_SNOW = ConfiguredFeatures.of("pile_snow");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PILE_ICE = ConfiguredFeatures.of("pile_ice");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PILE_PUMPKIN = ConfiguredFeatures.of("pile_pumpkin");

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		ConfiguredFeatures.register(featureRegisterable, PILE_HAY, Feature.BLOCK_PILE, new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK)));
		ConfiguredFeatures.register(featureRegisterable, PILE_MELON, Feature.BLOCK_PILE, new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.MELON)));
		ConfiguredFeatures.register(featureRegisterable, PILE_SNOW, Feature.BLOCK_PILE, new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.SNOW)));
		ConfiguredFeatures.register(
			featureRegisterable,
			PILE_ICE,
			Feature.BLOCK_PILE,
			new BlockPileFeatureConfig(
				new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.BLUE_ICE.getDefaultState(), 1).add(Blocks.PACKED_ICE.getDefaultState(), 5))
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PILE_PUMPKIN,
			Feature.BLOCK_PILE,
			new BlockPileFeatureConfig(
				new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.PUMPKIN.getDefaultState(), 19).add(Blocks.JACK_O_LANTERN.getDefaultState(), 1))
			)
		);
	}
}
