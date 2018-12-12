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
	public final FC field_13375;

	public ConfiguredFeature(Feature<FC> feature, FC featureConfig) {
		this.feature = feature;
		this.field_13375 = featureConfig;
	}

	public ConfiguredFeature(Feature<FC> feature, Dynamic<?> dynamic) {
		this(feature, feature.method_13148(dynamic));
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.FEATURE.getId(this.feature).toString()),
					dynamicOps.createString("config"),
					this.field_13375.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos) {
		return this.feature.method_13151(iWorld, chunkGenerator, random, blockPos, this.field_13375);
	}

	public static <T> ConfiguredFeature<?> deserialize(Dynamic<T> dynamic) {
		Feature<? extends FeatureConfig> feature = (Feature<? extends FeatureConfig>)Registry.FEATURE.get(new Identifier(dynamic.getString("name", "")));
		return new ConfiguredFeature<>(feature, (Dynamic<?>)dynamic.get("config").orElseGet(dynamic::emptyMap));
	}
}
