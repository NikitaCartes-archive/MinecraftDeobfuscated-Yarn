package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SimpleRandomFeature extends Feature<SimpleRandomFeatureConfig> {
	public SimpleRandomFeature(Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig> function, Function<Random, ? extends SimpleRandomFeatureConfig> function2) {
		super(function, function2);
	}

	public boolean generate(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		SimpleRandomFeatureConfig simpleRandomFeatureConfig
	) {
		int i = random.nextInt(simpleRandomFeatureConfig.features.size());
		ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)simpleRandomFeatureConfig.features.get(i);
		return configuredFeature.generate(iWorld, chunkGenerator, random, blockPos);
	}
}
