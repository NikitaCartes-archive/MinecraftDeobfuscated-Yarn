package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ConfiguredDecorator<DC extends DecoratorConfig> {
	public static final Codec<ConfiguredDecorator<?>> field_24981 = Registry.DECORATOR
		.dispatch("name", configuredDecorator -> configuredDecorator.decorator, Decorator::getCodec);
	public final Decorator<DC> decorator;
	public final DC config;

	public ConfiguredDecorator(Decorator<DC> decorator, DC decoratorConfig) {
		this.decorator = decorator;
		this.config = decoratorConfig;
	}

	public <FC extends FeatureConfig, F extends Feature<FC>> boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		ConfiguredFeature<FC, F> configuredFeature
	) {
		return this.decorator.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos, this.config, configuredFeature);
	}
}
