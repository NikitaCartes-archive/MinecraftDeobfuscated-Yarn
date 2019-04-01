package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_852 {
	private static final int field_4477 = (int)Math.pow(16.0, 0.0);
	private static final int field_4476 = (int)Math.pow(16.0, 1.0);
	private static final int field_4475 = (int)Math.pow(16.0, 2.0);
	private static final class_2350[] field_4479 = class_2350.values();
	private final BitSet field_4478 = new BitSet(4096);
	private static final int[] field_4474 = class_156.method_654(new int[1352], is -> {
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

	public void method_3682(class_2338 arg) {
		this.field_4478.set(method_3683(arg), true);
		this.field_4473--;
	}

	private static int method_3683(class_2338 arg) {
		return method_3681(arg.method_10263() & 15, arg.method_10264() & 15, arg.method_10260() & 15);
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

	public Set<class_2350> method_3686(class_2338 arg) {
		return this.method_3687(method_3683(arg));
	}

	private Set<class_2350> method_3687(int i) {
		Set<class_2350> set = EnumSet.noneOf(class_2350.class);
		IntPriorityQueue intPriorityQueue = new IntArrayFIFOQueue();
		intPriorityQueue.enqueue(i);
		this.field_4478.set(i, true);

		while (!intPriorityQueue.isEmpty()) {
			int j = intPriorityQueue.dequeueInt();
			this.method_3684(j, set);

			for (class_2350 lv : field_4479) {
				int k = this.method_3685(j, lv);
				if (k >= 0 && !this.field_4478.get(k)) {
					this.field_4478.set(k, true);
					intPriorityQueue.enqueue(k);
				}
			}
		}

		return set;
	}

	private void method_3684(int i, Set<class_2350> set) {
		int j = i >> 0 & 15;
		if (j == 0) {
			set.add(class_2350.field_11039);
		} else if (j == 15) {
			set.add(class_2350.field_11034);
		}

		int k = i >> 8 & 15;
		if (k == 0) {
			set.add(class_2350.field_11033);
		} else if (k == 15) {
			set.add(class_2350.field_11036);
		}

		int l = i >> 4 & 15;
		if (l == 0) {
			set.add(class_2350.field_11043);
		} else if (l == 15) {
			set.add(class_2350.field_11035);
		}
	}

	private int method_3685(int i, class_2350 arg) {
		switch (arg) {
			case field_11033:
				if ((i >> 8 & 15) == 0) {
					return -1;
				}

				return i - field_4475;
			case field_11036:
				if ((i >> 8 & 15) == 15) {
					return -1;
				}

				return i + field_4475;
			case field_11043:
				if ((i >> 4 & 15) == 0) {
					return -1;
				}

				return i - field_4476;
			case field_11035:
				if ((i >> 4 & 15) == 15) {
					return -1;
				}

				return i + field_4476;
			case field_11039:
				if ((i >> 0 & 15) == 0) {
					return -1;
				}

				return i - field_4477;
			case field_11034:
				if ((i >> 0 & 15) == 15) {
					return -1;
				}

				return i + field_4477;
			default:
				return -1;
		}
	}
}
