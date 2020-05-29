/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public static final Codec<MineshaftFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.DOUBLE.fieldOf("probability")).forGetter(mineshaftFeatureConfig -> mineshaftFeatureConfig.probability), ((MapCodec)MineshaftFeature.Type.field_24839.fieldOf("type")).forGetter(mineshaftFeatureConfig -> mineshaftFeatureConfig.type)).apply((Applicative<MineshaftFeatureConfig, ?>)instance, MineshaftFeatureConfig::new));
    public final double probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(double probability, MineshaftFeature.Type type) {
        this.probability = probability;
        this.type = type;
    }
}

