/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.apply2(RandomFeatureConfig::new, ((MapCodec)RandomFeatureEntry.CODEC.listOf().fieldOf("features")).forGetter(randomFeatureConfig -> randomFeatureConfig.features), ((MapCodec)ConfiguredFeature.CODEC.fieldOf("default")).forGetter(randomFeatureConfig -> randomFeatureConfig.defaultFeature)));
    public final List<RandomFeatureEntry> features;
    public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry> features, ConfiguredFeature<?, ?> defaultFeature) {
        this(features, () -> defaultFeature);
    }

    private RandomFeatureConfig(List<RandomFeatureEntry> list, Supplier<ConfiguredFeature<?, ?>> supplier) {
        this.features = list;
        this.defaultFeature = supplier;
    }
}

