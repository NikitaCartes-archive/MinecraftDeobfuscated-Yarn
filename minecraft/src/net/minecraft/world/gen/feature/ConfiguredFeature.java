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

public class ConfiguredFeature<FC extends FeatureConfig> {
	public final Feature<FC> feature;
	public final FC config;

	public ConfiguredFeature(Feature<FC> feature, FC featureConfig) {
		this.feature = feature;
		this.config = featureConfig;
	}

	public ConfiguredFeature(Feature<FC> feature, Dynamic<?> dynamic) {
		this(feature, feature.deserializeConfig(dynamic));
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.FEATURE.getId(this.feature).toString()),
					dynamicOps.createString("config"),
					this.config.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.generate(iWorld, chunkGenerator, random, blockPos, this.config);
	}

	public static <T> ConfiguredFeature<?> deserialize(Dynamic<T> dynamic) {
		Feature<? extends FeatureConfig> feature = (Feature<? extends FeatureConfig>)Registry.FEATURE.get(new Identifier(dynamic.get("name").asString("")));
		return new ConfiguredFeature<>(feature, dynamic.get("config").orElseEmptyMap());
	}
}
