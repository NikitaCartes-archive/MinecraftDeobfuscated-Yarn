/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
    public static final Logger log = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F feature, FC featureConfig) {
        this.feature = feature;
        this.config = featureConfig;
    }

    public ConfiguredFeature(F feature, Dynamic<?> dynamic) {
        this(feature, ((Feature)feature).deserializeConfig(dynamic));
    }

    public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> configuredDecorator) {
        Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
        return feature.configure(new DecoratedFeatureConfig(this, configuredDecorator));
    }

    public RandomFeatureEntry<FC> withChance(float f) {
        return new RandomFeatureEntry(this, f);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("name"), dynamicOps.createString(Registry.FEATURE.getId((Feature<?>)this.feature).toString()), dynamicOps.createString("config"), this.config.serialize(dynamicOps).getValue())));
    }

    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos) {
        return ((Feature)this.feature).generate(iWorld, chunkGenerator, random, blockPos, this.config);
    }

    public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("name").asString("");
        Feature<?> feature = Registry.FEATURE.get(new Identifier(string));
        try {
            return new ConfiguredFeature(feature, dynamic.get("config").orElseEmptyMap());
        } catch (RuntimeException runtimeException) {
            log.warn("Error while deserializing {}", (Object)string);
            return new ConfiguredFeature<DefaultFeatureConfig, Feature<DefaultFeatureConfig>>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
        }
    }
}

