package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseBasedCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseThresholdCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceWaterDepthFilterPlacementModifier;

public class VegetationPlacedFeatures {
	public static final RegistryKey<PlacedFeature> BAMBOO_LIGHT = PlacedFeatures.of("bamboo_light");
	public static final RegistryKey<PlacedFeature> BAMBOO = PlacedFeatures.of("bamboo");
	public static final RegistryKey<PlacedFeature> VINES = PlacedFeatures.of("vines");
	public static final RegistryKey<PlacedFeature> PATCH_SUNFLOWER = PlacedFeatures.of("patch_sunflower");
	public static final RegistryKey<PlacedFeature> PATCH_PUMPKIN = PlacedFeatures.of("patch_pumpkin");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_PLAIN = PlacedFeatures.of("patch_grass_plain");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_FOREST = PlacedFeatures.of("patch_grass_forest");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_BADLANDS = PlacedFeatures.of("patch_grass_badlands");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_SAVANNA = PlacedFeatures.of("patch_grass_savanna");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_NORMAL = PlacedFeatures.of("patch_grass_normal");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_TAIGA_2 = PlacedFeatures.of("patch_grass_taiga_2");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_TAIGA = PlacedFeatures.of("patch_grass_taiga");
	public static final RegistryKey<PlacedFeature> PATCH_GRASS_JUNGLE = PlacedFeatures.of("patch_grass_jungle");
	public static final RegistryKey<PlacedFeature> GRASS_BONEMEAL = PlacedFeatures.of("grass_bonemeal");
	public static final RegistryKey<PlacedFeature> PATCH_DEAD_BUSH_2 = PlacedFeatures.of("patch_dead_bush_2");
	public static final RegistryKey<PlacedFeature> PATCH_DEAD_BUSH = PlacedFeatures.of("patch_dead_bush");
	public static final RegistryKey<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS = PlacedFeatures.of("patch_dead_bush_badlands");
	public static final RegistryKey<PlacedFeature> PATCH_MELON = PlacedFeatures.of("patch_melon");
	public static final RegistryKey<PlacedFeature> PATCH_MELON_SPARSE = PlacedFeatures.of("patch_melon_sparse");
	public static final RegistryKey<PlacedFeature> PATCH_BERRY_COMMON = PlacedFeatures.of("patch_berry_common");
	public static final RegistryKey<PlacedFeature> PATCH_BERRY_RARE = PlacedFeatures.of("patch_berry_rare");
	public static final RegistryKey<PlacedFeature> PATCH_WATERLILY = PlacedFeatures.of("patch_waterlily");
	public static final RegistryKey<PlacedFeature> PATCH_TALL_GRASS_2 = PlacedFeatures.of("patch_tall_grass_2");
	public static final RegistryKey<PlacedFeature> PATCH_TALL_GRASS = PlacedFeatures.of("patch_tall_grass");
	public static final RegistryKey<PlacedFeature> PATCH_LARGE_FERN = PlacedFeatures.of("patch_large_fern");
	public static final RegistryKey<PlacedFeature> PATCH_CACTUS_DESERT = PlacedFeatures.of("patch_cactus_desert");
	public static final RegistryKey<PlacedFeature> PATCH_CACTUS_DECORATED = PlacedFeatures.of("patch_cactus_decorated");
	public static final RegistryKey<PlacedFeature> PATCH_SUGAR_CANE_SWAMP = PlacedFeatures.of("patch_sugar_cane_swamp");
	public static final RegistryKey<PlacedFeature> PATCH_SUGAR_CANE_DESERT = PlacedFeatures.of("patch_sugar_cane_desert");
	public static final RegistryKey<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS = PlacedFeatures.of("patch_sugar_cane_badlands");
	public static final RegistryKey<PlacedFeature> PATCH_SUGAR_CANE = PlacedFeatures.of("patch_sugar_cane");
	public static final RegistryKey<PlacedFeature> BROWN_MUSHROOM_NETHER = PlacedFeatures.of("brown_mushroom_nether");
	public static final RegistryKey<PlacedFeature> RED_MUSHROOM_NETHER = PlacedFeatures.of("red_mushroom_nether");
	public static final RegistryKey<PlacedFeature> BROWN_MUSHROOM_NORMAL = PlacedFeatures.of("brown_mushroom_normal");
	public static final RegistryKey<PlacedFeature> RED_MUSHROOM_NORMAL = PlacedFeatures.of("red_mushroom_normal");
	public static final RegistryKey<PlacedFeature> BROWN_MUSHROOM_TAIGA = PlacedFeatures.of("brown_mushroom_taiga");
	public static final RegistryKey<PlacedFeature> RED_MUSHROOM_TAIGA = PlacedFeatures.of("red_mushroom_taiga");
	public static final RegistryKey<PlacedFeature> BROWN_MUSHROOM_OLD_GROWTH = PlacedFeatures.of("brown_mushroom_old_growth");
	public static final RegistryKey<PlacedFeature> RED_MUSHROOM_OLD_GROWTH = PlacedFeatures.of("red_mushroom_old_growth");
	public static final RegistryKey<PlacedFeature> BROWN_MUSHROOM_SWAMP = PlacedFeatures.of("brown_mushroom_swamp");
	public static final RegistryKey<PlacedFeature> RED_MUSHROOM_SWAMP = PlacedFeatures.of("red_mushroom_swamp");
	public static final RegistryKey<PlacedFeature> FLOWER_WARM = PlacedFeatures.of("flower_warm");
	public static final RegistryKey<PlacedFeature> FLOWER_DEFAULT = PlacedFeatures.of("flower_default");
	public static final RegistryKey<PlacedFeature> FLOWER_FLOWER_FOREST = PlacedFeatures.of("flower_flower_forest");
	public static final RegistryKey<PlacedFeature> FLOWER_SWAMP = PlacedFeatures.of("flower_swamp");
	public static final RegistryKey<PlacedFeature> FLOWER_PLAIN = PlacedFeatures.of("flower_plains");
	public static final RegistryKey<PlacedFeature> FLOWER_MEADOW = PlacedFeatures.of("flower_meadow");
	public static final RegistryKey<PlacedFeature> FLOWER_CHERRY = PlacedFeatures.of("flower_cherry");
	public static final RegistryKey<PlacedFeature> TREES_PLAINS = PlacedFeatures.of("trees_plains");
	public static final RegistryKey<PlacedFeature> DARK_FOREST_VEGETATION = PlacedFeatures.of("dark_forest_vegetation");
	public static final RegistryKey<PlacedFeature> PALE_GARDEN_VEGETATION = PlacedFeatures.of("pale_garden_vegetation");
	public static final RegistryKey<PlacedFeature> FLOWER_FOREST_FLOWERS = PlacedFeatures.of("flower_forest_flowers");
	public static final RegistryKey<PlacedFeature> FOREST_FLOWERS = PlacedFeatures.of("forest_flowers");
	public static final RegistryKey<PlacedFeature> TREES_FLOWER_FOREST = PlacedFeatures.of("trees_flower_forest");
	public static final RegistryKey<PlacedFeature> TREES_MEADOW = PlacedFeatures.of("trees_meadow");
	public static final RegistryKey<PlacedFeature> TREES_CHERRY = PlacedFeatures.of("trees_cherry");
	public static final RegistryKey<PlacedFeature> TREES_TAIGA = PlacedFeatures.of("trees_taiga");
	public static final RegistryKey<PlacedFeature> TREES_GROVE = PlacedFeatures.of("trees_grove");
	public static final RegistryKey<PlacedFeature> TREES_BADLANDS = PlacedFeatures.of("trees_badlands");
	public static final RegistryKey<PlacedFeature> TREES_SNOWY = PlacedFeatures.of("trees_snowy");
	public static final RegistryKey<PlacedFeature> TREES_SWAMP = PlacedFeatures.of("trees_swamp");
	public static final RegistryKey<PlacedFeature> TREES_WINDSWEPT_SAVANNA = PlacedFeatures.of("trees_windswept_savanna");
	public static final RegistryKey<PlacedFeature> TREES_SAVANNA = PlacedFeatures.of("trees_savanna");
	public static final RegistryKey<PlacedFeature> BIRCH_TALL = PlacedFeatures.of("birch_tall");
	public static final RegistryKey<PlacedFeature> TREES_BIRCH = PlacedFeatures.of("trees_birch");
	public static final RegistryKey<PlacedFeature> TREES_WINDSWEPT_FOREST = PlacedFeatures.of("trees_windswept_forest");
	public static final RegistryKey<PlacedFeature> TREES_WINDSWEPT_HILLS = PlacedFeatures.of("trees_windswept_hills");
	public static final RegistryKey<PlacedFeature> TREES_WATER = PlacedFeatures.of("trees_water");
	public static final RegistryKey<PlacedFeature> TREES_BIRCH_AND_OAK = PlacedFeatures.of("trees_birch_and_oak");
	public static final RegistryKey<PlacedFeature> TREES_SPARSE_JUNGLE = PlacedFeatures.of("trees_sparse_jungle");
	public static final RegistryKey<PlacedFeature> TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacedFeatures.of("trees_old_growth_spruce_taiga");
	public static final RegistryKey<PlacedFeature> TREES_OLD_GROWTH_PINE_TAIGA = PlacedFeatures.of("trees_old_growth_pine_taiga");
	public static final RegistryKey<PlacedFeature> TREES_JUNGLE = PlacedFeatures.of("trees_jungle");
	public static final RegistryKey<PlacedFeature> BAMBOO_VEGETATION = PlacedFeatures.of("bamboo_vegetation");
	public static final RegistryKey<PlacedFeature> MUSHROOM_ISLAND_VEGETATION = PlacedFeatures.of("mushroom_island_vegetation");
	public static final RegistryKey<PlacedFeature> TREES_MANGROVE = PlacedFeatures.of("trees_mangrove");
	private static final PlacementModifier NOT_IN_SURFACE_WATER_MODIFIER = SurfaceWaterDepthFilterPlacementModifier.of(0);

