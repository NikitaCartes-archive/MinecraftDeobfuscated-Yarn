package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decoratable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> implements Decoratable<ConfiguredFeature<?, ?>> {
	public static final Codec<ConfiguredFeature<?, ?>> field_25833 = Registry.FEATURE.dispatch(configuredFeature -> configuredFeature.feature, Feature::getCodec);
	public static final Codec<Supplier<ConfiguredFeature<?, ?>>> CODEC = RegistryElementCodec.of(Registry.field_25914, field_25833);
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

	public ConfiguredFeature<?, ?> method_23388(ConfiguredDecorator<?> configuredDecorator) {
		return Feature.field_21217.configure(new DecoratedFeatureConfig(() -> this, configuredDecorator));
	}

	public RandomFeatureEntry withChance(float chance) {
		return new RandomFeatureEntry(this, chance);
	}

	public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.generate(structureWorldAccess, chunkGenerator, random, blockPos, this.config);
	}

	public Stream<ConfiguredFeature<?, ?>> method_30648() {
		return Stream.concat(Stream.of(this), this.config.method_30649());
	}
}
