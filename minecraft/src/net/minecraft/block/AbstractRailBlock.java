package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
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
		return state.isIn(BlockTags.field_15463) && state.getBlock() instanceof AbstractRailBlock;
	}

	protected AbstractRailBlock(boolean allowCurves, AbstractBlock.Settings settings) {
		super(settings);
		this.allowCurves = allowCurves;
	}

	public boolean canMakeCurves() {
		return this.allowCurves;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		RailShape railShape = state.isOf(this) ? state.get(this.getShapeProperty()) : null;
		return railShape != null && railShape.isAscending() ? ASCENDING_SHAPE : STRAIGHT_SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return hasTopRim(world, pos.method_10074());
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.updateCurves(state, world, pos, notify);
		}
	}

	protected BlockState updateCurves(BlockState state, World world, BlockPos pos, boolean notify) {
		state = this.updateBlockState(world, pos, state, true);
		if (this.allowCurves) {
			state.neighborUpdate(world, pos, this, pos, notify);
		}

		return state;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient && world.getBlockState(pos).isOf(this)) {
			RailShape railShape = state.get(this.getShapeProperty());
			if (shouldDropRail(pos, world, railShape)) {
				dropStacks(state, world, pos);
				world.removeBlock(pos, notify);
			} else {
				this.updateBlockState(state, world, pos, block);
			}
		}
	}

	/**
	 * Checks if this rail should be dropped.
	 * 
	 * <p>This method will return true if:
	 * <ul><li>The rail block is ascending.</li>
	 * <li>The block in the direction of ascent does not have a top rim.</li></ul>
	 */
	private static boolean shouldDropRail(BlockPos pos, World world, RailShape shape) {
		if (!hasTopRim(world, pos.method_10074())) {
			return true;
		} else {
			switch (shape) {
				case field_12667:
					return !hasTopRim(world, pos.east());
				case field_12666:
					return !hasTopRim(world, pos.west());
				case field_12670:
					return !hasTopRim(world, pos.north());
				case field_12668:
					return !hasTopRim(world, pos.south());
				default:
					return false;
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
		return PistonBehavior.field_15974;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved) {
			super.onStateReplaced(state, world, pos, newState, moved);
			if (((RailShape)state.get(this.getShapeProperty())).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}

			if (this.allowCurves) {
				world.updateNeighborsAlways(pos, this);
				world.updateNeighborsAlways(pos.method_10074(), this);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = super.getDefaultState();
		Direction direction = ctx.getPlayerFacing();
		boolean bl = direction == Direction.field_11034 || direction == Direction.field_11039;
		return blockState.with(this.getShapeProperty(), bl ? RailShape.field_12674 : RailShape.field_12665);
	}

	public abstract Property<RailShape> getShapeProperty();
}
