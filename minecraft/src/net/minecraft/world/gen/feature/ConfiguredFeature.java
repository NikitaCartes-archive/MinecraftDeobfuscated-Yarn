package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.class_5432;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> implements class_5432<ConfiguredFeature<?, ?>> {
	public static final MapCodec<ConfiguredFeature<?, ?>> field_25833 = Registry.FEATURE
		.<ConfiguredFeature<?, ?>>dispatchMap("name", configuredFeature -> configuredFeature.feature, Feature::getCodec)
		.orElseGet(ConfiguredFeature::method_30382);
	public static final Codec<Supplier<ConfiguredFeature<?, ?>>> CODEC = RegistryElementCodec.of(Registry.CONFIGURED_FEATURE_WORLDGEN, field_25833);
	public static final Logger LOGGER = LogManager.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC featureConfig) {
		this.feature = feature;
		this.config = featureConfig;
	}

	private static ConfiguredFeature<?, ?> method_30382() {
		return ConfiguredFeatures.NOPE;
	}

	public F method_30380() {
		return this.feature;
	}

	public FC method_30381() {
		return this.config;
	}

	public ConfiguredFeature<?, ?> method_30374(ConfiguredDecorator<?> configuredDecorator) {
		Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
		return feature.configure(new DecoratedFeatureConfig(() -> this, configuredDecorator));
	}

	public RandomFeatureEntry withChance(float chance) {
		return new RandomFeatureEntry(this, chance);
	}

	public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.generate(serverWorldAccess, chunkGenerator, random, blockPos, this.config);
	}
}
