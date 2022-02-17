package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface class_6989 {
	class_6989 field_36845 = new class_6989() {
		@Override
		public boolean method_40784(World world, BlockPos blockPos, BlockState blockState, byte b) {
			if (b == -1) {
				return ((SculkVeinBlock)Blocks.SCULK_VEIN).field_36872.method_40758(world.getBlockState(blockPos), world, blockPos) > 0L;
			} else {
				return b == 0 || !blockState.isAir() && (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill())
					? class_6989.super.method_40784(world, blockPos, blockState, b)
					: ((SculkVeinBlock)Blocks.SCULK_VEIN).method_40819(world, blockPos, blockState, b);
			}
		}

		@Override
		public short method_40786(SculkSpreadManager.Cursor cursor, World world, BlockPos blockPos, Random random) {
			return cursor.getDecay() > 0 ? cursor.getCharge() : 0;
		}

		@Override
		public byte method_40782(byte b) {
			return (byte)Math.max(b - 1, 0);
		}
	};

	default byte method_40781() {
		return 2;
	}

	default void method_40783(World world, BlockState blockState, BlockPos blockPos, Random random) {
	}

	default boolean method_40785(World world, BlockPos blockPos, Random random) {
		return false;
	}

	default boolean method_40784(World world, BlockPos blockPos, BlockState blockState, byte b) {
		return ((SculkVeinBlock)Blocks.SCULK_VEIN).field_36871.method_40758(blockState, world, blockPos) > 0L;
	}

	default boolean method_40787() {
		return true;
	}

	default byte method_40782(byte b) {
		return 2;
	}

	short method_40786(SculkSpreadManager.Cursor cursor, World world, BlockPos blockPos, Random random);
}
