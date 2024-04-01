package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class WeakRootsBlock extends ConnectingBlock implements Waterloggable {
	public static final MapCodec<WeakRootsBlock> CODEC = createCodec(WeakRootsBlock::new);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	@Override
	public MapCodec<WeakRootsBlock> getCodec() {
		return CODEC;
	}

	protected WeakRootsBlock(AbstractBlock.Settings settings) {
		super(0.3125F, settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
				.with(DOWN, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return StrongRootsBlock.getPlacementState(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		boolean bl = neighborState.isOf(this) || neighborState.isOf(Blocks.POWERFUL_POTATO);
		return state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
