package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SnowyBlock extends Block {
	public static final BooleanProperty SNOWY = Properties.SNOWY;

	protected SnowyBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SNOWY, Boolean.valueOf(false)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing != Direction.UP) {
			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		} else {
			Block block = neighborState.getBlock();
			return state.with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Block block = ctx.getWorld().getBlockState(ctx.getBlockPos().up()).getBlock();
		return this.getDefaultState().with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SNOWY);
	}
}
