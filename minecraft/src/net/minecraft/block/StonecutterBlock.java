package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StonecutterBlock extends Block {
	protected static final VoxelShape field_16407 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	public StonecutterBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_16407;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
