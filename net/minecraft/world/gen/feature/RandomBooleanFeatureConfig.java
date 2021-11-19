/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue), ((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)).apply((Applicative<RandomBooleanFeatureConfig, ?>)instance, RandomBooleanFeatureConfig::new));
    public final Supplier<PlacedFeature> featureTrue;
    public final Supplier<PlacedFeature> featureFalse;

    public RandomBooleanFeatureConfig(Supplier<PlacedFeature> featureTrue, Supplier<PlacedFeature> featureFalse) {
        this.featureTrue = featureTrue;
        this.featureFalse = featureFalse;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return Stream.concat(this.featureTrue.get().getDecoratedFeatures(), this.featureFalse.get().getDecoratedFeatures());
    }
}

