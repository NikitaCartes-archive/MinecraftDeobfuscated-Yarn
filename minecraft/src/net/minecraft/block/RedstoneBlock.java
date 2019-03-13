package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RedstoneBlock extends Block {
	public RedstoneBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 15;
	}
}
