package net.minecraft.block;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class CarpetBlock extends Block {
	protected static final VoxelShape field_11783 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private final DyeColor field_11784;

	protected CarpetBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.field_11784 = dyeColor;
	}

	public DyeColor method_10925() {
		return this.field_11784;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11783;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return !viewableWorld.isAir(blockPos.down());
	}
}
