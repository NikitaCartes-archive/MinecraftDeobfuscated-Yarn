package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractRailBlock extends Block {
	protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final boolean allowCurves;

	public static boolean isRail(World world, BlockPos pos) {
		return isRail(world.getBlockState(pos));
	}

	public static boolean isRail(BlockState state) {
		return state.matches(BlockTags.RAILS);
	}

	protected AbstractRailBlock(boolean allowCurves, Block.Settings settings) {
		super(settings);
		this.allowCurves = allowCurves;
	}

	public boolean canMakeCurves() {
		return this.allowCurves;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		RailShape railShape = state.getBlock() == this ? state.get(this.getShapeProperty()) : null;
		return railShape != null && railShape.isAscending() ? ASCENDING_SHAPE : STRAIGHT_SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return topCoversMediumSquare(world, pos.down());
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() != state.getBlock()) {
			this.method_24417(state, world, pos, moved);
		}
	}

	protected BlockState method_24417(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		blockState = this.updateBlockState(world, blockPos, blockState, true);
		if (this.allowCurves) {
			blockState.neighborUpdate(world, blockPos, this, blockPos, bl);
		}

		return blockState;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient) {
			RailShape railShape = state.get(this.getShapeProperty());
			boolean bl = false;
			BlockPos blockPos = pos.down();
			if (!topCoversMediumSquare(world, blockPos)) {
				bl = true;
			}

			BlockPos blockPos2 = pos.east();
			if (railShape == RailShape.ASCENDING_EAST && !topCoversMediumSquare(world, blockPos2)) {
				bl = true;
			} else {
				BlockPos blockPos3 = pos.west();
				if (railShape == RailShape.ASCENDING_WEST && !topCoversMediumSquare(world, blockPos3)) {
					bl = true;
				} else {
					BlockPos blockPos4 = pos.north();
					if (railShape == RailShape.ASCENDING_NORTH && !topCoversMediumSquare(world, blockPos4)) {
						bl = true;
					} else {
						BlockPos blockPos5 = pos.south();
						if (railShape == RailShape.ASCENDING_SOUTH && !topCoversMediumSquare(world, blockPos5)) {
							bl = true;
						}
					}
				}
			}

			if (bl && !world.isAir(pos)) {
				if (!moved) {
					dropStacks(state, world, pos);
				}

				world.removeBlock(pos, moved);
			} else {
				this.updateBlockState(state, world, pos, block);
			}
		}
	}

	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
	}

	protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean forceUpdate) {
		if (world.isClient) {
			return state;
		} else {
			RailShape railShape = state.get(this.getShapeProperty());
			return new RailPlacementHelper(world, pos, state).updateBlockState(world.isReceivingRedstonePower(pos), forceUpdate, railShape).getBlockState();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved) {
			super.onBlockRemoved(state, world, pos, newState, moved);
			if (((RailShape)state.get(this.getShapeProperty())).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}

			if (this.allowCurves) {
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.down(), this);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = super.getDefaultState();
		Direction direction = ctx.getPlayerFacing();
		boolean bl = direction == Direction.EAST || direction == Direction.WEST;
		return blockState.with(this.getShapeProperty(), bl ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
	}

	public abstract Property<RailShape> getShapeProperty();
}
