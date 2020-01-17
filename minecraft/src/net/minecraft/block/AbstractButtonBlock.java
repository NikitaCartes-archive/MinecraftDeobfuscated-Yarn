package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractButtonBlock extends WallMountedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final VoxelShape CEILING_X_SHAPE = Block.createCuboidShape(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape CEILING_Z_SHAPE = Block.createCuboidShape(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape FLOOR_X_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final VoxelShape FLOOR_Z_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final VoxelShape CEILING_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final VoxelShape CEILING_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final VoxelShape FLOOR_X_PRESSED_SHAPE = Block.createCuboidShape(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final VoxelShape FLOOR_Z_PRESSED_SHAPE = Block.createCuboidShape(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final VoxelShape NORTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape SOUTH_PRESSED_SHAPE = Block.createCuboidShape(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final VoxelShape WEST_PRESSED_SHAPE = Block.createCuboidShape(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape EAST_PRESSED_SHAPE = Block.createCuboidShape(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean wooden;

	protected AbstractButtonBlock(boolean wooden, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, WallMountLocation.WALL)
		);
		this.wooden = wooden;
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return this.wooden ? 30 : 20;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Direction direction = state.get(FACING);
		boolean bl = (Boolean)state.get(POWERED);
		switch ((WallMountLocation)state.get(FACE)) {
			case FLOOR:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? FLOOR_X_PRESSED_SHAPE : FLOOR_X_SHAPE;
				}

				return bl ? FLOOR_Z_PRESSED_SHAPE : FLOOR_Z_SHAPE;
			case WALL:
				switch (direction) {
					case EAST:
						return bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
					case WEST:
						return bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
					case SOUTH:
						return bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
					case NORTH:
					default:
						return bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
				}
			case CEILING:
			default:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? CEILING_X_PRESSED_SHAPE : CEILING_X_SHAPE;
				} else {
					return bl ? CEILING_Z_PRESSED_SHAPE : CEILING_Z_SHAPE;
				}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if ((Boolean)state.get(POWERED)) {
			return ActionResult.CONSUME;
		} else {
			this.method_21845(state, world, pos);
			this.playClickSound(player, world, pos, true);
			return ActionResult.SUCCESS;
		}
	}

	public void method_21845(BlockState blockState, World world, BlockPos blockPos) {
		world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(true)), 3);
		this.updateNeighbors(blockState, world, blockPos);
		world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
	}

	protected void playClickSound(@Nullable PlayerEntity player, IWorld world, BlockPos pos, boolean powered) {
		world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS, 0.3F, powered ? 0.6F : 0.5F);
	}

	protected abstract SoundEvent getClickSound(boolean powered);

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			if ((Boolean)state.get(POWERED)) {
				this.updateNeighbors(state, world, pos);
			}

			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return state.get(POWERED) && getDirection(state) == facing ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED)) {
			if (this.wooden) {
				this.tryPowerWithProjectiles(state, world, pos);
			} else {
				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 3);
				this.updateNeighbors(state, world, pos);
				this.playClickSound(null, world, pos, false);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && this.wooden && !(Boolean)state.get(POWERED)) {
			this.tryPowerWithProjectiles(state, world, pos);
		}
	}

	private void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
		List<? extends Entity> list = world.getNonSpectatingEntities(ProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)state.get(POWERED);
		if (bl != bl2) {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), 3);
			this.updateNeighbors(state, world, pos);
			this.playClickSound(null, world, pos, bl);
		}

		if (bl) {
			world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickRate(world));
		}
	}

	private void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, FACE);
	}
}
