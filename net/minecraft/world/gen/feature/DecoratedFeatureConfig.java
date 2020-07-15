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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DecoratedFeatureConfig
implements FeatureConfig {
    public static final Codec<DecoratedFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.CODEC.fieldOf("feature")).forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.feature), ((MapCodec)ConfiguredDecorator.CODEC.fieldOf("decorator")).forGetter(decoratedFeatureConfig -> decoratedFeatureConfig.decorator)).apply((Applicative<DecoratedFeatureConfig, ?>)instance, DecoratedFeatureConfig::new));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final ConfiguredDecorator<?> decorator;

    public DecoratedFeatureConfig(Supplier<ConfiguredFeature<?, ?>> supplier, ConfiguredDecorator<?> configuredDecorator) {
        this.feature = supplier;
        this.decorator = configuredDecorator;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId((Feature<?>)this.feature.get().getFeature()), this.decorator);
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> method_30649() {
        return this.feature.get().method_30648();
    }
}

