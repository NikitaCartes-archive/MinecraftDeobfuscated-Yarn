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

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleRandomFeatureConfig> CODEC = ((MapCodec)ConfiguredFeature.field_26756.fieldOf("features")).flatXmap(Codecs.method_36240(), Codecs.method_36240()).xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features).codec();
    public final List<Supplier<ConfiguredFeature<?, ?>>> features;

    public SimpleRandomFeatureConfig(List<Supplier<ConfiguredFeature<?, ?>>> features) {
        this.features = features;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return this.features.stream().flatMap(supplier -> ((ConfiguredFeature)supplier.get()).getDecoratedFeatures());
    }
}

