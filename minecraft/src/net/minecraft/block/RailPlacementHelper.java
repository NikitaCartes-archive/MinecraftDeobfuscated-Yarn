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

	public RailPlacementHelper(World world, BlockPos pos, BlockState state) {
		this.world = world;
		this.pos = pos;
		this.state = state;
		this.block = (AbstractRailBlock)state.getBlock();
		RailShape railShape = state.get(this.block.getShapeProperty());
		this.allowCurves = this.block.canMakeCurves();
		this.computeNeighbors(railShape);
	}

	public List<BlockPos> getNeighbors() {
		return this.neighbors;
	}

	private void computeNeighbors(RailShape shape) {
		this.neighbors.clear();
		switch (shape) {
			case field_12665:
				this.neighbors.add(this.pos.north());
				this.neighbors.add(this.pos.south());
				break;
			case field_12674:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.east());
				break;
			case field_12667:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.east().up());
				break;
			case field_12666:
				this.neighbors.add(this.pos.west().up());
				this.neighbors.add(this.pos.east());
				break;
			case field_12670:
				this.neighbors.add(this.pos.north().up());
				this.neighbors.add(this.pos.south());
				break;
			case field_12668:
				this.neighbors.add(this.pos.north());
				this.neighbors.add(this.pos.south().up());
				break;
			case field_12664:
				this.neighbors.add(this.pos.east());
				this.neighbors.add(this.pos.south());
				break;
			case field_12671:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.south());
				break;
			case field_12672:
				this.neighbors.add(this.pos.west());
				this.neighbors.add(this.pos.north());
				break;
			case field_12663:
				this.neighbors.add(this.pos.east());
				this.neighbors.add(this.pos.north());
		}
	}

	private void updateNeighborPositions() {
		for (int i = 0; i < this.neighbors.size(); i++) {
			RailPlacementHelper railPlacementHelper = this.getNeighboringRail((BlockPos)this.neighbors.get(i));
			if (railPlacementHelper != null && railPlacementHelper.isNeighbor(this)) {
				this.neighbors.set(i, railPlacementHelper.pos);
			} else {
				this.neighbors.remove(i--);
			}
		}
	}

	private boolean isVerticallyNearRail(BlockPos pos) {
		return AbstractRailBlock.isRail(this.world, pos)
			|| AbstractRailBlock.isRail(this.world, pos.up())
			|| AbstractRailBlock.isRail(this.world, pos.method_10074());
	}

	@Nullable
	private RailPlacementHelper getNeighboringRail(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		if (AbstractRailBlock.isRail(blockState)) {
			return new RailPlacementHelper(this.world, pos, blockState);
		} else {
			BlockPos blockPos = pos.up();
			blockState = this.world.getBlockState(blockPos);
			if (AbstractRailBlock.isRail(blockState)) {
				return new RailPlacementHelper(this.world, blockPos, blockState);
			} else {
				blockPos = pos.method_10074();
				blockState = this.world.getBlockState(blockPos);
				return AbstractRailBlock.isRail(blockState) ? new RailPlacementHelper(this.world, blockPos, blockState) : null;
			}
		}
	}

	private boolean isNeighbor(RailPlacementHelper other) {
		return this.isNeighbor(other.pos);
	}

	private boolean isNeighbor(BlockPos pos) {
		for (int i = 0; i < this.neighbors.size(); i++) {
			BlockPos blockPos = (BlockPos)this.neighbors.get(i);
			if (blockPos.getX() == pos.getX() && blockPos.getZ() == pos.getZ()) {
				return true;
			}
		}

		return false;
	}

	protected int getNeighborCount() {
		int i = 0;

		for (Direction direction : Direction.Type.field_11062) {
			if (this.isVerticallyNearRail(this.pos.offset(direction))) {
				i++;
			}
		}

		return i;
	}

	private boolean canConnect(RailPlacementHelper placementHelper) {
		return this.isNeighbor(placementHelper) || this.neighbors.size() != 2;
	}

	private void computeRailShape(RailPlacementHelper placementHelper) {
		this.neighbors.add(placementHelper.pos);
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
			railShape = RailShape.field_12665;
		}

		if (bl3 || bl4) {
			railShape = RailShape.field_12674;
		}

		if (!this.allowCurves) {
			if (bl2 && bl4 && !bl && !bl3) {
				railShape = RailShape.field_12664;
			}

			if (bl2 && bl3 && !bl && !bl4) {
				railShape = RailShape.field_12671;
			}

			if (bl && bl3 && !bl2 && !bl4) {
				railShape = RailShape.field_12672;
			}

			if (bl && bl4 && !bl2 && !bl3) {
				railShape = RailShape.field_12663;
			}
		}

		if (railShape == RailShape.field_12665) {
			if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
				railShape = RailShape.field_12670;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
				railShape = RailShape.field_12668;
			}
		}

		if (railShape == RailShape.field_12674) {
			if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
				railShape = RailShape.field_12667;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
				railShape = RailShape.field_12666;
			}
		}

		if (railShape == null) {
			railShape = RailShape.field_12665;
		}

		this.state = this.state.with(this.block.getShapeProperty(), railShape);
		this.world.setBlockState(this.pos, this.state, 3);
	}

	private boolean canConnect(BlockPos pos) {
		RailPlacementHelper railPlacementHelper = this.getNeighboringRail(pos);
		if (railPlacementHelper == null) {
			return false;
		} else {
			railPlacementHelper.updateNeighborPositions();
			return railPlacementHelper.canConnect(this);
		}
	}

	public RailPlacementHelper updateBlockState(boolean powered, boolean forceUpdate, RailShape railShape) {
		BlockPos blockPos = this.pos.north();
		BlockPos blockPos2 = this.pos.south();
		BlockPos blockPos3 = this.pos.west();
		BlockPos blockPos4 = this.pos.east();
		boolean bl = this.canConnect(blockPos);
		boolean bl2 = this.canConnect(blockPos2);
		boolean bl3 = this.canConnect(blockPos3);
		boolean bl4 = this.canConnect(blockPos4);
		RailShape railShape2 = null;
		boolean bl5 = bl || bl2;
		boolean bl6 = bl3 || bl4;
		if (bl5 && !bl6) {
			railShape2 = RailShape.field_12665;
		}

		if (bl6 && !bl5) {
			railShape2 = RailShape.field_12674;
		}

		boolean bl7 = bl2 && bl4;
		boolean bl8 = bl2 && bl3;
		boolean bl9 = bl && bl4;
		boolean bl10 = bl && bl3;
		if (!this.allowCurves) {
			if (bl7 && !bl && !bl3) {
				railShape2 = RailShape.field_12664;
			}

			if (bl8 && !bl && !bl4) {
				railShape2 = RailShape.field_12671;
			}

			if (bl10 && !bl2 && !bl4) {
				railShape2 = RailShape.field_12672;
			}

			if (bl9 && !bl2 && !bl3) {
				railShape2 = RailShape.field_12663;
			}
		}

		if (railShape2 == null) {
			if (bl5 && bl6) {
				railShape2 = railShape;
			} else if (bl5) {
				railShape2 = RailShape.field_12665;
			} else if (bl6) {
				railShape2 = RailShape.field_12674;
			}

			if (!this.allowCurves) {
				if (powered) {
					if (bl7) {
						railShape2 = RailShape.field_12664;
					}

					if (bl8) {
						railShape2 = RailShape.field_12671;
					}

					if (bl9) {
						railShape2 = RailShape.field_12663;
					}

					if (bl10) {
						railShape2 = RailShape.field_12672;
					}
				} else {
					if (bl10) {
						railShape2 = RailShape.field_12672;
					}

					if (bl9) {
						railShape2 = RailShape.field_12663;
					}

					if (bl8) {
						railShape2 = RailShape.field_12671;
					}

					if (bl7) {
						railShape2 = RailShape.field_12664;
					}
				}
			}
		}

		if (railShape2 == RailShape.field_12665) {
			if (AbstractRailBlock.isRail(this.world, blockPos.up())) {
				railShape2 = RailShape.field_12670;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos2.up())) {
				railShape2 = RailShape.field_12668;
			}
		}

		if (railShape2 == RailShape.field_12674) {
			if (AbstractRailBlock.isRail(this.world, blockPos4.up())) {
				railShape2 = RailShape.field_12667;
			}

			if (AbstractRailBlock.isRail(this.world, blockPos3.up())) {
				railShape2 = RailShape.field_12666;
			}
		}

		if (railShape2 == null) {
			railShape2 = railShape;
		}

		this.computeNeighbors(railShape2);
		this.state = this.state.with(this.block.getShapeProperty(), railShape2);
		if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
			this.world.setBlockState(this.pos, this.state, 3);

			for (int i = 0; i < this.neighbors.size(); i++) {
				RailPlacementHelper railPlacementHelper = this.getNeighboringRail((BlockPos)this.neighbors.get(i));
				if (railPlacementHelper != null) {
					railPlacementHelper.updateNeighborPositions();
					if (railPlacementHelper.canConnect(this)) {
						railPlacementHelper.computeRailShape(this);
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
