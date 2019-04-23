package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
	protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final boolean allowCurves;

	public static boolean isRail(World world, BlockPos blockPos) {
		return isRail(world.getBlockState(blockPos));
	}

	public static boolean isRail(BlockState blockState) {
		return blockState.matches(BlockTags.field_15463);
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
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return isSolidMediumSquare(viewableWorld, blockPos.down());
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (!world.isClient) {
				blockState = this.updateBlockState(world, blockPos, blockState, true);
				if (this.allowCurves) {
					blockState.neighborUpdate(world, blockPos, this, blockPos, bl);
				}
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			RailShape railShape = blockState.get(this.getShapeProperty());
			boolean bl2 = false;
			BlockPos blockPos3 = blockPos.down();
			if (!isSolidMediumSquare(world, blockPos3)) {
				bl2 = true;
			}

			BlockPos blockPos4 = blockPos.east();
			if (railShape == RailShape.field_12667 && !isSolidMediumSquare(world, blockPos4)) {
				bl2 = true;
			} else {
				BlockPos blockPos5 = blockPos.west();
				if (railShape == RailShape.field_12666 && !isSolidMediumSquare(world, blockPos5)) {
					bl2 = true;
				} else {
					BlockPos blockPos6 = blockPos.north();
					if (railShape == RailShape.field_12670 && !isSolidMediumSquare(world, blockPos6)) {
						bl2 = true;
					} else {
						BlockPos blockPos7 = blockPos.south();
						if (railShape == RailShape.field_12668 && !isSolidMediumSquare(world, blockPos7)) {
							bl2 = true;
						}
					}
				}
			}

			if (bl2 && !world.isAir(blockPos)) {
				if (!bl) {
					dropStacks(blockState, world, blockPos);
				}

				world.clearBlockState(blockPos, bl);
			} else {
				this.updateBlockState(blockState, world, blockPos, block);
			}
		}
	}

	protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
	}

	protected BlockState updateBlockState(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		return world.isClient
			? blockState
			: new RailPlacementHelper(world, blockPos, blockState).updateBlockState(world.isReceivingRedstonePower(blockPos), bl).getBlockState();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15974;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
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
				world.updateNeighborsAlways(blockPos.down(), this);
			}
		}
	}

	public abstract Property<RailShape> getShapeProperty();
}
