package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomFeature extends Feature<RandomFeatureConfig> {
	public RandomFeature(Codec<RandomFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomFeatureConfig> featureContext) {
		RandomFeatureConfig randomFeatureConfig = featureContext.getConfig();
		Random random = featureContext.getRandom();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		ChunkGenerator chunkGenerator = featureContext.getGenerator();
		BlockPos blockPos = featureContext.getPos();

		for (RandomFeatureEntry randomFeatureEntry : randomFeatureConfig.features) {
			if (random.nextFloat() < randomFeatureEntry.chance) {
				return randomFeatureEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
			}
		}

		return ((ConfiguredFeature)randomFeatureConfig.defaultFeature.get()).generate(structureWorldAccess, chunkGenerator, random, blockPos);
	}
}
