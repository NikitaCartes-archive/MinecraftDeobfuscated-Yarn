/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleRandomFeatureConfig> CODEC = ((MapCodec)Codecs.nonEmptyList(PlacedFeature.LIST_CODEC).fieldOf("features")).xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features).codec();
    public final List<Supplier<PlacedFeature>> features;

    public SimpleRandomFeatureConfig(List<Supplier<PlacedFeature>> features) {
        this.features = features;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return this.features.stream().flatMap(supplier -> ((PlacedFeature)supplier.get()).getDecoratedFeatures());
    }
}

