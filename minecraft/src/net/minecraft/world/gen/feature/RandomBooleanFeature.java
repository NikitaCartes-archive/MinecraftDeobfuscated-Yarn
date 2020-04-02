package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class RandomBooleanFeature extends Feature<RandomBooleanFeatureConfig> {
	public RandomBooleanFeature(Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		RandomBooleanFeatureConfig randomBooleanFeatureConfig
	) {
		boolean bl = random.nextBoolean();
		return bl
			? randomBooleanFeatureConfig.featureTrue.generate(iWorld, structureAccessor, chunkGenerator, random, blockPos)
			: randomBooleanFeatureConfig.featureFalse.generate(iWorld, structureAccessor, chunkGenerator, random, blockPos);
	}
}
