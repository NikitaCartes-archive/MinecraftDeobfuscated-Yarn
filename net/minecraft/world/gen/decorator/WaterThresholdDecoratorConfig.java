/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class WaterThresholdDecoratorConfig
implements DecoratorConfig {
    public static final Codec<WaterThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("max_water_depth")).forGetter(waterThresholdDecoratorConfig -> waterThresholdDecoratorConfig.maxWaterDepth)).apply((Applicative<WaterThresholdDecoratorConfig, ?>)instance, WaterThresholdDecoratorConfig::new));
    public final int maxWaterDepth;

    public WaterThresholdDecoratorConfig(int maxWaterDepth) {
        this.maxWaterDepth = maxWaterDepth;
    }
}

