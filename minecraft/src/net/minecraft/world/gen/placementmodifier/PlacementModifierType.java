package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PlacementModifierType<P extends PlacementModifier> {
	PlacementModifierType<BlockFilterPlacementModifier> BLOCK_PREDICATE_FILTER = register("block_predicate_filter", BlockFilterPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<RarityFilterPlacementModifier> RARITY_FILTER = register("rarity_filter", RarityFilterPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<SurfaceThresholdFilterPlacementModifier> SURFACE_RELATIVE_THRESHOLD_FILTER = register(
		"surface_relative_threshold_filter", SurfaceThresholdFilterPlacementModifier.MODIFIER_CODEC
	);
	PlacementModifierType<SurfaceWaterDepthFilterPlacementModifier> SURFACE_WATER_DEPTH_FILTER = register(
		"surface_water_depth_filter", SurfaceWaterDepthFilterPlacementModifier.MODIFIER_CODEC
	);
	PlacementModifierType<BiomePlacementModifier> BIOME = register("biome", BiomePlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<CountPlacementModifier> COUNT = register("count", CountPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<NoiseBasedCountPlacementModifier> NOISE_BASED_COUNT = register("noise_based_count", NoiseBasedCountPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<NoiseThresholdCountPlacementModifier> NOISE_THRESHOLD_COUNT = register(
		"noise_threshold_count", NoiseThresholdCountPlacementModifier.MODIFIER_CODEC
	);
	PlacementModifierType<CountMultilayerPlacementModifier> COUNT_ON_EVERY_LAYER = register(
		"count_on_every_layer", CountMultilayerPlacementModifier.MODIFIER_CODEC
	);
	PlacementModifierType<EnvironmentScanPlacementModifier> ENVIRONMENT_SCAN = register("environment_scan", EnvironmentScanPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<HeightmapPlacementModifier> HEIGHTMAP = register("heightmap", HeightmapPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<HeightRangePlacementModifier> HEIGHT_RANGE = register("height_range", HeightRangePlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<SquarePlacementModifier> IN_SQUARE = register("in_square", SquarePlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<RandomOffsetPlacementModifier> RANDOM_OFFSET = register("random_offset", RandomOffsetPlacementModifier.MODIFIER_CODEC);
	PlacementModifierType<CarvingMaskPlacementModifier> CARVING_MASK = register("carving_mask", CarvingMaskPlacementModifier.MODIFIER_CODEC);

	Codec<P> codec();

	private static <P extends PlacementModifier> PlacementModifierType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.PLACEMENT_MODIFIER_TYPE, id, () -> codec);
	}
}
