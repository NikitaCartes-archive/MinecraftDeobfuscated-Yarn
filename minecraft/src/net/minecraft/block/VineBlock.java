package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class VineBlock extends Block {
	public static final MapCodec<VineBlock> CODEC = createCodec(VineBlock::new);
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(Util.toMap());
	protected static final float field_31275 = 1.0F;
	private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private final Map<BlockState, VoxelShape> shapesByState;

	@Override
	public MapCodec<VineBlock> getCodec() {
		return CODEC;
	}

	public VineBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(UP, Boolean.valueOf(false))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
		);
		this.shapesByState = ImmutableMap.copyOf(
			(Map<? extends BlockState, ? extends VoxelShape>)this.stateManager
				.getStates()
				.stream()
				.collect(Collectors.toMap(Function.identity(), VineBlock::getShapeForState))
		);
	}

	private static VoxelShape getShapeForState(BlockState state) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)state.get(UP)) {
			voxelShape = UP_SHAPE;
		}

		if ((Boolean)state.get(NORTH)) {
			voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
		}

		if ((Boolean)state.get(SOUTH)) {
			voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
		}

		if ((Boolean)state.get(EAST)) {
			voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
		}

		if ((Boolean)state.get(WEST)) {
			voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
		}

		return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.shapesByState.get(state);
	}

	@Override
	protected boolean isTransparent(BlockState state) {
		return true;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos));
	}

	private boolean hasAdjacentBlocks(BlockState state) {
		return this.getAdjacentBlockCount(state) > 0;
	}

	private int getAdjacentBlockCount(BlockState state) {
		int i = 0;

		for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
			if ((Boolean)state.get(booleanProperty)) {
				i++;
			}
		}

		return i;
	}

	private boolean shouldHaveSide(BlockView world, BlockPos pos, Direction side) {
		if (side == Direction.DOWN) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(side);
			if (shouldConnectTo(world, blockPos, side)) {
				return true;
			} else if (side.getAxis() == Direction.Axis.Y) {
				return false;
			} else {
				BooleanProperty booleanProperty = (BooleanProperty)FACING_PROPERTIES.get(side);
				BlockState blockState = world.getBlockState(pos.up());
				return blockState.isOf(this) && (Boolean)blockState.get(booleanProperty);
			}
		}
	}

	public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
		return MultifaceGrowthBlock.canGrowOn(world, direction, pos, world.getBlockState(pos));
	}

	private BlockState getPlacementShape(BlockState state, BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		if ((Boolean)state.get(UP)) {
			state = state.with(UP, Boolean.valueOf(shouldConnectTo(world, blockPos, Direction.DOWN)));
		}

		BlockState blockState = null;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BooleanProperty booleanProperty = getFacingProperty(direction);
			if ((Boolean)state.get(booleanProperty)) {
				boolean bl = this.shouldHaveSide(world, pos, direction);
				if (!bl) {
					if (blockState == null) {
						blockState = world.getBlockState(blockPos);
					}

					bl = blockState.isOf(this) && (Boolean)blockState.get(booleanProperty);
				}

				state = state.with(booleanProperty, Boolean.valueOf(bl));
			}
		}

		return state;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			BlockState blockState = this.getPlacementShape(state, world, pos);
			return !this.hasAdjacentBlocks(blockState) ? Blocks.AIR.getDefaultState() : blockState;
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getGameRules().getBoolean(GameRules.DO_VINES_SPREAD)) {
			if (random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos = pos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)state.get(getFacingProperty(direction))) {
					if (this.canGrowAt(world, pos)) {
						BlockPos blockPos2 = pos.offset(direction);
						BlockState blockState = world.getBlockState(blockPos2);
						if (blockState.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)state.get(getFacingProperty(direction2));
							boolean bl2 = (Boolean)state.get(getFacingProperty(direction3));
							BlockPos blockPos3 = blockPos2.offset(direction2);
							BlockPos blockPos4 = blockPos2.offset(direction3);
							if (bl && shouldConnectTo(world, blockPos3, direction2)) {
								world.setBlockState(blockPos2, this.getDefaultState().with(getFacingProperty(direction2), Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
							} else if (bl2 && shouldConnectTo(world, blockPos4, direction3)) {
								world.setBlockState(blockPos2, this.getDefaultState().with(getFacingProperty(direction3), Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.isAir(blockPos3) && shouldConnectTo(world, pos.offset(direction2), direction4)) {
									world.setBlockState(blockPos3, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
								} else if (bl2 && world.isAir(blockPos4) && shouldConnectTo(world, pos.offset(direction3), direction4)) {
									world.setBlockState(blockPos4, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
								} else if ((double)random.nextFloat() < 0.05 && shouldConnectTo(world, blockPos2.up(), Direction.UP)) {
									world.setBlockState(blockPos2, this.getDefaultState().with(UP, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
								}
							}
						} else if (shouldConnectTo(world, blockPos2, direction)) {
							world.setBlockState(pos, state.with(getFacingProperty(direction), Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
						}
					}
				} else {
					if (direction == Direction.UP && pos.getY() < world.getTopYInclusive()) {
						if (this.shouldHaveSide(world, pos, direction)) {
							world.setBlockState(pos, state.with(UP, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
							return;
						}

						if (world.isAir(blockPos)) {
							if (!this.canGrowAt(world, pos)) {
								return;
							}

							BlockState blockState2 = state;

							for (Direction direction2 : Direction.Type.HORIZONTAL) {
								if (random.nextBoolean() || !shouldConnectTo(world, blockPos.offset(direction2), direction2)) {
									blockState2 = blockState2.with(getFacingProperty(direction2), Boolean.valueOf(false));
								}
							}

							if (this.hasHorizontalSide(blockState2)) {
								world.setBlockState(blockPos, blockState2, Block.NOTIFY_LISTENERS);
							}

							return;
						}
					}

					if (pos.getY() > world.getBottomY()) {
						BlockPos blockPos2 = pos.down();
						BlockState blockState = world.getBlockState(blockPos2);
						if (blockState.isAir() || blockState.isOf(this)) {
							BlockState blockState3 = blockState.isAir() ? this.getDefaultState() : blockState;
							BlockState blockState4 = this.getGrownState(state, blockState3, random);
							if (blockState3 != blockState4 && this.hasHorizontalSide(blockState4)) {
								world.setBlockState(blockPos2, blockState4, Block.NOTIFY_LISTENERS);
							}
						}
					}
				}
			}
		}
	}

	private BlockState getGrownState(BlockState above, BlockState state, Random random) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (random.nextBoolean()) {
				BooleanProperty booleanProperty = getFacingProperty(direction);
				if ((Boolean)above.get(booleanProperty)) {
					state = state.with(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return state;
	}

	private boolean hasHorizontalSide(BlockState state) {
		return (Boolean)state.get(NORTH) || (Boolean)state.get(EAST) || (Boolean)state.get(SOUTH) || (Boolean)state.get(WEST);
	}

	private boolean canGrowAt(BlockView world, BlockPos pos) {
		int i = 4;
		Iterable<BlockPos> iterable = BlockPos.iterate(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
		int j = 5;

		for (BlockPos blockPos : iterable) {
			if (world.getBlockState(blockPos).isOf(this)) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		return blockState.isOf(this) ? this.getAdjacentBlockCount(blockState) < FACING_PROPERTIES.size() : super.canReplace(state, context);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		boolean bl = blockState.isOf(this);
		BlockState blockState2 = bl ? blockState : this.getDefaultState();

		for (Direction direction : ctx.getPlacementDirections()) {
			if (direction != Direction.DOWN) {
				BooleanProperty booleanProperty = getFacingProperty(direction);
				boolean bl2 = bl && (Boolean)blockState.get(booleanProperty);
				if (!bl2 && this.shouldHaveSide(ctx.getWorld(), ctx.getBlockPos(), direction)) {
					return blockState2.with(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return bl ? blockState2 : null;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, (Boolean)state.get(SOUTH))
					.with(EAST, (Boolean)state.get(WEST))
					.with(SOUTH, (Boolean)state.get(NORTH))
					.with(WEST, (Boolean)state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(EAST))
					.with(EAST, (Boolean)state.get(SOUTH))
					.with(SOUTH, (Boolean)state.get(WEST))
					.with(WEST, (Boolean)state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(WEST))
					.with(EAST, (Boolean)state.get(NORTH))
					.with(SOUTH, (Boolean)state.get(EAST))
					.with(WEST, (Boolean)state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, (Boolean)state.get(SOUTH)).with(SOUTH, (Boolean)state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, (Boolean)state.get(WEST)).with(WEST, (Boolean)state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	public static BooleanProperty getFacingProperty(Direction direction) {
		return (BooleanProperty)FACING_PROPERTIES.get(direction);
	}
}
