package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_2452 {
	private final World field_11409;
	private final BlockPos field_11410;
	private final AbstractRailBlock field_11411;
	private BlockState field_11406;
	private final boolean field_11408;
	private final List<BlockPos> field_11407 = Lists.<BlockPos>newArrayList();

	public class_2452(World world, BlockPos blockPos, BlockState blockState) {
		this.field_11409 = world;
		this.field_11410 = blockPos;
		this.field_11406 = blockState;
		this.field_11411 = (AbstractRailBlock)blockState.getBlock();
		RailShape railShape = blockState.get(this.field_11411.getShapeProperty());
		this.field_11408 = this.field_11411.method_9478();
		this.method_10466(railShape);
	}

	public List<BlockPos> method_10457() {
		return this.field_11407;
	}

	private void method_10466(RailShape railShape) {
		this.field_11407.clear();
		switch (railShape) {
			case field_12665:
				this.field_11407.add(this.field_11410.north());
				this.field_11407.add(this.field_11410.south());
				break;
			case field_12674:
				this.field_11407.add(this.field_11410.west());
				this.field_11407.add(this.field_11410.east());
				break;
			case field_12667:
				this.field_11407.add(this.field_11410.west());
				this.field_11407.add(this.field_11410.east().up());
				break;
			case field_12666:
				this.field_11407.add(this.field_11410.west().up());
				this.field_11407.add(this.field_11410.east());
				break;
			case field_12670:
				this.field_11407.add(this.field_11410.north().up());
				this.field_11407.add(this.field_11410.south());
				break;
			case field_12668:
				this.field_11407.add(this.field_11410.north());
				this.field_11407.add(this.field_11410.south().up());
				break;
			case field_12664:
				this.field_11407.add(this.field_11410.east());
				this.field_11407.add(this.field_11410.south());
				break;
			case field_12671:
				this.field_11407.add(this.field_11410.west());
				this.field_11407.add(this.field_11410.south());
				break;
			case field_12672:
				this.field_11407.add(this.field_11410.west());
				this.field_11407.add(this.field_11410.north());
				break;
			case field_12663:
				this.field_11407.add(this.field_11410.east());
				this.field_11407.add(this.field_11410.north());
		}
	}

	private void method_10467() {
		for (int i = 0; i < this.field_11407.size(); i++) {
			class_2452 lv = this.method_10458((BlockPos)this.field_11407.get(i));
			if (lv != null && lv.method_10464(this)) {
				this.field_11407.set(i, lv.field_11410);
			} else {
				this.field_11407.remove(i--);
			}
		}
	}

	private boolean method_10456(BlockPos blockPos) {
		return AbstractRailBlock.isRail(this.field_11409, blockPos)
			|| AbstractRailBlock.isRail(this.field_11409, blockPos.up())
			|| AbstractRailBlock.isRail(this.field_11409, blockPos.down());
	}

	@Nullable
	private class_2452 method_10458(BlockPos blockPos) {
		BlockState blockState = this.field_11409.getBlockState(blockPos);
		if (AbstractRailBlock.method_9476(blockState)) {
			return new class_2452(this.field_11409, blockPos, blockState);
		} else {
			BlockPos blockPos2 = blockPos.up();
			blockState = this.field_11409.getBlockState(blockPos2);
			if (AbstractRailBlock.method_9476(blockState)) {
				return new class_2452(this.field_11409, blockPos2, blockState);
			} else {
				blockPos2 = blockPos.down();
				blockState = this.field_11409.getBlockState(blockPos2);
				return AbstractRailBlock.method_9476(blockState) ? new class_2452(this.field_11409, blockPos2, blockState) : null;
			}
		}
	}

	private boolean method_10464(class_2452 arg) {
		return this.method_10463(arg.field_11410);
	}

	private boolean method_10463(BlockPos blockPos) {
		for (int i = 0; i < this.field_11407.size(); i++) {
			BlockPos blockPos2 = (BlockPos)this.field_11407.get(i);
			if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
				return true;
			}
		}

		return false;
	}

	public int method_10460() {
		int i = 0;

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			if (this.method_10456(this.field_11410.method_10093(direction))) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10455(class_2452 arg) {
		return this.method_10464(arg) || this.field_11407.size() != 2;
	}

	private void method_10461(class_2452 arg) {
		this.field_11407.add(arg.field_11410);
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

		if (!this.field_11408) {
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
			if (AbstractRailBlock.isRail(this.field_11409, blockPos.up())) {
				railShape = RailShape.field_12670;
			}

			if (AbstractRailBlock.isRail(this.field_11409, blockPos2.up())) {
				railShape = RailShape.field_12668;
			}
		}

		if (railShape == RailShape.field_12674) {
			if (AbstractRailBlock.isRail(this.field_11409, blockPos4.up())) {
				railShape = RailShape.field_12667;
			}

			if (AbstractRailBlock.isRail(this.field_11409, blockPos3.up())) {
				railShape = RailShape.field_12666;
			}
		}

		if (railShape == null) {
			railShape = RailShape.field_12665;
		}

		this.field_11406 = this.field_11406.with(this.field_11411.getShapeProperty(), railShape);
		this.field_11409.setBlockState(this.field_11410, this.field_11406, 3);
	}

	private boolean method_10465(BlockPos blockPos) {
		class_2452 lv = this.method_10458(blockPos);
		if (lv == null) {
			return false;
		} else {
			lv.method_10467();
			return lv.method_10455(this);
		}
	}

	public class_2452 method_10459(boolean bl, boolean bl2) {
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

		if (!this.field_11408) {
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

			if (!this.field_11408) {
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
			if (AbstractRailBlock.isRail(this.field_11409, blockPos.up())) {
				railShape = RailShape.field_12670;
			}

			if (AbstractRailBlock.isRail(this.field_11409, blockPos2.up())) {
				railShape = RailShape.field_12668;
			}
		}

		if (railShape == RailShape.field_12674) {
			if (AbstractRailBlock.isRail(this.field_11409, blockPos4.up())) {
				railShape = RailShape.field_12667;
			}

			if (AbstractRailBlock.isRail(this.field_11409, blockPos3.up())) {
				railShape = RailShape.field_12666;
			}
		}

		if (railShape == null) {
			railShape = RailShape.field_12665;
		}

		this.method_10466(railShape);
		this.field_11406 = this.field_11406.with(this.field_11411.getShapeProperty(), railShape);
		if (bl2 || this.field_11409.getBlockState(this.field_11410) != this.field_11406) {
			this.field_11409.setBlockState(this.field_11410, this.field_11406, 3);

			for (int i = 0; i < this.field_11407.size(); i++) {
				class_2452 lv = this.method_10458((BlockPos)this.field_11407.get(i));
				if (lv != null) {
					lv.method_10467();
					if (lv.method_10455(this)) {
						lv.method_10461(this);
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
