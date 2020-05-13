package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomFeature extends Feature<RandomFeatureConfig> {
	public RandomFeature(Function<Dynamic<?>, ? extends RandomFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		RandomFeatureConfig randomFeatureConfig
	) {
		for (RandomFeatureEntry<?> randomFeatureEntry : randomFeatureConfig.features) {
			if (random.nextFloat() < randomFeatureEntry.chance) {
				return randomFeatureEntry.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
			}
		}

		return randomFeatureConfig.defaultFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
	}
}
