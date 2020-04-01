package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class AntBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public AntBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState blockState = world.getBlockState(pos.down());
		if (blockState.getBlock() == Blocks.WHITE_CONCRETE) {
			this.move(state, world, pos, AntBlock.Clockwiseness.CW);
		} else if (blockState.getBlock() == Blocks.BLACK_CONCRETE) {
			this.move(state, world, pos, AntBlock.Clockwiseness.CCW);
		}
	}

	private void move(BlockState state, ServerWorld world, BlockPos pos, AntBlock.Clockwiseness clockwiseness) {
		Direction direction = state.get(FACING);
		Direction direction2 = clockwiseness == AntBlock.Clockwiseness.CW ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
		BlockPos blockPos = pos.offset(direction2);
		if (world.canSetBlock(blockPos)) {
			switch (clockwiseness) {
				case CW:
					world.setBlockState(pos.down(), Blocks.BLACK_CONCRETE.getDefaultState(), 19);
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					world.setBlockState(blockPos, state.with(FACING, direction2), 3);
					break;
				case CCW:
					world.setBlockState(pos.down(), Blocks.WHITE_CONCRETE.getDefaultState(), 19);
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					world.setBlockState(blockPos, state.with(FACING, direction2), 3);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.getBlockTickScheduler().schedule(pos, this, 1);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	static enum Clockwiseness {
		CW,
		CCW;
	}
}
