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

	public static boolean isRail(World world, BlockPos blockPos) {
		return isRail(world.getBlockState(blockPos));
	}

	public static boolean isRail(BlockState blockState) {
		return blockState.matches(BlockTags.RAILS);
	}

	protected AbstractRailBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.allowCurves = bl;
	}

	public boolean canMakeCurves() {
		return this.allowCurves;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		RailShape railShape = blockState.getBlock() == this ? blockState.get(this.getShapeProperty()) : null;
		return railShape != null && railShape.isAscending() ? ASCENDING_SHAPE : STRAIGHT_SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		return topCoversMediumSquare(worldView, blockPos.method_10074());
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			blockState = this.updateBlockState(world, blockPos, blockState, true);
			if (this.allowCurves) {
				blockState.neighborUpdate(world, blockPos, this, blockPos, bl);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			RailShape railShape = blockState.get(this.getShapeProperty());
			boolean bl2 = false;
			BlockPos blockPos3 = blockPos.method_10074();
			if (!topCoversMediumSquare(world, blockPos3)) {
				bl2 = true;
			}

			BlockPos blockPos4 = blockPos.east();
			if (railShape == RailShape.ASCENDING_EAST && !topCoversMediumSquare(world, blockPos4)) {
				bl2 = true;
			} else {
				BlockPos blockPos5 = blockPos.west();
				if (railShape == RailShape.ASCENDING_WEST && !topCoversMediumSquare(world, blockPos5)) {
					bl2 = true;
				} else {
					BlockPos blockPos6 = blockPos.north();
					if (railShape == RailShape.ASCENDING_NORTH && !topCoversMediumSquare(world, blockPos6)) {
						bl2 = true;
					} else {
						BlockPos blockPos7 = blockPos.south();
						if (railShape == RailShape.ASCENDING_SOUTH && !topCoversMediumSquare(world, blockPos7)) {
							bl2 = true;
						}
					}
				}
			}

			if (bl2 && !world.isAir(blockPos)) {
				if (!bl) {
					dropStacks(blockState, world, blockPos);
				}

				world.removeBlock(blockPos, bl);
			} else {
				this.updateBlockState(blockState, world, blockPos, block);
			}
		}
	}

	protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
	}

	protected BlockState updateBlockState(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		if (world.isClient) {
			return blockState;
		} else {
			RailShape railShape = blockState.get(this.getShapeProperty());
			return new RailPlacementHelper(world, blockPos, blockState).updateBlockState(world.isReceivingRedstonePower(blockPos), bl, railShape).getBlockState();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			if (((RailShape)blockState.get(this.getShapeProperty())).isAscending()) {
				world.updateNeighborsAlways(blockPos.up(), this);
			}

			if (this.allowCurves) {
				world.updateNeighborsAlways(blockPos, this);
				world.updateNeighborsAlways(blockPos.method_10074(), this);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.getDefaultState();
		Direction direction = itemPlacementContext.getPlayerFacing();
		boolean bl = direction == Direction.EAST || direction == Direction.WEST;
		return blockState.with(this.getShapeProperty(), bl ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
	}

	public abstract Property<RailShape> getShapeProperty();
}
