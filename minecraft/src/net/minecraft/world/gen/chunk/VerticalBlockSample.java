package net.minecraft.world.gen.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public final class VerticalBlockSample implements BlockView {
	private final BlockState[] states;

	public VerticalBlockSample(BlockState[] states) {
		this.states = states;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getY();
		return i >= 0 && i < this.states.length ? this.states[i] : Blocks.AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.getBlockState(pos).getFluidState();
	}
}
