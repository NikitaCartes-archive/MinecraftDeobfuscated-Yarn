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
		this.setDefaultState(this.stateFactory.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)));
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
				case NORTH_SOUTH:
					if (bl) {
						l++;
					} else {
						l--;
					}
					break;
				case EAST_WEST:
					if (bl) {
						j--;
					} else {
						j++;
					}
					break;
				case ASCENDING_EAST:
					if (bl) {
						j--;
					} else {
						j++;
						k++;
						bl2 = false;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_WEST:
					if (bl) {
						j--;
						k++;
						bl2 = false;
					} else {
						j++;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_NORTH:
					if (bl) {
						l++;
					} else {
						l--;
						k++;
						bl2 = false;
					}

					railShape = RailShape.NORTH_SOUTH;
					break;
				case ASCENDING_SOUTH:
					if (bl) {
						l++;
						k++;
						bl2 = false;
					} else {
						l--;
					}

					railShape = RailShape.NORTH_SOUTH;
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
			if (railShape != RailShape.EAST_WEST
				|| railShape2 != RailShape.NORTH_SOUTH && railShape2 != RailShape.ASCENDING_NORTH && railShape2 != RailShape.ASCENDING_SOUTH) {
				if (railShape != RailShape.NORTH_SOUTH
					|| railShape2 != RailShape.EAST_WEST && railShape2 != RailShape.ASCENDING_EAST && railShape2 != RailShape.ASCENDING_WEST) {
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
			case CLOCKWISE_180:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
				}
			case CLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					default:
						return super.mirror(blockState, blockMirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}
}
