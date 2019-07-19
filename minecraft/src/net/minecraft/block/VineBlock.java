package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VineBlock extends Block {
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
	protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public VineBlock(Block.Settings settings) {
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
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)state.get(UP)) {
			voxelShape = VoxelShapes.union(voxelShape, UP_SHAPE);
		}

		if ((Boolean)state.get(NORTH)) {
			voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
		}

		if ((Boolean)state.get(EAST)) {
			voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
		}

		if ((Boolean)state.get(SOUTH)) {
			voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
		}

		if ((Boolean)state.get(WEST)) {
			voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
		}

		return voxelShape;
	}

	@Override
	public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
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
				return blockState.getBlock() == this && (Boolean)blockState.get(booleanProperty);
			}
		}
	}

	public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
		BlockState blockState = world.getBlockState(pos);
		return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction.getOpposite());
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

					bl = blockState.getBlock() == this && (Boolean)blockState.get(booleanProperty);
				}

				state = state.with(booleanProperty, Boolean.valueOf(bl));
			}
		}

		return state;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		} else {
			BlockState blockState = this.getPlacementShape(state, world, pos);
			return !this.hasAdjacentBlocks(blockState) ? Blocks.AIR.getDefaultState() : blockState;
		}
	}

	@Override
	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			BlockState blockState = this.getPlacementShape(state, world, pos);
			if (blockState != state) {
				if (this.hasAdjacentBlocks(blockState)) {
					world.setBlockState(pos, blockState, 2);
				} else {
					dropStacks(state, world, pos);
					world.removeBlock(pos, false);
				}
			} else if (world.random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos = pos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)state.get(getFacingProperty(direction))) {
					if (this.canGrowAt(world, pos)) {
						BlockPos blockPos2 = pos.offset(direction);
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)state.get(getFacingProperty(direction2));
							boolean bl2 = (Boolean)state.get(getFacingProperty(direction3));
							BlockPos blockPos3 = blockPos2.offset(direction2);
							BlockPos blockPos4 = blockPos2.offset(direction3);
							if (bl && shouldConnectTo(world, blockPos3, direction2)) {
								world.setBlockState(blockPos2, this.getDefaultState().with(getFacingProperty(direction2), Boolean.valueOf(true)), 2);
							} else if (bl2 && shouldConnectTo(world, blockPos4, direction3)) {
								world.setBlockState(blockPos2, this.getDefaultState().with(getFacingProperty(direction3), Boolean.valueOf(true)), 2);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.isAir(blockPos3) && shouldConnectTo(world, pos.offset(direction2), direction4)) {
									world.setBlockState(blockPos3, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), 2);
								} else if (bl2 && world.isAir(blockPos4) && shouldConnectTo(world, pos.offset(direction3), direction4)) {
									world.setBlockState(blockPos4, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), 2);
								} else if ((double)world.random.nextFloat() < 0.05 && shouldConnectTo(world, blockPos2.up(), Direction.UP)) {
									world.setBlockState(blockPos2, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
								}
							}
						} else if (shouldConnectTo(world, blockPos2, direction)) {
							world.setBlockState(pos, state.with(getFacingProperty(direction), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (direction == Direction.UP && pos.getY() < 255) {
						if (this.shouldHaveSide(world, pos, direction)) {
							world.setBlockState(pos, state.with(UP, Boolean.valueOf(true)), 2);
							return;
						}

						if (world.isAir(blockPos)) {
							if (!this.canGrowAt(world, pos)) {
								return;
							}

							BlockState blockState3 = state;

							for (Direction direction2 : Direction.Type.HORIZONTAL) {
								if (random.nextBoolean() || !shouldConnectTo(world, blockPos.offset(direction2), Direction.UP)) {
									blockState3 = blockState3.with(getFacingProperty(direction2), Boolean.valueOf(false));
								}
							}

							if (this.hasHorizontalSide(blockState3)) {
								world.setBlockState(blockPos, blockState3, 2);
							}

							return;
						}
					}

					if (pos.getY() > 0) {
						BlockPos blockPos2 = pos.down();
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.isAir() || blockState2.getBlock() == this) {
							BlockState blockState4 = blockState2.isAir() ? this.getDefaultState() : blockState2;
							BlockState blockState5 = this.getGrownState(state, blockState4, random);
							if (blockState4 != blockState5 && this.hasHorizontalSide(blockState5)) {
								world.setBlockState(blockPos2, blockState5, 2);
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
			if (world.getBlockState(blockPos).getBlock() == this) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		return blockState.getBlock() == this ? this.getAdjacentBlockCount(blockState) < FACING_PROPERTIES.size() : super.canReplace(state, ctx);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		boolean bl = blockState.getBlock() == this;
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
	public RenderLayer getRenderLayer() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	public static BooleanProperty getFacingProperty(Direction direction) {
		return (BooleanProperty)FACING_PROPERTIES.get(direction);
	}
}
