package net.minecraft;

import java.util.Collection;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public interface class_7124 {
	class_7124 field_37602 = new class_7124() {
		@Override
		public boolean method_41469(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection, boolean bl) {
			if (collection == null) {
				return ((SculkVeinBlock)Blocks.SCULK_VEIN).method_41516().method_41452(worldAccess.getBlockState(blockPos), worldAccess, blockPos, bl) > 0L;
			} else if (!collection.isEmpty()) {
				return !blockState.isAir() && !blockState.getFluidState().isOf(Fluids.WATER)
					? false
					: SculkVeinBlock.method_41514(worldAccess, blockPos, blockState, collection);
			} else {
				return class_7124.super.method_41469(worldAccess, blockPos, blockState, collection, bl);
			}
		}

		@Override
		public int method_41471(class_7128.class_7129 arg, WorldAccess worldAccess, BlockPos blockPos, Random random, class_7128 arg2, boolean bl) {
			return arg.method_41510() > 0 ? arg.method_41508() : 0;
		}

		@Override
		public int method_41473(int i) {
			return Math.max(i - 1, 0);
		}
	};

	default byte method_41467() {
		return 1;
	}

	default void method_41468(WorldAccess worldAccess, BlockState blockState, BlockPos blockPos, Random random) {
	}

	default boolean method_41470(WorldAccess worldAccess, BlockPos blockPos, Random random) {
		return false;
	}

	default boolean method_41469(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, @Nullable Collection<Direction> collection, boolean bl) {
		return ((AbstractLichenBlock)Blocks.SCULK_VEIN).method_41432().method_41452(blockState, worldAccess, blockPos, bl) > 0L;
	}

	default boolean method_41472() {
		return true;
	}

	default int method_41473(int i) {
		return 1;
	}

	int method_41471(class_7128.class_7129 arg, WorldAccess worldAccess, BlockPos blockPos, Random random, class_7128 arg2, boolean bl);
}
