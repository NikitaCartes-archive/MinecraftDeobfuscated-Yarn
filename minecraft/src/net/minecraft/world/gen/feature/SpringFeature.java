package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SpringFeature extends Feature<SpringFeatureConfig> {
	public SpringFeature(Function<Dynamic<?>, ? extends SpringFeatureConfig> function) {
		super(function);
	}

	public boolean method_13979(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SpringFeatureConfig springFeatureConfig
	) {
		if (!Block.isNaturalStone(iWorld.getBlockState(blockPos.up()).getBlock())) {
			return false;
		} else if (!Block.isNaturalStone(iWorld.getBlockState(blockPos.down()).getBlock())) {
			return false;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos);
			if (!blockState.isAir() && !Block.isNaturalStone(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (Block.isNaturalStone(iWorld.getBlockState(blockPos.west()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.getBlockState(blockPos.east()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.getBlockState(blockPos.north()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.getBlockState(blockPos.south()).getBlock())) {
					j++;
				}

				int k = 0;
				if (iWorld.isAir(blockPos.west())) {
					k++;
				}

				if (iWorld.isAir(blockPos.east())) {
					k++;
				}

				if (iWorld.isAir(blockPos.north())) {
					k++;
				}

				if (iWorld.isAir(blockPos.south())) {
					k++;
				}

				if (j == 3 && k == 1) {
					iWorld.setBlockState(blockPos, springFeatureConfig.state.getBlockState(), 2);
					iWorld.getFluidTickScheduler().schedule(blockPos, springFeatureConfig.state.getFluid(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
