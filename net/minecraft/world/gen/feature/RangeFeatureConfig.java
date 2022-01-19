/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RangeFeatureConfig
implements FeatureConfig {
    public static final Codec<RangeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)HeightProvider.CODEC.fieldOf("height")).forGetter(rangeFeatureConfig -> rangeFeatureConfig.heightProvider)).apply((Applicative<RangeFeatureConfig, ?>)instance, RangeFeatureConfig::new));
    public final HeightProvider heightProvider;

    public RangeFeatureConfig(HeightProvider heightProvider) {
        this.heightProvider = heightProvider;
    }
}

