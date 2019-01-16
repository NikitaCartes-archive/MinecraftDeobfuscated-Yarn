package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class class_852 {
	private static final int field_4477 = (int)Math.pow(16.0, 0.0);
	private static final int field_4476 = (int)Math.pow(16.0, 1.0);
	private static final int field_4475 = (int)Math.pow(16.0, 2.0);
	private static final Direction[] field_4479 = Direction.values();
	private final BitSet field_4478 = new BitSet(4096);
	private static final int[] field_4474 = SystemUtil.consume(new int[1352], is -> {
		int i = 0;
		int j = 15;
		int k = 0;

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					if (l == 0 || l == 15 || m == 0 || m == 15 || n == 0 || n == 15) {
						is[k++] = method_3681(l, m, n);
					}
				}
			}
		}
	});
	private int field_4473 = 4096;

	public void method_3682(BlockPos blockPos) {
		this.field_4478.set(method_3683(blockPos), true);
		this.field_4473--;
	}

	private static int method_3683(BlockPos blockPos) {
		return method_3681(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
	}

	private static int method_3681(int i, int j, int k) {
		return i << 0 | j << 8 | k << 4;
	}

	public class_854 method_3679() {
		class_854 lv = new class_854();
		if (4096 - this.field_4473 < 256) {
			lv.method_3694(true);
		} else if (this.field_4473 == 0) {
			lv.method_3694(false);
		} else {
			for (int i : field_4474) {
				if (!this.field_4478.get(i)) {
					lv.method_3693(this.method_3687(i));
				}
			}
		}

		return lv;
	}

	public Set<Direction> method_3686(BlockPos blockPos) {
		return this.method_3687(method_3683(blockPos));
	}

	private Set<Direction> method_3687(int i) {
		Set<Direction> set = EnumSet.noneOf(Direction.class);
		IntPriorityQueue intPriorityQueue = new IntArrayFIFOQueue();
		intPriorityQueue.enqueue(i);
		this.field_4478.set(i, true);

		while (!intPriorityQueue.isEmpty()) {
			int j = intPriorityQueue.dequeueInt();
			this.method_3684(j, set);

			for (Direction direction : field_4479) {
				int k = this.method_3685(j, direction);
				if (k >= 0 && !this.field_4478.get(k)) {
					this.field_4478.set(k, true);
					intPriorityQueue.enqueue(k);
				}
			}
		}

		return set;
	}

	private void method_3684(int i, Set<Direction> set) {
		int j = i >> 0 & 15;
		if (j == 0) {
			set.add(Direction.WEST);
		} else if (j == 15) {
			set.add(Direction.EAST);
		}

		int k = i >> 8 & 15;
		if (k == 0) {
			set.add(Direction.DOWN);
		} else if (k == 15) {
			set.add(Direction.UP);
		}

		int l = i >> 4 & 15;
		if (l == 0) {
			set.add(Direction.NORTH);
		} else if (l == 15) {
			set.add(Direction.SOUTH);
		}
	}

	private int method_3685(int i, Direction direction) {
		switch (direction) {
			case DOWN:
				if ((i >> 8 & 15) == 0) {
					return -1;
				}

				return i - field_4475;
			case UP:
				if ((i >> 8 & 15) == 15) {
					return -1;
				}

				return i + field_4475;
			case NORTH:
				if ((i >> 4 & 15) == 0) {
					return -1;
				}

				return i - field_4476;
			case SOUTH:
				if ((i >> 4 & 15) == 15) {
					return -1;
				}

				return i + field_4476;
			case WEST:
				if ((i >> 0 & 15) == 0) {
					return -1;
				}

				return i - field_4477;
			case EAST:
				if ((i >> 0 & 15) == 15) {
					return -1;
				}

				return i + field_4477;
			default:
				return -1;
		}
	}
}
