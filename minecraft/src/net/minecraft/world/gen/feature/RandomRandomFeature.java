package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomRandomFeature extends Feature<RandomRandomFeatureConfig> {
	public RandomRandomFeature(Function<Dynamic<?>, ? extends RandomRandomFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		RandomRandomFeatureConfig randomRandomFeatureConfig
	) {
		int i = random.nextInt(5) - 3 + randomRandomFeatureConfig.count;

		for (int j = 0; j < i; j++) {
			int k = random.nextInt(randomRandomFeatureConfig.features.size());
			ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)randomRandomFeatureConfig.features.get(k);
			configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
		}

		return true;
	}
}
