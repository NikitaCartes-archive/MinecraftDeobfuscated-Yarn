package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomFeature extends Feature<RandomFeatureConfig> {
	public RandomFeature(Codec<RandomFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomFeatureConfig> context) {
		RandomFeatureConfig randomFeatureConfig = context.getConfig();
		AbstractRandom abstractRandom = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkGenerator chunkGenerator = context.getGenerator();
		BlockPos blockPos = context.getOrigin();

		for (RandomFeatureEntry randomFeatureEntry : randomFeatureConfig.features) {
			if (abstractRandom.nextFloat() < randomFeatureEntry.chance) {
				return randomFeatureEntry.generate(structureWorldAccess, chunkGenerator, abstractRandom, blockPos);
			}
		}

		return randomFeatureConfig.defaultFeature.value().generateUnregistered(structureWorldAccess, chunkGenerator, abstractRandom, blockPos);
	}
}
