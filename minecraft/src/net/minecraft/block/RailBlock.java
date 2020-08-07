package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.RAIL_SHAPE;

	protected RailBlock(AbstractBlock.Settings settings) {
		super(false, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.field_12665));
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
		if (neighbor.getDefaultState().emitsRedstonePower() && new RailPlacementHelper(world, pos, state).getNeighborCount() == 3) {
			this.updateBlockState(world, pos, state, false);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case field_11464:
				switch ((RailShape)state.get(SHAPE)) {
					case field_12667:
						return state.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return state.with(SHAPE, RailShape.field_12667);
					case field_12670:
						return state.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return state.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return state.with(SHAPE, RailShape.field_12672);
					case field_12671:
						return state.with(SHAPE, RailShape.field_12663);
					case field_12672:
						return state.with(SHAPE, RailShape.field_12664);
					case field_12663:
						return state.with(SHAPE, RailShape.field_12671);
				}
			case field_11465:
				switch ((RailShape)state.get(SHAPE)) {
					case field_12667:
						return state.with(SHAPE, RailShape.field_12670);
					case field_12666:
						return state.with(SHAPE, RailShape.field_12668);
					case field_12670:
						return state.with(SHAPE, RailShape.field_12666);
					case field_12668:
						return state.with(SHAPE, RailShape.field_12667);
					case field_12664:
						return state.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return state.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return state.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return state.with(SHAPE, RailShape.field_12672);
					case field_12665:
						return state.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return state.with(SHAPE, RailShape.field_12665);
				}
			case field_11463:
				switch ((RailShape)state.get(SHAPE)) {
					case field_12667:
						return state.with(SHAPE, RailShape.field_12668);
					case field_12666:
						return state.with(SHAPE, RailShape.field_12670);
					case field_12670:
						return state.with(SHAPE, RailShape.field_12667);
					case field_12668:
						return state.with(SHAPE, RailShape.field_12666);
					case field_12664:
						return state.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return state.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return state.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return state.with(SHAPE, RailShape.field_12664);
					case field_12665:
						return state.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return state.with(SHAPE, RailShape.field_12665);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.get(SHAPE);
		switch (mirror) {
			case field_11300:
				switch (railShape) {
					case field_12670:
						return state.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return state.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return state.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return state.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return state.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return state.with(SHAPE, RailShape.field_12664);
					default:
						return super.mirror(state, mirror);
				}
			case field_11301:
				switch (railShape) {
					case field_12667:
						return state.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return state.with(SHAPE, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return state.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return state.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return state.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return state.with(SHAPE, RailShape.field_12672);
				}
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHAPE);
	}
}
