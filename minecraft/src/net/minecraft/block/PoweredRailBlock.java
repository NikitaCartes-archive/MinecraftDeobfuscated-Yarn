package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> field_11365 = Properties.RAIL_SHAPE_STRAIGHT;
	public static final BooleanProperty field_11364 = Properties.POWERED;

	protected PoweredRailBlock(Block.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11365, RailShape.field_12665).with(field_11364, Boolean.valueOf(false)));
	}

	protected boolean method_10413(World world, BlockPos blockPos, BlockState blockState, boolean bl, int i) {
		if (i >= 8) {
			return false;
		} else {
			int j = blockPos.getX();
			int k = blockPos.getY();
			int l = blockPos.getZ();
			boolean bl2 = true;
			RailShape railShape = blockState.get(field_11365);
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

			return this.method_10414(world, new BlockPos(j, k, l), bl, i, railShape)
				? true
				: bl2 && this.method_10414(world, new BlockPos(j, k - 1, l), bl, i, railShape);
		}
	}

	protected boolean method_10414(World world, BlockPos blockPos, boolean bl, int i, RailShape railShape) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			RailShape railShape2 = blockState.get(field_11365);
			if (railShape != RailShape.field_12674 || railShape2 != RailShape.field_12665 && railShape2 != RailShape.field_12670 && railShape2 != RailShape.field_12668) {
				if (railShape != RailShape.field_12665 || railShape2 != RailShape.field_12674 && railShape2 != RailShape.field_12667 && railShape2 != RailShape.field_12666
					)
				 {
					if (!(Boolean)blockState.get(field_11364)) {
						return false;
					} else {
						return world.isReceivingRedstonePower(blockPos) ? true : this.method_10413(world, blockPos, blockState, bl, i + 1);
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
	protected void method_9477(BlockState blockState, World world, BlockPos blockPos, Block block) {
		boolean bl = (Boolean)blockState.get(field_11364);
		boolean bl2 = world.isReceivingRedstonePower(blockPos)
			|| this.method_10413(world, blockPos, blockState, true, 0)
			|| this.method_10413(world, blockPos, blockState, false, 0);
		if (bl2 != bl) {
			world.setBlockState(blockPos, blockState.with(field_11364, Boolean.valueOf(bl2)), 3);
			world.updateNeighborsAlways(blockPos.down(), this);
			if (((RailShape)blockState.get(field_11365)).isAscending()) {
				world.updateNeighborsAlways(blockPos.up(), this);
			}
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return field_11365;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				switch ((RailShape)blockState.get(field_11365)) {
					case field_12667:
						return blockState.with(field_11365, RailShape.field_12666);
					case field_12666:
						return blockState.with(field_11365, RailShape.field_12667);
					case field_12670:
						return blockState.with(field_11365, RailShape.field_12668);
					case field_12668:
						return blockState.with(field_11365, RailShape.field_12670);
					case field_12664:
						return blockState.with(field_11365, RailShape.field_12672);
					case field_12671:
						return blockState.with(field_11365, RailShape.field_12663);
					case field_12672:
						return blockState.with(field_11365, RailShape.field_12664);
					case field_12663:
						return blockState.with(field_11365, RailShape.field_12671);
				}
			case ROT_270:
				switch ((RailShape)blockState.get(field_11365)) {
					case field_12665:
						return blockState.with(field_11365, RailShape.field_12674);
					case field_12674:
						return blockState.with(field_11365, RailShape.field_12665);
					case field_12667:
						return blockState.with(field_11365, RailShape.field_12670);
					case field_12666:
						return blockState.with(field_11365, RailShape.field_12668);
					case field_12670:
						return blockState.with(field_11365, RailShape.field_12666);
					case field_12668:
						return blockState.with(field_11365, RailShape.field_12667);
					case field_12664:
						return blockState.with(field_11365, RailShape.field_12663);
					case field_12671:
						return blockState.with(field_11365, RailShape.field_12664);
					case field_12672:
						return blockState.with(field_11365, RailShape.field_12671);
					case field_12663:
						return blockState.with(field_11365, RailShape.field_12672);
				}
			case ROT_90:
				switch ((RailShape)blockState.get(field_11365)) {
					case field_12665:
						return blockState.with(field_11365, RailShape.field_12674);
					case field_12674:
						return blockState.with(field_11365, RailShape.field_12665);
					case field_12667:
						return blockState.with(field_11365, RailShape.field_12668);
					case field_12666:
						return blockState.with(field_11365, RailShape.field_12670);
					case field_12670:
						return blockState.with(field_11365, RailShape.field_12667);
					case field_12668:
						return blockState.with(field_11365, RailShape.field_12666);
					case field_12664:
						return blockState.with(field_11365, RailShape.field_12671);
					case field_12671:
						return blockState.with(field_11365, RailShape.field_12672);
					case field_12672:
						return blockState.with(field_11365, RailShape.field_12663);
					case field_12663:
						return blockState.with(field_11365, RailShape.field_12664);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		RailShape railShape = blockState.get(field_11365);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case field_12670:
						return blockState.with(field_11365, RailShape.field_12668);
					case field_12668:
						return blockState.with(field_11365, RailShape.field_12670);
					case field_12664:
						return blockState.with(field_11365, RailShape.field_12663);
					case field_12671:
						return blockState.with(field_11365, RailShape.field_12672);
					case field_12672:
						return blockState.with(field_11365, RailShape.field_12671);
					case field_12663:
						return blockState.with(field_11365, RailShape.field_12664);
					default:
						return super.applyMirror(blockState, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case field_12667:
						return blockState.with(field_11365, RailShape.field_12666);
					case field_12666:
						return blockState.with(field_11365, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return blockState.with(field_11365, RailShape.field_12671);
					case field_12671:
						return blockState.with(field_11365, RailShape.field_12664);
					case field_12672:
						return blockState.with(field_11365, RailShape.field_12663);
					case field_12663:
						return blockState.with(field_11365, RailShape.field_12672);
				}
		}

		return super.applyMirror(blockState, mirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11365, field_11364);
	}
}
