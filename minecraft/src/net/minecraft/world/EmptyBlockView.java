package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

public enum EmptyBlockView implements BlockView {
	INSTANCE;

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return Fluids.EMPTY.getDefaultState();
	}
}
