/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CountNoiseDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CountNoiseDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("noise_level")).forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.noiseLevel), ((MapCodec)Codec.INT.fieldOf("below_noise")).forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.belowNoise), ((MapCodec)Codec.INT.fieldOf("above_noise")).forGetter(countNoiseDecoratorConfig -> countNoiseDecoratorConfig.aboveNoise)).apply((Applicative<CountNoiseDecoratorConfig, ?>)instance, CountNoiseDecoratorConfig::new));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public CountNoiseDecoratorConfig(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
}

