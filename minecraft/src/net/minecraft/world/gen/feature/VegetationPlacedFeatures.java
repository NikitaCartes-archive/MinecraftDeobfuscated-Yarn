package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
	public static final PlacedFeature BAMBOO_LIGHT = PlacedFeatures.register(
		"bamboo_light",
		VegetationConfiguredFeatures.BAMBOO_NO_PODZOL
			.withPlacement(RarityFilterPlacementModifier.of(4), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature BAMBOO = PlacedFeatures.register(
		"bamboo",
		VegetationConfiguredFeatures.BAMBOO_SOME_PODZOL
			.withPlacement(
				NoiseBasedCountPlacementModifier.of(160, 80.0, 0.3), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature VINES = PlacedFeatures.register(
		"vines",
		VegetationConfiguredFeatures.VINES
			.withPlacement(
				CountPlacementModifier.of(127),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(100)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature PATCH_SUNFLOWER = PlacedFeatures.register(
		"patch_sunflower",
		VegetationConfiguredFeatures.PATCH_SUNFLOWER
			.withPlacement(RarityFilterPlacementModifier.of(3), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_PUMPKIN = PlacedFeatures.register(
		"patch_pumpkin",
		VegetationConfiguredFeatures.PATCH_PUMPKIN
			.withPlacement(RarityFilterPlacementModifier.of(300), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_GRASS_PLAIN = PlacedFeatures.register(
		"patch_grass_plain",
		VegetationConfiguredFeatures.PATCH_GRASS
			.withPlacement(
				NoiseThresholdCountPlacementModifier.of(-0.8, 5, 10), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature PATCH_GRASS_FOREST = PlacedFeatures.register(
		"patch_grass_forest", VegetationConfiguredFeatures.PATCH_GRASS.withPlacement(modifiers(2))
	);
	public static final PlacedFeature PATCH_GRASS_BADLANDS = PlacedFeatures.register(
		"patch_grass_badlands",
		VegetationConfiguredFeatures.PATCH_GRASS.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_GRASS_SAVANNA = PlacedFeatures.register(
		"patch_grass_savanna", VegetationConfiguredFeatures.PATCH_GRASS.withPlacement(modifiers(20))
	);
	public static final PlacedFeature PATCH_GRASS_NORMAL = PlacedFeatures.register(
		"patch_grass_normal", VegetationConfiguredFeatures.PATCH_GRASS.withPlacement(modifiers(5))
	);
	public static final PlacedFeature PATCH_GRASS_TAIGA_2 = PlacedFeatures.register(
		"patch_grass_taiga_2",
		VegetationConfiguredFeatures.PATCH_TAIGA_GRASS
			.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_GRASS_TAIGA = PlacedFeatures.register(
		"patch_grass_taiga", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS.withPlacement(modifiers(7))
	);
	public static final PlacedFeature PATCH_GRASS_JUNGLE = PlacedFeatures.register(
		"patch_grass_jungle", VegetationConfiguredFeatures.PATCH_GRASS_JUNGLE.withPlacement(modifiers(25))
	);
	public static final PlacedFeature GRASS_BONEMEAL = PlacedFeatures.register(
		"grass_bonemeal", VegetationConfiguredFeatures.SINGLE_PIECE_OF_GRASS.withInAirFilter()
	);
	public static final PlacedFeature PATCH_DEAD_BUSH_2 = PlacedFeatures.register(
		"patch_dead_bush_2", VegetationConfiguredFeatures.PATCH_DEAD_BUSH.withPlacement(modifiers(2))
	);
	public static final PlacedFeature PATCH_DEAD_BUSH = PlacedFeatures.register(
		"patch_dead_bush",
		VegetationConfiguredFeatures.PATCH_DEAD_BUSH
			.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_DEAD_BUSH_BADLANDS = PlacedFeatures.register(
		"patch_dead_bush_badlands", VegetationConfiguredFeatures.PATCH_DEAD_BUSH.withPlacement(modifiers(20))
	);
	public static final PlacedFeature PATCH_MELON = PlacedFeatures.register(
		"patch_melon",
		VegetationConfiguredFeatures.PATCH_MELON
			.withPlacement(RarityFilterPlacementModifier.of(6), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_MELON_SPARSE = PlacedFeatures.register(
		"patch_melon_sparse",
		VegetationConfiguredFeatures.PATCH_MELON
			.withPlacement(RarityFilterPlacementModifier.of(64), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_BERRY_COMMON = PlacedFeatures.register(
		"patch_berry_common",
		VegetationConfiguredFeatures.PATCH_BERRY_BUSH
			.withPlacement(RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_BERRY_RARE = PlacedFeatures.register(
		"patch_berry_rare",
		VegetationConfiguredFeatures.PATCH_BERRY_BUSH
			.withPlacement(RarityFilterPlacementModifier.of(384), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_WATERLILY = PlacedFeatures.register(
		"patch_waterlily", VegetationConfiguredFeatures.PATCH_WATERLILY.withPlacement(modifiers(4))
	);
	public static final PlacedFeature PATCH_TALL_GRASS_2 = PlacedFeatures.register(
		"patch_tall_grass_2",
		VegetationConfiguredFeatures.PATCH_TALL_GRASS
			.withPlacement(
				NoiseThresholdCountPlacementModifier.of(-0.8, 0, 7),
				RarityFilterPlacementModifier.of(32),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature PATCH_TALL_GRASS = PlacedFeatures.register(
		"patch_tall_grass",
		VegetationConfiguredFeatures.PATCH_TALL_GRASS
			.withPlacement(RarityFilterPlacementModifier.of(5), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_LARGE_FERN = PlacedFeatures.register(
		"patch_large_fern",
		VegetationConfiguredFeatures.PATCH_LARGE_FERN
			.withPlacement(RarityFilterPlacementModifier.of(5), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_CACTUS_DESERT = PlacedFeatures.register(
		"patch_cactus_desert",
		VegetationConfiguredFeatures.PATCH_CACTUS
			.withPlacement(RarityFilterPlacementModifier.of(6), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_CACTUS_DECORATED = PlacedFeatures.register(
		"patch_cactus_decorated",
		VegetationConfiguredFeatures.PATCH_CACTUS
			.withPlacement(RarityFilterPlacementModifier.of(13), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_SUGAR_CANE_SWAMP = PlacedFeatures.register(
		"patch_sugar_cane_swamp",
		VegetationConfiguredFeatures.PATCH_SUGAR_CANE
			.withPlacement(RarityFilterPlacementModifier.of(3), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_SUGAR_CANE_DESERT = PlacedFeatures.register(
		"patch_sugar_cane_desert",
		VegetationConfiguredFeatures.PATCH_SUGAR_CANE
			.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_SUGAR_CANE_BADLANDS = PlacedFeatures.register(
		"patch_sugar_cane_badlands",
		VegetationConfiguredFeatures.PATCH_SUGAR_CANE
			.withPlacement(RarityFilterPlacementModifier.of(5), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature PATCH_SUGAR_CANE = PlacedFeatures.register(
		"patch_sugar_cane",
		VegetationConfiguredFeatures.PATCH_SUGAR_CANE
			.withPlacement(RarityFilterPlacementModifier.of(6), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature BROWN_MUSHROOM_NETHER = PlacedFeatures.register(
		"brown_mushroom_nether",
		VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM
			.withPlacement(RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE, BiomePlacementModifier.of())
	);
	public static final PlacedFeature RED_MUSHROOM_NETHER = PlacedFeatures.register(
		"red_mushroom_nether",
		VegetationConfiguredFeatures.PATCH_RED_MUSHROOM
			.withPlacement(RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_TOP_RANGE, BiomePlacementModifier.of())
	);
	public static final PlacedFeature BROWN_MUSHROOM_NORMAL = PlacedFeatures.register(
		"brown_mushroom_normal", VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM.withPlacement(modifiersWithChance(256, null))
	);
	public static final PlacedFeature RED_MUSHROOM_NORMAL = PlacedFeatures.register(
		"red_mushroom_normal", VegetationConfiguredFeatures.PATCH_RED_MUSHROOM.withPlacement(modifiersWithChance(512, null))
	);
	public static final PlacedFeature BROWN_MUSHROOM_TAIGA = PlacedFeatures.register(
		"brown_mushroom_taiga", VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM.withPlacement(modifiersWithChance(4, null))
	);
	public static final PlacedFeature RED_MUSHROOM_TAIGA = PlacedFeatures.register(
		"red_mushroom_taiga", VegetationConfiguredFeatures.PATCH_RED_MUSHROOM.withPlacement(modifiersWithChance(256, null))
	);
	public static final PlacedFeature BROWN_MUSHROOM_OLD_GROWTH = PlacedFeatures.register(
		"brown_mushroom_old_growth", VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM.withPlacement(modifiersWithChance(4, CountPlacementModifier.of(3)))
	);
	public static final PlacedFeature RED_MUSHROOM_OLD_GROWTH = PlacedFeatures.register(
		"red_mushroom_old_growth", VegetationConfiguredFeatures.PATCH_RED_MUSHROOM.withPlacement(modifiersWithChance(171, null))
	);
	public static final PlacedFeature BROWN_MUSHROOM_SWAMP = PlacedFeatures.register(
		"brown_mushroom_swamp", VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM.withPlacement(modifiersWithChance(0, CountPlacementModifier.of(2)))
	);
	public static final PlacedFeature RED_MUSHROOM_SWAMP = PlacedFeatures.register(
		"red_mushroom_swamp", VegetationConfiguredFeatures.PATCH_RED_MUSHROOM.withPlacement(modifiersWithChance(64, null))
	);
	public static final PlacedFeature FLOWER_WARM = PlacedFeatures.register(
		"flower_warm",
		VegetationConfiguredFeatures.FLOWER_DEFAULT
			.withPlacement(RarityFilterPlacementModifier.of(16), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature FLOWER_DEFAULT = PlacedFeatures.register(
		"flower_default",
		VegetationConfiguredFeatures.FLOWER_DEFAULT
			.withPlacement(RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature FLOWER_FLOWER_FOREST = PlacedFeatures.register(
		"flower_flower_forest",
		VegetationConfiguredFeatures.FLOWER_FLOWER_FOREST
			.withPlacement(
				CountPlacementModifier.of(3),
				RarityFilterPlacementModifier.of(2),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature FLOWER_SWAMP = PlacedFeatures.register(
		"flower_swamp",
		VegetationConfiguredFeatures.FLOWER_SWAMP
			.withPlacement(RarityFilterPlacementModifier.of(32), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature FLOWER_PLAIN = PlacedFeatures.register(
		"flower_plains",
		VegetationConfiguredFeatures.FLOWER_PLAIN
			.withPlacement(
				NoiseThresholdCountPlacementModifier.of(-0.8, 15, 4),
				RarityFilterPlacementModifier.of(32),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature FLOWER_MEADOW = PlacedFeatures.register(
		"flower_meadow",
		VegetationConfiguredFeatures.FLOWER_MEADOW.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacementModifier NOT_IN_SURFACE_WATER_MODIFIER = SurfaceWaterDepthFilterPlacementModifier.of(0);
	public static final PlacedFeature TREES_PLAINS = PlacedFeatures.register(
		"trees_plains",
		VegetationConfiguredFeatures.TREES_PLAINS
			.withPlacement(
				PlacedFeatures.createCountExtraModifier(0, 0.05F, 1),
				SquarePlacementModifier.of(),
				NOT_IN_SURFACE_WATER_MODIFIER,
				PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
				BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.getDefaultState(), BlockPos.ORIGIN)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature DARK_FOREST_VEGETATION = PlacedFeatures.register(
		"dark_forest_vegetation",
		VegetationConfiguredFeatures.DARK_FOREST_VEGETATION
			.withPlacement(
				CountPlacementModifier.of(16),
				SquarePlacementModifier.of(),
				NOT_IN_SURFACE_WATER_MODIFIER,
				PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature FLOWER_FOREST_FLOWERS = PlacedFeatures.register(
		"flower_forest_flowers",
		VegetationConfiguredFeatures.FOREST_FLOWERS
			.withPlacement(
				RarityFilterPlacementModifier.of(7),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				CountPlacementModifier.of(ClampedIntProvider.create(UniformIntProvider.create(-1, 3), 0, 3)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature FOREST_FLOWERS = PlacedFeatures.register(
		"forest_flowers",
		VegetationConfiguredFeatures.FOREST_FLOWERS
			.withPlacement(
				RarityFilterPlacementModifier.of(7),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				CountPlacementModifier.of(ClampedIntProvider.create(UniformIntProvider.create(-3, 1), 0, 1)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature TREES_FLOWER_FOREST = PlacedFeatures.register(
		"trees_flower_forest", VegetationConfiguredFeatures.TREES_FLOWER_FOREST.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(6, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_MEADOW = PlacedFeatures.register(
		"trees_meadow", VegetationConfiguredFeatures.MEADOW_TREES.withPlacement(modifiers(RarityFilterPlacementModifier.of(100)))
	);
	public static final PlacedFeature TREES_TAIGA = PlacedFeatures.register(
		"trees_taiga", VegetationConfiguredFeatures.TREES_TAIGA.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_GROVE = PlacedFeatures.register(
		"trees_grove", VegetationConfiguredFeatures.TREES_GROVE.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_BADLANDS = PlacedFeatures.register(
		"trees_badlands",
		TreeConfiguredFeatures.OAK.withPlacement(modifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(5, 0.1F, 1), Blocks.OAK_SAPLING))
	);
	public static final PlacedFeature TREES_SNOWY = PlacedFeatures.register(
		"trees_snowy",
		TreeConfiguredFeatures.SPRUCE.withPlacement(modifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1), Blocks.SPRUCE_SAPLING))
	);
	public static final PlacedFeature TREES_SWAMP = PlacedFeatures.register(
		"trees_swamp",
		TreeConfiguredFeatures.SWAMP_OAK
			.withPlacement(
				PlacedFeatures.createCountExtraModifier(2, 0.1F, 1),
				SquarePlacementModifier.of(),
				SurfaceWaterDepthFilterPlacementModifier.of(2),
				PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
				BiomePlacementModifier.of(),
				BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.getDefaultState(), BlockPos.ORIGIN))
			)
	);
	public static final PlacedFeature TREES_WINDSWEPT_SAVANNA = PlacedFeatures.register(
		"trees_windswept_savanna", VegetationConfiguredFeatures.TREES_SAVANNA.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(2, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_SAVANNA = PlacedFeatures.register(
		"trees_savanna", VegetationConfiguredFeatures.TREES_SAVANNA.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(1, 0.1F, 1)))
	);
	public static final PlacedFeature BIRCH_TALL = PlacedFeatures.register(
		"birch_tall", VegetationConfiguredFeatures.BIRCH_TALL.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_BIRCH = PlacedFeatures.register(
		"trees_birch",
		TreeConfiguredFeatures.BIRCH_BEES_0002.withPlacement(modifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1), Blocks.BIRCH_SAPLING))
	);
	public static final PlacedFeature TREES_WINDSWEPT_FOREST = PlacedFeatures.register(
		"trees_windswept_forest", VegetationConfiguredFeatures.TREES_WINDSWEPT_HILLS.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(3, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_WINDSWEPT_HILLS = PlacedFeatures.register(
		"trees_windswept_hills", VegetationConfiguredFeatures.TREES_WINDSWEPT_HILLS.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_WATER = PlacedFeatures.register(
		"trees_water", VegetationConfiguredFeatures.TREES_WATER.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(0, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_BIRCH_AND_OAK = PlacedFeatures.register(
		"trees_birch_and_oak", VegetationConfiguredFeatures.TREES_BIRCH_AND_OAK.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_SPARSE_JUNGLE = PlacedFeatures.register(
		"trees_sparse_jungle", VegetationConfiguredFeatures.TREES_SPARSE_JUNGLE.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(2, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacedFeatures.register(
		"trees_old_growth_spruce_taiga",
		VegetationConfiguredFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_OLD_GROWTH_PINE_TAIGA = PlacedFeatures.register(
		"trees_old_growth_pine_taiga",
		VegetationConfiguredFeatures.TREES_OLD_GROWTH_PINE_TAIGA.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(10, 0.1F, 1)))
	);
	public static final PlacedFeature TREES_JUNGLE = PlacedFeatures.register(
		"trees_jungle", VegetationConfiguredFeatures.TREES_JUNGLE.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(50, 0.1F, 1)))
	);
	public static final PlacedFeature BAMBOO_VEGETATION = PlacedFeatures.register(
		"bamboo_vegetation", VegetationConfiguredFeatures.BAMBOO_VEGETATION.withPlacement(modifiers(PlacedFeatures.createCountExtraModifier(30, 0.1F, 1)))
	);
	public static final PlacedFeature MUSHROOM_ISLAND_VEGETATION = PlacedFeatures.register(
		"mushroom_island_vegetation",
		VegetationConfiguredFeatures.MUSHROOM_ISLAND_VEGETATION
			.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);

	public static List<PlacementModifier> modifiers(int count) {
		return List.of(CountPlacementModifier.of(count), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
	}

	private static List<PlacementModifier> modifiersWithChance(int chance, @Nullable PlacementModifier modifier) {
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

	private static Builder<PlacementModifier> modifiersBuilder(PlacementModifier countModifier) {
		return ImmutableList.<PlacementModifier>builder()
			.add(countModifier)
			.add(SquarePlacementModifier.of())
			.add(NOT_IN_SURFACE_WATER_MODIFIER)
			.add(PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP)
			.add(BiomePlacementModifier.of());
	}

	public static List<PlacementModifier> modifiers(PlacementModifier modifier) {
		return modifiersBuilder(modifier).build();
	}

	public static List<PlacementModifier> modifiersWithWouldSurvive(PlacementModifier modifier, Block block) {
		return modifiersBuilder(modifier).add(BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN))).build();
	}
}
