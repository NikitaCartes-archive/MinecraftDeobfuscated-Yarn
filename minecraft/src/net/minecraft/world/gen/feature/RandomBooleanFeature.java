package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomBooleanFeature extends Feature<RandomBooleanFeatureConfig> {
	public RandomBooleanFeature(Codec<RandomBooleanFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfig randomBooleanFeatureConfig
	) {
		boolean bl = random.nextBoolean();
		return bl
			? randomBooleanFeatureConfig.featureTrue.generate(serverWorldAccess, chunkGenerator, random, blockPos)
			: randomBooleanFeatureConfig.featureFalse.generate(serverWorldAccess, chunkGenerator, random, blockPos);
	}
}
