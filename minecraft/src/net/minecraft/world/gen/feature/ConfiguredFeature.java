package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
	public static final Logger log = LogManager.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
	}

	public ConfiguredFeature(F feature, Dynamic<?> dynamic) {
		this(feature, feature.deserializeConfig(dynamic));
	}

	public ConfiguredFeature<?, ?> createDecoratedFeature(ConfiguredDecorator<?> configuredDecorator) {
		Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
		return feature.configure(new DecoratedFeatureConfig(this, configuredDecorator));
	}

	public RandomFeatureEntry<FC> withChance(float chance) {
		return new RandomFeatureEntry<>(this, chance);
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("name"),
					ops.createString(Registry.FEATURE.getId(this.feature).toString()),
					ops.createString("config"),
					this.config.serialize(ops).getValue()
				)
			)
		);
	}

	public boolean generate(
		IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos
	) {
		return this.feature.generate(iWorld, structureAccessor, chunkGenerator, random, blockPos, this.config);
	}

	public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> dynamic) {
		String string = dynamic.get("name").asString("");
		Feature<? extends FeatureConfig> feature = (Feature<? extends FeatureConfig>)Registry.FEATURE.get(new Identifier(string));

		try {
			return new ConfiguredFeature<>(feature, dynamic.get("config").orElseEmptyMap());
		} catch (RuntimeException var4) {
			log.warn("Error while deserializing {}", string);
			return new ConfiguredFeature<>(Feature.NO_OP, DefaultFeatureConfig.DEFAULT);
		}
	}
}
