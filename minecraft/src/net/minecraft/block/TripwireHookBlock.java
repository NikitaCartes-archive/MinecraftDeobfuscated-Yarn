package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public TripwireHookBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction)state.get(FACING)) {
			case EAST:
			default:
				return WEST_SHAPE;
			case WEST:
				return EAST_SHAPE;
			case SOUTH:
				return NORTH_SHAPE;
			case NORTH:
				return SOUTH_SHAPE;
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false));
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		Direction[] directions = ctx.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(FACING, direction2);
				if (blockState.canPlaceAt(worldView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		this.update(world, pos, state, false, false, -1, null);
	}

	public void update(World world, BlockPos pos, BlockState state, boolean beingRemoved, boolean bl, int i, @Nullable BlockState blockState) {
		Direction direction = state.get(FACING);
		boolean bl2 = (Boolean)state.get(ATTACHED);
		boolean bl3 = (Boolean)state.get(POWERED);
		boolean bl4 = !beingRemoved;
		boolean bl5 = false;
		int j = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int k = 1; k < 42; k++) {
			BlockPos blockPos = pos.offset(direction, k);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (blockState2.isOf(Blocks.TRIPWIRE_HOOK)) {
				if (blockState2.get(FACING) == direction.getOpposite()) {
					j = k;
				}
				break;
			}

			if (!blockState2.isOf(Blocks.TRIPWIRE) && k != i) {
				blockStates[k] = null;
				bl4 = false;
			} else {
				if (k == i) {
					blockState2 = MoreObjects.firstNonNull(blockState, blockState2);
				}

				boolean bl6 = !(Boolean)blockState2.get(TripwireBlock.DISARMED);
				boolean bl7 = (Boolean)blockState2.get(TripwireBlock.POWERED);
				bl5 |= bl6 && bl7;
				blockStates[k] = blockState2;
				if (k == i) {
					world.getBlockTickScheduler().schedule(pos, this, 10);
					bl4 &= bl6;
				}
			}
		}

		bl4 &= j > 1;
		bl5 &= bl4;
		BlockState blockState3 = this.getDefaultState().with(ATTACHED, Boolean.valueOf(bl4)).with(POWERED, Boolean.valueOf(bl5));
		if (j > 0) {
			BlockPos blockPosx = pos.offset(direction, j);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPosx, blockState3.with(FACING, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPosx, direction2);
			this.playSound(world, blockPosx, bl4, bl5, bl2, bl3);
		}

		this.playSound(world, pos, bl4, bl5, bl2, bl3);
		if (!beingRemoved) {
			world.setBlockState(pos, blockState3.with(FACING, direction), 3);
			if (bl) {
				this.updateNeighborsOnAxis(world, pos, direction);
			}
		}

		if (bl2 != bl4) {
			for (int l = 1; l < j; l++) {
				BlockPos blockPos2 = pos.offset(direction, l);
				BlockState blockState4 = blockStates[l];
				if (blockState4 != null) {
					world.setBlockState(blockPos2, blockState4.with(ATTACHED, Boolean.valueOf(bl4)), 3);
					if (!world.getBlockState(blockPos2).isAir()) {
					}
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.update(world, pos, state, false, true, -1, null);
	}

	private void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
		if (on && !off) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
		} else if (!on && off) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
		} else if (attached && !detached) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (!attached && detached) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void updateNeighborsOnAxis(World world, BlockPos pos, Direction direction) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!notify && !state.isOf(newState.getBlock())) {
			boolean bl = (Boolean)state.get(ATTACHED);
			boolean bl2 = (Boolean)state.get(POWERED);
			if (bl || bl2) {
				this.update(world, pos, state, true, false, -1, null);
			}

			if (bl2) {
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.offset(((Direction)state.get(FACING)).getOpposite()), this);
			}

			super.onBlockRemoved(state, world, pos, newState, notify);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, ATTACHED);
	}
}
