package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final Box BOX = new Box(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected AbstractPressurePlateBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.getRedstoneOutput(blockState) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 20;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return isSolidMediumSquare(viewableWorld, blockPos2) || isSolidSmallSquare(viewableWorld, blockPos2, Direction.field_11036);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(blockState);
			if (i > 0) {
				this.updatePlateState(world, blockPos, blockState, i);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(blockState);
			if (i == 0) {
				this.updatePlateState(world, blockPos, blockState, i);
			}
		}
	}

	protected void updatePlateState(World world, BlockPos blockPos, BlockState blockState, int i) {
		int j = this.getRedstoneOutput(world, blockPos);
		boolean bl = i > 0;
		boolean bl2 = j > 0;
		if (i != j) {
			BlockState blockState2 = this.setRedstoneOutput(blockState, j);
			world.setBlockState(blockPos, blockState2, 2);
			this.updateNeighbors(world, blockPos);
			world.scheduleBlockRender(blockPos, blockState, blockState2);
		}

		if (!bl2 && bl) {
			this.playDepressSound(world, blockPos);
		} else if (bl2 && !bl) {
			this.playPressSound(world, blockPos);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	protected abstract void playPressSound(IWorld iWorld, BlockPos blockPos);

	protected abstract void playDepressSound(IWorld iWorld, BlockPos blockPos);

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if (this.getRedstoneOutput(blockState) > 0) {
				this.updateNeighbors(world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	protected void updateNeighbors(World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.down(), this);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getRedstoneOutput(blockState);
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.field_11036 ? this.getRedstoneOutput(blockState) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	protected abstract int getRedstoneOutput(World world, BlockPos blockPos);

	protected abstract int getRedstoneOutput(BlockState blockState);

	protected abstract BlockState setRedstoneOutput(BlockState blockState, int i);
}
