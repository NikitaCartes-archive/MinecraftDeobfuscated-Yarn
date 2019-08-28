package net.minecraft.block;

import net.minecraft.class_4538;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class PlantBlock extends Block {
	protected PlantBlock(Block.Settings settings) {
		super(settings);
	}

	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Block block = blockState.getBlock();
		return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return this.canPlantOnTop(arg.getBlockState(blockPos2), arg, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return blockPlacementEnvironment == BlockPlacementEnvironment.AIR && !this.collidable
			? true
			: super.canPlaceAtSide(blockState, blockView, blockPos, blockPlacementEnvironment);
	}
}
