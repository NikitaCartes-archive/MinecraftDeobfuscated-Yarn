/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CarvingMaskPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightmapPlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseBasedCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.NoiseThresholdCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceThresholdFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SurfaceWaterDepthFilterPlacementModifier;

public interface PlacementModifierType<P extends PlacementModifier> {
    public static final PlacementModifierType<BlockFilterPlacementModifier> BLOCK_PREDICATE_FILTER = PlacementModifierType.register("block_predicate_filter", BlockFilterPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<RarityFilterPlacementModifier> RARITY_FILTER = PlacementModifierType.register("rarity_filter", RarityFilterPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<SurfaceThresholdFilterPlacementModifier> SURFACE_RELATIVE_THRESHOLD_FILTER = PlacementModifierType.register("surface_relative_threshold_filter", SurfaceThresholdFilterPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<SurfaceWaterDepthFilterPlacementModifier> SURFACE_WATER_DEPTH_FILTER = PlacementModifierType.register("surface_water_depth_filter", SurfaceWaterDepthFilterPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<BiomePlacementModifier> BIOME = PlacementModifierType.register("biome", BiomePlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<CountPlacementModifier> COUNT = PlacementModifierType.register("count", CountPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<NoiseBasedCountPlacementModifier> NOISE_BASED_COUNT = PlacementModifierType.register("noise_based_count", NoiseBasedCountPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<NoiseThresholdCountPlacementModifier> NOISE_THRESHOLD_COUNT = PlacementModifierType.register("noise_threshold_count", NoiseThresholdCountPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<CountMultilayerPlacementModifier> COUNT_ON_EVERY_LAYER = PlacementModifierType.register("count_on_every_layer", CountMultilayerPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<EnvironmentScanPlacementModifier> ENVIRONMENT_SCAN = PlacementModifierType.register("environment_scan", EnvironmentScanPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<HeightmapPlacementModifier> HEIGHTMAP = PlacementModifierType.register("heightmap", HeightmapPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<HeightRangePlacementModifier> HEIGHT_RANGE = PlacementModifierType.register("height_range", HeightRangePlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<SquarePlacementModifier> IN_SQUARE = PlacementModifierType.register("in_square", SquarePlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<RandomOffsetPlacementModifier> RANDOM_OFFSET = PlacementModifierType.register("random_offset", RandomOffsetPlacementModifier.MODIFIER_CODEC);
    public static final PlacementModifierType<CarvingMaskPlacementModifier> CARVING_MASK = PlacementModifierType.register("carving_mask", CarvingMaskPlacementModifier.MODIFIER_CODEC);

    public Codec<P> codec();

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.PLACEMENT_MODIFIER_TYPE, id, () -> codec);
    }
}

