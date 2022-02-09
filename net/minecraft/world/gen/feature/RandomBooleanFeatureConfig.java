/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("feature_true")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue), ((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("feature_false")).forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)).apply((Applicative<RandomBooleanFeatureConfig, ?>)instance, RandomBooleanFeatureConfig::new));
    public final RegistryEntry<PlacedFeature> featureTrue;
    public final RegistryEntry<PlacedFeature> featureFalse;

    public RandomBooleanFeatureConfig(RegistryEntry<PlacedFeature> registryEntry, RegistryEntry<PlacedFeature> registryEntry2) {
        this.featureTrue = registryEntry;
        this.featureFalse = registryEntry2;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return Stream.concat(this.featureTrue.value().getDecoratedFeatures(), this.featureFalse.value().getDecoratedFeatures());
    }
}

