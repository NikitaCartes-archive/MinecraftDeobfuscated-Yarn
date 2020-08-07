package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty POWERED = Properties.POWERED;

	protected PoweredRailBlock(AbstractBlock.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.field_12665).with(POWERED, Boolean.valueOf(false)));
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance) {
		if (distance >= 8) {
			return false;
		} else {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			boolean bl = true;
			RailShape railShape = state.get(SHAPE);
			switch (railShape) {
				case field_12665:
					if (boolean4) {
						k++;
					} else {
						k--;
					}
					break;
				case field_12674:
					if (boolean4) {
						i--;
					} else {
						i++;
					}
					break;
				case field_12667:
					if (boolean4) {
						i--;
					} else {
						i++;
						j++;
						bl = false;
					}

					railShape = RailShape.field_12674;
					break;
				case field_12666:
					if (boolean4) {
						i--;
						j++;
						bl = false;
					} else {
						i++;
					}

					railShape = RailShape.field_12674;
					break;
				case field_12670:
					if (boolean4) {
						k++;
					} else {
						k--;
						j++;
						bl = false;
					}

					railShape = RailShape.field_12665;
					break;
				case field_12668:
					if (boolean4) {
						k++;
						j++;
						bl = false;
					} else {
						k--;
					}

					railShape = RailShape.field_12665;
			}

			return this.isPoweredByOtherRails(world, new BlockPos(i, j, k), boolean4, distance, railShape)
				? true
				: bl && this.isPoweredByOtherRails(world, new BlockPos(i, j - 1, k), boolean4, distance, railShape);
		}
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isOf(this)) {
			return false;
		} else {
			RailShape railShape = blockState.get(SHAPE);
			if (shape != RailShape.field_12674 || railShape != RailShape.field_12665 && railShape != RailShape.field_12670 && railShape != RailShape.field_12668) {
				if (shape != RailShape.field_12665 || railShape != RailShape.field_12674 && railShape != RailShape.field_12667 && railShape != RailShape.field_12666) {
					if (!(Boolean)blockState.get(POWERED)) {
						return false;
					} else {
						return world.isReceivingRedstonePower(pos) ? true : this.isPoweredByOtherRails(world, pos, blockState, bl, distance + 1);
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
		boolean bl = (Boolean)state.get(POWERED);
		boolean bl2 = world.isReceivingRedstonePower(pos)
			|| this.isPoweredByOtherRails(world, pos, state, true, 0)
			|| this.isPoweredByOtherRails(world, pos, state, false, 0);
		if (bl2 != bl) {
			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl2)), 3);
			world.updateNeighborsAlways(pos.method_10074(), this);
			if (((RailShape)state.get(SHAPE)).isAscending()) {
				world.updateNeighborsAlways(pos.up(), this);
			}
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
					case field_12665:
						return state.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return state.with(SHAPE, RailShape.field_12665);
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
				}
			case field_11463:
				switch ((RailShape)state.get(SHAPE)) {
					case field_12665:
						return state.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return state.with(SHAPE, RailShape.field_12665);
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
		builder.add(SHAPE, POWERED);
	}
}
