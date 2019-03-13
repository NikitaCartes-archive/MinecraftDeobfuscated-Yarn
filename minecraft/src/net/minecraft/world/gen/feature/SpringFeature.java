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
		if (!Block.isNaturalStone(iWorld.method_8320(blockPos.up()).getBlock())) {
			return false;
		} else if (!Block.isNaturalStone(iWorld.method_8320(blockPos.down()).getBlock())) {
			return false;
		} else {
			BlockState blockState = iWorld.method_8320(blockPos);
			if (!blockState.isAir() && !Block.isNaturalStone(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (Block.isNaturalStone(iWorld.method_8320(blockPos.west()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.method_8320(blockPos.east()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.method_8320(blockPos.north()).getBlock())) {
					j++;
				}

				if (Block.isNaturalStone(iWorld.method_8320(blockPos.south()).getBlock())) {
					j++;
				}

				int k = 0;
				if (iWorld.method_8623(blockPos.west())) {
					k++;
				}

				if (iWorld.method_8623(blockPos.east())) {
					k++;
				}

				if (iWorld.method_8623(blockPos.north())) {
					k++;
				}

				if (iWorld.method_8623(blockPos.south())) {
					k++;
				}

				if (j == 3 && k == 1) {
					iWorld.method_8652(blockPos, springFeatureConfig.field_13850.getBlockState(), 2);
					iWorld.method_8405().method_8676(blockPos, springFeatureConfig.field_13850.getFluid(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
