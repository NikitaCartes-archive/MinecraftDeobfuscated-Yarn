package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleRandomFeature extends Feature<SimpleRandomFeatureConfig> {
	public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SimpleRandomFeatureConfig> context) {
		AbstractRandom abstractRandom = context.getRandom();
		SimpleRandomFeatureConfig simpleRandomFeatureConfig = context.getConfig();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		ChunkGenerator chunkGenerator = context.getGenerator();
		int i = abstractRandom.nextInt(simpleRandomFeatureConfig.features.size());
		PlacedFeature placedFeature = simpleRandomFeatureConfig.features.get(i).value();
		return placedFeature.generateUnregistered(structureWorldAccess, chunkGenerator, abstractRandom, blockPos);
	}
}
