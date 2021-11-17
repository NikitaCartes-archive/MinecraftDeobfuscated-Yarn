package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.BlockFilterPlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class VegetationConfiguredFeatures {
	public static final ConfiguredFeature<ProbabilityConfig, ?> BAMBOO_NO_PODZOL = ConfiguredFeatures.register(
		"bamboo_no_podzol", Feature.BAMBOO.configure(new ProbabilityConfig(0.0F))
	);
	public static final ConfiguredFeature<ProbabilityConfig, ?> BAMBOO_SOME_PODZOL = ConfiguredFeatures.register(
		"bamboo_some_podzol", Feature.BAMBOO.configure(new ProbabilityConfig(0.2F))
	);
	public static final ConfiguredFeature<DefaultFeatureConfig, ?> VINES = ConfiguredFeatures.register("vines", Feature.VINES.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> PATCH_BROWN_MUSHROOM = ConfiguredFeatures.register(
		"patch_brown_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(
					Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BROWN_MUSHROOM)))
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_RED_MUSHROOM = ConfiguredFeatures.register(
		"patch_red_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_MUSHROOM))))
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_SUNFLOWER = ConfiguredFeatures.register(
		"patch_sunflower",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SUNFLOWER))))
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_PUMPKIN = ConfiguredFeatures.register(
		"patch_pumpkin",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(
					Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PUMPKIN))), List.of(Blocks.GRASS_BLOCK)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_BUSH = ConfiguredFeatures.register(
		"patch_berry_bush",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(
					Feature.SIMPLE_BLOCK
						.configure(
							new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3))))
						),
					List.of(Blocks.GRASS_BLOCK)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TAIGA_GRASS = ConfiguredFeatures.register(
		"patch_taiga_grass",
		Feature.RANDOM_PATCH
			.configure(
				createRandomPatchFeatureConfig(
					new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.GRASS.getDefaultState(), 1).add(Blocks.FERN.getDefaultState(), 4)), 32
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_GRASS = ConfiguredFeatures.register(
		"patch_grass", Feature.RANDOM_PATCH.configure(createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.GRASS), 32))
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_GRASS_JUNGLE = ConfiguredFeatures.register(
		"patch_grass_jungle",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					32,
					7,
					3,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.GRASS.getDefaultState(), 3).add(Blocks.FERN.getDefaultState(), 1))
								)
							)
							.withBlockPredicateFilter(
								BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.not(BlockPredicate.matchingBlock(Blocks.PODZOL, new BlockPos(0, -1, 0))))
							)
				)
			)
	);
	public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> SINGLE_PIECE_OF_GRASS = ConfiguredFeatures.register(
		"single_piece_of_grass", Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.GRASS.getDefaultState())))
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_DEAD_BUSH = ConfiguredFeatures.register(
		"patch_dead_bush", Feature.RANDOM_PATCH.configure(createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.DEAD_BUSH), 4))
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_MELON = ConfiguredFeatures.register(
		"patch_melon",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					64,
					7,
					3,
					() -> Feature.SIMPLE_BLOCK
							.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.MELON)))
							.withBlockPredicateFilter(BlockPredicate.bothOf(BlockPredicate.replaceable(), BlockPredicate.matchingBlock(Blocks.GRASS_BLOCK, new BlockPos(0, -1, 0))))
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_WATERLILY = ConfiguredFeatures.register(
		"patch_waterlily",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					10, 7, 3, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_PAD))).withInAirFilter()
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_TALL_GRASS = ConfiguredFeatures.register(
		"patch_tall_grass",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.TALL_GRASS))))
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_LARGE_FERN = ConfiguredFeatures.register(
		"patch_large_fern",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LARGE_FERN))))
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS = ConfiguredFeatures.register(
		"patch_cactus",
		Feature.RANDOM_PATCH
			.configure(
				ConfiguredFeatures.createRandomPatchFeatureConfig(
					10,
					Feature.BLOCK_COLUMN
						.configure(BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(Blocks.CACTUS)))
						.withPlacement(
							BlockFilterPlacementModifier.of(
								BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.wouldSurvive(Blocks.CACTUS.getDefaultState(), BlockPos.ORIGIN))
							)
						)
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PATCH_SUGAR_CANE = ConfiguredFeatures.register(
		"patch_sugar_cane",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					20,
					4,
					0,
					() -> Feature.BLOCK_COLUMN
							.configure(BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(2, 4), BlockStateProvider.of(Blocks.SUGAR_CANE)))
							.withPlacement(
								BlockFilterPlacementModifier.of(
									BlockPredicate.allOf(
										BlockPredicate.matchingBlock(Blocks.AIR, BlockPos.ORIGIN),
										BlockPredicate.wouldSurvive(Blocks.SUGAR_CANE.getDefaultState(), BlockPos.ORIGIN),
										BlockPredicate.anyOf(
											BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(1, -1, 0)),
											BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(-1, -1, 0)),
											BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, 1)),
											BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, -1))
										)
									)
								)
							)
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_DEFAULT = ConfiguredFeatures.register(
		"flower_default",
		Feature.FLOWER
			.configure(
				createRandomPatchFeatureConfig(
					new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.POPPY.getDefaultState(), 2).add(Blocks.DANDELION.getDefaultState(), 1)), 64
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_FLOWER_FOREST = ConfiguredFeatures.register(
		"flower_flower_forest",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					96,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new NoiseBlockStateProvider(
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(0, 1.0),
										0.020833334F,
										List.of(
											Blocks.DANDELION.getDefaultState(),
											Blocks.POPPY.getDefaultState(),
											Blocks.ALLIUM.getDefaultState(),
											Blocks.AZURE_BLUET.getDefaultState(),
											Blocks.RED_TULIP.getDefaultState(),
											Blocks.ORANGE_TULIP.getDefaultState(),
											Blocks.WHITE_TULIP.getDefaultState(),
											Blocks.PINK_TULIP.getDefaultState(),
											Blocks.OXEYE_DAISY.getDefaultState(),
											Blocks.CORNFLOWER.getDefaultState(),
											Blocks.LILY_OF_THE_VALLEY.getDefaultState()
										)
									)
								)
							)
							.withInAirFilter()
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_SWAMP = ConfiguredFeatures.register(
		"flower_swamp",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					64, 6, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID))).withInAirFilter()
				)
			)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN = ConfiguredFeatures.register(
		"flower_plain",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					64,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new NoiseThresholdBlockStateProvider(
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(0, 1.0),
										0.005F,
										-0.8F,
										0.33333334F,
										Blocks.DANDELION.getDefaultState(),
										List.of(
											Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()
										),
										List.of(
											Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()
										)
									)
								)
							)
							.withInAirFilter()
				)
			)
	);
	public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> FLOWER_MEADOW = ConfiguredFeatures.register(
		"flower_meadow",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					96,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new DualNoiseBlockStateProvider(
										new Range(1, 3),
										new DoublePerlinNoiseSampler.NoiseParameters(-10, 1.0),
										1.0F,
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0),
										1.0F,
										List.of(
											Blocks.TALL_GRASS.getDefaultState(),
											Blocks.ALLIUM.getDefaultState(),
											Blocks.POPPY.getDefaultState(),
											Blocks.AZURE_BLUET.getDefaultState(),
											Blocks.DANDELION.getDefaultState(),
											Blocks.CORNFLOWER.getDefaultState(),
											Blocks.OXEYE_DAISY.getDefaultState(),
											Blocks.GRASS.getDefaultState()
										)
									)
								)
							)
							.withInAirFilter()
				)
			)
	);
	public static final ConfiguredFeature<SimpleRandomFeatureConfig, ?> FOREST_FLOWERS = ConfiguredFeatures.register(
		"forest_flowers",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					List.of(
						(Supplier)() -> Feature.RANDOM_PATCH
								.configure(
									ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILAC))))
								)
								.withPlacement(),
						(Supplier)() -> Feature.RANDOM_PATCH
								.configure(
									ConfiguredFeatures.createRandomPatchFeatureConfig(
										Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ROSE_BUSH)))
									)
								)
								.withPlacement(),
						(Supplier)() -> Feature.RANDOM_PATCH
								.configure(
									ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PEONY))))
								)
								.withPlacement(),
						(Supplier)() -> Feature.NO_BONEMEAL_FLOWER
								.configure(
									ConfiguredFeatures.createRandomPatchFeatureConfig(
										Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))
									)
								)
								.withPlacement()
					)
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> DARK_FOREST_VEGETATION = ConfiguredFeatures.register(
		"dark_forest_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(
						new RandomFeatureEntry(TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM.withPlacement(), 0.025F),
						new RandomFeatureEntry(TreeConfiguredFeatures.HUGE_RED_MUSHROOM.withPlacement(), 0.05F),
						new RandomFeatureEntry(TreePlacedFeatures.DARK_OAK_CHECKED, 0.6666667F),
						new RandomFeatureEntry(TreePlacedFeatures.BIRCH_CHECKED, 0.2F),
						new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)
					),
					TreePlacedFeatures.OAK_CHECKED
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_FLOWER_FOREST = ConfiguredFeatures.register(
		"trees_flower_forest",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreePlacedFeatures.BIRCH_BEES_002, 0.2F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES_002, 0.1F)),
					TreePlacedFeatures.OAK_BEES_002
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> MEADOW_TREES = ConfiguredFeatures.register(
		"meadow_trees",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES, 0.5F)), TreePlacedFeatures.SUPER_BIRCH_BEES))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_TAIGA = ConfiguredFeatures.register(
		"trees_taiga",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)), TreePlacedFeatures.SPRUCE_CHECKED))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_GROVE = ConfiguredFeatures.register(
		"trees_grove",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.PINE_ON_SNOW, 0.33333334F)), TreePlacedFeatures.SPRUCE_ON_SNOW))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_SAVANNA = ConfiguredFeatures.register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.ACACIA_CHECKED, 0.8F)), TreePlacedFeatures.OAK_CHECKED))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> BIRCH_TALL = ConfiguredFeatures.register(
		"birch_tall",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.SUPER_BIRCH_BEES_0002, 0.5F)), TreePlacedFeatures.BIRCH_BEES_0002))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_WINDSWEPT_HILLS = ConfiguredFeatures.register(
		"trees_windswept_hills",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreePlacedFeatures.SPRUCE_CHECKED, 0.666F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)),
					TreePlacedFeatures.OAK_CHECKED
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_WATER = ConfiguredFeatures.register(
		"trees_water",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)), TreePlacedFeatures.OAK_CHECKED))
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_BIRCH_AND_OAK = ConfiguredFeatures.register(
		"trees_birch_and_oak",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreePlacedFeatures.BIRCH_BEES_0002, 0.2F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES_0002, 0.1F)),
					TreePlacedFeatures.OAK_BEES_0002
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_PLAINS = ConfiguredFeatures.register(
		"trees_plains",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreeConfiguredFeatures.FANCY_OAK_BEES_005.withPlacement(), 0.33333334F)),
					TreeConfiguredFeatures.OAK_BEES_005.withPlacement()
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_SPARSE_JUNGLE = ConfiguredFeatures.register(
		"trees_sparse_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F), new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.5F)),
					TreePlacedFeatures.JUNGLE_TREE
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_OLD_GROWTH_SPRUCE_TAIGA = ConfiguredFeatures.register(
		"trees_old_growth_spruce_taiga",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(new RandomFeatureEntry(TreePlacedFeatures.MEGA_SPRUCE_CHECKED, 0.33333334F), new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)),
					TreePlacedFeatures.SPRUCE_CHECKED
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_OLD_GROWTH_PINE_TAIGA = ConfiguredFeatures.register(
		"trees_old_growth_pine_taiga",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(
						new RandomFeatureEntry(TreePlacedFeatures.MEGA_SPRUCE_CHECKED, 0.025641026F),
						new RandomFeatureEntry(TreePlacedFeatures.MEGA_PINE_CHECKED, 0.30769232F),
						new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)
					),
					TreePlacedFeatures.SPRUCE_CHECKED
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> TREES_JUNGLE = ConfiguredFeatures.register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(
						new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F),
						new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.5F),
						new RandomFeatureEntry(TreePlacedFeatures.MEGA_JUNGLE_TREE_CHECKED, 0.33333334F)
					),
					TreePlacedFeatures.JUNGLE_TREE
				)
			)
	);
	public static final ConfiguredFeature<RandomFeatureConfig, ?> BAMBOO_VEGETATION = ConfiguredFeatures.register(
		"bamboo_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					List.of(
						new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.05F),
						new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.15F),
						new RandomFeatureEntry(TreePlacedFeatures.MEGA_JUNGLE_TREE_CHECKED, 0.7F)
					),
					PATCH_GRASS_JUNGLE.withPlacement()
				)
			)
	);
	public static final ConfiguredFeature<RandomBooleanFeatureConfig, ?> MUSHROOM_ISLAND_VEGETATION = ConfiguredFeatures.register(
		"mushroom_island_vegetation",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> TreeConfiguredFeatures.HUGE_RED_MUSHROOM, () -> TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM))
	);

	private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(BlockStateProvider block, int tries) {
		return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(block)).withInAirFilter());
	}
}