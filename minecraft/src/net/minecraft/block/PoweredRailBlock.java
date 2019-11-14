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

	protected PoweredRailBlock(Block.Settings settings) {
		super(true, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)));
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
				case NORTH_SOUTH:
					if (boolean4) {
						k++;
					} else {
						k--;
					}
					break;
				case EAST_WEST:
					if (boolean4) {
						i--;
					} else {
						i++;
					}
					break;
				case ASCENDING_EAST:
					if (boolean4) {
						i--;
					} else {
						i++;
						j++;
						bl = false;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_WEST:
					if (boolean4) {
						i--;
						j++;
						bl = false;
					} else {
						i++;
					}

					railShape = RailShape.EAST_WEST;
					break;
				case ASCENDING_NORTH:
					if (boolean4) {
						k++;
					} else {
						k--;
						j++;
						bl = false;
					}

					railShape = RailShape.NORTH_SOUTH;
					break;
				case ASCENDING_SOUTH:
					if (boolean4) {
						k++;
						j++;
						bl = false;
					} else {
						k--;
					}

					railShape = RailShape.NORTH_SOUTH;
			}

			return this.isPoweredByOtherRails(world, new BlockPos(i, j, k), boolean4, distance, railShape)
				? true
				: bl && this.isPoweredByOtherRails(world, new BlockPos(i, j - 1, k), boolean4, distance, railShape);
		}
	}

	protected boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() != this) {
			return false;
		} else {
			RailShape railShape = blockState.get(SHAPE);
			if (shape != RailShape.EAST_WEST || railShape != RailShape.NORTH_SOUTH && railShape != RailShape.ASCENDING_NORTH && railShape != RailShape.ASCENDING_SOUTH) {
				if (shape != RailShape.NORTH_SOUTH || railShape != RailShape.EAST_WEST && railShape != RailShape.ASCENDING_EAST && railShape != RailShape.ASCENDING_WEST) {
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
			case CLOCKWISE_180:
				switch ((RailShape)state.get(SHAPE)) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.get(SHAPE)) {
					case NORTH_SOUTH:
						return state.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.get(SHAPE)) {
					case NORTH_SOUTH:
						return state.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.with(SHAPE, RailShape.NORTH_SOUTH);
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.get(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					default:
						return super.mirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return state.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.with(SHAPE, RailShape.NORTH_WEST);
				}
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}
}
