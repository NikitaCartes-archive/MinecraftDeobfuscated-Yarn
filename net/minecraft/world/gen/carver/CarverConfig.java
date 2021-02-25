/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;

public class CarverConfig
extends ProbabilityConfig {
    public static final Codec<CarverConfig> CONFIG_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(carverConfig -> Float.valueOf(carverConfig.probability)), CarverDebugConfig.CODEC.optionalFieldOf("debug_settings", CarverDebugConfig.DEFAULT).forGetter(CarverConfig::getDebugConfig)).apply((Applicative<CarverConfig, ?>)instance, CarverConfig::new));
    private final CarverDebugConfig debugConfig;

    public CarverConfig(float chance, CarverDebugConfig debugConfig) {
        super(chance);
        this.debugConfig = debugConfig;
    }

    public CarverConfig(float f) {
        this(f, CarverDebugConfig.DEFAULT);
    }

    public CarverDebugConfig getDebugConfig() {
        return this.debugConfig;
    }
}

