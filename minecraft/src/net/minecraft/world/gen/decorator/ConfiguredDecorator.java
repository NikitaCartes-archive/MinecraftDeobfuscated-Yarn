package net.minecraft.world.gen.decorator;

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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
	public final Decorator<DC> field_14115;
	public final DC config;

	public ConfiguredDecorator(Decorator<DC> decorator, Dynamic<?> dynamic) {
		this(decorator, decorator.deserialize(dynamic));
	}

	public ConfiguredDecorator(Decorator<DC> decorator, DC decoratorConfig) {
		this.field_14115 = decorator;
		this.config = decoratorConfig;
	}

	public <FC extends FeatureConfig> boolean method_14358(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, ConfiguredFeature<FC> configuredFeature
	) {
		return this.field_14115.method_15927(iWorld, chunkGenerator, random, blockPos, this.config, configuredFeature);
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.DECORATOR.method_10221(this.field_14115).toString()),
					dynamicOps.createString("config"),
					this.config.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public static <T> ConfiguredDecorator<?> deserialize(Dynamic<T> dynamic) {
		Decorator<? extends DecoratorConfig> decorator = (Decorator<? extends DecoratorConfig>)Registry.DECORATOR
			.method_10223(new Identifier(dynamic.get("name").asString("")));
		return new ConfiguredDecorator<>(decorator, dynamic.get("config").orElseEmptyMap());
	}
}
