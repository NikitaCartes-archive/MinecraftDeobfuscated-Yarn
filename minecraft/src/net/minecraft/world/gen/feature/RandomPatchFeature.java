package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RandomPatchFeature extends Feature<RandomPatchFeatureConfig> {
	public RandomPatchFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomPatchFeatureConfig randomPatchFeatureConfig
	) {
		BlockState blockState = randomPatchFeatureConfig.stateProvider.getBlockState(random, blockPos);
		BlockPos blockPos2;
		if (randomPatchFeatureConfig.project) {
			blockPos2 = serverWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
		} else {
			blockPos2 = blockPos;
		}

		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < randomPatchFeatureConfig.tries; j++) {
			mutable.set(
				blockPos2,
				random.nextInt(randomPatchFeatureConfig.spreadX + 1) - random.nextInt(randomPatchFeatureConfig.spreadX + 1),
				random.nextInt(randomPatchFeatureConfig.spreadY + 1) - random.nextInt(randomPatchFeatureConfig.spreadY + 1),
				random.nextInt(randomPatchFeatureConfig.spreadZ + 1) - random.nextInt(randomPatchFeatureConfig.spreadZ + 1)
			);
			BlockPos blockPos3 = mutable.down();
			BlockState blockState2 = serverWorldAccess.getBlockState(blockPos3);
			if ((serverWorldAccess.isAir(mutable) || randomPatchFeatureConfig.canReplace && serverWorldAccess.getBlockState(mutable).getMaterial().isReplaceable())
				&& blockState.canPlaceAt(serverWorldAccess, mutable)
				&& (randomPatchFeatureConfig.whitelist.isEmpty() || randomPatchFeatureConfig.whitelist.contains(blockState2.getBlock()))
				&& !randomPatchFeatureConfig.blacklist.contains(blockState2)
				&& (
					!randomPatchFeatureConfig.needsWater
						|| serverWorldAccess.getFluidState(blockPos3.west()).isIn(FluidTags.WATER)
						|| serverWorldAccess.getFluidState(blockPos3.east()).isIn(FluidTags.WATER)
						|| serverWorldAccess.getFluidState(blockPos3.north()).isIn(FluidTags.WATER)
						|| serverWorldAccess.getFluidState(blockPos3.south()).isIn(FluidTags.WATER)
				)) {
				randomPatchFeatureConfig.blockPlacer.method_23403(serverWorldAccess, mutable, blockState, random);
				i++;
			}
		}

		return i > 0;
	}
}
