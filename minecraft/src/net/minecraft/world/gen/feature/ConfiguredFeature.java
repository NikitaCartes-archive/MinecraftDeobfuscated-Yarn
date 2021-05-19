package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decoratable;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> implements Decoratable<ConfiguredFeature<?, ?>> {
	public static final Codec<ConfiguredFeature<?, ?>> CODEC = Registry.FEATURE.dispatch(configuredFeature -> configuredFeature.feature, Feature::getCodec);
	public static final Codec<Supplier<ConfiguredFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_FEATURE_KEY, CODEC);
	public static final Codec<List<Supplier<ConfiguredFeature<?, ?>>>> field_26756 = RegistryElementCodec.method_31194(Registry.CONFIGURED_FEATURE_KEY, CODEC);
	public static final Logger LOGGER = LogManager.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
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

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos origin) {
		return this.feature.generate(new FeatureContext<>(world, chunkGenerator, random, origin, this.config));
	}

	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(Stream.of(this), this.config.getDecoratedFeatures());
	}

	public String toString() {
		return (String)BuiltinRegistries.CONFIGURED_FEATURE.getKey(this).map(Objects::toString).orElseGet(() -> CODEC.encodeStart(JsonOps.INSTANCE, this).toString());
	}
}
