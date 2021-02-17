package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomBooleanFeature extends Feature<RandomBooleanFeatureConfig> {
	public RandomBooleanFeature(Codec<RandomBooleanFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomBooleanFeatureConfig> context) {
		Random random = context.getRandom();
		RandomBooleanFeatureConfig randomBooleanFeatureConfig = context.getConfig();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkGenerator chunkGenerator = context.getGenerator();
		BlockPos blockPos = context.getOrigin();
		boolean bl = random.nextBoolean();
		return bl
			? ((ConfiguredFeature)randomBooleanFeatureConfig.featureTrue.get()).generate(structureWorldAccess, chunkGenerator, random, blockPos)
			: ((ConfiguredFeature)randomBooleanFeatureConfig.featureFalse.get()).generate(structureWorldAccess, chunkGenerator, random, blockPos);
	}
}
