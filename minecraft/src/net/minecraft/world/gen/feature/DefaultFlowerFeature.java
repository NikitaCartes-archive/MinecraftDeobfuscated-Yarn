package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class DefaultFlowerFeature extends FlowerFeature<RandomPatchFeatureConfig> {
	public DefaultFlowerFeature(Function<Dynamic<?>, ? extends RandomPatchFeatureConfig> function) {
		super(function);
	}

	public boolean isPosValid(WorldAccess worldAccess, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return !randomPatchFeatureConfig.blacklist.contains(worldAccess.getBlockState(blockPos));
	}

	public int getFlowerAmount(RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return randomPatchFeatureConfig.tries;
	}

	public BlockPos getPos(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return blockPos.add(
			random.nextInt(randomPatchFeatureConfig.spreadX) - random.nextInt(randomPatchFeatureConfig.spreadX),
			random.nextInt(randomPatchFeatureConfig.spreadY) - random.nextInt(randomPatchFeatureConfig.spreadY),
			random.nextInt(randomPatchFeatureConfig.spreadZ) - random.nextInt(randomPatchFeatureConfig.spreadZ)
		);
	}

	public BlockState getFlowerState(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
	}
}
