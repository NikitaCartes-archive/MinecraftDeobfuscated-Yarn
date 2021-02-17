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
	public boolean generate(FeatureContext<RandomFeatureConfig> context) {
		RandomFeatureConfig randomFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkGenerator chunkGenerator = context.getGenerator();
		BlockPos blockPos = context.getOrigin();

		for (RandomFeatureEntry randomFeatureEntry : randomFeatureConfig.features) {
			if (random.nextFloat() < randomFeatureEntry.chance) {
				return randomFeatureEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
			}
		}

		return ((ConfiguredFeature)randomFeatureConfig.defaultFeature.get()).generate(structureWorldAccess, chunkGenerator, random, blockPos);
	}
}
