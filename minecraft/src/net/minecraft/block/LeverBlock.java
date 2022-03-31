package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class LeverBlock extends WallMountedBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final int field_31184 = 6;
	protected static final int field_31185 = 6;
	protected static final int field_31186 = 8;
	protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected LeverBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, WallMountLocation.WALL)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((WallMountLocation)state.get(FACE)) {
			case FLOOR:
				switch (((Direction)state.get(FACING)).getAxis()) {
					case X:
						return FLOOR_X_AXIS_SHAPE;
					case Z:
					default:
						return FLOOR_Z_AXIS_SHAPE;
				}
			case WALL:
				switch ((Direction)state.get(FACING)) {
					case EAST:
						return EAST_WALL_SHAPE;
					case WEST:
						return WEST_WALL_SHAPE;
					case SOUTH:
						return SOUTH_WALL_SHAPE;
					case NORTH:
					default:
						return NORTH_WALL_SHAPE;
				}
			case CEILING:
			default:
				switch (((Direction)state.get(FACING)).getAxis()) {
					case X:
						return CEILING_X_AXIS_SHAPE;
					case Z:
					default:
						return CEILING_Z_AXIS_SHAPE;
				}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			BlockState blockState = state.cycle(POWERED);
			if ((Boolean)blockState.get(POWERED)) {
				spawnParticles(blockState, world, pos, 1.0F);
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockState = this.togglePower(state, world, pos);
			float f = blockState.get(POWERED) ? 0.6F : 0.5F;
			world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
			world.emitGameEvent(player, blockState.get(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
			return ActionResult.CONSUME;
		}
	}

	public BlockState togglePower(BlockState state, World world, BlockPos pos) {
		state = state.cycle(POWERED);
		world.setBlockState(pos, state, Block.NOTIFY_ALL);
		this.updateNeighbors(state, world, pos);
		return state;
	}

	private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		Direction direction2 = getDirection(state).getOpposite();
		double d = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double f = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		world.addParticle(new DustParticleEffect(DustParticleEffect.RED, alpha), d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED) && random.nextFloat() < 0.25F) {
			spawnParticles(state, world, pos, 0.5F);
		}
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

	private void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACE, FACING, POWERED);
	}
}
