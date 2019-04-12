package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class VineBlock extends Block {
	public static final BooleanProperty UP = ConnectedPlantBlock.UP;
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(SystemUtil.toMap());
	protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public VineBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(UP, Boolean.valueOf(false))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)blockState.get(UP)) {
			voxelShape = VoxelShapes.union(voxelShape, UP_SHAPE);
		}

		if ((Boolean)blockState.get(NORTH)) {
			voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
		}

		if ((Boolean)blockState.get(EAST)) {
			voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
		}

		if ((Boolean)blockState.get(SOUTH)) {
			voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
		}

		if ((Boolean)blockState.get(WEST)) {
			voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
		}

		return voxelShape;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.hasAdjacentBlocks(this.getPlacementShape(blockState, viewableWorld, blockPos));
	}

	private boolean hasAdjacentBlocks(BlockState blockState) {
		return this.getAdjacentBlockCount(blockState) > 0;
	}

	private int getAdjacentBlockCount(BlockState blockState) {
		int i = 0;

		for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
			if ((Boolean)blockState.get(booleanProperty)) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10829(BlockView blockView, BlockPos blockPos, Direction direction) {
		if (direction == Direction.DOWN) {
			return false;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (shouldConnectTo(blockView, blockPos2, direction)) {
				return true;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				return false;
			} else {
				BooleanProperty booleanProperty = (BooleanProperty)FACING_PROPERTIES.get(direction);
				BlockState blockState = blockView.getBlockState(blockPos.up());
				return blockState.getBlock() == this && (Boolean)blockState.get(booleanProperty);
			}
		}
	}

	public static boolean shouldConnectTo(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos), direction.getOpposite());
	}

	private BlockState getPlacementShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		if ((Boolean)blockState.get(UP)) {
			blockState = blockState.with(UP, Boolean.valueOf(shouldConnectTo(blockView, blockPos2, Direction.DOWN)));
		}

		BlockState blockState2 = null;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BooleanProperty booleanProperty = getFacingProperty(direction);
			if ((Boolean)blockState.get(booleanProperty)) {
				boolean bl = this.method_10829(blockView, blockPos, direction);
				if (!bl) {
					if (blockState2 == null) {
						blockState2 = blockView.getBlockState(blockPos2);
					}

					bl = blockState2.getBlock() == this && (Boolean)blockState2.get(booleanProperty);
				}

				blockState = blockState.with(booleanProperty, Boolean.valueOf(bl));
			}
		}

		return blockState;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			BlockState blockState3 = this.getPlacementShape(blockState, iWorld, blockPos);
			return !this.hasAdjacentBlocks(blockState3) ? Blocks.AIR.getDefaultState() : blockState3;
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			BlockState blockState2 = this.getPlacementShape(blockState, world, blockPos);
			if (blockState2 != blockState) {
				if (this.hasAdjacentBlocks(blockState2)) {
					world.setBlockState(blockPos, blockState2, 2);
				} else {
					dropStacks(blockState, world, blockPos);
					world.clearBlockState(blockPos, false);
				}
			} else if (world.random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos2 = blockPos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)blockState.get(getFacingProperty(direction))) {
					if (this.method_10824(world, blockPos)) {
						BlockPos blockPos3 = blockPos.offset(direction);
						BlockState blockState3 = world.getBlockState(blockPos3);
						if (blockState3.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)blockState.get(getFacingProperty(direction2));
							boolean bl2 = (Boolean)blockState.get(getFacingProperty(direction3));
							BlockPos blockPos4 = blockPos3.offset(direction2);
							BlockPos blockPos5 = blockPos3.offset(direction3);
							if (bl && shouldConnectTo(world, blockPos4, direction2)) {
								world.setBlockState(blockPos3, this.getDefaultState().with(getFacingProperty(direction2), Boolean.valueOf(true)), 2);
							} else if (bl2 && shouldConnectTo(world, blockPos5, direction3)) {
								world.setBlockState(blockPos3, this.getDefaultState().with(getFacingProperty(direction3), Boolean.valueOf(true)), 2);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.isAir(blockPos4) && shouldConnectTo(world, blockPos.offset(direction2), direction4)) {
									world.setBlockState(blockPos4, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), 2);
								} else if (bl2 && world.isAir(blockPos5) && shouldConnectTo(world, blockPos.offset(direction3), direction4)) {
									world.setBlockState(blockPos5, this.getDefaultState().with(getFacingProperty(direction4), Boolean.valueOf(true)), 2);
								} else if ((double)world.random.nextFloat() < 0.05 && shouldConnectTo(world, blockPos3.up(), Direction.UP)) {
									world.setBlockState(blockPos3, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
								}
							}
						} else if (shouldConnectTo(world, blockPos3, direction)) {
							world.setBlockState(blockPos, blockState.with(getFacingProperty(direction), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (direction == Direction.UP && blockPos.getY() < 255) {
						if (this.method_10829(world, blockPos, direction)) {
							world.setBlockState(blockPos, blockState.with(UP, Boolean.valueOf(true)), 2);
							return;
						}

						if (world.isAir(blockPos2)) {
							if (!this.method_10824(world, blockPos)) {
								return;
							}

							BlockState blockState4 = blockState;

							for (Direction direction2 : Direction.Type.HORIZONTAL) {
								if (random.nextBoolean() || !shouldConnectTo(world, blockPos2.offset(direction2), Direction.UP)) {
									blockState4 = blockState4.with(getFacingProperty(direction2), Boolean.valueOf(false));
								}
							}

							if (this.method_10830(blockState4)) {
								world.setBlockState(blockPos2, blockState4, 2);
							}

							return;
						}
					}

					if (blockPos.getY() > 0) {
						BlockPos blockPos3 = blockPos.down();
						BlockState blockState3 = world.getBlockState(blockPos3);
						if (blockState3.isAir() || blockState3.getBlock() == this) {
							BlockState blockState5 = blockState3.isAir() ? this.getDefaultState() : blockState3;
							BlockState blockState6 = this.method_10820(blockState, blockState5, random);
							if (blockState5 != blockState6 && this.method_10830(blockState6)) {
								world.setBlockState(blockPos3, blockState6, 2);
							}
						}
					}
				}
			}
		}
	}

	private BlockState method_10820(BlockState blockState, BlockState blockState2, Random random) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (random.nextBoolean()) {
				BooleanProperty booleanProperty = getFacingProperty(direction);
				if ((Boolean)blockState.get(booleanProperty)) {
					blockState2 = blockState2.with(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return blockState2;
	}

	private boolean method_10830(BlockState blockState) {
		return (Boolean)blockState.get(NORTH) || (Boolean)blockState.get(EAST) || (Boolean)blockState.get(SOUTH) || (Boolean)blockState.get(WEST);
	}

	private boolean method_10824(BlockView blockView, BlockPos blockPos) {
		int i = 4;
		Iterable<BlockPos> iterable = BlockPos.iterateBoxPositions(
			blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4
		);
		int j = 5;

		for (BlockPos blockPos2 : iterable) {
			if (blockView.getBlockState(blockPos2).getBlock() == this) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		BlockState blockState2 = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
		return blockState2.getBlock() == this
			? this.getAdjacentBlockCount(blockState2) < FACING_PROPERTIES.size()
			: super.canReplace(blockState, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
		boolean bl = blockState.getBlock() == this;
		BlockState blockState2 = bl ? blockState : this.getDefaultState();

		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			if (direction != Direction.DOWN) {
				BooleanProperty booleanProperty = getFacingProperty(direction);
				boolean bl2 = bl && (Boolean)blockState.get(booleanProperty);
				if (!bl2 && this.method_10829(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), direction)) {
					return blockState2.with(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return bl ? blockState2 : null;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(UP, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(EAST, blockState.get(WEST)).with(SOUTH, blockState.get(NORTH)).with(WEST, blockState.get(EAST));
			case ROT_270:
				return blockState.with(NORTH, blockState.get(EAST)).with(EAST, blockState.get(SOUTH)).with(SOUTH, blockState.get(WEST)).with(WEST, blockState.get(NORTH));
			case ROT_90:
				return blockState.with(NORTH, blockState.get(WEST)).with(EAST, blockState.get(NORTH)).with(SOUTH, blockState.get(EAST)).with(WEST, blockState.get(SOUTH));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(SOUTH, blockState.get(NORTH));
			case FRONT_BACK:
				return blockState.with(EAST, blockState.get(WEST)).with(WEST, blockState.get(EAST));
			default:
				return super.mirror(blockState, mirror);
		}
	}

	public static BooleanProperty getFacingProperty(Direction direction) {
		return (BooleanProperty)FACING_PROPERTIES.get(direction);
	}
}
