package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomPatchFeature extends Feature<RandomPatchFeatureConfig> {
	public RandomPatchFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RandomPatchFeatureConfig> context) {
		RandomPatchFeatureConfig randomPatchFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = randomPatchFeatureConfig.xzSpread() + 1;
		int k = randomPatchFeatureConfig.ySpread() + 1;

		for (int l = 0; l < randomPatchFeatureConfig.tries(); l++) {
			mutable.set(blockPos, random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j));
			if (method_38907(structureWorldAccess, mutable, randomPatchFeatureConfig)
				&& ((ConfiguredFeature)randomPatchFeatureConfig.feature().get()).generate(structureWorldAccess, context.getGenerator(), random, mutable)) {
				i++;
			}
		}

		return i > 0;
	}

	public static boolean method_38907(WorldAccess worldAccess, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		BlockState blockState = worldAccess.getBlockState(blockPos.down());
		return (!randomPatchFeatureConfig.onlyInAir() || worldAccess.isAir(blockPos))
			&& (randomPatchFeatureConfig.allowedOn().isEmpty() || randomPatchFeatureConfig.allowedOn().contains(blockState.getBlock()))
			&& !randomPatchFeatureConfig.disallowedOn().contains(blockState);
	}
}
