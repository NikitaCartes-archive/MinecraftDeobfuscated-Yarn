package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.RAIL_SHAPE;

	protected RailBlock(Block.Settings settings) {
		super(false, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(SHAPE, RailShape.field_12665));
	}

	@Override
	protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (block.getDefaultState().emitsRedstonePower() && new RailPlacementHelper(world, blockPos, blockState).method_10460() == 3) {
			this.updateBlockState(world, blockPos, blockState, false);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12671);
				}
			case ROT_270:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
				}
			case ROT_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case field_12670:
						return blockState.with(SHAPE, RailShape.field_12668);
					case field_12668:
						return blockState.with(SHAPE, RailShape.field_12670);
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12672);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12664);
					default:
						return super.mirror(blockState, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case field_12667:
						return blockState.with(SHAPE, RailShape.field_12666);
					case field_12666:
						return blockState.with(SHAPE, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return blockState.with(SHAPE, RailShape.field_12671);
					case field_12671:
						return blockState.with(SHAPE, RailShape.field_12664);
					case field_12672:
						return blockState.with(SHAPE, RailShape.field_12663);
					case field_12663:
						return blockState.with(SHAPE, RailShape.field_12672);
				}
		}

		return super.mirror(blockState, mirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(SHAPE);
	}
}
