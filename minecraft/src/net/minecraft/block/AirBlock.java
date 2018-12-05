package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AirBlock extends Block {
	protected AirBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.NONE;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean canCollideWith(BlockState blockState) {
		return false;
	}

	@Override
	public boolean isAir(BlockState blockState) {
		return true;
	}
}
