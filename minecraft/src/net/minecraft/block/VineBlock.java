package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
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
	public static final BooleanProperty field_11703 = ConnectedPlantBlock.field_11327;
	public static final BooleanProperty field_11706 = ConnectedPlantBlock.field_11332;
	public static final BooleanProperty field_11702 = ConnectedPlantBlock.field_11335;
	public static final BooleanProperty field_11699 = ConnectedPlantBlock.field_11331;
	public static final BooleanProperty field_11696 = ConnectedPlantBlock.field_11328;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.field_11033)
		.collect(SystemUtil.toMap());
	protected static final VoxelShape field_11698 = Block.method_9541(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11705 = Block.method_9541(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape field_11704 = Block.method_9541(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11700 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape field_11701 = Block.method_9541(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public VineBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11703, Boolean.valueOf(false))
				.method_11657(field_11706, Boolean.valueOf(false))
				.method_11657(field_11702, Boolean.valueOf(false))
				.method_11657(field_11699, Boolean.valueOf(false))
				.method_11657(field_11696, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		VoxelShape voxelShape = VoxelShapes.method_1073();
		if ((Boolean)blockState.method_11654(field_11703)) {
			voxelShape = VoxelShapes.method_1084(voxelShape, field_11698);
		}

		if ((Boolean)blockState.method_11654(field_11706)) {
			voxelShape = VoxelShapes.method_1084(voxelShape, field_11700);
		}

		if ((Boolean)blockState.method_11654(field_11702)) {
			voxelShape = VoxelShapes.method_1084(voxelShape, field_11704);
		}

		if ((Boolean)blockState.method_11654(field_11699)) {
			voxelShape = VoxelShapes.method_1084(voxelShape, field_11701);
		}

		if ((Boolean)blockState.method_11654(field_11696)) {
			voxelShape = VoxelShapes.method_1084(voxelShape, field_11705);
		}

		return voxelShape;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.method_10823(this.method_10827(blockState, viewableWorld, blockPos));
	}

	private boolean method_10823(BlockState blockState) {
		return this.method_10822(blockState) > 0;
	}

	private int method_10822(BlockState blockState) {
		int i = 0;

		for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
			if ((Boolean)blockState.method_11654(booleanProperty)) {
				i++;
			}
		}

		return i;
	}

	private boolean shouldHaveSide(BlockView blockView, BlockPos blockPos, Direction direction) {
		if (direction == Direction.field_11033) {
			return false;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (shouldConnectTo(blockView, blockPos2, direction)) {
				return true;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				return false;
			} else {
				BooleanProperty booleanProperty = (BooleanProperty)FACING_PROPERTIES.get(direction);
				BlockState blockState = blockView.method_8320(blockPos.up());
				return blockState.getBlock() == this && (Boolean)blockState.method_11654(booleanProperty);
			}
		}
	}

	public static boolean shouldConnectTo(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.method_8320(blockPos);
		return Block.method_9501(blockState.method_11628(blockView, blockPos), direction.getOpposite());
	}

	private BlockState method_10827(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		if ((Boolean)blockState.method_11654(field_11703)) {
			blockState = blockState.method_11657(field_11703, Boolean.valueOf(shouldConnectTo(blockView, blockPos2, Direction.field_11033)));
		}

		BlockState blockState2 = null;

		for (Direction direction : Direction.Type.field_11062) {
			BooleanProperty booleanProperty = method_10828(direction);
			if ((Boolean)blockState.method_11654(booleanProperty)) {
				boolean bl = this.shouldHaveSide(blockView, blockPos, direction);
				if (!bl) {
					if (blockState2 == null) {
						blockState2 = blockView.method_8320(blockPos2);
					}

					bl = blockState2.getBlock() == this && (Boolean)blockState2.method_11654(booleanProperty);
				}

				blockState = blockState.method_11657(booleanProperty, Boolean.valueOf(bl));
			}
		}

		return blockState;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.field_11033) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			BlockState blockState3 = this.method_10827(blockState, iWorld, blockPos);
			return !this.method_10823(blockState3) ? Blocks.field_10124.method_9564() : blockState3;
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			BlockState blockState2 = this.method_10827(blockState, world, blockPos);
			if (blockState2 != blockState) {
				if (this.method_10823(blockState2)) {
					world.method_8652(blockPos, blockState2, 2);
				} else {
					method_9497(blockState, world, blockPos);
					world.clearBlockState(blockPos, false);
				}
			} else if (world.random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos2 = blockPos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)blockState.method_11654(method_10828(direction))) {
					if (this.canGrowAt(world, blockPos)) {
						BlockPos blockPos3 = blockPos.offset(direction);
						BlockState blockState3 = world.method_8320(blockPos3);
						if (blockState3.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)blockState.method_11654(method_10828(direction2));
							boolean bl2 = (Boolean)blockState.method_11654(method_10828(direction3));
							BlockPos blockPos4 = blockPos3.offset(direction2);
							BlockPos blockPos5 = blockPos3.offset(direction3);
							if (bl && shouldConnectTo(world, blockPos4, direction2)) {
								world.method_8652(blockPos3, this.method_9564().method_11657(method_10828(direction2), Boolean.valueOf(true)), 2);
							} else if (bl2 && shouldConnectTo(world, blockPos5, direction3)) {
								world.method_8652(blockPos3, this.method_9564().method_11657(method_10828(direction3), Boolean.valueOf(true)), 2);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.isAir(blockPos4) && shouldConnectTo(world, blockPos.offset(direction2), direction4)) {
									world.method_8652(blockPos4, this.method_9564().method_11657(method_10828(direction4), Boolean.valueOf(true)), 2);
								} else if (bl2 && world.isAir(blockPos5) && shouldConnectTo(world, blockPos.offset(direction3), direction4)) {
									world.method_8652(blockPos5, this.method_9564().method_11657(method_10828(direction4), Boolean.valueOf(true)), 2);
								} else if ((double)world.random.nextFloat() < 0.05 && shouldConnectTo(world, blockPos3.up(), Direction.field_11036)) {
									world.method_8652(blockPos3, this.method_9564().method_11657(field_11703, Boolean.valueOf(true)), 2);
								}
							}
						} else if (shouldConnectTo(world, blockPos3, direction)) {
							world.method_8652(blockPos, blockState.method_11657(method_10828(direction), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (direction == Direction.field_11036 && blockPos.getY() < 255) {
						if (this.shouldHaveSide(world, blockPos, direction)) {
							world.method_8652(blockPos, blockState.method_11657(field_11703, Boolean.valueOf(true)), 2);
							return;
						}

						if (world.isAir(blockPos2)) {
							if (!this.canGrowAt(world, blockPos)) {
								return;
							}

							BlockState blockState4 = blockState;

							for (Direction direction2 : Direction.Type.field_11062) {
								if (random.nextBoolean() || !shouldConnectTo(world, blockPos2.offset(direction2), Direction.field_11036)) {
									blockState4 = blockState4.method_11657(method_10828(direction2), Boolean.valueOf(false));
								}
							}

							if (this.method_10830(blockState4)) {
								world.method_8652(blockPos2, blockState4, 2);
							}

							return;
						}
					}

					if (blockPos.getY() > 0) {
						BlockPos blockPos3 = blockPos.down();
						BlockState blockState3 = world.method_8320(blockPos3);
						if (blockState3.isAir() || blockState3.getBlock() == this) {
							BlockState blockState5 = blockState3.isAir() ? this.method_9564() : blockState3;
							BlockState blockState6 = this.method_10820(blockState, blockState5, random);
							if (blockState5 != blockState6 && this.method_10830(blockState6)) {
								world.method_8652(blockPos3, blockState6, 2);
							}
						}
					}
				}
			}
		}
	}

	private BlockState method_10820(BlockState blockState, BlockState blockState2, Random random) {
		for (Direction direction : Direction.Type.field_11062) {
			if (random.nextBoolean()) {
				BooleanProperty booleanProperty = method_10828(direction);
				if ((Boolean)blockState.method_11654(booleanProperty)) {
					blockState2 = blockState2.method_11657(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return blockState2;
	}

	private boolean method_10830(BlockState blockState) {
		return (Boolean)blockState.method_11654(field_11706)
			|| (Boolean)blockState.method_11654(field_11702)
			|| (Boolean)blockState.method_11654(field_11699)
			|| (Boolean)blockState.method_11654(field_11696);
	}

	private boolean canGrowAt(BlockView blockView, BlockPos blockPos) {
		int i = 4;
		Iterable<BlockPos> iterable = BlockPos.iterate(
			blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4
		);
		int j = 5;

		for (BlockPos blockPos2 : iterable) {
			if (blockView.method_8320(blockPos2).getBlock() == this) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		BlockState blockState2 = itemPlacementContext.method_8045().method_8320(itemPlacementContext.getBlockPos());
		return blockState2.getBlock() == this ? this.method_10822(blockState2) < FACING_PROPERTIES.size() : super.method_9616(blockState, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.getBlockPos());
		boolean bl = blockState.getBlock() == this;
		BlockState blockState2 = bl ? blockState : this.method_9564();

		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction != Direction.field_11033) {
				BooleanProperty booleanProperty = method_10828(direction);
				boolean bl2 = bl && (Boolean)blockState.method_11654(booleanProperty);
				if (!bl2 && this.shouldHaveSide(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos(), direction)) {
					return blockState2.method_11657(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return bl ? blockState2 : null;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11703, field_11706, field_11702, field_11699, field_11696);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				return blockState.method_11657(field_11706, blockState.method_11654(field_11699))
					.method_11657(field_11702, blockState.method_11654(field_11696))
					.method_11657(field_11699, blockState.method_11654(field_11706))
					.method_11657(field_11696, blockState.method_11654(field_11702));
			case field_11465:
				return blockState.method_11657(field_11706, blockState.method_11654(field_11702))
					.method_11657(field_11702, blockState.method_11654(field_11699))
					.method_11657(field_11699, blockState.method_11654(field_11696))
					.method_11657(field_11696, blockState.method_11654(field_11706));
			case field_11463:
				return blockState.method_11657(field_11706, blockState.method_11654(field_11696))
					.method_11657(field_11702, blockState.method_11654(field_11706))
					.method_11657(field_11699, blockState.method_11654(field_11702))
					.method_11657(field_11696, blockState.method_11654(field_11699));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		switch (blockMirror) {
			case field_11300:
				return blockState.method_11657(field_11706, blockState.method_11654(field_11699)).method_11657(field_11699, blockState.method_11654(field_11706));
			case field_11301:
				return blockState.method_11657(field_11702, blockState.method_11654(field_11696)).method_11657(field_11696, blockState.method_11654(field_11702));
			default:
				return super.method_9569(blockState, blockMirror);
		}
	}

	public static BooleanProperty method_10828(Direction direction) {
		return (BooleanProperty)FACING_PROPERTIES.get(direction);
	}
}