	public static List<PlacementModifier> modifiers(int count) {
		return List.of(CountPlacementModifier.of(count), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
	}

	private static List<PlacementModifier> mushroomModifiers(int chance, @Nullable PlacementModifier modifier) {
		Builder<PlacementModifier> builder = ImmutableList.builder();
		if (modifier != null) {
			builder.add(modifier);
		}

		if (chance != 0) {
			builder.add(RarityFilterPlacementModifier.of(chance));
		}

		builder.add(SquarePlacementModifier.of());
		builder.add(PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP);
		builder.add(BiomePlacementModifier.of());
		return builder.build();
	}

	private static Builder<PlacementModifier> treeModifiersBuilder(PlacementModifier countModifier) {
		return ImmutableList.<PlacementModifier>builder()
			.add(countModifier)
			.add(SquarePlacementModifier.of())
			.add(NOT_IN_SURFACE_WATER_MODIFIER)
			.add(PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP)
			.add(BiomePlacementModifier.of());
	}

	public static List<PlacementModifier> treeModifiers(PlacementModifier modifier) {
		return treeModifiersBuilder(modifier).build();
	}

	public static List<PlacementModifier> treeModifiersWithWouldSurvive(PlacementModifier modifier, Block block) {
		return treeModifiersBuilder(modifier).add(BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN))).build();
	}

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.BAMBOO_NO_PODZOL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.BAMBOO_SOME_PODZOL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.VINES);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_SUNFLOWER);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_PUMPKIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_TAIGA_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_GRASS_JUNGLE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.SINGLE_PIECE_OF_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_DEAD_BUSH);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_MELON);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BERRY_BUSH);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_WATERLILY);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry14 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_TALL_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry15 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_LARGE_FERN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry16 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_CACTUS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry17 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_SUGAR_CANE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry18 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry19 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_RED_MUSHROOM);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry20 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_DEFAULT);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry21 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry22 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_SWAMP);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry23 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_PLAIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry24 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_MEADOW);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry25 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_CHERRY);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry26 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_PLAINS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry27 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.DARK_FOREST_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry28 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PALE_GARDEN_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry29 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FOREST_FLOWERS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry30 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_FLOWER_FOREST);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry31 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.MEADOW_TREES);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry32 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_TAIGA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry33 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_GROVE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry34 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry35 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SPRUCE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry36 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.CHERRY_BEES_005);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry37 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SWAMP_OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry38 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_SAVANNA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry39 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.BIRCH_TALL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry40 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.BIRCH_BEES_0002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry41 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_WINDSWEPT_HILLS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry42 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_WATER);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry43 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_BIRCH_AND_OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry44 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_SPARSE_JUNGLE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry45 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry46 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry47 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.TREES_JUNGLE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry48 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.BAMBOO_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry49 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.MUSHROOM_ISLAND_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry50 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.MANGROVE_VEGETATION);
		PlacedFeatures.register(
			featureRegisterable,
			BAMBOO_LIGHT,
			registryEntry,
			RarityFilterPlacementModifier.of(4),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			BAMBOO,
			registryEntry2,
			NoiseBasedCountPlacementModifier.of(160, 80.0, 0.3),
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			VINES,
			registryEntry3,
			CountPlacementModifier.of(127),
			SquarePlacementModifier.of(),
			HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(100)),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_SUNFLOWER,
			registryEntry4,
			RarityFilterPlacementModifier.of(3),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_PUMPKIN,
			registryEntry5,
			RarityFilterPlacementModifier.of(300),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_GRASS_PLAIN,
			registryEntry6,
			NoiseThresholdCountPlacementModifier.of(-0.8, 5, 10),
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_GRASS_FOREST, registryEntry6, modifiers(2));
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_GRASS_BADLANDS,
			registryEntry6,
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_GRASS_SAVANNA, registryEntry6, modifiers(20));
		PlacedFeatures.register(featureRegisterable, PATCH_GRASS_NORMAL, registryEntry6, modifiers(5));
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_GRASS_TAIGA_2,
			registryEntry7,
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_GRASS_TAIGA, registryEntry7, modifiers(7));
		PlacedFeatures.register(featureRegisterable, PATCH_GRASS_JUNGLE, registryEntry8, modifiers(25));
		PlacedFeatures.register(featureRegisterable, GRASS_BONEMEAL, registryEntry9, PlacedFeatures.isAir());
		PlacedFeatures.register(featureRegisterable, PATCH_DEAD_BUSH_2, registryEntry10, modifiers(2));
		PlacedFeatures.register(
			featureRegisterable, PATCH_DEAD_BUSH, registryEntry10, SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_DEAD_BUSH_BADLANDS, registryEntry10, modifiers(20));
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_MELON,
			registryEntry11,
			RarityFilterPlacementModifier.of(6),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_MELON_SPARSE,
			registryEntry11,
			RarityFilterPlacementModifier.of(64),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_BERRY_COMMON,
			registryEntry12,
			RarityFilterPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_BERRY_RARE,
			registryEntry12,
			RarityFilterPlacementModifier.of(384),
			SquarePlacementModifier.of(),
			PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_WATERLILY, registryEntry13, modifiers(4));
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_TALL_GRASS_2,
			registryEntry14,
			NoiseThresholdCountPlacementModifier.of(-0.8, 0, 7),
			RarityFilterPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_TALL_GRASS,
			registryEntry14,
			RarityFilterPlacementModifier.of(5),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_LARGE_FERN,
			registryEntry15,
			RarityFilterPlacementModifier.of(5),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_CACTUS_DESERT,
			registryEntry16,
			RarityFilterPlacementModifier.of(6),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_CACTUS_DECORATED,
			registryEntry16,
			RarityFilterPlacementModifier.of(13),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_SUGAR_CANE_SWAMP,
			registryEntry17,
			RarityFilterPlacementModifier.of(3),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_SUGAR_CANE_DESERT,
			registryEntry17,
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_SUGAR_CANE_BADLANDS,
			registryEntry17,
			RarityFilterPlacementModifier.of(5),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PATCH_SUGAR_CANE,
			registryEntry17,
			RarityFilterPlacementModifier.of(6),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			BROWN_MUSHROOM_NETHER,
			registryEntry18,
			RarityFilterPlacementModifier.of(2),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			RED_MUSHROOM_NETHER,
			registryEntry19,
			RarityFilterPlacementModifier.of(2),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, BROWN_MUSHROOM_NORMAL, registryEntry18, mushroomModifiers(256, null));
		PlacedFeatures.register(featureRegisterable, RED_MUSHROOM_NORMAL, registryEntry19, mushroomModifiers(512, null));
		PlacedFeatures.register(featureRegisterable, BROWN_MUSHROOM_TAIGA, registryEntry18, mushroomModifiers(4, null));
		PlacedFeatures.register(featureRegisterable, RED_MUSHROOM_TAIGA, registryEntry19, mushroomModifiers(256, null));
		PlacedFeatures.register(featureRegisterable, BROWN_MUSHROOM_OLD_GROWTH, registryEntry18, mushroomModifiers(4, CountPlacementModifier.of(3)));
		PlacedFeatures.register(featureRegisterable, RED_MUSHROOM_OLD_GROWTH, registryEntry19, mushroomModifiers(171, null));
		PlacedFeatures.register(featureRegisterable, BROWN_MUSHROOM_SWAMP, registryEntry18, mushroomModifiers(0, CountPlacementModifier.of(2)));
		PlacedFeatures.register(featureRegisterable, RED_MUSHROOM_SWAMP, registryEntry19, mushroomModifiers(64, null));
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_WARM,
			registryEntry20,
			RarityFilterPlacementModifier.of(16),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_DEFAULT,
			registryEntry20,
			RarityFilterPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_FLOWER_FOREST,
			registryEntry21,
			CountPlacementModifier.of(3),
			RarityFilterPlacementModifier.of(2),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_SWAMP,
			registryEntry22,
			RarityFilterPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_PLAIN,
			registryEntry23,
			NoiseThresholdCountPlacementModifier.of(-0.8, 15, 4),
			RarityFilterPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_CHERRY,
			registryEntry25,
			NoiseThresholdCountPlacementModifier.of(-0.8, 5, 10),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable, FLOWER_MEADOW, registryEntry24, SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()
		);
		PlacementModifier placementModifier = SurfaceWaterDepthFilterPlacementModifier.of(0);
		PlacedFeatures.register(
			featureRegisterable,
			TREES_PLAINS,
			registryEntry26,
			PlacedFeatures.createCountExtraModifier(0, 0.05F, 1),
			SquarePlacementModifier.of(),
			placementModifier,
			PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
			BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.getDefaultState(), BlockPos.ORIGIN)),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			DARK_FOREST_VEGETATION,
			registryEntry27,
			CountPlacementModifier.of(16),
			SquarePlacementModifier.of(),
			placementModifier,
			PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			PALE_GARDEN_VEGETATION,
			registryEntry28,
			CountPlacementModifier.of(16),
			SquarePlacementModifier.of(),
			placementModifier,
			PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FLOWER_FOREST_FLOWERS,
			registryEntry29,
			RarityFilterPlacementModifier.of(7),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			CountPlacementModifier.of(ClampedIntProvider.create(UniformIntProvider.create(-1, 3), 0, 3)),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			FOREST_FLOWERS,
			registryEntry29,
			RarityFilterPlacementModifier.of(7),
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			CountPlacementModifier.of(ClampedIntProvider.create(UniformIntProvider.create(-3, 1), 0, 1)),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, TREES_FLOWER_FOREST, registryEntry30, treeModifiers(PlacedFeatures.createCountExtraModifier(6, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_MEADOW, registryEntry31, treeModifiers(RarityFilterPlacementModifier.of(100)));
		PlacedFeatures.register(
			featureRegisterable,
			TREES_CHERRY,
			registryEntry36,
			treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1), Blocks.CHERRY_SAPLING)
		);
		PlacedFeatures.register(featureRegisterable, TREES_TAIGA, registryEntry32, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_GROVE, registryEntry33, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)));
		PlacedFeatures.register(
			featureRegisterable, TREES_BADLANDS, registryEntry34, treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(5, 0.1F, 1), Blocks.OAK_SAPLING)
		);
		PlacedFeatures.register(
			featureRegisterable, TREES_SNOWY, registryEntry35, treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1), Blocks.SPRUCE_SAPLING)
		);
		PlacedFeatures.register(
			featureRegisterable,
			TREES_SWAMP,
			registryEntry37,
			PlacedFeatures.createCountExtraModifier(2, 0.1F, 1),
			SquarePlacementModifier.of(),
			SurfaceWaterDepthFilterPlacementModifier.of(2),
			PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
			BiomePlacementModifier.of(),
			BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.getDefaultState(), BlockPos.ORIGIN))
		);
		PlacedFeatures.register(featureRegisterable, TREES_WINDSWEPT_SAVANNA, registryEntry38, treeModifiers(PlacedFeatures.createCountExtraModifier(2, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_SAVANNA, registryEntry38, treeModifiers(PlacedFeatures.createCountExtraModifier(1, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, BIRCH_TALL, registryEntry39, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)));
		PlacedFeatures.register(
			featureRegisterable, TREES_BIRCH, registryEntry40, treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1), Blocks.BIRCH_SAPLING)
		);
		PlacedFeatures.register(featureRegisterable, TREES_WINDSWEPT_FOREST, registryEntry41, treeModifiers(PlacedFeatures.createCountExtraModifier(3, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_WINDSWEPT_HILLS, registryEntry41, treeModifiers(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_WATER, registryEntry42, treeModifiers(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_BIRCH_AND_OAK, registryEntry43, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, TREES_SPARSE_JUNGLE, registryEntry44, treeModifiers(PlacedFeatures.createCountExtraModifier(2, 0.1F, 1)));
		PlacedFeatures.register(
			featureRegisterable, TREES_OLD_GROWTH_SPRUCE_TAIGA, registryEntry45, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1))
		);
		PlacedFeatures.register(
			featureRegisterable, TREES_OLD_GROWTH_PINE_TAIGA, registryEntry46, treeModifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1))
		);
		PlacedFeatures.register(featureRegisterable, TREES_JUNGLE, registryEntry47, treeModifiers(PlacedFeatures.createCountExtraModifier(50, 0.1F, 1)));
		PlacedFeatures.register(featureRegisterable, BAMBOO_VEGETATION, registryEntry48, treeModifiers(PlacedFeatures.createCountExtraModifier(30, 0.1F, 1)));
		PlacedFeatures.register(
			featureRegisterable,
			MUSHROOM_ISLAND_VEGETATION,
			registryEntry49,
			SquarePlacementModifier.of(),
			PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			TREES_MANGROVE,
			registryEntry50,
			CountPlacementModifier.of(25),
			SquarePlacementModifier.of(),
			SurfaceWaterDepthFilterPlacementModifier.of(5),
			PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
			BiomePlacementModifier.of(),
			BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.getDefaultState(), BlockPos.ORIGIN))
		);
	}
}
