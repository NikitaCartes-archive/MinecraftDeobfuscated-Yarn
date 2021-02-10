/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class UnderwaterMagmaFeatureConfig
implements FeatureConfig {
    public static final Codec<UnderwaterMagmaFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(0, 512).fieldOf("floor_search_range")).forGetter(config -> config.floorSearchRange), ((MapCodec)Codec.intRange(0, 64).fieldOf("placement_radius_around_floor")).forGetter(config -> config.placementRadiusAroundFloor), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("placement_probability_per_valid_position")).forGetter(config -> Float.valueOf(config.placementProbabilityPerValidPosition))).apply((Applicative<UnderwaterMagmaFeatureConfig, ?>)instance, UnderwaterMagmaFeatureConfig::new));
    public final int floorSearchRange;
    public final int placementRadiusAroundFloor;
    public final float placementProbabilityPerValidPosition;

    public UnderwaterMagmaFeatureConfig(int floorSearchRange, int placementRadiusAroundFloor, float placementProbabilityPerValidPosition) {
        this.floorSearchRange = floorSearchRange;
        this.placementRadiusAroundFloor = placementRadiusAroundFloor;
        this.placementProbabilityPerValidPosition = placementProbabilityPerValidPosition;
    }
}

