package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public ObserverBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.field_11035).with(POWERED, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(POWERED)) {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)), 2);
		} else {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 2);
			world.getBlockTickScheduler().schedule(blockPos, this, 2);
		}

		this.updateNeighbors(world, blockPos, blockState);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (blockState.get(FACING) == direction && !(Boolean)blockState.get(POWERED)) {
			this.scheduleTick(iWorld, blockPos);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private void scheduleTick(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.isClient() && !iWorld.getBlockTickScheduler().isScheduled(blockPos, this)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 2);
		}
	}

	protected void updateNeighbors(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos2, this, blockPos);
		world.updateNeighborsExcept(blockPos2, this, direction);
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.getWeakRedstonePower(blockView, blockPos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) && blockState.get(FACING) == direction ? 15 : 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient() && (Boolean)blockState.get(POWERED) && !world.getBlockTickScheduler().isScheduled(blockPos, this)) {
				BlockState blockState3 = blockState.with(POWERED, Boolean.valueOf(false));
				world.setBlockState(blockPos, blockState3, 18);
				this.updateNeighbors(world, blockPos, blockState3);
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient && (Boolean)blockState.get(POWERED) && world.getBlockTickScheduler().isScheduled(blockPos, this)) {
				this.updateNeighbors(world, blockPos, blockState.with(POWERED, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerLookDirection().getOpposite().getOpposite());
	}
}
