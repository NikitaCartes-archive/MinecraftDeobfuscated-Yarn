package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SpringFeature extends Feature<SpringFeatureConfig> {
	public SpringFeature(Codec<SpringFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SpringFeatureConfig springFeatureConfig
	) {
		if (!springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.up()).getBlock())) {
			return false;
		} else if (springFeatureConfig.requiresBlockBelow && !springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.down()).getBlock())) {
			return false;
		} else {
			BlockState blockState = serverWorldAccess.getBlockState(blockPos);
			if (!blockState.isAir() && !springFeatureConfig.validBlocks.contains(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.west()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.east()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.north()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.south()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.validBlocks.contains(serverWorldAccess.getBlockState(blockPos.down()).getBlock())) {
					j++;
				}

				int k = 0;
				if (serverWorldAccess.isAir(blockPos.west())) {
					k++;
				}

				if (serverWorldAccess.isAir(blockPos.east())) {
					k++;
				}

				if (serverWorldAccess.isAir(blockPos.north())) {
					k++;
				}

				if (serverWorldAccess.isAir(blockPos.south())) {
					k++;
				}

				if (serverWorldAccess.isAir(blockPos.down())) {
					k++;
				}

				if (j == springFeatureConfig.rockCount && k == springFeatureConfig.holeCount) {
					serverWorldAccess.setBlockState(blockPos, springFeatureConfig.state.getBlockState(), 2);
					serverWorldAccess.getFluidTickScheduler().schedule(blockPos, springFeatureConfig.state.getFluid(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
