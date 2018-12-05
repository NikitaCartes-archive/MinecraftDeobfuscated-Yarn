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
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.decorator.DecoratorConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
	public final Decorator<DC> decorator;
	public final DC config;

	public ConfiguredDecorator(Decorator<DC> decorator, Dynamic<?> dynamic) {
		this(decorator, decorator.deserialize(dynamic));
	}

	public ConfiguredDecorator(Decorator<DC> decorator, DC decoratorConfig) {
		this.decorator = decorator;
		this.config = decoratorConfig;
	}

	public <FC extends FeatureConfig> boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, ConfiguredFeature<FC> configuredFeature
	) {
		return this.decorator.generate(iWorld, chunkGenerator, random, blockPos, this.config, configuredFeature);
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("name"),
					dynamicOps.createString(Registry.DECORATOR.getId(this.decorator).toString()),
					dynamicOps.createString("config"),
					this.config.serialize(dynamicOps).getValue()
				)
			)
		);
	}

	public static <T> ConfiguredDecorator<?> deserialize(Dynamic<T> dynamic) {
		Decorator<? extends DecoratorConfig> decorator = (Decorator<? extends DecoratorConfig>)Registry.DECORATOR.get(new Identifier(dynamic.getString("name", "")));
		return new ConfiguredDecorator<>(decorator, (Dynamic<?>)dynamic.get("config").orElseGet(dynamic::emptyMap));
	}
}
