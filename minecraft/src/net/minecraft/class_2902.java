package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class class_2902 {
	private static final Predicate<class_2680> field_16744 = arg -> true;
	private static final Predicate<class_2680> field_16745 = arg -> arg.method_11620().method_15801();
	private final class_3508 field_13192 = new class_3508(9, 256);
	private final Predicate<class_2680> field_13193;
	private final class_2791 field_13191;

	public class_2902(class_2791 arg, class_2902.class_2903 arg2) {
		this.field_13193 = arg2.method_16402();
		this.field_13191 = arg;
	}

	public static void method_16684(class_2791 arg, Set<class_2902.class_2903> set) {
		int i = set.size();
		ObjectList<class_2902> objectList = new ObjectArrayList<>(i);
		ObjectListIterator<class_2902> objectListIterator = objectList.iterator();
		int j = arg.method_12031() + 16;

		try (class_2338.class_2340 lv = class_2338.class_2340.method_10109()) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					for (class_2902.class_2903 lv2 : set) {
						objectList.add(arg.method_12032(lv2));
					}

					for (int m = j - 1; m >= 0; m--) {
						lv.method_10113(k, m, l);
						class_2680 lv3 = arg.method_8320(lv);
						if (lv3.method_11614() != class_2246.field_10124) {
							while (objectListIterator.hasNext()) {
								class_2902 lv4 = (class_2902)objectListIterator.next();
								if (lv4.field_13193.test(lv3)) {
									lv4.method_12602(k, l, m + 1);
									objectListIterator.remove();
								}
							}

							if (objectList.isEmpty()) {
								break;
							}

							objectListIterator.back(i);
						}
					}
				}
			}
		}
	}

	public boolean method_12597(int i, int j, int k, class_2680 arg) {
		int l = this.method_12603(i, k);
		if (j <= l - 2) {
			return false;
		} else {
			if (this.field_13193.test(arg)) {
				if (j >= l) {
					this.method_12602(i, k, j + 1);
					return true;
				}
			} else if (l - 1 == j) {
				class_2338.class_2339 lv = new class_2338.class_2339();

				for (int m = j - 1; m >= 0; m--) {
					lv.method_10103(i, m, k);
					if (this.field_13193.test(this.field_13191.method_8320(lv))) {
						this.method_12602(i, k, m + 1);
						break;
					}

					if (m == 0) {
						this.method_12602(i, k, 0);
					}
				}

				return true;
			}

			return false;
		}
	}

	public int method_12603(int i, int j) {
		return this.method_12601(method_12595(i, j));
	}

	private int method_12601(int i) {
		return this.field_13192.method_15211(i);
	}

	private void method_12602(int i, int j, int k) {
		this.field_13192.method_15210(method_12595(i, j), k);
	}

	public void method_12600(long[] ls) {
		System.arraycopy(ls, 0, this.field_13192.method_15212(), 0, ls.length);
	}

	public long[] method_12598() {
		return this.field_13192.method_15212();
	}

	private static int method_12595(int i, int j) {
		return i + j * 16;
	}

	public static enum class_2903 {
		field_13194("WORLD_SURFACE_WG", class_2902.class_2904.field_13207, class_2902.field_16744),
		field_13202("WORLD_SURFACE", class_2902.class_2904.field_13206, class_2902.field_16744),
		field_13195("OCEAN_FLOOR_WG", class_2902.class_2904.field_13207, class_2902.field_16745),
		field_13200("OCEAN_FLOOR", class_2902.class_2904.field_13206, class_2902.field_16745),
		field_13197("MOTION_BLOCKING", class_2902.class_2904.field_16424, arg -> arg.method_11620().method_15801() || !arg.method_11618().method_15769()),
		field_13203(
			"MOTION_BLOCKING_NO_LEAVES",
			class_2902.class_2904.field_13206,
			arg -> (arg.method_11620().method_15801() || !arg.method_11618().method_15769()) && !(arg.method_11614() instanceof class_2397)
		);

		private final String field_13204;
		private final class_2902.class_2904 field_13198;
		private final Predicate<class_2680> field_16568;
		private static final Map<String, class_2902.class_2903> field_13205 = class_156.method_654(Maps.<String, class_2902.class_2903>newHashMap(), hashMap -> {
			for (class_2902.class_2903 lv : values()) {
				hashMap.put(lv.field_13204, lv);
			}
		});

		private class_2903(String string2, class_2902.class_2904 arg, Predicate<class_2680> predicate) {
			this.field_13204 = string2;
			this.field_13198 = arg;
			this.field_16568 = predicate;
		}

		public String method_12605() {
			return this.field_13204;
		}

		public boolean method_16136() {
			return this.field_13198 != class_2902.class_2904.field_13207;
		}

		public boolean method_16137() {
			return this.field_13198 == class_2902.class_2904.field_16424;
		}

		public static class_2902.class_2903 method_12609(String string) {
			return (class_2902.class_2903)field_13205.get(string);
		}

		public Predicate<class_2680> method_16402() {
			return this.field_16568;
		}
	}

	public static enum class_2904 {
		field_13207,
		field_13206,
		field_16424;
	}
}
