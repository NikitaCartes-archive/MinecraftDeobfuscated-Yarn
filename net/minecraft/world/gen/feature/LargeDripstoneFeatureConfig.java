/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5863;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;

public class LargeDripstoneFeatureConfig
implements FeatureConfig {
    public static final Codec<LargeDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range")).orElse(30).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.floorToCeilingSearchRange), ((MapCodec)UniformIntDistribution.createValidatedCodec(1, 30, 30).fieldOf("column_radius")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.columnRadius), ((MapCodec)class_5863.method_33916(0.0f, 20.0f).fieldOf("height_scale")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.heightScale), ((MapCodec)Codec.floatRange(0.1f, 1.0f).fieldOf("max_column_radius_to_cave_height_ratio")).forGetter(largeDripstoneFeatureConfig -> Float.valueOf(largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio)), ((MapCodec)class_5863.method_33916(0.1f, 10.0f).fieldOf("stalactite_bluntness")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalactiteBluntness), ((MapCodec)class_5863.method_33916(0.1f, 10.0f).fieldOf("stalagmite_bluntness")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalagmiteBluntness), ((MapCodec)class_5863.method_33916(0.0f, 2.0f).fieldOf("wind_speed")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.windSpeed), ((MapCodec)Codec.intRange(0, 100).fieldOf("min_radius_for_wind")).forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minRadiusForWind), ((MapCodec)Codec.floatRange(0.0f, 5.0f).fieldOf("min_bluntness_for_wind")).forGetter(largeDripstoneFeatureConfig -> Float.valueOf(largeDripstoneFeatureConfig.minBluntnessForWind))).apply((Applicative<LargeDripstoneFeatureConfig, ?>)instance, LargeDripstoneFeatureConfig::new));
    public final int floorToCeilingSearchRange;
    public final UniformIntDistribution columnRadius;
    public final class_5863 heightScale;
    public final float maxColumnRadiusToCaveHeightRatio;
    public final class_5863 stalactiteBluntness;
    public final class_5863 stalagmiteBluntness;
    public final class_5863 windSpeed;
    public final int minRadiusForWind;
    public final float minBluntnessForWind;

    public LargeDripstoneFeatureConfig(int floorToCeilingSearchRange, UniformIntDistribution columnRadius, class_5863 heightScale, float maxColumnRadiusToCaveHeightRatio, class_5863 stalactiteBluntness, class_5863 stalagmiteBluntness, class_5863 windSpeed, int minRadiusForWind, float minBluntnessForWind) {
        this.floorToCeilingSearchRange = floorToCeilingSearchRange;
        this.columnRadius = columnRadius;
        this.heightScale = heightScale;
        this.maxColumnRadiusToCaveHeightRatio = maxColumnRadiusToCaveHeightRatio;
        this.stalactiteBluntness = stalactiteBluntness;
        this.stalagmiteBluntness = stalagmiteBluntness;
        this.windSpeed = windSpeed;
        this.minRadiusForWind = minRadiusForWind;
        this.minBluntnessForWind = minBluntnessForWind;
    }
}

