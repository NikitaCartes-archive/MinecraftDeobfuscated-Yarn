package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomPatchFeature extends Feature<RandomPatchFeatureConfig> {
	public RandomPatchFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomPatchFeatureConfig> context) {
		RandomPatchFeatureConfig randomPatchFeatureConfig = context.getConfig();
		AbstractRandom abstractRandom = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = randomPatchFeatureConfig.xzSpread() + 1;
		int k = randomPatchFeatureConfig.ySpread() + 1;

		for (int l = 0; l < randomPatchFeatureConfig.tries(); l++) {
			mutable.set(
				blockPos,
				abstractRandom.nextInt(j) - abstractRandom.nextInt(j),
				abstractRandom.nextInt(k) - abstractRandom.nextInt(k),
				abstractRandom.nextInt(j) - abstractRandom.nextInt(j)
			);
			if (randomPatchFeatureConfig.feature().value().generateUnregistered(structureWorldAccess, context.getGenerator(), abstractRandom, mutable)) {
				i++;
			}
		}

		return i > 0;
	}
}
