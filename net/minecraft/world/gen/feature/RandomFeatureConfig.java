/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.apply2(RandomFeatureConfig::new, ((MapCodec)RandomFeatureEntry.CODEC.listOf().fieldOf("features")).forGetter(config -> config.features), ((MapCodec)PlacedFeature.REGISTRY_CODEC.fieldOf("default")).forGetter(config -> config.defaultFeature)));
    public final List<RandomFeatureEntry> features;
    public final RegistryEntry<PlacedFeature> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry> features, RegistryEntry<PlacedFeature> defaultFeature) {
        this.features = features;
        this.defaultFeature = defaultFeature;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return Stream.concat(this.features.stream().flatMap(entry -> entry.feature.value().getDecoratedFeatures()), this.defaultFeature.value().getDecoratedFeatures());
    }
}

