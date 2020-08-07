package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class DefaultFlowerFeature extends FlowerFeature<RandomPatchFeatureConfig> {
	public DefaultFlowerFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_23390(WorldAccess worldAccess, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return !randomPatchFeatureConfig.blacklist.contains(worldAccess.getBlockState(blockPos));
	}

	public int method_23391(RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return randomPatchFeatureConfig.tries;
	}

	public BlockPos method_23392(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return blockPos.add(
			random.nextInt(randomPatchFeatureConfig.spreadX) - random.nextInt(randomPatchFeatureConfig.spreadX),
			random.nextInt(randomPatchFeatureConfig.spreadY) - random.nextInt(randomPatchFeatureConfig.spreadY),
			random.nextInt(randomPatchFeatureConfig.spreadZ) - random.nextInt(randomPatchFeatureConfig.spreadZ)
		);
	}

	public BlockState method_23393(Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig) {
		return randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
	}
}
