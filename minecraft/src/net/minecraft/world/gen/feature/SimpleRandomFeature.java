package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleRandomFeature extends Feature<SimpleRandomFeatureConfig> {
	public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SimpleRandomFeatureConfig> featureContext) {
		Random random = featureContext.getRandom();
		SimpleRandomFeatureConfig simpleRandomFeatureConfig = featureContext.getConfig();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos blockPos = featureContext.getPos();
		ChunkGenerator chunkGenerator = featureContext.getGenerator();
		int i = random.nextInt(simpleRandomFeatureConfig.features.size());
		ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)((Supplier)simpleRandomFeatureConfig.features.get(i)).get();
		return configuredFeature.generate(structureWorldAccess, chunkGenerator, random, blockPos);
	}
}
