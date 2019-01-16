package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
	protected static final VoxelShape SHAPE = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	protected static final VoxelShape ASCENDING_SHAPE = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
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
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		RailShape railShape = blockState.getBlock() == this ? blockState.get(this.getShapeProperty()) : null;
		return railShape != null && railShape.isAscending() ? ASCENDING_SHAPE : SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.getBlockState(blockPos2).hasSolidTopSurface(viewableWorld, blockPos2);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (!world.isClient) {
				blockState = this.method_9475(world, blockPos, blockState, true);
				if (this.allowCurves) {
					blockState.neighborUpdate(world, blockPos, this, blockPos);
				}
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			RailShape railShape = blockState.get(this.getShapeProperty());
			boolean bl = false;
			BlockPos blockPos3 = blockPos.down();
			if (!world.getBlockState(blockPos3).hasSolidTopSurface(world, blockPos3)) {
				bl = true;
			}

			BlockPos blockPos4 = blockPos.east();
			if (railShape == RailShape.field_12667 && !world.getBlockState(blockPos4).hasSolidTopSurface(world, blockPos4)) {
				bl = true;
			} else {
				BlockPos blockPos5 = blockPos.west();
				if (railShape == RailShape.field_12666 && !world.getBlockState(blockPos5).hasSolidTopSurface(world, blockPos5)) {
					bl = true;
				} else {
					BlockPos blockPos6 = blockPos.north();
					if (railShape == RailShape.field_12670 && !world.getBlockState(blockPos6).hasSolidTopSurface(world, blockPos6)) {
						bl = true;
					} else {
						BlockPos blockPos7 = blockPos.south();
						if (railShape == RailShape.field_12668 && !world.getBlockState(blockPos7).hasSolidTopSurface(world, blockPos7)) {
							bl = true;
						}
					}
				}
			}

			if (bl && !world.isAir(blockPos)) {
				dropStacks(blockState, world, blockPos);
				world.clearBlockState(blockPos);
			} else {
				this.method_9477(blockState, world, blockPos, block);
			}
		}
	}

	protected void method_9477(BlockState blockState, World world, BlockPos blockPos, Block block) {
	}

	protected BlockState method_9475(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		return world.isClient
			? blockState
			: new RailPlacementHelper(world, blockPos, blockState).method_10459(world.isReceivingRedstonePower(blockPos), bl).getBlockState();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15974;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
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
