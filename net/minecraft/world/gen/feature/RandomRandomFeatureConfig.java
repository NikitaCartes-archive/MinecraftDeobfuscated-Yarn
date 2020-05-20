/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomRandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.field_24833.listOf().fieldOf("features")).forGetter(randomRandomFeatureConfig -> randomRandomFeatureConfig.features), ((MapCodec)Codec.INT.fieldOf("count")).withDefault(0).forGetter(randomRandomFeatureConfig -> randomRandomFeatureConfig.count)).apply((Applicative<RandomRandomFeatureConfig, ?>)instance, RandomRandomFeatureConfig::new));
    public final List<ConfiguredFeature<?, ?>> features;
    public final int count;

    public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features, int count) {
        this.features = features;
        this.count = count;
    }
}

