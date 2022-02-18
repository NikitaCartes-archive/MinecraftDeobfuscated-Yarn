/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DripstoneClusterFeatureConfig
implements FeatureConfig {
    public static final Codec<DripstoneClusterFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range")).forGetter(config -> config.floorToCeilingSearchRange), ((MapCodec)IntProvider.createValidatingCodec(1, 128).fieldOf("height")).forGetter(config -> config.height), ((MapCodec)IntProvider.createValidatingCodec(1, 128).fieldOf("radius")).forGetter(config -> config.radius), ((MapCodec)Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff")).forGetter(config -> config.maxStalagmiteStalactiteHeightDiff), ((MapCodec)Codec.intRange(1, 64).fieldOf("height_deviation")).forGetter(config -> config.heightDeviation), ((MapCodec)IntProvider.createValidatingCodec(0, 128).fieldOf("dripstone_block_layer_thickness")).forGetter(config -> config.dripstoneBlockLayerThickness), ((MapCodec)FloatProvider.createValidatedCodec(0.0f, 2.0f).fieldOf("density")).forGetter(config -> config.density), ((MapCodec)FloatProvider.createValidatedCodec(0.0f, 2.0f).fieldOf("wetness")).forGetter(config -> config.wetness), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_dripstone_column_at_max_distance_from_center")).forGetter(config -> Float.valueOf(config.chanceOfDripstoneColumnAtMaxDistanceFromCenter)), ((MapCodec)Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column")).forGetter(config -> config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn), ((MapCodec)Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias")).forGetter(config -> config.maxDistanceFromCenterAffectingHeightBias)).apply((Applicative<DripstoneClusterFeatureConfig, ?>)instance, DripstoneClusterFeatureConfig::new));
    public final int floorToCeilingSearchRange;
    public final IntProvider height;
    public final IntProvider radius;
    public final int maxStalagmiteStalactiteHeightDiff;
    public final int heightDeviation;
    public final IntProvider dripstoneBlockLayerThickness;
    public final FloatProvider density;
    public final FloatProvider wetness;
    public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
    public final int maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
    public final int maxDistanceFromCenterAffectingHeightBias;

    public DripstoneClusterFeatureConfig(int floorToCeilingSearchRange, IntProvider height, IntProvider radius, int maxStalagmiteStalactiteHeightDiff, int heightDeviation, IntProvider dripstoneBlockLayerThickness, FloatProvider density, FloatProvider wetness, float wetnessMean, int maxDistanceFromCenterAffectingChanceOfDripstoneColumn, int maxDistanceFromCenterAffectingHeightBias) {
        this.floorToCeilingSearchRange = floorToCeilingSearchRange;
        this.height = height;
        this.radius = radius;
        this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
        this.heightDeviation = heightDeviation;
        this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
        this.density = density;
        this.wetness = wetness;
        this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = wetnessMean;
        this.maxDistanceFromCenterAffectingChanceOfDripstoneColumn = maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
        this.maxDistanceFromCenterAffectingHeightBias = maxDistanceFromCenterAffectingHeightBias;
    }
}

