package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class PileConfiguredFeatures {
	public static final ConfiguredFeature<?, ?> PILE_HAY = ConfiguredFeatures.register(
		"pile_hay", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK)))
	);
	public static final ConfiguredFeature<?, ?> PILE_MELON = ConfiguredFeatures.register(
		"pile_melon", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.MELON)))
	);
	public static final ConfiguredFeature<?, ?> PILE_SNOW = ConfiguredFeatures.register(
		"pile_snow", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.SNOW)))
	);
	public static final ConfiguredFeature<?, ?> PILE_ICE = ConfiguredFeatures.register(
		"pile_ice",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.BLUE_ICE.getDefaultState(), 1).add(Blocks.PACKED_ICE.getDefaultState(), 5))
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = ConfiguredFeatures.register(
		"pile_pumpkin",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.PUMPKIN.getDefaultState(), 19).add(Blocks.JACK_O_LANTERN.getDefaultState(), 1))
				)
			)
	);
}
