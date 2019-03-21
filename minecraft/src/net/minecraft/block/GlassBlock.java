package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GlassBlock extends TransparentBlock {
	public GlassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}
}
