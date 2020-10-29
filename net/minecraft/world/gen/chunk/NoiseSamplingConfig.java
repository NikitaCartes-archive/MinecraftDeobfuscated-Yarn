/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingConfig {
    private static final Codec<Double> CODEC_RANGE = Codec.doubleRange(0.001, 1000.0);
    public static final Codec<NoiseSamplingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)CODEC_RANGE.fieldOf("xz_scale")).forGetter(NoiseSamplingConfig::getXZScale), ((MapCodec)CODEC_RANGE.fieldOf("y_scale")).forGetter(NoiseSamplingConfig::getYScale), ((MapCodec)CODEC_RANGE.fieldOf("xz_factor")).forGetter(NoiseSamplingConfig::getXZFactor), ((MapCodec)CODEC_RANGE.fieldOf("y_factor")).forGetter(NoiseSamplingConfig::getYFactor)).apply((Applicative<NoiseSamplingConfig, ?>)instance, NoiseSamplingConfig::new));
    private final double xzScale;
    private final double yScale;
    private final double xzFactor;
    private final double yFactor;

    public NoiseSamplingConfig(double xzScale, double yScale, double xzFactor, double yFactor) {
        this.xzScale = xzScale;
        this.yScale = yScale;
        this.xzFactor = xzFactor;
        this.yFactor = yFactor;
    }

    public double getXZScale() {
        return this.xzScale;
    }

    public double getYScale() {
        return this.yScale;
    }

    public double getXZFactor() {
        return this.xzFactor;
    }

    public double getYFactor() {
        return this.yFactor;
    }
}

