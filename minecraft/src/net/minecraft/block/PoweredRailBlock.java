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
	public static final EnumProperty<RailShape> field_11365 = Properties.field_12542;
	public static final BooleanProperty field_11364 = Properties.field_12484;

	protected PoweredRailBlock(Block.Settings settings) {
		super(true, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11365, RailShape.field_12665).method_11657(field_11364, Boolean.valueOf(false)));
	}

	protected boolean method_10413(World world, BlockPos blockPos, BlockState blockState, boolean bl, int i) {
		if (i >= 8) {
			return false;
		} else {
			int j = blockPos.getX();
			int k = blockPos.getY();
			int l = blockPos.getZ();
			boolean bl2 = true;
			RailShape railShape = blockState.method_11654(field_11365);
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
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			RailShape railShape2 = blockState.method_11654(field_11365);
			if (railShape != RailShape.field_12674 || railShape2 != RailShape.field_12665 && railShape2 != RailShape.field_12670 && railShape2 != RailShape.field_12668) {
				if (railShape != RailShape.field_12665 || railShape2 != RailShape.field_12674 && railShape2 != RailShape.field_12667 && railShape2 != RailShape.field_12666
					)
				 {
					if (!(Boolean)blockState.method_11654(field_11364)) {
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
		boolean bl = (Boolean)blockState.method_11654(field_11364);
		boolean bl2 = world.isReceivingRedstonePower(blockPos)
			|| this.method_10413(world, blockPos, blockState, true, 0)
			|| this.method_10413(world, blockPos, blockState, false, 0);
		if (bl2 != bl) {
			world.method_8652(blockPos, blockState.method_11657(field_11364, Boolean.valueOf(bl2)), 3);
			world.method_8452(blockPos.down(), this);
			if (((RailShape)blockState.method_11654(field_11365)).isAscending()) {
				world.method_8452(blockPos.up(), this);
			}
		}
	}

	@Override
	public Property<RailShape> method_9474() {
		return field_11365;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				switch ((RailShape)blockState.method_11654(field_11365)) {
					case field_12667:
						return blockState.method_11657(field_11365, RailShape.field_12666);
					case field_12666:
						return blockState.method_11657(field_11365, RailShape.field_12667);
					case field_12670:
						return blockState.method_11657(field_11365, RailShape.field_12668);
					case field_12668:
						return blockState.method_11657(field_11365, RailShape.field_12670);
					case field_12664:
						return blockState.method_11657(field_11365, RailShape.field_12672);
					case field_12671:
						return blockState.method_11657(field_11365, RailShape.field_12663);
					case field_12672:
						return blockState.method_11657(field_11365, RailShape.field_12664);
					case field_12663:
						return blockState.method_11657(field_11365, RailShape.field_12671);
				}
			case field_11465:
				switch ((RailShape)blockState.method_11654(field_11365)) {
					case field_12665:
						return blockState.method_11657(field_11365, RailShape.field_12674);
					case field_12674:
						return blockState.method_11657(field_11365, RailShape.field_12665);
					case field_12667:
						return blockState.method_11657(field_11365, RailShape.field_12670);
					case field_12666:
						return blockState.method_11657(field_11365, RailShape.field_12668);
					case field_12670:
						return blockState.method_11657(field_11365, RailShape.field_12666);
					case field_12668:
						return blockState.method_11657(field_11365, RailShape.field_12667);
					case field_12664:
						return blockState.method_11657(field_11365, RailShape.field_12663);
					case field_12671:
						return blockState.method_11657(field_11365, RailShape.field_12664);
					case field_12672:
						return blockState.method_11657(field_11365, RailShape.field_12671);
					case field_12663:
						return blockState.method_11657(field_11365, RailShape.field_12672);
				}
			case field_11463:
				switch ((RailShape)blockState.method_11654(field_11365)) {
					case field_12665:
						return blockState.method_11657(field_11365, RailShape.field_12674);
					case field_12674:
						return blockState.method_11657(field_11365, RailShape.field_12665);
					case field_12667:
						return blockState.method_11657(field_11365, RailShape.field_12668);
					case field_12666:
						return blockState.method_11657(field_11365, RailShape.field_12670);
					case field_12670:
						return blockState.method_11657(field_11365, RailShape.field_12667);
					case field_12668:
						return blockState.method_11657(field_11365, RailShape.field_12666);
					case field_12664:
						return blockState.method_11657(field_11365, RailShape.field_12671);
					case field_12671:
						return blockState.method_11657(field_11365, RailShape.field_12672);
					case field_12672:
						return blockState.method_11657(field_11365, RailShape.field_12663);
					case field_12663:
						return blockState.method_11657(field_11365, RailShape.field_12664);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.method_11654(field_11365);
		switch (blockMirror) {
			case field_11300:
				switch (railShape) {
					case field_12670:
						return blockState.method_11657(field_11365, RailShape.field_12668);
					case field_12668:
						return blockState.method_11657(field_11365, RailShape.field_12670);
					case field_12664:
						return blockState.method_11657(field_11365, RailShape.field_12663);
					case field_12671:
						return blockState.method_11657(field_11365, RailShape.field_12672);
					case field_12672:
						return blockState.method_11657(field_11365, RailShape.field_12671);
					case field_12663:
						return blockState.method_11657(field_11365, RailShape.field_12664);
					default:
						return super.method_9569(blockState, blockMirror);
				}
			case field_11301:
				switch (railShape) {
					case field_12667:
						return blockState.method_11657(field_11365, RailShape.field_12666);
					case field_12666:
						return blockState.method_11657(field_11365, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return blockState.method_11657(field_11365, RailShape.field_12671);
					case field_12671:
						return blockState.method_11657(field_11365, RailShape.field_12664);
					case field_12672:
						return blockState.method_11657(field_11365, RailShape.field_12663);
					case field_12663:
						return blockState.method_11657(field_11365, RailShape.field_12672);
				}
		}

		return super.method_9569(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11365, field_11364);
	}
}
