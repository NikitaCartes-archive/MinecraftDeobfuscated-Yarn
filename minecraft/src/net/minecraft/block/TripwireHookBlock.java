package net.minecraft.block;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

public class TripwireHookBlock extends Block {
	public static final MapCodec<TripwireHookBlock> CODEC = createCodec(TripwireHookBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	protected static final int field_31268 = 1;
	protected static final int field_31269 = 42;
	private static final int SCHEDULED_TICK_DELAY = 10;
	protected static final int field_31270 = 3;
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	@Override
	public MapCodec<TripwireHookBlock> getCodec() {
		return CODEC;
	}

	public TripwireHookBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false))
		);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
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
		update(world, pos, state, false, false, -1, null);
	}

	public static void update(World world, BlockPos pos, BlockState state, boolean bl, boolean bl2, int i, @Nullable BlockState blockState) {
		Optional<Direction> optional = state.getOrEmpty(FACING);
		if (optional.isPresent()) {
			Direction direction = (Direction)optional.get();
			boolean bl3 = (Boolean)state.getOrEmpty(ATTACHED).orElse(false);
			boolean bl4 = (Boolean)state.getOrEmpty(POWERED).orElse(false);
			Block block = state.getBlock();
			boolean bl5 = !bl;
			boolean bl6 = false;
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
					bl5 = false;
				} else {
					if (k == i) {
						blockState2 = MoreObjects.firstNonNull(blockState, blockState2);
					}

					boolean bl7 = !(Boolean)blockState2.get(TripwireBlock.DISARMED);
					boolean bl8 = (Boolean)blockState2.get(TripwireBlock.POWERED);
					bl6 |= bl7 && bl8;
					blockStates[k] = blockState2;
					if (k == i) {
						world.scheduleBlockTick(pos, block, 10);
						bl5 &= bl7;
					}
				}
			}

			bl5 &= j > 1;
			bl6 &= bl5;
			BlockState blockState3 = block.getDefaultState().withIfExists(ATTACHED, Boolean.valueOf(bl5)).withIfExists(POWERED, Boolean.valueOf(bl6));
			if (j > 0) {
				BlockPos blockPosx = pos.offset(direction, j);
				Direction direction2 = direction.getOpposite();
				world.setBlockState(blockPosx, blockState3.with(FACING, direction2), Block.NOTIFY_ALL);
				updateNeighborsOnAxis(block, world, blockPosx, direction2);
				playSound(world, blockPosx, bl5, bl6, bl3, bl4);
			}

			playSound(world, pos, bl5, bl6, bl3, bl4);
			if (!bl) {
				world.setBlockState(pos, blockState3.with(FACING, direction), Block.NOTIFY_ALL);
				if (bl2) {
					updateNeighborsOnAxis(block, world, pos, direction);
				}
			}

			if (bl3 != bl5) {
				for (int l = 1; l < j; l++) {
					BlockPos blockPos2 = pos.offset(direction, l);
					BlockState blockState4 = blockStates[l];
					if (blockState4 != null) {
						BlockState blockState5 = world.getBlockState(blockPos2);
						if (blockState5.isOf(Blocks.TRIPWIRE) || blockState5.isOf(Blocks.TRIPWIRE_HOOK)) {
							world.setBlockState(blockPos2, blockState4.withIfExists(ATTACHED, Boolean.valueOf(bl5)), Block.NOTIFY_ALL);
						}
					}
				}
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		update(world, pos, state, false, true, -1, null);
	}

	private static void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
		if (on && !off) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
			world.emitGameEvent(null, GameEvent.BLOCK_ACTIVATE, pos);
		} else if (!on && off) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
			world.emitGameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
		} else if (attached && !detached) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
			world.emitGameEvent(null, GameEvent.BLOCK_ATTACH, pos);
		} else if (!attached && detached) {
			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
			world.emitGameEvent(null, GameEvent.BLOCK_DETACH, pos);
		}
	}

	private static void updateNeighborsOnAxis(Block block, World world, BlockPos pos, Direction direction) {
		Direction direction2 = direction.getOpposite();
		WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction2, Direction.UP);
		world.updateNeighborsAlways(pos, block, wireOrientation);
		world.updateNeighborsAlways(pos.offset(direction2), block, wireOrientation);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			boolean bl = (Boolean)state.get(ATTACHED);
			boolean bl2 = (Boolean)state.get(POWERED);
			if (bl || bl2) {
				update(world, pos, state, true, false, -1, null);
			}

			if (bl2) {
				updateNeighborsOnAxis(this, world, pos, state.get(FACING));
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, ATTACHED);
	}
}
