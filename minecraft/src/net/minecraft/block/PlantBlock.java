package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class PlantBlock extends Block {
	protected PlantBlock(Block.Settings settings) {
		super(settings);
	}

	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		Block block = floor.getBlock();
		return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment env) {
		return env == BlockPlacementEnvironment.AIR && !this.collidable ? true : super.canPlaceAtSide(state, world, pos, env);
	}
}
