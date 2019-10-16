package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
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
		if (!springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.up()).getBlock())) {
			return false;
		} else if (springFeatureConfig.field_21284 && !springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.method_10074()).getBlock())) {
			return false;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos);
			if (!blockState.isAir() && !springFeatureConfig.field_21287.contains(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.west()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.east()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.north()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.south()).getBlock())) {
					j++;
				}

				if (springFeatureConfig.field_21287.contains(iWorld.getBlockState(blockPos.method_10074()).getBlock())) {
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

				if (iWorld.isAir(blockPos.method_10074())) {
					k++;
				}

				if (j == springFeatureConfig.field_21285 && k == springFeatureConfig.field_21286) {
					iWorld.setBlockState(blockPos, springFeatureConfig.field_21283.getBlockState(), 2);
					iWorld.getFluidTickScheduler().schedule(blockPos, springFeatureConfig.field_21283.getFluid(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
