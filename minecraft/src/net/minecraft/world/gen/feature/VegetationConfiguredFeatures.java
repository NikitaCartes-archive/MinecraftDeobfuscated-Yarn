package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class VegetationConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> BAMBOO_NO_PODZOL = ConfiguredFeatures.register(
		"bamboo_no_podzol", Feature.BAMBOO, new ProbabilityConfig(0.0F)
	);
	public static final RegistryEntry<ConfiguredFeature<ProbabilityConfig, ?>> BAMBOO_SOME_PODZOL = ConfiguredFeatures.register(
		"bamboo_some_podzol", Feature.BAMBOO, new ProbabilityConfig(0.2F)
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> VINES = ConfiguredFeatures.register("vines", Feature.VINES);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_BROWN_MUSHROOM = ConfiguredFeatures.register(
		"patch_brown_mushroom",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BROWN_MUSHROOM)))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_RED_MUSHROOM = ConfiguredFeatures.register(
		"patch_red_mushroom",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_MUSHROOM)))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_SUNFLOWER = ConfiguredFeatures.register(
		"patch_sunflower",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SUNFLOWER)))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_PUMPKIN = ConfiguredFeatures.register(
		"patch_pumpkin",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(
			Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PUMPKIN)), List.of(Blocks.GRASS_BLOCK)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_BERRY_BUSH = ConfiguredFeatures.register(
		"patch_berry_bush",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(
			Feature.SIMPLE_BLOCK,
			new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3)))),
			List.of(Blocks.GRASS_BLOCK)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_TAIGA_GRASS = ConfiguredFeatures.register(
		"patch_taiga_grass",
		Feature.RANDOM_PATCH,
		createRandomPatchFeatureConfig(
			new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.GRASS.getDefaultState(), 1).add(Blocks.FERN.getDefaultState(), 4)), 32
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_GRASS = ConfiguredFeatures.register(
		"patch_grass", Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.GRASS), 32)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_GRASS_JUNGLE = ConfiguredFeatures.register(
		"patch_grass_jungle",
		Feature.RANDOM_PATCH,
		new RandomPatchFeatureConfig(
			32,
			7,
			3,
			PlacedFeatures.createEntry(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.GRASS.getDefaultState(), 3).add(Blocks.FERN.getDefaultState(), 1))
				),
				BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.PODZOL)))
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> SINGLE_PIECE_OF_GRASS = ConfiguredFeatures.register(
		"single_piece_of_grass", Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.GRASS.getDefaultState()))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_DEAD_BUSH = ConfiguredFeatures.register(
		"patch_dead_bush", Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.DEAD_BUSH), 4)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_MELON = ConfiguredFeatures.register(
		"patch_melon",
		Feature.RANDOM_PATCH,
		new RandomPatchFeatureConfig(
			64,
			7,
			3,
			PlacedFeatures.createEntry(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.MELON)),
				BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.noFluid(), BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.GRASS_BLOCK))
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_WATERLILY = ConfiguredFeatures.register(
		"patch_waterlily",
		Feature.RANDOM_PATCH,
		new RandomPatchFeatureConfig(10, 7, 3, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_PAD))))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_TALL_GRASS = ConfiguredFeatures.register(
		"patch_tall_grass",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.TALL_GRASS)))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_LARGE_FERN = ConfiguredFeatures.register(
		"patch_large_fern",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LARGE_FERN)))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_CACTUS = ConfiguredFeatures.register(
		"patch_cactus",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(
			10,
			PlacedFeatures.createEntry(
				Feature.BLOCK_COLUMN,
				BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(Blocks.CACTUS)),
				BlockFilterPlacementModifier.of(BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.wouldSurvive(Blocks.CACTUS.getDefaultState(), BlockPos.ORIGIN)))
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_SUGAR_CANE = ConfiguredFeatures.register(
		"patch_sugar_cane",
		Feature.RANDOM_PATCH,
		new RandomPatchFeatureConfig(
			20,
			4,
			0,
			PlacedFeatures.createEntry(
				Feature.BLOCK_COLUMN,
				BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(2, 4), BlockStateProvider.of(Blocks.SUGAR_CANE)),
				BlockFilterPlacementModifier.of(
					BlockPredicate.allOf(
						BlockPredicate.IS_AIR,
						BlockPredicate.wouldSurvive(Blocks.SUGAR_CANE.getDefaultState(), BlockPos.ORIGIN),
						BlockPredicate.anyOf(
							BlockPredicate.matchingFluids(new BlockPos(1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER),
							BlockPredicate.matchingFluids(new BlockPos(-1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER),
							BlockPredicate.matchingFluids(new BlockPos(0, -1, 1), Fluids.WATER, Fluids.FLOWING_WATER),
							BlockPredicate.matchingFluids(new BlockPos(0, -1, -1), Fluids.WATER, Fluids.FLOWING_WATER)
						)
					)
				)
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_DEFAULT = ConfiguredFeatures.register(
		"flower_default",
		Feature.FLOWER,
		createRandomPatchFeatureConfig(
			new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.POPPY.getDefaultState(), 2).add(Blocks.DANDELION.getDefaultState(), 1)), 64
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_FLOWER_FOREST = ConfiguredFeatures.register(
		"flower_flower_forest",
		Feature.FLOWER,
		new RandomPatchFeatureConfig(
			96,
			6,
			2,
			PlacedFeatures.createEntry(
				Feature.SIMPLE_BLOCK,
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
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_SWAMP = ConfiguredFeatures.register(
		"flower_swamp",
		Feature.FLOWER,
		new RandomPatchFeatureConfig(
			64, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID)))
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_PLAIN = ConfiguredFeatures.register(
		"flower_plain",
		Feature.FLOWER,
		new RandomPatchFeatureConfig(
			64,
			6,
			2,
			PlacedFeatures.createEntry(
				Feature.SIMPLE_BLOCK,
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
						List.of(Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState())
					)
				)
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> FLOWER_MEADOW = ConfiguredFeatures.register(
		"flower_meadow",
		Feature.FLOWER,
		new RandomPatchFeatureConfig(
			96,
			6,
			2,
			PlacedFeatures.createEntry(
				Feature.SIMPLE_BLOCK,
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
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SimpleRandomFeatureConfig, ?>> FOREST_FLOWERS = ConfiguredFeatures.register(
		"forest_flowers",
		Feature.SIMPLE_RANDOM_SELECTOR,
		new SimpleRandomFeatureConfig(
			RegistryEntryList.of(
				PlacedFeatures.createEntry(
					Feature.RANDOM_PATCH,
					ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILAC)))
				),
				PlacedFeatures.createEntry(
					Feature.RANDOM_PATCH,
					ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ROSE_BUSH)))
				),
				PlacedFeatures.createEntry(
					Feature.RANDOM_PATCH,
					ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PEONY)))
				),
				PlacedFeatures.createEntry(
					Feature.NO_BONEMEAL_FLOWER,
					ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))
				)
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> DARK_FOREST_VEGETATION = ConfiguredFeatures.register(
		"dark_forest_vegetation",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(
				new RandomFeatureEntry(PlacedFeatures.createEntry(TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM), 0.025F),
				new RandomFeatureEntry(PlacedFeatures.createEntry(TreeConfiguredFeatures.HUGE_RED_MUSHROOM), 0.05F),
				new RandomFeatureEntry(TreePlacedFeatures.DARK_OAK_CHECKED, 0.6666667F),
				new RandomFeatureEntry(TreePlacedFeatures.BIRCH_CHECKED, 0.2F),
				new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)
			),
			TreePlacedFeatures.OAK_CHECKED
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_FLOWER_FOREST = ConfiguredFeatures.register(
		"trees_flower_forest",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(TreePlacedFeatures.BIRCH_BEES_002, 0.2F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES_002, 0.1F)),
			TreePlacedFeatures.OAK_BEES_002
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> MEADOW_TREES = ConfiguredFeatures.register(
		"meadow_trees",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES, 0.5F)), TreePlacedFeatures.SUPER_BIRCH_BEES)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_TAIGA = ConfiguredFeatures.register(
		"trees_taiga",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)), TreePlacedFeatures.SPRUCE_CHECKED)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_GROVE = ConfiguredFeatures.register(
		"trees_grove",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.PINE_ON_SNOW, 0.33333334F)), TreePlacedFeatures.SPRUCE_ON_SNOW)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_SAVANNA = ConfiguredFeatures.register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.ACACIA_CHECKED, 0.8F)), TreePlacedFeatures.OAK_CHECKED)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> BIRCH_TALL = ConfiguredFeatures.register(
		"birch_tall",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.SUPER_BIRCH_BEES_0002, 0.5F)), TreePlacedFeatures.BIRCH_BEES_0002)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_WINDSWEPT_HILLS = ConfiguredFeatures.register(
		"trees_windswept_hills",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(TreePlacedFeatures.SPRUCE_CHECKED, 0.666F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)),
			TreePlacedFeatures.OAK_CHECKED
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_WATER = ConfiguredFeatures.register(
		"trees_water",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F)), TreePlacedFeatures.OAK_CHECKED)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_BIRCH_AND_OAK = ConfiguredFeatures.register(
		"trees_birch_and_oak",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(TreePlacedFeatures.BIRCH_BEES_0002, 0.2F), new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_BEES_0002, 0.1F)),
			TreePlacedFeatures.OAK_BEES_0002
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_PLAINS = ConfiguredFeatures.register(
		"trees_plains",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(PlacedFeatures.createEntry(TreeConfiguredFeatures.FANCY_OAK_BEES_005), 0.33333334F)),
			PlacedFeatures.createEntry(TreeConfiguredFeatures.OAK_BEES_005)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_SPARSE_JUNGLE = ConfiguredFeatures.register(
		"trees_sparse_jungle",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F), new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.5F)),
			TreePlacedFeatures.JUNGLE_TREE
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_OLD_GROWTH_SPRUCE_TAIGA = ConfiguredFeatures.register(
		"trees_old_growth_spruce_taiga",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(new RandomFeatureEntry(TreePlacedFeatures.MEGA_SPRUCE_CHECKED, 0.33333334F), new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)),
			TreePlacedFeatures.SPRUCE_CHECKED
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_OLD_GROWTH_PINE_TAIGA = ConfiguredFeatures.register(
		"trees_old_growth_pine_taiga",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(
				new RandomFeatureEntry(TreePlacedFeatures.MEGA_SPRUCE_CHECKED, 0.025641026F),
				new RandomFeatureEntry(TreePlacedFeatures.MEGA_PINE_CHECKED, 0.30769232F),
				new RandomFeatureEntry(TreePlacedFeatures.PINE_CHECKED, 0.33333334F)
			),
			TreePlacedFeatures.SPRUCE_CHECKED
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> TREES_JUNGLE = ConfiguredFeatures.register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(
				new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.1F),
				new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.5F),
				new RandomFeatureEntry(TreePlacedFeatures.MEGA_JUNGLE_TREE_CHECKED, 0.33333334F)
			),
			TreePlacedFeatures.JUNGLE_TREE
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> BAMBOO_VEGETATION = ConfiguredFeatures.register(
		"bamboo_vegetation",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(
			List.of(
				new RandomFeatureEntry(TreePlacedFeatures.FANCY_OAK_CHECKED, 0.05F),
				new RandomFeatureEntry(TreePlacedFeatures.JUNGLE_BUSH, 0.15F),
				new RandomFeatureEntry(TreePlacedFeatures.MEGA_JUNGLE_TREE_CHECKED, 0.7F)
			),
			PlacedFeatures.createEntry(PATCH_GRASS_JUNGLE)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomBooleanFeatureConfig, ?>> MUSHROOM_ISLAND_VEGETATION = ConfiguredFeatures.register(
		"mushroom_island_vegetation",
		Feature.RANDOM_BOOLEAN_SELECTOR,
		new RandomBooleanFeatureConfig(
			PlacedFeatures.createEntry(TreeConfiguredFeatures.HUGE_RED_MUSHROOM), PlacedFeatures.createEntry(TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomFeatureConfig, ?>> MANGROVE_VEGETATION = ConfiguredFeatures.register(
		"mangrove_vegetation",
		Feature.RANDOM_SELECTOR,
		new RandomFeatureConfig(List.of(new RandomFeatureEntry(TreePlacedFeatures.TALL_MANGROVE_CHECKED, 0.85F)), TreePlacedFeatures.MANGROVE_CHECKED)
	);

	private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(BlockStateProvider block, int tries) {
		return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(block)));
	}
}
