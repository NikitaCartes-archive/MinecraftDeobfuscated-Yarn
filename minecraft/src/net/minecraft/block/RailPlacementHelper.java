package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RailPlacementHelper {
	private final World world;
	private final BlockPos pos;
	private final AbstractRailBlock block;
	private BlockState state;
	private final boolean allowCurves;
	private final List<BlockPos> neighbors = Lists.<BlockPos>newArrayList();

	public RailPlacementHelper(World world, BlockPos blockPos, BlockState blockState) {
		this.world = world;
		this.pos = blockPos;
		this.state = blockState;
		this.block = (AbstractRailBlock)blockState.getBlock();
		RailShape railShape = blockState.get(this.block.getShapeProperty());
		this.allowCurves = this.block.canMakeCurves();
		this.computeNeighbors(railShape);
	}

	public List<BlockPos> getNeighbors() {
		return this.neighbors;
	}

	private void computeNeighbors(RailShape railShape) {
		this.neighbors.clear();
		switch (railShape) {
			case NORTH_SOUTH:
				this.neighbors.add(this.pos.north());
				this.neighbors.add(this.pos.south());
				break;
			case EAST_WEST:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.east());
				break;
			case ASCENDING_EAST:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.east().up());
				break;
			case ASCENDING_WEST:
				this.neighbors.add(this.pos.west().up());
				this.neighbors.add(this.pos.east());
				break;
			case ASCENDING_NORTH:
				this.neighbors.add(this.pos.north().up());
				this.neighbors.add(this.pos.south());
				break;
			case ASCENDING_SOUTH:
				this.neighbors.add(this.pos.north());
				this.neighbors.add(this.pos.south().up());
				break;
			case SOUTH_EAST:
				this.neighbors.add(this.pos.east());
				this.neighbors.add(this.pos.south());
				break;
			case SOUTH_WEST:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.south());
				break;
			case NORTH_WEST:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.north());
				break;
			case NORTH_EAST:
				this.neighbors.add(this.pos.east());
				this.neighbors.add(this.pos.north());
		}
	}

	private void method_10467() {
		for (int i = 0; i < this.neighbors.size(); i++) {
			RailPlacementHelper railPlacementHelper = this.method_10458((BlockPos)this.neighbors.get(i));
			if (railPlacementHelper != null && railPlacementHelper.isNeighbor(this)) {
				this.neighbors.set(i, railPlacementHelper.pos);
			} else {
				this.neighbors.remove(i--);
			}
		}
	}

	private boolean isVerticallyNearRail(BlockPos blockPos) {
		return AbstractRailBlock.isRail(this.world, blockPos)
			|| AbstractRailBlock.isRail(this.world, blockPos.up())
			|| AbstractRailBlock.isRail(this.world, blockPos.down());
	}

	@Nullable
	private RailPlacementHelper method_10458(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);
		if (AbstractRailBlock.isRail(blockState)) {
			return new RailPlacementHelper(this.world, blockPos, blockState);
		} else {
			BlockPos blockPos2 = blockPos.up();
			blockState = this.world.getBlockState(blockPos2);
			if (AbstractRailBlock.isRail(blockState)) {
				return new RailPlacementHelper(this.world, blockPos2, blockState);
			} else {
				blockPos2 = blockPos.down();
				blockState = this.world.getBlockState(blockPos2);
				return AbstractRailBlock.isRail(blockState) ? new RailPlacementHelper(this.world, blockPos2, blockState) : null;
			}
		}
	}

	private boolean isNeighbor(RailPlacementHelper railPlacementHelper) {
		return this.isNeighbor(railPlacementHelper.pos);
	}

	private boolean isNeighbor(BlockPos blockPos) {
		for (int i = 0; i < this.neighbors.size(); i++) {
			BlockPos blockPos2 = (BlockPos)this.neighbors.get(i);
			if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
				return true;
			}
		}

		return false;
	}

	protected int method_10460() {
		int i = 0;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (this.isVerticallyNearRail(this.pos.offset(direction))) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10455(RailPlacementHelper railPlacementHelper) {
		return this.isNeighbor(railPlacementHelper) || this.neighbors.size() != 2;
	}

	private void method_10461(RailPlacementHelper railPlacementHelper) {
		this.neighbors.add(railPlacementHelper.pos);
		BlockPos blockPos = this.pos.north();
		BlockPos blockPos2 = this.pos.south();
		BlockPos blockPos3 = this.pos.west();
		BlockPos blockPos4 = this.pos.east();
		boolean bl = this.isNeighbor(blockPos);
		boolean bl2 = this.isNeighbor(blockPos2);
		boolean bl3 = this.isNeighbor(blockPos3);
		boolean bl4 = this.isNeighbor(blockPos4);
		RailShape railShape = null;
		if (bl || bl2) {
			railShape = RailShape.NORTH_SOUTH;
		}

		if (bl3 || bl4) {
			railShape = RailShape.EAST_WEST;
		}

		if (!this.allowCurves) {
			if (bl2 && bl4 && !bl && !bl3) {
				railShape = RailShape.SOUTH_EAST;
			}

			if (bl2 && bl3 && !bl && !bl4) {
				railShape = RailShape.SOUTH_WEST;
			}

			if (bl && bl3 && !bl2 && !bl4) {
				railShape = RailShape.NORTH_WEST;
			}

			if (bl && bl4 && !bl2 && !bl3) {
				railShape = RailShape.NORTH_EAST;
			}
		}

		if (railShape == RailShape.NORTH_SOUTH) {
			if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
				railShape = RailShape.ASCENDING_NORTH;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
				railShape = RailShape.ASCENDING_SOUTH;
			}
		}

		if (railShape == RailShape.EAST_WEST) {
			if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
				railShape = RailShape.ASCENDING_EAST;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
				railShape = RailShape.ASCENDING_WEST;
			}
		}

		if (railShape == null) {
			railShape = RailShape.NORTH_SOUTH;
		}

		this.state = this.state.with(this.block.getShapeProperty(), railShape);
		this.world.setBlockState(this.pos, this.state, 3);
	}

	private boolean method_10465(BlockPos blockPos) {
		RailPlacementHelper railPlacementHelper = this.method_10458(blockPos);
		if (railPlacementHelper == null) {
			return false;
		} else {
			railPlacementHelper.method_10467();
			return railPlacementHelper.method_10455(this);
		}
	}

	public RailPlacementHelper updateBlockState(boolean bl, boolean bl2) {
		BlockPos blockPos = this.pos.north();
		BlockPos blockPos2 = this.pos.south();
		BlockPos blockPos3 = this.pos.west();
		BlockPos blockPos4 = this.pos.east();
		boolean bl3 = this.method_10465(blockPos);
		boolean bl4 = this.method_10465(blockPos2);
		boolean bl5 = this.method_10465(blockPos3);
		boolean bl6 = this.method_10465(blockPos4);
		RailShape railShape = null;
		if ((bl3 || bl4) && !bl5 && !bl6) {
			railShape = RailShape.NORTH_SOUTH;
		}

		if ((bl5 || bl6) && !bl3 && !bl4) {
			railShape = RailShape.EAST_WEST;
		}

		if (!this.allowCurves) {
			if (bl4 && bl6 && !bl3 && !bl5) {
				railShape = RailShape.SOUTH_EAST;
			}

			if (bl4 && bl5 && !bl3 && !bl6) {
				railShape = RailShape.SOUTH_WEST;
			}

			if (bl3 && bl5 && !bl4 && !bl6) {
				railShape = RailShape.NORTH_WEST;
			}

			if (bl3 && bl6 && !bl4 && !bl5) {
				railShape = RailShape.NORTH_EAST;
			}
		}

		if (railShape == null) {
			if (bl3 || bl4) {
				railShape = RailShape.NORTH_SOUTH;
			}

			if (bl5 || bl6) {
				railShape = RailShape.EAST_WEST;
			}

			if (!this.allowCurves) {
				if (bl) {
					if (bl4 && bl6) {
						railShape = RailShape.SOUTH_EAST;
					}

					if (bl5 && bl4) {
						railShape = RailShape.SOUTH_WEST;
					}

					if (bl6 && bl3) {
						railShape = RailShape.NORTH_EAST;
					}

					if (bl3 && bl5) {
						railShape = RailShape.NORTH_WEST;
					}
				} else {
					if (bl3 && bl5) {
						railShape = RailShape.NORTH_WEST;
					}

					if (bl6 && bl3) {
						railShape = RailShape.NORTH_EAST;
					}

					if (bl5 && bl4) {
						railShape = RailShape.SOUTH_WEST;
					}

					if (bl4 && bl6) {
						railShape = RailShape.SOUTH_EAST;
					}
				}
			}
		}

		if (railShape == RailShape.NORTH_SOUTH) {
			if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
				railShape = RailShape.ASCENDING_NORTH;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
				railShape = RailShape.ASCENDING_SOUTH;
			}
		}

		if (railShape == RailShape.EAST_WEST) {
			if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
				railShape = RailShape.ASCENDING_EAST;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
				railShape = RailShape.ASCENDING_WEST;
			}
		}

		if (railShape == null) {
			railShape = RailShape.NORTH_SOUTH;
		}

		this.computeNeighbors(railShape);
		this.state = this.state.with(this.block.getShapeProperty(), railShape);
		if (bl2 || this.world.getBlockState(this.pos) != this.state) {
			this.world.setBlockState(this.pos, this.state, 3);

			for (int i = 0; i < this.neighbors.size(); i++) {
				RailPlacementHelper railPlacementHelper = this.method_10458((BlockPos)this.neighbors.get(i));
				if (railPlacementHelper != null) {
					railPlacementHelper.method_10467();
					if (railPlacementHelper.method_10455(this)) {
						railPlacementHelper.method_10461(this);
					}
				}
			}
		}

		return this;
	}

	public BlockState getBlockState() {
		return this.state;
	}
}
