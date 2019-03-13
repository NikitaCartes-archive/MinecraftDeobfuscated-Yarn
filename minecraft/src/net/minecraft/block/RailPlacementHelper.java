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
	private final BlockPos field_11410;
	private final AbstractRailBlock block;
	private BlockState field_11406;
	private final boolean allowCurves;
	private final List<BlockPos> neighbors = Lists.<BlockPos>newArrayList();

	public RailPlacementHelper(World world, BlockPos blockPos, BlockState blockState) {
		this.world = world;
		this.field_11410 = blockPos;
		this.field_11406 = blockState;
		this.block = (AbstractRailBlock)blockState.getBlock();
		RailShape railShape = blockState.method_11654(this.block.method_9474());
		this.allowCurves = this.block.canMakeCurves();
		this.method_10466(railShape);
	}

	public List<BlockPos> getNeighbors() {
		return this.neighbors;
	}

	private void method_10466(RailShape railShape) {
		this.neighbors.clear();
		switch (railShape) {
			case field_12665:
				this.neighbors.add(this.field_11410.north());
				this.neighbors.add(this.field_11410.south());
				break;
			case field_12674:
				this.neighbors.add(this.field_11410.west());
				this.neighbors.add(this.field_11410.east());
				break;
			case field_12667:
				this.neighbors.add(this.field_11410.west());
				this.neighbors.add(this.field_11410.east().up());
				break;
			case field_12666:
				this.neighbors.add(this.field_11410.west().up());
				this.neighbors.add(this.field_11410.east());
				break;
			case field_12670:
				this.neighbors.add(this.field_11410.north().up());
				this.neighbors.add(this.field_11410.south());
				break;
			case field_12668:
				this.neighbors.add(this.field_11410.north());
				this.neighbors.add(this.field_11410.south().up());
				break;
			case field_12664:
				this.neighbors.add(this.field_11410.east());
				this.neighbors.add(this.field_11410.south());
				break;
			case field_12671:
				this.neighbors.add(this.field_11410.west());
				this.neighbors.add(this.field_11410.south());
				break;
			case field_12672:
				this.neighbors.add(this.field_11410.west());
				this.neighbors.add(this.field_11410.north());
				break;
			case field_12663:
				this.neighbors.add(this.field_11410.east());
				this.neighbors.add(this.field_11410.north());
		}
	}

	private void method_10467() {
		for (int i = 0; i < this.neighbors.size(); i++) {
			RailPlacementHelper railPlacementHelper = this.method_10458((BlockPos)this.neighbors.get(i));
			if (railPlacementHelper != null && railPlacementHelper.method_10464(this)) {
				this.neighbors.set(i, railPlacementHelper.field_11410);
			} else {
				this.neighbors.remove(i--);
			}
		}
	}

	private boolean method_10456(BlockPos blockPos) {
		return AbstractRailBlock.method_9479(this.world, blockPos)
			|| AbstractRailBlock.method_9479(this.world, blockPos.up())
			|| AbstractRailBlock.method_9479(this.world, blockPos.down());
	}

	@Nullable
	private RailPlacementHelper method_10458(BlockPos blockPos) {
		BlockState blockState = this.world.method_8320(blockPos);
		if (AbstractRailBlock.method_9476(blockState)) {
			return new RailPlacementHelper(this.world, blockPos, blockState);
		} else {
			BlockPos blockPos2 = blockPos.up();
			blockState = this.world.method_8320(blockPos2);
			if (AbstractRailBlock.method_9476(blockState)) {
				return new RailPlacementHelper(this.world, blockPos2, blockState);
			} else {
				blockPos2 = blockPos.down();
				blockState = this.world.method_8320(blockPos2);
				return AbstractRailBlock.method_9476(blockState) ? new RailPlacementHelper(this.world, blockPos2, blockState) : null;
			}
		}
	}

	private boolean method_10464(RailPlacementHelper railPlacementHelper) {
		return this.method_10463(railPlacementHelper.field_11410);
	}

	private boolean method_10463(BlockPos blockPos) {
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
			if (this.method_10456(this.field_11410.method_10093(direction))) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10455(RailPlacementHelper railPlacementHelper) {
		return this.method_10464(railPlacementHelper) || this.neighbors.size() != 2;
	}

	private void method_10461(RailPlacementHelper railPlacementHelper) {
		this.neighbors.add(railPlacementHelper.field_11410);
		BlockPos blockPos = this.field_11410.north();
		BlockPos blockPos2 = this.field_11410.south();
		BlockPos blockPos3 = this.field_11410.west();
		BlockPos blockPos4 = this.field_11410.east();
		boolean bl = this.method_10463(blockPos);
		boolean bl2 = this.method_10463(blockPos2);
		boolean bl3 = this.method_10463(blockPos3);
		boolean bl4 = this.method_10463(blockPos4);
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
			if (AbstractRailBlock.method_9479(this.world, blockPos.up())) {
				railShape = RailShape.field_12670;
			}

			if (AbstractRailBlock.method_9479(this.world, blockPos2.up())) {
				railShape = RailShape.field_12668;
			}
		}

		if (railShape == RailShape.field_12674) {
			if (AbstractRailBlock.method_9479(this.world, blockPos4.up())) {
				railShape = RailShape.field_12667;
			}

			if (AbstractRailBlock.method_9479(this.world, blockPos3.up())) {
				railShape = RailShape.field_12666;
			}
		}

		if (railShape == null) {
			railShape = RailShape.field_12665;
		}

		this.field_11406 = this.field_11406.method_11657(this.block.method_9474(), railShape);
		this.world.method_8652(this.field_11410, this.field_11406, 3);
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
		BlockPos blockPos = this.field_11410.north();
		BlockPos blockPos2 = this.field_11410.south();
		BlockPos blockPos3 = this.field_11410.west();
		BlockPos blockPos4 = this.field_11410.east();
		boolean bl3 = this.method_10465(blockPos);
		boolean bl4 = this.method_10465(blockPos2);
		boolean bl5 = this.method_10465(blockPos3);
		boolean bl6 = this.method_10465(blockPos4);
		RailShape railShape = null;
		if ((bl3 || bl4) && !bl5 && !bl6) {
			railShape = RailShape.field_12665;
		}

		if ((bl5 || bl6) && !bl3 && !bl4) {
			railShape = RailShape.field_12674;
		}

		if (!this.allowCurves) {
			if (bl4 && bl6 && !bl3 && !bl5) {
				railShape = RailShape.field_12664;
			}

			if (bl4 && bl5 && !bl3 && !bl6) {
				railShape = RailShape.field_12671;
			}

			if (bl3 && bl5 && !bl4 && !bl6) {
				railShape = RailShape.field_12672;
			}

			if (bl3 && bl6 && !bl4 && !bl5) {
				railShape = RailShape.field_12663;
			}
		}

		if (railShape == null) {
			if (bl3 || bl4) {
				railShape = RailShape.field_12665;
			}

			if (bl5 || bl6) {
				railShape = RailShape.field_12674;
			}

			if (!this.allowCurves) {
				if (bl) {
					if (bl4 && bl6) {
						railShape = RailShape.field_12664;
					}

					if (bl5 && bl4) {
						railShape = RailShape.field_12671;
					}

					if (bl6 && bl3) {
						railShape = RailShape.field_12663;
					}

					if (bl3 && bl5) {
						railShape = RailShape.field_12672;
					}
				} else {
					if (bl3 && bl5) {
						railShape = RailShape.field_12672;
					}

					if (bl6 && bl3) {
						railShape = RailShape.field_12663;
					}

					if (bl5 && bl4) {
						railShape = RailShape.field_12671;
					}

					if (bl4 && bl6) {
						railShape = RailShape.field_12664;
					}
				}
			}
		}

		if (railShape == RailShape.field_12665) {
			if (AbstractRailBlock.method_9479(this.world, blockPos.up())) {
				railShape = RailShape.field_12670;
			}

			if (AbstractRailBlock.method_9479(this.world, blockPos2.up())) {
				railShape = RailShape.field_12668;
			}
		}

		if (railShape == RailShape.field_12674) {
			if (AbstractRailBlock.method_9479(this.world, blockPos4.up())) {
				railShape = RailShape.field_12667;
			}

			if (AbstractRailBlock.method_9479(this.world, blockPos3.up())) {
				railShape = RailShape.field_12666;
			}
		}

		if (railShape == null) {
			railShape = RailShape.field_12665;
		}

		this.method_10466(railShape);
		this.field_11406 = this.field_11406.method_11657(this.block.method_9474(), railShape);
		if (bl2 || this.world.method_8320(this.field_11410) != this.field_11406) {
			this.world.method_8652(this.field_11410, this.field_11406, 3);

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

	public BlockState method_10462() {
		return this.field_11406;
	}
}
