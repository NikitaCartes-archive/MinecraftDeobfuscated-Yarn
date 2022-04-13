package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.heightprovider.VeryBiasedToBottomHeightProvider;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceThresholdFilterPlacementModifier;

public class MiscPlacedFeatures {
	public static final RegistryEntry<PlacedFeature> ICE_SPIKE = PlacedFeatures.register(
		"ice_spike",
		MiscConfiguredFeatures.ICE_SPIKE,
		CountPlacementModifier.of(3),
		SquarePlacementModifier.of(),
		PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> ICE_PATCH = PlacedFeatures.register(
		"ice_patch",
		MiscConfiguredFeatures.ICE_PATCH,
		CountPlacementModifier.of(2),
		SquarePlacementModifier.of(),
		PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
		RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)),
		BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(Blocks.SNOW_BLOCK)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> FOREST_ROCK = PlacedFeatures.register(
		"forest_rock",
		MiscConfiguredFeatures.FOREST_ROCK,
		CountPlacementModifier.of(2),
		SquarePlacementModifier.of(),
		PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> ICEBERG_PACKED = PlacedFeatures.register(
		"iceberg_packed", MiscConfiguredFeatures.ICEBERG_PACKED, RarityFilterPlacementModifier.of(16), SquarePlacementModifier.of(), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> ICEBERG_BLUE = PlacedFeatures.register(
		"iceberg_blue", MiscConfiguredFeatures.ICEBERG_BLUE, RarityFilterPlacementModifier.of(200), SquarePlacementModifier.of(), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> BLUE_ICE = PlacedFeatures.register(
		"blue_ice",
		MiscConfiguredFeatures.BLUE_ICE,
		CountPlacementModifier.of(UniformIntProvider.create(0, 19)),
		SquarePlacementModifier.of(),
		HeightRangePlacementModifier.uniform(YOffset.fixed(30), YOffset.fixed(61)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> LAKE_LAVA_UNDERGROUND = PlacedFeatures.register(
		"lake_lava_underground",
		MiscConfiguredFeatures.LAKE_LAVA,
		RarityFilterPlacementModifier.of(9),
		SquarePlacementModifier.of(),
		HeightRangePlacementModifier.of(UniformHeightProvider.create(YOffset.fixed(0), YOffset.getTop())),
		EnvironmentScanPlacementModifier.of(
			Direction.DOWN, BlockPredicate.bothOf(BlockPredicate.not(BlockPredicate.IS_AIR), BlockPredicate.insideWorldBounds(new BlockPos(0, -5, 0))), 32
		),
		SurfaceThresholdFilterPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> LAKE_LAVA_SURFACE = PlacedFeatures.register(
		"lake_lava_surface",
		MiscConfiguredFeatures.LAKE_LAVA,
		RarityFilterPlacementModifier.of(200),
		SquarePlacementModifier.of(),
		PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> DISK_CLAY = PlacedFeatures.register(
		"disk_clay",
		MiscConfiguredFeatures.DISK_CLAY,
		SquarePlacementModifier.of(),
		PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP,
		BlockFilterPlacementModifier.of(BlockPredicate.matchingFluids(Fluids.WATER)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> DISK_GRAVEL = PlacedFeatures.register(
		"disk_gravel",
		MiscConfiguredFeatures.DISK_GRAVEL,
		SquarePlacementModifier.of(),
		PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP,
		BlockFilterPlacementModifier.of(BlockPredicate.matchingFluids(Fluids.WATER)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> DISK_SAND = PlacedFeatures.register(
		"disk_sand",
		MiscConfiguredFeatures.DISK_SAND,
		CountPlacementModifier.of(3),
		SquarePlacementModifier.of(),
		PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP,
		BlockFilterPlacementModifier.of(BlockPredicate.matchingFluids(Fluids.WATER)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> DISK_GRASS = PlacedFeatures.register(
		"disk_grass",
		MiscConfiguredFeatures.DISK_GRASS,
		CountPlacementModifier.of(1),
		SquarePlacementModifier.of(),
		PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP,
		RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)),
		BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(Blocks.MUD)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> FREEZE_TOP_LAYER = PlacedFeatures.register(
		"freeze_top_layer", MiscConfiguredFeatures.FREEZE_TOP_LAYER, BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> VOID_START_PLATFORM = PlacedFeatures.register(
		"void_start_platform", MiscConfiguredFeatures.VOID_START_PLATFORM, BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> DESERT_WELL = PlacedFeatures.register(
		"desert_well",
		MiscConfiguredFeatures.DESERT_WELL,
		RarityFilterPlacementModifier.of(1000),
		SquarePlacementModifier.of(),
		PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_LAVA = PlacedFeatures.register(
		"spring_lava",
		MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD,
		CountPlacementModifier.of(20),
		SquarePlacementModifier.of(),
		HeightRangePlacementModifier.of(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_LAVA_FROZEN = PlacedFeatures.register(
		"spring_lava_frozen",
		MiscConfiguredFeatures.SPRING_LAVA_FROZEN,
		CountPlacementModifier.of(20),
		SquarePlacementModifier.of(),
		HeightRangePlacementModifier.of(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)),
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_WATER = PlacedFeatures.register(
		"spring_water",
		MiscConfiguredFeatures.SPRING_WATER,
		CountPlacementModifier.of(25),
		SquarePlacementModifier.of(),
		HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(192)),
		BiomePlacementModifier.of()
	);
}
