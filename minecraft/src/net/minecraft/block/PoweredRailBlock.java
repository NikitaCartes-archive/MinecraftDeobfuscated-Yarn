package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateFactory;
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

	protected PoweredRailBlock(Block.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(SHAPE, RailShape.field_12665).with(POWERED, Boolean.valueOf(false)));
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos blockPos, BlockState blockState, boolean bl, int i) {
		if (i >= 8) {
			return false;
		} else {
			int j = blockPos.getX();
			int k = blockPos.getY();
			int l = blockPos.getZ();
			boolean bl2 = true;
			RailShape railShape = blockState.get(SHAPE);
			switch (railShape) {
				case field_12665:
					if (bl) {
						l++;
					} else {
						l--;
					}
					break;
				case field_12674:
					if (bl) {
						j--;
					} else {
						j++;
					}
					break;
				case field_12667:
					if (bl) {
						j--;
					} else {
						j++;
						k++;
						bl2 = false;
					}

					railShape = RailShape.field_12674;
					break;
				case field_12666:
					if (bl) {
						j--;
						k++;
						bl2 = false;
					} else {
						j++;
					}

					railShape = RailShape.field_12674;
					break;
				case field_12670:
					if (bl) {
						l++;
					} else {
						l--;
						k++;
						bl2 = false;
					}

					railShape = RailShape.field_12665;
					break;
				case field_12668:
					if (bl) {
						l++;
						k++;
						bl2 = false;
					} else {
						l--;
					}

					railShape = RailShape.field_12665;
			}

			return this.isPoweredByOtherRails(world, new BlockPos(j, k, l), bl, i, railShape)
				? true
				: bl2 && this.isPoweredByOtherRails(world, new BlockPos(j, k - 1, l), bl, i, railShape);
		}
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos blockPos, boolean bl, int i, RailShape railShape) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			RailShape railShape2 = blockState.get(SHAPE);
			if (railShape != RailShape.field_12674 || railShape2 != RailShape.field_12665 && railShape2 != RailShape.field_12670 && railShape2 != RailShape.field_12668) {
				if (railShape != RailShape.field_12665 || railShape2 != RailShape.field_12674 && railShape2 != RailShape.field_12667 && railShape2 != RailShape.field_12666
					)
				 {
					if (!(Boolean)blockState.get(POWERED)) {
						return false;
					} else {
						return world.isReceivingRedstonePower(blockPos) ? true : this.isPoweredByOtherRails(world, blockPos, blockState, bl, i + 1);
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
	protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
		boolean bl = (Boolean)blockState.get(POWERED);
		boolean bl2 = world.isReceivingRedstonePower(blockPos)
			|| this.isPoweredByOtherRails(world, blockPos, blockState, true, 0)
			|| this.isPoweredByOtherRails(world, blockPos, blockState, false, 0);
		if (bl2 != bl) {
			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl2)), 3);
			world.updateNeighborsAlways(blockPos.down(), this);
			if (((RailShape)blockState.get(SHAPE)).isAscending()) {
				world.updateNeighborsAlways(blockPos.up(), this);
			}
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
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
			case field_11465:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
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
				}
			case field_11463:
				switch ((RailShape)blockState.get(SHAPE)) {
					case field_12665:
						return blockState.with(SHAPE, RailShape.field_12674);
					case field_12674:
						return blockState.with(SHAPE, RailShape.field_12665);
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
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case field_11300:
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
						return super.mirror(blockState, blockMirror);
				}
			case field_11301:
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

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}
}
