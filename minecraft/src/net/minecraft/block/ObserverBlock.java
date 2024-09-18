package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;

public class ObserverBlock extends FacingBlock {
	public static final MapCodec<ObserverBlock> CODEC = createCodec(ObserverBlock::new);
	public static final BooleanProperty POWERED = Properties.POWERED;

	@Override
	public MapCodec<ObserverBlock> getCodec() {
		return CODEC;
	}

	public ObserverBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(POWERED, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED)) {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
		} else {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			world.scheduleBlockTick(pos, this, 2);
		}

		this.updateNeighbors(world, pos, state);
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
		if (state.get(FACING) == direction && !(Boolean)state.get(POWERED)) {
			this.scheduleTick(world, tickView, pos);
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	private void scheduleTick(WorldView world, ScheduledTickView tickView, BlockPos pos) {
		if (!world.isClient() && !tickView.getBlockTickScheduler().isQueued(pos, this)) {
			tickView.scheduleBlockTick(pos, this, 2);
		}
	}

	protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction.getOpposite(), null);
		world.updateNeighbor(blockPos, this, wireOrientation);
		world.updateNeighborsExcept(blockPos, this, direction, wireOrientation);
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!state.isOf(oldState.getBlock())) {
			if (!world.isClient() && (Boolean)state.get(POWERED) && !world.getBlockTickScheduler().isQueued(pos, this)) {
				BlockState blockState = state.with(POWERED, Boolean.valueOf(false));
				world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
				this.updateNeighbors(world, pos, blockState);
			}
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (!world.isClient && (Boolean)state.get(POWERED) && world.getBlockTickScheduler().isQueued(pos, this)) {
				this.updateNeighbors(world, pos, state.with(POWERED, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
	}
}
