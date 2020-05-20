package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
	public static final ConfiguredFeature<?, ?> field_24832 = new ConfiguredFeature<>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
	public static final Codec<ConfiguredFeature<?, ?>> field_24833 = Registry.FEATURE
		.<ConfiguredFeature<?, ?>>dispatch("name", configuredFeature -> configuredFeature.feature, Feature::method_28627)
		.withDefault(field_24832);
	public static final Logger log = LogManager.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC featureConfig) {
		this.feature = feature;
		this.config = featureConfig;
	}

	public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> configuredDecorator) {
		Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
		return feature.configure(new DecoratedFeatureConfig(this, configuredDecorator));
	}

	public RandomFeatureEntry<FC> withChance(float chance) {
		return new RandomFeatureEntry<>(this, chance);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos
	) {
		return this.feature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos, this.config);
	}
}
