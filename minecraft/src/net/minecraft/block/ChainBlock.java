package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ChainBlock extends PillarBlock implements Waterloggable {
	public static final MapCodec<ChainBlock> CODEC = createCodec(ChainBlock::new);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final float field_31054 = 6.5F;
	protected static final float field_31055 = 9.5F;
	protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.5, 6.5, 0.0, 9.5, 9.5, 16.0);
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 6.5, 6.5, 16.0, 9.5, 9.5);

	@Override
	public MapCodec<ChainBlock> getCodec() {
		return CODEC;
	}

	public ChainBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(AXIS, Direction.Axis.Y));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction.Axis)state.get(AXIS)) {
			case X:
			default:
				return X_SHAPE;
			case Z:
				return Z_SHAPE;
			case Y:
				return Y_SHAPE;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean bl = fluidState.getFluid() == Fluids.WATER;
		return super.getPlacementState(ctx).with(WATERLOGGED, Boolean.valueOf(bl));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED).add(AXIS);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
