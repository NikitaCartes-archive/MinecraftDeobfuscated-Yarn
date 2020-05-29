/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.CODEC.fieldOf("feature_true")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue), ((MapCodec)ConfiguredFeature.CODEC.fieldOf("feature_false")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)).apply((Applicative<RandomBooleanFeatureConfig, ?>)instance, RandomBooleanFeatureConfig::new));
    public final ConfiguredFeature<?, ?> featureTrue;
    public final ConfiguredFeature<?, ?> featureFalse;

    public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> featureTrue, ConfiguredFeature<?, ?> featureFalse) {
        this.featureTrue = featureTrue;
        this.featureFalse = featureFalse;
    }
}

