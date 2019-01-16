package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AirBlock extends Block {
	protected AirBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean isAir(BlockState blockState) {
		return true;
	}
}
