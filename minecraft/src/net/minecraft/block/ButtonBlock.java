package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class ButtonBlock extends WallMountedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	private static final int field_31040 = 1;
	private static final int field_31041 = 2;
	protected static final int field_31042 = 2;
	protected static final int field_31043 = 3;
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
	private final BlockSetType blockSetType;
	private final int pressTicks;
	private final boolean wooden;

	protected ButtonBlock(AbstractBlock.Settings settings, BlockSetType blockSetType, int pressTicks, boolean wooden) {
		super(settings.sounds(blockSetType.soundType()));
		this.blockSetType = blockSetType;
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, WallMountLocation.WALL)
		);
		this.pressTicks = pressTicks;
		this.wooden = wooden;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		boolean bl = (Boolean)state.get(POWERED);
		switch ((WallMountLocation)state.get(FACE)) {
			case FLOOR:
				if (direction.getAxis() == Direction.Axis.X) {
					return bl ? FLOOR_X_PRESSED_SHAPE : FLOOR_X_SHAPE;
				}

				return bl ? FLOOR_Z_PRESSED_SHAPE : FLOOR_Z_SHAPE;
			case WALL:
				return switch (direction) {
					case EAST -> bl ? EAST_PRESSED_SHAPE : EAST_SHAPE;
					case WEST -> bl ? WEST_PRESSED_SHAPE : WEST_SHAPE;
					case SOUTH -> bl ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
					case NORTH, UP, DOWN -> bl ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
				};
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
			this.powerOn(state, world, pos);
			this.playClickSound(player, world, pos, true);
			world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
			return ActionResult.success(world.isClient);
		}
	}

	public void powerOn(BlockState state, World world, BlockPos pos) {
		world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), Block.NOTIFY_ALL);
		this.updateNeighbors(state, world, pos);
		world.scheduleBlockTick(pos, this, this.pressTicks);
	}

	protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
		world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS);
	}

	protected SoundEvent getClickSound(boolean powered) {
		return powered ? this.blockSetType.buttonClickOn() : this.blockSetType.buttonClickOff();
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			if ((Boolean)state.get(POWERED)) {
				this.updateNeighbors(state, world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) && getDirection(state) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED)) {
			this.tryPowerWithProjectiles(state, world, pos);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && this.wooden && !(Boolean)state.get(POWERED)) {
			this.tryPowerWithProjectiles(state, world, pos);
		}
	}

	protected void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
		PersistentProjectileEntity persistentProjectileEntity = this.wooden
			? (PersistentProjectileEntity)world.getNonSpectatingEntities(
					PersistentProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos)
				)
				.stream()
				.findFirst()
				.orElse(null)
			: null;
		boolean bl = persistentProjectileEntity != null;
		boolean bl2 = (Boolean)state.get(POWERED);
		if (bl != bl2) {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_ALL);
			this.updateNeighbors(state, world, pos);
			this.playClickSound(null, world, pos, bl);
			world.emitGameEvent(persistentProjectileEntity, bl ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
		}

		if (bl) {
			world.scheduleBlockTick(new BlockPos(pos), this, this.pressTicks);
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
