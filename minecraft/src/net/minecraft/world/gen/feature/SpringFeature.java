package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_4642;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SpringFeature extends Feature<class_4642> {
	public SpringFeature(Function<Dynamic<?>, ? extends class_4642> function) {
		super(function);
	}

	public boolean method_13979(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, class_4642 arg) {
		if (!arg.field_21287.contains(iWorld.getBlockState(blockPos.up()).getBlock())) {
			return false;
		} else if (arg.field_21284 && !arg.field_21287.contains(iWorld.getBlockState(blockPos.method_10074()).getBlock())) {
			return false;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos);
			if (!blockState.isAir() && !arg.field_21287.contains(blockState.getBlock())) {
				return false;
			} else {
				int i = 0;
				int j = 0;
				if (arg.field_21287.contains(iWorld.getBlockState(blockPos.west()).getBlock())) {
					j++;
				}

				if (arg.field_21287.contains(iWorld.getBlockState(blockPos.east()).getBlock())) {
					j++;
				}

				if (arg.field_21287.contains(iWorld.getBlockState(blockPos.north()).getBlock())) {
					j++;
				}

				if (arg.field_21287.contains(iWorld.getBlockState(blockPos.south()).getBlock())) {
					j++;
				}

				if (arg.field_21287.contains(iWorld.getBlockState(blockPos.method_10074()).getBlock())) {
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

				if (j == arg.field_21285 && k == arg.field_21286) {
					iWorld.setBlockState(blockPos, arg.field_21283.getBlockState(), 2);
					iWorld.getFluidTickScheduler().schedule(blockPos, arg.field_21283.getFluid(), 0);
					i++;
				}

				return i > 0;
			}
		}
	}
}
