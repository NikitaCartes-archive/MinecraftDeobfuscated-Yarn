package net.minecraft;

import java.util.Collection;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface class_7331 {
	class_7331 field_38571 = new class_7331() {
		@Override
		public boolean method_42918(World world, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection) {
			if (collection == null) {
				return ((class_7336)Blocks.SCULK_VEIN).method_42954().method_42895(world.getBlockState(blockPos), world, blockPos) > 0L;
			} else if (!collection.isEmpty()) {
				return !blockState.isAir() && !blockState.getFluidState().isOf(Fluids.WATER) ? false : class_7336.method_42952(world, blockPos, blockState, collection);
			} else {
				return class_7331.super.method_42918(world, blockPos, blockState, collection);
			}
		}

		@Override
		public int method_42920(class_7334.class_7335 arg, World world, BlockPos blockPos, Random random) {
			return arg.method_42948() > 0 ? arg.method_42945() : 0;
		}

		@Override
		public int method_42922(int i) {
			return Math.max(i - 1, 0);
		}
	};

	default byte method_42916() {
		return 1;
	}

	default void method_42917(World world, BlockState blockState, BlockPos blockPos, Random random) {
	}

	default boolean method_42919(World world, BlockPos blockPos, Random random) {
		return false;
	}

	default boolean method_42918(World world, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection) {
		return ((AbstractLichenBlock)Blocks.SCULK_VEIN).method_42882().method_42895(blockState, world, blockPos) > 0L;
	}

	default boolean method_42921() {
		return true;
	}

	default int method_42922(int i) {
		return 1;
	}

	int method_42920(class_7334.class_7335 arg, World world, BlockPos blockPos, Random random);
}
