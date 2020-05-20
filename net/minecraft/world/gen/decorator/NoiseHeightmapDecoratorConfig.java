/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class NoiseHeightmapDecoratorConfig
implements DecoratorConfig {
    public static final Codec<NoiseHeightmapDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("noise_level")).forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.noiseLevel), ((MapCodec)Codec.INT.fieldOf("below_noise")).forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.belowNoise), ((MapCodec)Codec.INT.fieldOf("above_noise")).forGetter(noiseHeightmapDecoratorConfig -> noiseHeightmapDecoratorConfig.aboveNoise)).apply((Applicative<NoiseHeightmapDecoratorConfig, ?>)instance, NoiseHeightmapDecoratorConfig::new));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseHeightmapDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
}

