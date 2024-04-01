package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class VegetationConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> BAMBOO_NO_PODZOL = ConfiguredFeatures.of("bamboo_no_podzol");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BAMBOO_SOME_PODZOL = ConfiguredFeatures.of("bamboo_some_podzol");
	public static final RegistryKey<ConfiguredFeature<?, ?>> VINES = ConfiguredFeatures.of("vines");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_BROWN_MUSHROOM = ConfiguredFeatures.of("patch_brown_mushroom");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_RED_MUSHROOM = ConfiguredFeatures.of("patch_red_mushroom");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_SUNFLOWER = ConfiguredFeatures.of("patch_sunflower");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_PUMPKIN = ConfiguredFeatures.of("patch_pumpkin");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_POTATO = ConfiguredFeatures.of("patch_potato");
	public static final RegistryKey<ConfiguredFeature<?, ?>> POTATO_FIELD = ConfiguredFeatures.of("potato_field");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PARK_LANE = ConfiguredFeatures.of("park_lane");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PARK_LANE_SURFACE = ConfiguredFeatures.of("park_lane_surface");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_BERRY_BUSH = ConfiguredFeatures.of("patch_berry_bush");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_TAIGA_GRASS = ConfiguredFeatures.of("patch_taiga_grass");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_GRASS = ConfiguredFeatures.of("patch_grass");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_GRASS_JUNGLE = ConfiguredFeatures.of("patch_grass_jungle");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SINGLE_PIECE_OF_GRASS = ConfiguredFeatures.of("single_piece_of_grass");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_DEAD_BUSH = ConfiguredFeatures.of("patch_dead_bush");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_MELON = ConfiguredFeatures.of("patch_melon");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_WATERLILY = ConfiguredFeatures.of("patch_waterlily");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_TALL_GRASS = ConfiguredFeatures.of("patch_tall_grass");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_LARGE_FERN = ConfiguredFeatures.of("patch_large_fern");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_CACTUS = ConfiguredFeatures.of("patch_cactus");
	public static final RegistryKey<ConfiguredFeature<?, ?>> LEAF_PILE = ConfiguredFeatures.of("leaf_pile");
	public static final RegistryKey<ConfiguredFeature<?, ?>> VENOMOUS_COLUMN = ConfiguredFeatures.of("venomous_column");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_SUGAR_CANE = ConfiguredFeatures.of("patch_sugar_cane");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_DEFAULT = ConfiguredFeatures.of("flower_default");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_FLOWER_FOREST = ConfiguredFeatures.of("flower_flower_forest");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_SWAMP = ConfiguredFeatures.of("flower_swamp");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_PLAIN = ConfiguredFeatures.of("flower_plain");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_MEADOW = ConfiguredFeatures.of("flower_meadow");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FLOWER_CHERRY = ConfiguredFeatures.of("flower_cherry");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FOREST_FLOWERS = ConfiguredFeatures.of("forest_flowers");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_FOREST_VEGETATION = ConfiguredFeatures.of("dark_forest_vegetation");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ARBORETUM_TREES = ConfiguredFeatures.of("arboretum_trees");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_FLOWER_FOREST = ConfiguredFeatures.of("trees_flower_forest");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MEADOW_TREES = ConfiguredFeatures.of("meadow_trees");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_TAIGA = ConfiguredFeatures.of("trees_taiga");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_GROVE = ConfiguredFeatures.of("trees_grove");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_SAVANNA = ConfiguredFeatures.of("trees_savanna");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BIRCH_TALL = ConfiguredFeatures.of("birch_tall");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_WINDSWEPT_HILLS = ConfiguredFeatures.of("trees_windswept_hills");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_WATER = ConfiguredFeatures.of("trees_water");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_BIRCH_AND_OAK = ConfiguredFeatures.of("trees_birch_and_oak");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_PLAINS = ConfiguredFeatures.of("trees_plains");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_SPARSE_JUNGLE = ConfiguredFeatures.of("trees_sparse_jungle");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_OLD_GROWTH_SPRUCE_TAIGA = ConfiguredFeatures.of("trees_old_growth_spruce_taiga");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_OLD_GROWTH_PINE_TAIGA = ConfiguredFeatures.of("trees_old_growth_pine_taiga");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TREES_JUNGLE = ConfiguredFeatures.of("trees_jungle");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BAMBOO_VEGETATION = ConfiguredFeatures.of("bamboo_vegetation");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MUSHROOM_ISLAND_VEGETATION = ConfiguredFeatures.of("mushroom_island_vegetation");
	public static final RegistryKey<ConfiguredFeature<?, ?>> MANGROVE_VEGETATION = ConfiguredFeatures.of("mangrove_vegetation");

	private static RandomPatchFeatureConfig createRandomPatchFeatureConfig(BlockStateProvider block, int tries) {
		return ConfiguredFeatures.createRandomPatchFeatureConfig(tries, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(block)));
	}

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.HUGE_RED_MUSHROOM);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.FANCY_OAK_BEES_005);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK_BEES_005);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(PATCH_GRASS_JUNGLE);
		RegistryEntryLookup<PlacedFeature> registryEntryLookup2 = featureRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntry<PlacedFeature> registryEntry6 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.DARK_OAK_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry7 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.BIRCH_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry8 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.FANCY_OAK_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry9 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.BIRCH_BEES_002);
		RegistryEntry<PlacedFeature> registryEntry10 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.FANCY_OAK_BEES_002);
		RegistryEntry<PlacedFeature> registryEntry11 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.FANCY_OAK_BEES);
		RegistryEntry<PlacedFeature> registryEntry12 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.PINE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry13 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.SPRUCE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry14 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.PINE_ON_SNOW);
		RegistryEntry<PlacedFeature> registryEntry15 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.ACACIA_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry16 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.CHERRY_BEES_005);
		RegistryEntry<PlacedFeature> registryEntry17 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.CHERRY_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry18 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.POTATO_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry19 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.MOTHER_POTATO_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry20 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.SUPER_BIRCH_BEES_0002);
		RegistryEntry<PlacedFeature> registryEntry21 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.BIRCH_BEES_0002);
		RegistryEntry<PlacedFeature> registryEntry22 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.FANCY_OAK_BEES_0002);
		RegistryEntry<PlacedFeature> registryEntry23 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.JUNGLE_BUSH);
		RegistryEntry<PlacedFeature> registryEntry24 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.MEGA_SPRUCE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry25 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.MEGA_PINE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry26 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.MEGA_JUNGLE_TREE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry27 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.TALL_MANGROVE_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry28 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.OAK_CHECKED);
		RegistryEntry<PlacedFeature> registryEntry29 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.OAK_BEES_002);
		RegistryEntry<PlacedFeature> registryEntry30 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.SUPER_BIRCH_BEES);
		RegistryEntry<PlacedFeature> registryEntry31 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.SPRUCE_ON_SNOW);
		RegistryEntry<PlacedFeature> registryEntry32 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.OAK_BEES_0002);
		RegistryEntry<PlacedFeature> registryEntry33 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.JUNGLE_TREE);
		RegistryEntry<PlacedFeature> registryEntry34 = registryEntryLookup2.getOrThrow(TreePlacedFeatures.MANGROVE_CHECKED);
		ConfiguredFeatures.register(featureRegisterable, BAMBOO_NO_PODZOL, Feature.BAMBOO, new ProbabilityConfig(0.0F));
		ConfiguredFeatures.register(featureRegisterable, BAMBOO_SOME_PODZOL, Feature.BAMBOO, new ProbabilityConfig(0.2F));
		ConfiguredFeatures.register(featureRegisterable, VINES, Feature.VINES);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_BROWN_MUSHROOM,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BROWN_MUSHROOM)))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_RED_MUSHROOM,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_MUSHROOM)))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_SUNFLOWER,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SUNFLOWER)))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_PUMPKIN,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PUMPKIN)),
				List.of(Blocks.GRASS_BLOCK, Blocks.PEELGRASS_BLOCK, Blocks.CORRUPTED_PEELGRASS_BLOCK)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_POTATO,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						DataPool.<BlockState>builder()
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(0)), 1)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(1)), 2)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(2)), 3)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(3)), 4)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(4)), 5)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(5)), 6)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(6)), 7)
							.add(Blocks.POTATOES.getDefaultState().with(CropBlock.AGE, Integer.valueOf(7)), 8)
							.add(Blocks.POTATO_FLOWER.getDefaultState(), 5)
					)
				),
				List.of(Blocks.PEELGRASS_BLOCK, Blocks.CORRUPTED_PEELGRASS_BLOCK, Blocks.GRAVTATER),
				128
			)
		);
		ConfiguredFeatures.register(featureRegisterable, POTATO_FIELD, Feature.POTATO_FIELD, FeatureConfig.DEFAULT);
		ConfiguredFeatures.register(featureRegisterable, PARK_LANE, Feature.PARK_LANE, FeatureConfig.DEFAULT);
		ConfiguredFeatures.register(featureRegisterable, PARK_LANE_SURFACE, Feature.PARK_LANE_SURFACE, FeatureConfig.DEFAULT);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_BERRY_BUSH,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3)))),
				List.of(Blocks.GRASS_BLOCK, Blocks.PEELGRASS_BLOCK, Blocks.CORRUPTED_PEELGRASS_BLOCK)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_TAIGA_GRASS,
			Feature.RANDOM_PATCH,
			createRandomPatchFeatureConfig(
				new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.SHORT_GRASS.getDefaultState(), 1).add(Blocks.FERN.getDefaultState(), 4)), 32
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable, PATCH_GRASS, Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.SHORT_GRASS), 32)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_GRASS_JUNGLE,
			Feature.RANDOM_PATCH,
			new RandomPatchFeatureConfig(
				32,
				7,
				3,
				PlacedFeatures.createEntry(
					Feature.SIMPLE_BLOCK,
					new SimpleBlockFeatureConfig(
						new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.SHORT_GRASS.getDefaultState(), 3).add(Blocks.FERN.getDefaultState(), 1))
					),
					BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.not(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.PODZOL)))
				)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable, SINGLE_PIECE_OF_GRASS, Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SHORT_GRASS.getDefaultState()))
		);
		ConfiguredFeatures.register(
			featureRegisterable, PATCH_DEAD_BUSH, Feature.RANDOM_PATCH, createRandomPatchFeatureConfig(BlockStateProvider.of(Blocks.DEAD_BUSH), 4)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_MELON,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_WATERLILY,
			Feature.RANDOM_PATCH,
			new RandomPatchFeatureConfig(
				10, 7, 3, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_PAD)))
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_TALL_GRASS,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.TALL_GRASS)))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_LARGE_FERN,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LARGE_FERN)))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_CACTUS,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				10,
				PlacedFeatures.createEntry(
					Feature.BLOCK_COLUMN,
					BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(Blocks.CACTUS)),
					BlockFilterPlacementModifier.of(
						BlockPredicate.bothOf(BlockPredicate.IS_AIR, BlockPredicate.wouldSurvive(Blocks.CACTUS.getDefaultState(), BlockPos.ORIGIN))
					)
				)
			)
		);
		Block block = (Block)Blocks.POTATO_PEELS_BLOCKS.get(DyeColor.LIME);
		ConfiguredFeatures.register(
			featureRegisterable,
			LEAF_PILE,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				10,
				PlacedFeatures.createEntry(
					Feature.BLOCK_COLUMN,
					BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(block)),
					BlockFilterPlacementModifier.of(
						BlockPredicate.bothOf(
							BlockPredicate.matchingBlocks(block, Blocks.AIR), BlockPredicate.not(BlockPredicate.matchingBlocks(new Vec3i(0, -1, 0), Blocks.AIR))
						)
					)
				)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			VENOMOUS_COLUMN,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				30,
				PlacedFeatures.createEntry(
					Feature.BLOCK_COLUMN,
					new BlockColumnFeatureConfig(
						List.of(
							BlockColumnFeatureConfig.createLayer(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(Blocks.GRAVTATER)),
							BlockColumnFeatureConfig.createLayer(ConstantIntProvider.create(1), BlockStateProvider.of(Blocks.VICIOUS_POTATO))
						),
						Direction.UP,
						BlockPredicate.IS_AIR,
						true
					),
					BlockFilterPlacementModifier.of(
						BlockPredicate.bothOf(
							BlockPredicate.matchingBlocks(block, Blocks.AIR), BlockPredicate.not(BlockPredicate.matchingBlocks(new Vec3i(0, -1, 0), Blocks.AIR))
						)
					)
				)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_SUGAR_CANE,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_DEFAULT,
			Feature.FLOWER,
			createRandomPatchFeatureConfig(
				new WeightedBlockStateProvider(DataPool.<BlockState>builder().add(Blocks.POPPY.getDefaultState(), 2).add(Blocks.DANDELION.getDefaultState(), 1)), 64
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_FLOWER_FOREST,
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
								Blocks.LILY_OF_THE_VALLEY.getDefaultState(),
								Blocks.POTATO_FLOWER.getDefaultState()
							)
						)
					)
				)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_SWAMP,
			Feature.FLOWER,
			new RandomPatchFeatureConfig(
				64, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID)))
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_PLAIN,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_MEADOW,
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
								Blocks.SHORT_GRASS.getDefaultState()
							)
						)
					)
				)
			)
		);
		DataPool.Builder<BlockState> builder = DataPool.builder();

		for (int i = 1; i <= 4; i++) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				builder.add(Blocks.PINK_PETALS.getDefaultState().with(FlowerbedBlock.FLOWER_AMOUNT, Integer.valueOf(i)).with(FlowerbedBlock.FACING, direction), 1);
			}
		}

		ConfiguredFeatures.register(
			featureRegisterable,
			FLOWER_CHERRY,
			Feature.FLOWER,
			new RandomPatchFeatureConfig(
				96, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(builder)))
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			FOREST_FLOWERS,
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
						Feature.RANDOM_PATCH,
						ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.POTATO_FLOWER)))
					),
					PlacedFeatures.createEntry(
						Feature.NO_BONEMEAL_FLOWER,
						ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))
					)
				)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			DARK_FOREST_VEGETATION,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(
				List.of(
					new RandomFeatureEntry(PlacedFeatures.createEntry(registryEntry), 0.025F),
					new RandomFeatureEntry(PlacedFeatures.createEntry(registryEntry2), 0.05F),
					new RandomFeatureEntry(registryEntry6, 0.6666667F),
					new RandomFeatureEntry(registryEntry7, 0.2F),
					new RandomFeatureEntry(registryEntry8, 0.1F)
				),
				registryEntry28
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			ARBORETUM_TREES,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(
				List.of(
					new RandomFeatureEntry(registryEntry28, 0.05F),
					new RandomFeatureEntry(registryEntry7, 0.05F),
					new RandomFeatureEntry(registryEntry8, 0.05F),
					new RandomFeatureEntry(registryEntry13, 0.05F),
					new RandomFeatureEntry(registryEntry12, 0.05F),
					new RandomFeatureEntry(registryEntry15, 0.05F),
					new RandomFeatureEntry(registryEntry17, 0.02F),
					new RandomFeatureEntry(registryEntry18, 0.02F),
					new RandomFeatureEntry(registryEntry23, 0.02F),
					new RandomFeatureEntry(registryEntry33, 0.05F),
					new RandomFeatureEntry(registryEntry34, 0.02F),
					new RandomFeatureEntry(PlacedFeatures.createEntry(registryEntry), 0.025F),
					new RandomFeatureEntry(PlacedFeatures.createEntry(registryEntry2), 0.025F),
					new RandomFeatureEntry(registryEntry16, 0.02F),
					new RandomFeatureEntry(registryEntry20, 0.05F),
					new RandomFeatureEntry(registryEntry21, 0.05F),
					new RandomFeatureEntry(registryEntry22, 0.05F),
					new RandomFeatureEntry(registryEntry29, 0.05F),
					new RandomFeatureEntry(registryEntry30, 0.05F),
					new RandomFeatureEntry(registryEntry31, 0.05F),
					new RandomFeatureEntry(registryEntry32, 0.05F),
					new RandomFeatureEntry(registryEntry27, 0.01F),
					new RandomFeatureEntry(registryEntry6, 0.01F),
					new RandomFeatureEntry(registryEntry26, 0.01F),
					new RandomFeatureEntry(registryEntry19, 0.003F)
				),
				registryEntry28
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_FLOWER_FOREST,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry9, 0.2F), new RandomFeatureEntry(registryEntry10, 0.1F)), registryEntry29)
		);
		ConfiguredFeatures.register(
			featureRegisterable, MEADOW_TREES, Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry11, 0.5F)), registryEntry30)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_TAIGA,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry12, 0.33333334F)), registryEntry13)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_GROVE,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry14, 0.33333334F)), registryEntry31)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_SAVANNA,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry15, 0.8F)), registryEntry28)
		);
		ConfiguredFeatures.register(
			featureRegisterable, BIRCH_TALL, Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry20, 0.5F)), registryEntry21)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_WINDSWEPT_HILLS,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry13, 0.666F), new RandomFeatureEntry(registryEntry8, 0.1F)), registryEntry28)
		);
		ConfiguredFeatures.register(
			featureRegisterable, TREES_WATER, Feature.RANDOM_SELECTOR, new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry8, 0.1F)), registryEntry28)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_BIRCH_AND_OAK,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry21, 0.2F), new RandomFeatureEntry(registryEntry22, 0.1F)), registryEntry32)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_PLAINS,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(PlacedFeatures.createEntry(registryEntry3), 0.33333334F)), PlacedFeatures.createEntry(registryEntry4))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_SPARSE_JUNGLE,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry8, 0.1F), new RandomFeatureEntry(registryEntry23, 0.5F)), registryEntry33)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_OLD_GROWTH_SPRUCE_TAIGA,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry24, 0.33333334F), new RandomFeatureEntry(registryEntry12, 0.33333334F)), registryEntry13)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_OLD_GROWTH_PINE_TAIGA,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(
				List.of(
					new RandomFeatureEntry(registryEntry24, 0.025641026F),
					new RandomFeatureEntry(registryEntry25, 0.30769232F),
					new RandomFeatureEntry(registryEntry12, 0.33333334F)
				),
				registryEntry13
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			TREES_JUNGLE,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(
				List.of(new RandomFeatureEntry(registryEntry8, 0.1F), new RandomFeatureEntry(registryEntry23, 0.5F), new RandomFeatureEntry(registryEntry26, 0.33333334F)),
				registryEntry33
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			BAMBOO_VEGETATION,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(
				List.of(new RandomFeatureEntry(registryEntry8, 0.05F), new RandomFeatureEntry(registryEntry23, 0.15F), new RandomFeatureEntry(registryEntry26, 0.7F)),
				PlacedFeatures.createEntry(registryEntry5)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			MUSHROOM_ISLAND_VEGETATION,
			Feature.RANDOM_BOOLEAN_SELECTOR,
			new RandomBooleanFeatureConfig(PlacedFeatures.createEntry(registryEntry2), PlacedFeatures.createEntry(registryEntry))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			MANGROVE_VEGETATION,
			Feature.RANDOM_SELECTOR,
			new RandomFeatureConfig(List.of(new RandomFeatureEntry(registryEntry27, 0.85F)), registryEntry34)
		);
	}
}
