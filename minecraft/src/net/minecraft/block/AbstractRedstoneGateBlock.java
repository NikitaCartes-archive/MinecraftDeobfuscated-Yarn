package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final BooleanProperty POWERED = Properties.POWERED;

	protected AbstractRedstoneGateBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasTopRim(world, pos.down());
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = (Boolean)state.get(POWERED);
			boolean bl2 = this.hasPower(world, pos, state);
			if (bl && !bl2) {
				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 2);
			} else if (!bl) {
				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 2);
				if (!bl2) {
					world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
				}
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == direction ? this.getOutputLevel(world, pos, state) : 0;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.canPlaceAt(world, pos)) {
			this.updatePowered(world, pos, state);
		} else {
			BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
			dropStacks(state, world, pos, blockEntity);
			world.removeBlock(pos, false);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		if (!this.isLocked(world, pos, state)) {
			boolean bl = (Boolean)state.get(POWERED);
			boolean bl2 = this.hasPower(world, pos, state);
			if (bl != bl2 && !world.getBlockTickScheduler().isTicking(pos, this)) {
				TickPriority tickPriority = TickPriority.HIGH;
				if (this.isTargetNotAligned(world, pos, state)) {
					tickPriority = TickPriority.EXTREMELY_HIGH;
				} else if (bl) {
					tickPriority = TickPriority.VERY_HIGH;
				}

				world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), tickPriority);
			}
		}
	}

	public boolean isLocked(WorldView world, BlockPos pos, BlockState state) {
		return false;
	}

	protected boolean hasPower(World world, BlockPos pos, BlockState state) {
		return this.getPower(world, pos, state) > 0;
	}

	protected int getPower(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction);
		int i = world.getEmittedRedstonePower(blockPos, direction);
		if (i >= 15) {
			return i;
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? (Integer)blockState.get(RedstoneWireBlock.POWER) : 0);
		}
	}

	protected int getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(this.getInputLevel(world, pos.offset(direction2), direction2), this.getInputLevel(world, pos.offset(direction3), direction3));
	}

	protected int getInputLevel(WorldView world, BlockPos pos, Direction dir) {
		BlockState blockState = world.getBlockState(pos);
		if (this.isValidInput(blockState)) {
			if (blockState.isOf(Blocks.REDSTONE_BLOCK)) {
				return 15;
			} else {
				return blockState.isOf(Blocks.REDSTONE_WIRE) ? (Integer)blockState.get(RedstoneWireBlock.POWER) : world.getStrongRedstonePower(pos, dir);
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (this.hasPower(world, pos, state)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		this.updateTarget(world, pos, state);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			super.onStateReplaced(state, world, pos, newState, moved);
			this.updateTarget(world, pos, state);
		}
	}

	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos, this, pos);
		world.updateNeighborsExcept(blockPos, this, direction);
	}

	protected boolean isValidInput(BlockState state) {
		return state.emitsRedstonePower();
	}

	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		return 15;
	}

	public static boolean isRedstoneGate(BlockState state) {
		return state.getBlock() instanceof AbstractRedstoneGateBlock;
	}

	public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		BlockState blockState = world.getBlockState(pos.offset(direction));
		return isRedstoneGate(blockState) && blockState.get(FACING) != direction;
	}

	protected abstract int getUpdateDelayInternal(BlockState state);
}
