package net.minecraft.world.gen.feature;

import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class EndPlacedFeatures {
	public static final PlacedFeature END_SPIKE = PlacedFeatures.register("end_spike", EndConfiguredFeatures.END_SPIKE.withPlacement(BiomePlacementModifier.of()));
	public static final PlacedFeature END_GATEWAY_RETURN = PlacedFeatures.register(
		"end_gateway_return",
		EndConfiguredFeatures.END_GATEWAY_RETURN
			.withPlacement(
				RarityFilterPlacementModifier.of(700),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				RandomOffsetPlacementModifier.vertically(UniformIntProvider.create(3, 9)),
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature CHORUS_PLANT = PlacedFeatures.register(
		"chorus_plant",
		EndConfiguredFeatures.CHORUS_PLANT
			.withPlacement(
				CountPlacementModifier.of(UniformIntProvider.create(0, 4)),
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				BiomePlacementModifier.of()
			)
	);
	public static final PlacedFeature END_ISLAND_DECORATED = PlacedFeatures.register(
		"end_island_decorated",
		EndConfiguredFeatures.END_ISLAND
			.withPlacement(
				RarityFilterPlacementModifier.of(14),
				PlacedFeatures.createCountExtraModifier(1, 0.25F, 1),
				SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.fixed(55), YOffset.fixed(70)),
				BiomePlacementModifier.of()
			)
	);
}
