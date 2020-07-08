/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ProbabilityConfig
implements CarverConfig,
FeatureConfig {
    public static final Codec<ProbabilityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(probabilityConfig -> Float.valueOf(probabilityConfig.probability))).apply((Applicative<ProbabilityConfig, ?>)instance, ProbabilityConfig::new));
    public final float probability;

    public ProbabilityConfig(float probability) {
        this.probability = probability;
    }
}

