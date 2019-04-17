package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends WallMountedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final VoxelShape SHAPE_CEILING_X = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape SHAPE_CEILING_Z = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape SHAPE_FLOOR_X = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final VoxelShape SHAPE_FLOOR_Z = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final VoxelShape SHAPE_CEILING_X_POWERED = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape SHAPE_CEILING_Z_POWERED = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape SHAPE_FLOOR_X_POWERED = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final VoxelShape SHAPE_FLOOR_Z_POWERED = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final VoxelShape SHAPE_NORTH_POWERED = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SHAPE_SOUTH_POWERED = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final VoxelShape SHAPE_WEST_POWERED = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape SHAPE_EAST_POWERED = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean wooden;

	protected AbstractButtonBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, WallMountLocation.field_12471)
		);
		this.wooden = bl;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.wooden ? 30 : 20;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.get(FACING);
		boolean bl = (Boolean)blockState.get(POWERED);
		switch ((WallMountLocation)blockState.get(FACE)) {
			case field_12475:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? SHAPE_FLOOR_X_POWERED : SHAPE_FLOOR_X;
				}

				return bl ? SHAPE_FLOOR_Z_POWERED : SHAPE_FLOOR_Z;
			case field_12471:
				switch (direction) {
					case EAST:
						return bl ? SHAPE_EAST_POWERED : SHAPE_EAST;
					case WEST:
						return bl ? SHAPE_WEST_POWERED : SHAPE_WEST;
					case SOUTH:
						return bl ? SHAPE_SOUTH_POWERED : SHAPE_SOUTH;
					case NORTH:
					default:
						return bl ? SHAPE_NORTH_POWERED : SHAPE_NORTH;
				}
			case field_12473:
			default:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? SHAPE_CEILING_X_POWERED : SHAPE_CEILING_X;
				} else {
					return bl ? SHAPE_CEILING_Z_POWERED : SHAPE_CEILING_Z;
				}
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.get(POWERED)) {
			return true;
		} else {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 3);
			this.playClickSound(playerEntity, world, blockPos, true);
			this.updateNeighbors(blockState, world, blockPos);
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
			return true;
		}
	}

	protected void playClickSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos, boolean bl) {
		iWorld.playSound(bl ? playerEntity : null, blockPos, this.getClickSound(bl), SoundCategory.field_15245, 0.3F, bl ? 0.6F : 0.5F);
	}

	protected abstract SoundEvent getClickSound(boolean bl);

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(POWERED)) {
				this.updateNeighbors(blockState, world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) && getDirection(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient && (Boolean)blockState.get(POWERED)) {
			if (this.wooden) {
				this.tryPowerWithProjectiles(blockState, world, blockPos);
			} else {
				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)), 3);
				this.updateNeighbors(blockState, world, blockPos);
				this.playClickSound(null, world, blockPos, false);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && this.wooden && !(Boolean)blockState.get(POWERED)) {
			this.tryPowerWithProjectiles(blockState, world, blockPos);
		}
	}

	private void tryPowerWithProjectiles(BlockState blockState, World world, BlockPos blockPos) {
		List<? extends Entity> list = world.getEntities(ProjectileEntity.class, blockState.getOutlineShape(world, blockPos).getBoundingBox().offset(blockPos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)blockState.get(POWERED);
		if (bl != bl2) {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl)), 3);
			this.updateNeighbors(blockState, world, blockPos);
			this.playClickSound(null, world, blockPos, bl);
		}

		if (bl) {
			world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	private void updateNeighbors(BlockState blockState, World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(getDirection(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, POWERED, FACE);
	}
}
