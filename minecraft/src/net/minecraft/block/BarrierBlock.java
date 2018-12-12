package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BarrierBlock extends Block {
	protected BarrierBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1.0F;
	}
}
