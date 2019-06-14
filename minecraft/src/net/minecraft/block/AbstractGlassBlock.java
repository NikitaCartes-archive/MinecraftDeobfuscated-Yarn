package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class AbstractGlassBlock extends TransparentBlock {
	protected AbstractGlassBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1.0F;
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_9521(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_9523(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return false;
	}
}
