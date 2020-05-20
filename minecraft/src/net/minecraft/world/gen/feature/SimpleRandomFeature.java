package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SimpleRandomFeature extends Feature<SimpleRandomFeatureConfig> {
	public SimpleRandomFeature(Codec<SimpleRandomFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		SimpleRandomFeatureConfig simpleRandomFeatureConfig
	) {
		int i = random.nextInt(simpleRandomFeatureConfig.features.size());
		ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)simpleRandomFeatureConfig.features.get(i);
		return configuredFeature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
	}
}
