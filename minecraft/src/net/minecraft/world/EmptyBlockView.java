package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

public enum EmptyBlockView implements BlockView {
	field_12294;

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		return null;
	}

	@Override
	public BlockState method_8320(BlockPos blockPos) {
		return Blocks.field_10124.method_9564();
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		return Fluids.field_15906.method_15785();
	}
}
