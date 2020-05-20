/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeagrassFeatureConfig
implements FeatureConfig {
    public static final Codec<SeagrassFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("count")).forGetter(seagrassFeatureConfig -> seagrassFeatureConfig.count), ((MapCodec)Codec.DOUBLE.fieldOf("probability")).forGetter(seagrassFeatureConfig -> seagrassFeatureConfig.tallSeagrassProbability)).apply((Applicative<SeagrassFeatureConfig, ?>)instance, SeagrassFeatureConfig::new));
    public final int count;
    public final double tallSeagrassProbability;

    public SeagrassFeatureConfig(int count, double tallSeagrassProbability) {
        this.count = count;
        this.tallSeagrassProbability = tallSeagrassProbability;
    }
}

