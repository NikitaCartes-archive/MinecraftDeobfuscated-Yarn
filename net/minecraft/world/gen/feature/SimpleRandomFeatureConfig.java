/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public final List<ConfiguredFeature<?>> features;

    public SimpleRandomFeatureConfig(List<ConfiguredFeature<?>> list) {
        this.features = list;
    }

    public SimpleRandomFeatureConfig(Feature<?>[] features, FeatureConfig[] featureConfigs) {
        this(IntStream.range(0, features.length).mapToObj(i -> SimpleRandomFeatureConfig.configure(features[i], featureConfigs[i])).collect(Collectors.toList()));
    }

    private static <FC extends FeatureConfig> ConfiguredFeature<FC> configure(Feature<FC> feature, FeatureConfig featureConfig) {
        return new ConfiguredFeature<FeatureConfig>(feature, featureConfig);
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("features"), dynamicOps.createList(this.features.stream().map(configuredFeature -> configuredFeature.serialize(dynamicOps).getValue())))));
    }

    public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> dynamic) {
        List<ConfiguredFeature<?>> list = dynamic.get("features").asList(ConfiguredFeature::deserialize);
        return new SimpleRandomFeatureConfig(list);
    }
}

