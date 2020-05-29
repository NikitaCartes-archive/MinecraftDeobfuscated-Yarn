/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleRandomFeatureConfig> CODEC = ((MapCodec)ConfiguredFeature.CODEC.listOf().fieldOf("features")).xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features).codec();
    public final List<ConfiguredFeature<?, ?>> features;

    public SimpleRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features) {
        this.features = features;
    }
}

