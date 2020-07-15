/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public interface FeatureConfig {
    public static final DefaultFeatureConfig DEFAULT = DefaultFeatureConfig.INSTANCE;

    default public Stream<ConfiguredFeature<?, ?>> method_30649() {
        return Stream.empty();
    }
}

