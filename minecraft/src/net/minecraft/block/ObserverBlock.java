package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty field_11322 = Properties.POWERED;

	public ObserverBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10927, Direction.SOUTH).with(field_11322, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10927, field_11322);
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_10927, rotation.method_10503(blockState.get(field_10927)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_10927)));
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11322)) {
			world.setBlockState(blockPos, blockState.with(field_11322, Boolean.valueOf(false)), 2);
		} else {
			world.setBlockState(blockPos, blockState.with(field_11322, Boolean.valueOf(true)), 2);
			world.getBlockTickScheduler().schedule(blockPos, this, 2);
		}

		this.method_10365(world, blockPos, blockState);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (blockState.get(field_10927) == direction && !(Boolean)blockState.get(field_11322)) {
			this.method_10366(iWorld, blockPos);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private void method_10366(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.isClient() && !iWorld.getBlockTickScheduler().isScheduled(blockPos, this)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 2);
		}
	}

	protected void method_10365(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(field_10927);
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
		return blockState.get(field_11322) && blockState.get(field_10927) == direction ? 15 : 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient() && (Boolean)blockState.get(field_11322) && !world.getBlockTickScheduler().isScheduled(blockPos, this)) {
				BlockState blockState3 = blockState.with(field_11322, Boolean.valueOf(false));
				world.setBlockState(blockPos, blockState3, 18);
				this.method_10365(world, blockPos, blockState3);
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient && (Boolean)blockState.get(field_11322) && world.getBlockTickScheduler().isScheduled(blockPos, this)) {
				this.method_10365(world, blockPos, blockState.with(field_11322, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_10927, itemPlacementContext.getPlayerFacing().getOpposite().getOpposite());
	}
}
