package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
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
	public static final BooleanProperty field_11703 = ConnectedPlantBlock.UP;
	public static final BooleanProperty field_11706 = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty field_11702 = ConnectedPlantBlock.EAST;
	public static final BooleanProperty field_11699 = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty field_11696 = ConnectedPlantBlock.WEST;
	public static final Map<Direction, BooleanProperty> field_11697 = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(SystemUtil.toMap());
	protected static final VoxelShape field_11698 = Block.createCubeShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11705 = Block.createCubeShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape field_11704 = Block.createCubeShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11700 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape field_11701 = Block.createCubeShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public VineBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11703, Boolean.valueOf(false))
				.with(field_11706, Boolean.valueOf(false))
				.with(field_11702, Boolean.valueOf(false))
				.with(field_11699, Boolean.valueOf(false))
				.with(field_11696, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)blockState.get(field_11703)) {
			voxelShape = VoxelShapes.union(voxelShape, field_11698);
		}

		if ((Boolean)blockState.get(field_11706)) {
			voxelShape = VoxelShapes.union(voxelShape, field_11700);
		}

		if ((Boolean)blockState.get(field_11702)) {
			voxelShape = VoxelShapes.union(voxelShape, field_11704);
		}

		if ((Boolean)blockState.get(field_11699)) {
			voxelShape = VoxelShapes.union(voxelShape, field_11701);
		}

		if ((Boolean)blockState.get(field_11696)) {
			voxelShape = VoxelShapes.union(voxelShape, field_11705);
		}

		return voxelShape;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.method_10823(this.method_10827(blockState, viewableWorld, blockPos));
	}

	private boolean method_10823(BlockState blockState) {
		return this.method_10822(blockState) > 0;
	}

	private int method_10822(BlockState blockState) {
		int i = 0;

		for (BooleanProperty booleanProperty : field_11697.values()) {
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
			if (method_10821(blockView, blockPos2, direction)) {
				return true;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				return false;
			} else {
				BooleanProperty booleanProperty = (BooleanProperty)field_11697.get(direction);
				BlockState blockState = blockView.getBlockState(blockPos.up());
				return blockState.getBlock() == this && (Boolean)blockState.get(booleanProperty);
			}
		}
	}

	public static boolean method_10821(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return Block.isFaceFullCube(blockState.getCollisionShape(blockView, blockPos), direction.getOpposite()) && !method_10825(blockState.getBlock());
	}

	protected static boolean method_10825(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof StainedGlassBlock
			|| block == Blocks.field_10327
			|| block == Blocks.field_10593
			|| block == Blocks.field_10033
			|| block == Blocks.field_10560
			|| block == Blocks.field_10615
			|| block == Blocks.field_10379
			|| block.matches(BlockTags.field_15491);
	}

	private BlockState method_10827(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		if ((Boolean)blockState.get(field_11703)) {
			blockState = blockState.with(field_11703, Boolean.valueOf(method_10821(blockView, blockPos2, Direction.DOWN)));
		}

		BlockState blockState2 = null;

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			BooleanProperty booleanProperty = method_10828(direction);
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
			BlockState blockState3 = this.method_10827(blockState, iWorld, blockPos);
			return !this.method_10823(blockState3) ? Blocks.field_10124.getDefaultState() : blockState3;
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			BlockState blockState2 = this.method_10827(blockState, world, blockPos);
			if (blockState2 != blockState) {
				if (this.method_10823(blockState2)) {
					world.setBlockState(blockPos, blockState2, 2);
				} else {
					dropStacks(blockState, world, blockPos);
					world.clearBlockState(blockPos);
				}
			} else if (world.random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos2 = blockPos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)blockState.get(method_10828(direction))) {
					if (this.method_10824(world, blockPos)) {
						BlockPos blockPos3 = blockPos.offset(direction);
						BlockState blockState3 = world.getBlockState(blockPos3);
						if (blockState3.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)blockState.get(method_10828(direction2));
							boolean bl2 = (Boolean)blockState.get(method_10828(direction3));
							BlockPos blockPos4 = blockPos3.offset(direction2);
							BlockPos blockPos5 = blockPos3.offset(direction3);
							if (bl && method_10821(world, blockPos4, direction2)) {
								world.setBlockState(blockPos3, this.getDefaultState().with(method_10828(direction2), Boolean.valueOf(true)), 2);
							} else if (bl2 && method_10821(world, blockPos5, direction3)) {
								world.setBlockState(blockPos3, this.getDefaultState().with(method_10828(direction3), Boolean.valueOf(true)), 2);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.isAir(blockPos4) && method_10821(world, blockPos.offset(direction2), direction4)) {
									world.setBlockState(blockPos4, this.getDefaultState().with(method_10828(direction4), Boolean.valueOf(true)), 2);
								} else if (bl2 && world.isAir(blockPos5) && method_10821(world, blockPos.offset(direction3), direction4)) {
									world.setBlockState(blockPos5, this.getDefaultState().with(method_10828(direction4), Boolean.valueOf(true)), 2);
								} else if ((double)world.random.nextFloat() < 0.05 && method_10821(world, blockPos3.up(), Direction.UP)) {
									world.setBlockState(blockPos3, this.getDefaultState().with(field_11703, Boolean.valueOf(true)), 2);
								}
							}
						} else if (method_10821(world, blockPos3, direction)) {
							world.setBlockState(blockPos, blockState.with(method_10828(direction), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (direction == Direction.UP && blockPos.getY() < 255) {
						if (this.method_10829(world, blockPos, direction)) {
							world.setBlockState(blockPos, blockState.with(field_11703, Boolean.valueOf(true)), 2);
							return;
						}

						if (world.isAir(blockPos2)) {
							if (!this.method_10824(world, blockPos)) {
								return;
							}

							BlockState blockState4 = blockState;

							for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
								if (random.nextBoolean() || !method_10821(world, blockPos2.offset(direction2), Direction.UP)) {
									blockState4 = blockState4.with(method_10828(direction2), Boolean.valueOf(false));
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
		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			if (random.nextBoolean()) {
				BooleanProperty booleanProperty = method_10828(direction);
				if ((Boolean)blockState.get(booleanProperty)) {
					blockState2 = blockState2.with(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return blockState2;
	}

	private boolean method_10830(BlockState blockState) {
		return (Boolean)blockState.get(field_11706)
			|| (Boolean)blockState.get(field_11702)
			|| (Boolean)blockState.get(field_11699)
			|| (Boolean)blockState.get(field_11696);
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
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		BlockState blockState2 = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		return blockState2.getBlock() == this ? this.method_10822(blockState2) < field_11697.size() : super.method_9616(blockState, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		boolean bl = blockState.getBlock() == this;
		BlockState blockState2 = bl ? blockState : this.getDefaultState();

		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			if (direction != Direction.DOWN) {
				BooleanProperty booleanProperty = method_10828(direction);
				boolean bl2 = bl && (Boolean)blockState.get(booleanProperty);
				if (!bl2 && this.method_10829(itemPlacementContext.getWorld(), itemPlacementContext.getPos(), direction)) {
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
		builder.with(field_11703, field_11706, field_11702, field_11699, field_11696);
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.with(field_11706, blockState.get(field_11699))
					.with(field_11702, blockState.get(field_11696))
					.with(field_11699, blockState.get(field_11706))
					.with(field_11696, blockState.get(field_11702));
			case ROT_270:
				return blockState.with(field_11706, blockState.get(field_11702))
					.with(field_11702, blockState.get(field_11699))
					.with(field_11699, blockState.get(field_11696))
					.with(field_11696, blockState.get(field_11706));
			case ROT_90:
				return blockState.with(field_11706, blockState.get(field_11696))
					.with(field_11702, blockState.get(field_11706))
					.with(field_11699, blockState.get(field_11702))
					.with(field_11696, blockState.get(field_11699));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.with(field_11706, blockState.get(field_11699)).with(field_11699, blockState.get(field_11706));
			case FRONT_BACK:
				return blockState.with(field_11702, blockState.get(field_11696)).with(field_11696, blockState.get(field_11702));
			default:
				return super.applyMirror(blockState, mirror);
		}
	}

	public static BooleanProperty method_10828(Direction direction) {
		return (BooleanProperty)field_11697.get(direction);
	}
}
