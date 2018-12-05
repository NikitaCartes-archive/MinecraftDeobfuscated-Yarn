package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape DEPRESSED_SHAPE = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final BoundingBox BOX = new BoundingBox(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected AbstractPressurePlateBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.getRedstoneOutput(blockState) > 0 ? DEPRESSED_SHAPE : DEFAULT_SHAPE;
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
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState2.hasSolidTopSurface(viewableWorld, blockPos2) || blockState2.getBlock().matches(BlockTags.field_16584);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isRemote) {
			int i = this.getRedstoneOutput(blockState);
			if (i > 0) {
				this.updatePlateState(world, blockPos, blockState, i);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isRemote) {
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
			blockState = this.setRedstoneOutput(blockState, j);
			world.setBlockState(blockPos, blockState, 2);
			this.method_9437(world, blockPos);
			world.method_16109(blockPos);
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
				this.method_9437(world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	protected void method_9437(World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.down(), this);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.getRedstoneOutput(blockState);
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.UP ? this.getRedstoneOutput(blockState) : 0;
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
