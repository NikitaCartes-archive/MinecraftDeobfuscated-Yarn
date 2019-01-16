package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class RandomFeature extends Feature<RandomFeatureConfig> {
	public RandomFeature(Function<Dynamic<?>, ? extends RandomFeatureConfig> function) {
		super(function);
	}

	public boolean method_13798(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, RandomFeatureConfig randomFeatureConfig
	) {
		for (RandomFeatureEntry<?> randomFeatureEntry : randomFeatureConfig.features) {
			if (random.nextFloat() < randomFeatureEntry.chance) {
				return randomFeatureEntry.generate(iWorld, chunkGenerator, random, blockPos);
			}
		}

		return randomFeatureConfig.defaultFeature.generate(iWorld, chunkGenerator, random, blockPos);
	}
}
