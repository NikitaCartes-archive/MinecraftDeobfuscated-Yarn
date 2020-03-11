package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class SproutsBlock extends Block {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

	public SproutsBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType env) {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		return block.isIn(BlockTags.NYLIUM)
			|| block == Blocks.SOUL_SOIL
			|| block == Blocks.GRASS_BLOCK
			|| block == Blocks.DIRT
			|| block == Blocks.COARSE_DIRT
			|| block == Blocks.PODZOL
			|| block == Blocks.FARMLAND;
	}
}
