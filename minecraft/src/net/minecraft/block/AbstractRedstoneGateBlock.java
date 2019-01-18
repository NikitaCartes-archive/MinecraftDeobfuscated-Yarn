package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final BooleanProperty POWERED = Properties.POWERED;

	protected AbstractRedstoneGateBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.getBlockState(blockPos2).hasSolidTopSurface(viewableWorld, blockPos2);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!this.isLocked(world, blockPos, blockState)) {
			boolean bl = (Boolean)blockState.get(POWERED);
			boolean bl2 = this.method_9990(world, blockPos, blockState);
			if (bl && !bl2) {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)), 2);
			} else if (!bl) {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 2);
				if (!bl2) {
					world.getBlockTickScheduler().schedule(blockPos, this, this.getUpdateDelayInternal(blockState), TaskPriority.field_9310);
				}
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.getWeakRedstonePower(blockView, blockPos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.get(POWERED)) {
			return 0;
		} else {
			return blockState.get(FACING) == direction ? this.getOutputLevel(blockView, blockPos, blockState) : 0;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (blockState.canPlaceAt(world, blockPos)) {
			this.method_9998(world, blockPos, blockState);
		} else {
			BlockEntity blockEntity = this.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
			dropStacks(blockState, world, blockPos, blockEntity);
			world.clearBlockState(blockPos);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}
		}
	}

	protected void method_9998(World world, BlockPos blockPos, BlockState blockState) {
		if (!this.isLocked(world, blockPos, blockState)) {
			boolean bl = (Boolean)blockState.get(POWERED);
			boolean bl2 = this.method_9990(world, blockPos, blockState);
			if (bl != bl2 && !world.getBlockTickScheduler().isTicking(blockPos, this)) {
				TaskPriority taskPriority = TaskPriority.field_9310;
				if (this.method_9988(world, blockPos, blockState)) {
					taskPriority = TaskPriority.field_9315;
				} else if (bl) {
					taskPriority = TaskPriority.field_9313;
				}

				world.getBlockTickScheduler().schedule(blockPos, this, this.getUpdateDelayInternal(blockState), taskPriority);
			}
		}
	}

	public boolean isLocked(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		return false;
	}

	protected boolean method_9990(World world, BlockPos blockPos, BlockState blockState) {
		return this.method_9991(world, blockPos, blockState) > 0;
	}

	protected int method_9991(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction);
		int i = world.getEmittedRedstonePower(blockPos2, direction);
		if (i >= 15) {
			return i;
		} else {
			BlockState blockState2 = world.getBlockState(blockPos2);
			return Math.max(i, blockState2.getBlock() == Blocks.field_10091 ? (Integer)blockState2.get(RedstoneWireBlock.POWER) : 0);
		}
	}

	protected int getMaxInputLevelSides(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(
			this.getInputLevel(viewableWorld, blockPos.offset(direction2), direction2), this.getInputLevel(viewableWorld, blockPos.offset(direction3), direction3)
		);
	}

	protected int getInputLevel(ViewableWorld viewableWorld, BlockPos blockPos, Direction direction) {
		BlockState blockState = viewableWorld.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (this.isValidInput(blockState)) {
			if (block == Blocks.field_10002) {
				return 15;
			} else {
				return block == Blocks.field_10091 ? (Integer)blockState.get(RedstoneWireBlock.POWER) : viewableWorld.getEmittedStrongRedstonePower(blockPos, direction);
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerHorizontalFacing().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (this.method_9990(world, blockPos, blockState)) {
			world.getBlockTickScheduler().schedule(blockPos, this, 1);
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		this.method_9997(world, blockPos, blockState);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			this.method_9994(world, blockPos);
			this.method_9997(world, blockPos, blockState);
		}
	}

	protected void method_9994(World world, BlockPos blockPos) {
	}

	protected void method_9997(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		world.updateNeighbor(blockPos2, this, blockPos);
		world.updateNeighborsExcept(blockPos2, this, direction);
	}

	protected boolean isValidInput(BlockState blockState) {
		return blockState.emitsRedstonePower();
	}

	protected int getOutputLevel(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return 15;
	}

	public static boolean method_9999(BlockState blockState) {
		return blockState.getBlock() instanceof AbstractRedstoneGateBlock;
	}

	public boolean method_9988(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = ((Direction)blockState.get(FACING)).getOpposite();
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		return method_9999(blockState2) && blockState2.get(FACING) != direction;
	}

	protected abstract int getUpdateDelayInternal(BlockState blockState);

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}
}
