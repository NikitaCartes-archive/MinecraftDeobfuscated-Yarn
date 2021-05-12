/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RavineCarverConfig
extends CarverConfig {
    public static final Codec<RavineCarverConfig> RAVINE_CODEC = RecordCodecBuilder.create(instance -> instance.group(CarverConfig.CONFIG_CODEC.forGetter(ravineCarverConfig -> ravineCarverConfig), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("vertical_rotation")).forGetter(ravineCarverConfig -> ravineCarverConfig.verticalRotation), ((MapCodec)Shape.CODEC.fieldOf("shape")).forGetter(ravineCarverConfig -> ravineCarverConfig.shape)).apply((Applicative<RavineCarverConfig, ?>)instance, RavineCarverConfig::new));
    public final FloatProvider verticalRotation;
    public final Shape shape;

    public RavineCarverConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, boolean aquifers, CarverDebugConfig debugConfig, FloatProvider verticalRotation, Shape shape) {
        super(probability, y, yScale, lavaLevel, aquifers, debugConfig);
        this.verticalRotation = verticalRotation;
        this.shape = shape;
    }

    public RavineCarverConfig(CarverConfig config, FloatProvider verticalRotation, Shape shape) {
        this(config.probability, config.y, config.yScale, config.lavaLevel, config.aquifers, config.debugConfig, verticalRotation, shape);
    }

    public static class Shape {
        public static final Codec<Shape> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("distance_factor")).forGetter(shape -> shape.distanceFactor), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("thickness")).forGetter(shape -> shape.thickness), ((MapCodec)Codecs.field_33441.fieldOf("width_smoothness")).forGetter(shape -> shape.widthSmoothness), ((MapCodec)FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_factor")).forGetter(shape -> shape.horizontalRadiusFactor), ((MapCodec)Codec.FLOAT.fieldOf("vertical_radius_default_factor")).forGetter(shape -> Float.valueOf(shape.verticalRadiusDefaultFactor)), ((MapCodec)Codec.FLOAT.fieldOf("vertical_radius_center_factor")).forGetter(shape -> Float.valueOf(shape.verticalRadiusCenterFactor))).apply((Applicative<Shape, ?>)instance, Shape::new));
        public final FloatProvider distanceFactor;
        public final FloatProvider thickness;
        public final int widthSmoothness;
        public final FloatProvider horizontalRadiusFactor;
        public final float verticalRadiusDefaultFactor;
        public final float verticalRadiusCenterFactor;

        public Shape(FloatProvider distanceFactor, FloatProvider thickness, int widthSmoothness, FloatProvider horizontalRadiusFactor, float verticalRadiusDefaultFactor, float verticalRadiusCenterFactor) {
            this.widthSmoothness = widthSmoothness;
            this.horizontalRadiusFactor = horizontalRadiusFactor;
            this.verticalRadiusDefaultFactor = verticalRadiusDefaultFactor;
            this.verticalRadiusCenterFactor = verticalRadiusCenterFactor;
            this.distanceFactor = distanceFactor;
            this.thickness = thickness;
        }
    }
}

