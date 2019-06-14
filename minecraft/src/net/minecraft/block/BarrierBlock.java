package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BarrierBlock extends Block {
	protected BarrierBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1.0F;
	}

	@Override
	public boolean method_9523(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return false;
	}
}
