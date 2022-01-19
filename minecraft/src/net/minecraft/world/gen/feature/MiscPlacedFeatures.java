package net.minecraft.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.heightprovider.VeryBiasedToBottomHeightProvider;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceThresholdFilterPlacementModifier;

public class MiscPlacedFeatures {
	public static final PlacedFeature ICE_SPIKE = PlacedFeatures.register(
		"ice_spike",
		MiscConfiguredFeatures.ICE_SPIKE
			.withPlacement(CountPlacementModifier.of(3), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature ICE_PATCH = PlacedFeatures.register(
		"ice_patch",
		MiscConfiguredFeatures.ICE_PATCH
			.withPlacement(CountPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature FOREST_ROCK = PlacedFeatures.register(
		"forest_rock",
		MiscConfiguredFeatures.FOREST_ROCK
			.withPlacement(CountPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature ICEBERG_PACKED = PlacedFeatures.register(
		"iceberg_packed",
		MiscConfiguredFeatures.ICEBERG_PACKED.withPlacement(RarityFilterPlacementModifier.of(16), SquarePlacementModifier.of(), BiomePlacementModifier.of())
	);
	public static final PlacedFeature ICEBERG_BLUE = PlacedFeatures.register(
		"iceberg_blue",
		MiscConfiguredFeatures.ICEBERG_BLUE.withPlacement(RarityFilterPlacementModifier.of(200), SquarePlacementModifier.of(), BiomePlacementModifier.of())
	);
	public static final PlacedFeature BLUE_ICE = PlacedFeatures.register(
		"blue_ice",
		MiscConfiguredFeatures.BLUE_ICE
			.withPlacement(
				CountPlacementModifier.of(UniformIntProvider.create(0, 19)),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.fixed(30), YOffset.fixed(61)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature LAKE_LAVA_UNDERGROUND = PlacedFeatures.register(
		"lake_lava_underground",
		MiscConfiguredFeatures.LAKE_LAVA
			.withPlacement(
				RarityFilterPlacementModifier.of(9),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.of(UniformHeightProvider.create(YOffset.fixed(0), YOffset.getTop())),
				EnvironmentScanPlacementModifier.of(
					Direction.DOWN, BlockPredicate.bothOf(BlockPredicate.not(BlockPredicate.IS_AIR), BlockPredicate.insideWorldBounds(new BlockPos(0, -5, 0))), 32
				),
				SurfaceThresholdFilterPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature LAKE_LAVA_SURFACE = PlacedFeatures.register(
		"lake_lava_surface",
		MiscConfiguredFeatures.LAKE_LAVA
			.withPlacement(RarityFilterPlacementModifier.of(200), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature DISK_CLAY = PlacedFeatures.register(
		"disk_clay",
		MiscConfiguredFeatures.DISK_CLAY.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature DISK_GRAVEL = PlacedFeatures.register(
		"disk_gravel",
		MiscConfiguredFeatures.DISK_GRAVEL.withPlacement(SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature DISK_SAND = PlacedFeatures.register(
		"disk_sand",
		MiscConfiguredFeatures.DISK_SAND
			.withPlacement(CountPlacementModifier.of(3), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature FREEZE_TOP_LAYER = PlacedFeatures.register(
		"freeze_top_layer", MiscConfiguredFeatures.FREEZE_TOP_LAYER.withPlacement(BiomePlacementModifier.of())
	);
	public static final PlacedFeature VOID_START_PLATFORM = PlacedFeatures.register(
		"void_start_platform", MiscConfiguredFeatures.VOID_START_PLATFORM.withPlacement(BiomePlacementModifier.of())
	);
	public static final PlacedFeature DESERT_WELL = PlacedFeatures.register(
		"desert_well",
		MiscConfiguredFeatures.DESERT_WELL
			.withPlacement(RarityFilterPlacementModifier.of(1000), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of())
	);
	public static final PlacedFeature SPRING_LAVA = PlacedFeatures.register(
		"spring_lava",
		MiscConfiguredFeatures.SPRING_LAVA_OVERWORLD
			.withPlacement(
				CountPlacementModifier.of(20),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.of(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature SPRING_LAVA_FROZEN = PlacedFeatures.register(
		"spring_lava_frozen",
		MiscConfiguredFeatures.SPRING_LAVA_FROZEN
			.withPlacement(
				CountPlacementModifier.of(20),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.of(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature SPRING_WATER = PlacedFeatures.register(
		"spring_water",
		MiscConfiguredFeatures.SPRING_WATER
			.withPlacement(
				CountPlacementModifier.of(25),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(192)),
				BiomePlacementModifier.of()
			)
	);
}
