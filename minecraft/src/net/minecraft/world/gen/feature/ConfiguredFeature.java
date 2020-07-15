package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decoratable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> implements Decoratable<ConfiguredFeature<?, ?>> {
	public static final MapCodec<ConfiguredFeature<?, ?>> field_25833 = Registry.FEATURE
		.<ConfiguredFeature<?, ?>>dispatchMap("name", configuredFeature -> configuredFeature.feature, Feature::getCodec)
		.orElseGet(ConfiguredFeature::method_30382);
	public static final Codec<Supplier<ConfiguredFeature<?, ?>>> CODEC = RegistryElementCodec.of(Registry.CONFIGURED_FEATURE_WORLDGEN, field_25833);
	public static final Logger LOGGER = LogManager.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
	}

	private static ConfiguredFeature<?, ?> method_30382() {
		return ConfiguredFeatures.NOPE;
	}

	public F getFeature() {
		return this.feature;
	}

	public FC getConfig() {
		return this.config;
	}

	public ConfiguredFeature<?, ?> decorate(ConfiguredDecorator<?> configuredDecorator) {
		return Feature.DECORATED.configure(new DecoratedFeatureConfig(() -> this, configuredDecorator));
	}

	public RandomFeatureEntry withChance(float chance) {
		return new RandomFeatureEntry(this, chance);
	}

	public boolean generate(ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.generate(serverWorldAccess, chunkGenerator, random, blockPos, this.config);
	}

	public Stream<ConfiguredFeature<?, ?>> method_30648() {
		return Stream.concat(Stream.of(this), this.config.method_30649());
	}
}
