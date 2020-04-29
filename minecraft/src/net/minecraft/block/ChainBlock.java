package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class ChainBlock extends Block implements Waterloggable {
	public static final VoxelShape SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	public static final BooleanProperty field_24411 = Properties.WATERLOGGED;

	public ChainBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_24411, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean bl = fluidState.matches(FluidTags.WATER) && fluidState.getLevel() == 8;
		return super.getPlacementState(ctx).with(field_24411, Boolean.valueOf(bl));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		if ((Boolean)state.get(field_24411)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_24411);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(field_24411) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
