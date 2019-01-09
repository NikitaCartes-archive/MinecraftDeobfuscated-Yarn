package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3471 {
	public static void method_15029(class_3485 arg, class_2338 arg2, class_2470 arg3, List<class_3471.class_3480> list, Random random) {
		class_3471.class_3474 lv = new class_3471.class_3474(random);
		class_3471.class_3475 lv2 = new class_3471.class_3475(arg, random);
		lv2.method_15050(arg2, arg3, list, lv);
	}

	static class class_3472 extends class_3471.class_3473 {
		private class_3472() {
		}

		@Override
		public String method_15037(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15032(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15033(Random random, boolean bl) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String method_15031(Random random, boolean bl) {
			return "1x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15035(Random random) {
			return "1x2_s" + (random.nextInt(2) + 1);
		}

		@Override
		public String method_15034(Random random) {
			return "2x2_a" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15036(Random random) {
			return "2x2_s1";
		}
	}

	abstract static class class_3473 {
		private class_3473() {
		}

		public abstract String method_15037(Random random);

		public abstract String method_15032(Random random);

		public abstract String method_15033(Random random, boolean bl);

		public abstract String method_15031(Random random, boolean bl);

		public abstract String method_15035(Random random);

		public abstract String method_15034(Random random);

		public abstract String method_15036(Random random);
	}

	static class class_3474 {
		private final Random field_15438;
		private final class_3471.class_3478 field_15440;
		private final class_3471.class_3478 field_15439;
		private final class_3471.class_3478[] field_15443;
		private final int field_15442;
		private final int field_15441;

		public class_3474(Random random) {
			this.field_15438 = random;
			int i = 11;
			this.field_15442 = 7;
			this.field_15441 = 4;
			this.field_15440 = new class_3471.class_3478(11, 11, 5);
			this.field_15440.method_15062(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
			this.field_15440.method_15062(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
			this.field_15440.method_15062(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 - 1, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 + 2, 1);
			this.field_15440.method_15062(0, 0, 11, 1, 5);
			this.field_15440.method_15062(0, 9, 11, 11, 5);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, class_2350.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, class_2350.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, class_2350.field_11039, 3);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, class_2350.field_11039, 3);

			while (this.method_15046(this.field_15440)) {
			}

			this.field_15443 = new class_3471.class_3478[3];
			this.field_15443[0] = new class_3471.class_3478(11, 11, 5);
			this.field_15443[1] = new class_3471.class_3478(11, 11, 5);
			this.field_15443[2] = new class_3471.class_3478(11, 11, 5);
			this.method_15042(this.field_15440, this.field_15443[0]);
			this.method_15042(this.field_15440, this.field_15443[1]);
			this.field_15443[0].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15443[1].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15439 = new class_3471.class_3478(this.field_15440.field_15454, this.field_15440.field_15453, 5);
			this.method_15048();
			this.method_15042(this.field_15439, this.field_15443[2]);
		}

		public static boolean method_15047(class_3471.class_3478 arg, int i, int j) {
			int k = arg.method_15066(i, j);
			return k == 1 || k == 2 || k == 3 || k == 4;
		}

		public boolean method_15039(class_3471.class_3478 arg, int i, int j, int k, int l) {
			return (this.field_15443[k].method_15066(i, j) & 65535) == l;
		}

		@Nullable
		public class_2350 method_15040(class_3471.class_3478 arg, int i, int j, int k, int l) {
			for (class_2350 lv : class_2350.class_2353.field_11062) {
				if (this.method_15039(arg, i + lv.method_10148(), j + lv.method_10165(), k, l)) {
					return lv;
				}
			}

			return null;
		}

		private void method_15045(class_3471.class_3478 arg, int i, int j, class_2350 arg2, int k) {
			if (k > 0) {
				arg.method_15065(i, j, 1);
				arg.method_15061(i + arg2.method_10148(), j + arg2.method_10165(), 0, 1);

				for (int l = 0; l < 8; l++) {
					class_2350 lv = class_2350.method_10139(this.field_15438.nextInt(4));
					if (lv != arg2.method_10153() && (lv != class_2350.field_11034 || !this.field_15438.nextBoolean())) {
						int m = i + arg2.method_10148();
						int n = j + arg2.method_10165();
						if (arg.method_15066(m + lv.method_10148(), n + lv.method_10165()) == 0 && arg.method_15066(m + lv.method_10148() * 2, n + lv.method_10165() * 2) == 0) {
							this.method_15045(arg, i + arg2.method_10148() + lv.method_10148(), j + arg2.method_10165() + lv.method_10165(), lv, k - 1);
							break;
						}
					}
				}

				class_2350 lv2 = arg2.method_10170();
				class_2350 lv = arg2.method_10160();
				arg.method_15061(i + lv2.method_10148(), j + lv2.method_10165(), 0, 2);
				arg.method_15061(i + lv.method_10148(), j + lv.method_10165(), 0, 2);
				arg.method_15061(i + arg2.method_10148() + lv2.method_10148(), j + arg2.method_10165() + lv2.method_10165(), 0, 2);
				arg.method_15061(i + arg2.method_10148() + lv.method_10148(), j + arg2.method_10165() + lv.method_10165(), 0, 2);
				arg.method_15061(i + arg2.method_10148() * 2, j + arg2.method_10165() * 2, 0, 2);
				arg.method_15061(i + lv2.method_10148() * 2, j + lv2.method_10165() * 2, 0, 2);
				arg.method_15061(i + lv.method_10148() * 2, j + lv.method_10165() * 2, 0, 2);
			}
		}

		private boolean method_15046(class_3471.class_3478 arg) {
			boolean bl = false;

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 0) {
						int k = 0;
						k += method_15047(arg, j + 1, i) ? 1 : 0;
						k += method_15047(arg, j - 1, i) ? 1 : 0;
						k += method_15047(arg, j, i + 1) ? 1 : 0;
						k += method_15047(arg, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							arg.method_15065(j, i, 2);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += method_15047(arg, j + 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j + 1, i - 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								arg.method_15065(j, i, 2);
								bl = true;
							}
						}
					}
				}
			}

			return bl;
		}

		private void method_15048() {
			List<class_3545<Integer, Integer>> list = Lists.<class_3545<Integer, Integer>>newArrayList();
			class_3471.class_3478 lv = this.field_15443[1];

			for (int i = 0; i < this.field_15439.field_15453; i++) {
				for (int j = 0; j < this.field_15439.field_15454; j++) {
					int k = lv.method_15066(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new class_3545<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
			} else {
				class_3545<Integer, Integer> lv2 = (class_3545<Integer, Integer>)list.get(this.field_15438.nextInt(list.size()));
				int jx = lv.method_15066(lv2.method_15442(), lv2.method_15441());
				lv.method_15065(lv2.method_15442(), lv2.method_15441(), jx | 4194304);
				class_2350 lv3 = this.method_15040(this.field_15440, lv2.method_15442(), lv2.method_15441(), 1, jx & 65535);
				int l = lv2.method_15442() + lv3.method_10148();
				int m = lv2.method_15441() + lv3.method_10165();

				for (int n = 0; n < this.field_15439.field_15453; n++) {
					for (int o = 0; o < this.field_15439.field_15454; o++) {
						if (!method_15047(this.field_15440, o, n)) {
							this.field_15439.method_15065(o, n, 5);
						} else if (o == lv2.method_15442() && n == lv2.method_15441()) {
							this.field_15439.method_15065(o, n, 3);
						} else if (o == l && n == m) {
							this.field_15439.method_15065(o, n, 3);
							this.field_15443[2].method_15065(o, n, 8388608);
						}
					}
				}

				List<class_2350> list2 = Lists.<class_2350>newArrayList();

				for (class_2350 lv4 : class_2350.class_2353.field_11062) {
					if (this.field_15439.method_15066(l + lv4.method_10148(), m + lv4.method_10165()) == 0) {
						list2.add(lv4);
					}
				}

				if (list2.isEmpty()) {
					this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
					lv.method_15065(lv2.method_15442(), lv2.method_15441(), jx);
				} else {
					class_2350 lv5 = (class_2350)list2.get(this.field_15438.nextInt(list2.size()));
					this.method_15045(this.field_15439, l + lv5.method_10148(), m + lv5.method_10165(), lv5, 4);

					while (this.method_15046(this.field_15439)) {
					}
				}
			}
		}

		private void method_15042(class_3471.class_3478 arg, class_3471.class_3478 arg2) {
			List<class_3545<Integer, Integer>> list = Lists.<class_3545<Integer, Integer>>newArrayList();

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 2) {
						list.add(new class_3545<>(j, i));
					}
				}
			}

			Collections.shuffle(list, this.field_15438);
			int i = 10;

			for (class_3545<Integer, Integer> lv : list) {
				int k = lv.method_15442();
				int l = lv.method_15441();
				if (arg2.method_15066(k, l) == 0) {
					int m = k;
					int n = k;
					int o = l;
					int p = l;
					int q = 65536;
					if (arg2.method_15066(k + 1, l) == 0
						&& arg2.method_15066(k, l + 1) == 0
						&& arg2.method_15066(k + 1, l + 1) == 0
						&& arg.method_15066(k + 1, l) == 2
						&& arg.method_15066(k, l + 1) == 2
						&& arg.method_15066(k + 1, l + 1) == 2) {
						n = k + 1;
						p = l + 1;
						q = 262144;
					} else if (arg2.method_15066(k - 1, l) == 0
						&& arg2.method_15066(k, l + 1) == 0
						&& arg2.method_15066(k - 1, l + 1) == 0
						&& arg.method_15066(k - 1, l) == 2
						&& arg.method_15066(k, l + 1) == 2
						&& arg.method_15066(k - 1, l + 1) == 2) {
						m = k - 1;
						p = l + 1;
						q = 262144;
					} else if (arg2.method_15066(k - 1, l) == 0
						&& arg2.method_15066(k, l - 1) == 0
						&& arg2.method_15066(k - 1, l - 1) == 0
						&& arg.method_15066(k - 1, l) == 2
						&& arg.method_15066(k, l - 1) == 2
						&& arg.method_15066(k - 1, l - 1) == 2) {
						m = k - 1;
						o = l - 1;
						q = 262144;
					} else if (arg2.method_15066(k + 1, l) == 0 && arg.method_15066(k + 1, l) == 2) {
						n = k + 1;
						q = 131072;
					} else if (arg2.method_15066(k, l + 1) == 0 && arg.method_15066(k, l + 1) == 2) {
						p = l + 1;
						q = 131072;
					} else if (arg2.method_15066(k - 1, l) == 0 && arg.method_15066(k - 1, l) == 2) {
						m = k - 1;
						q = 131072;
					} else if (arg2.method_15066(k, l - 1) == 0 && arg.method_15066(k, l - 1) == 2) {
						o = l - 1;
						q = 131072;
					}

					int r = this.field_15438.nextBoolean() ? m : n;
					int s = this.field_15438.nextBoolean() ? o : p;
					int t = 2097152;
					if (!arg.method_15067(r, s, 1)) {
						r = r == m ? n : m;
						s = s == o ? p : o;
						if (!arg.method_15067(r, s, 1)) {
							s = s == o ? p : o;
							if (!arg.method_15067(r, s, 1)) {
								r = r == m ? n : m;
								s = s == o ? p : o;
								if (!arg.method_15067(r, s, 1)) {
									t = 0;
									r = m;
									s = o;
								}
							}
						}
					}

					for (int u = o; u <= p; u++) {
						for (int v = m; v <= n; v++) {
							if (v == r && u == s) {
								arg2.method_15065(v, u, 1048576 | t | q | i);
							} else {
								arg2.method_15065(v, u, q | i);
							}
						}
					}

					i++;
				}
			}
		}
	}

	static class class_3475 {
		private final class_3485 field_15444;
		private final Random field_15447;
		private int field_15446;
		private int field_15445;

		public class_3475(class_3485 arg, Random random) {
			this.field_15444 = arg;
			this.field_15447 = random;
		}

		public void method_15050(class_2338 arg, class_2470 arg2, List<class_3471.class_3480> list, class_3471.class_3474 arg3) {
			class_3471.class_3476 lv = new class_3471.class_3476();
			lv.field_15449 = arg;
			lv.field_15450 = arg2;
			lv.field_15448 = "wall_flat";
			class_3471.class_3476 lv2 = new class_3471.class_3476();
			this.method_15054(list, lv);
			lv2.field_15449 = lv.field_15449.method_10086(8);
			lv2.field_15450 = lv.field_15450;
			lv2.field_15448 = "wall_window";
			if (!list.isEmpty()) {
			}

			class_3471.class_3478 lv3 = arg3.field_15440;
			class_3471.class_3478 lv4 = arg3.field_15439;
			this.field_15446 = arg3.field_15442 + 1;
			this.field_15445 = arg3.field_15441 + 1;
			int i = arg3.field_15442 + 1;
			int j = arg3.field_15441;
			this.method_15051(list, lv, lv3, class_2350.field_11035, this.field_15446, this.field_15445, i, j);
			this.method_15051(list, lv2, lv3, class_2350.field_11035, this.field_15446, this.field_15445, i, j);
			class_3471.class_3476 lv5 = new class_3471.class_3476();
			lv5.field_15449 = lv.field_15449.method_10086(19);
			lv5.field_15450 = lv.field_15450;
			lv5.field_15448 = "wall_window";
			boolean bl = false;

			for (int k = 0; k < lv4.field_15453 && !bl; k++) {
				for (int l = lv4.field_15454 - 1; l >= 0 && !bl; l--) {
					if (class_3471.class_3474.method_15047(lv4, l, k)) {
						lv5.field_15449 = lv5.field_15449.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (k - this.field_15445) * 8);
						lv5.field_15449 = lv5.field_15449.method_10079(arg2.method_10503(class_2350.field_11034), (l - this.field_15446) * 8);
						this.method_15052(list, lv5);
						this.method_15051(list, lv5, lv4, class_2350.field_11035, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_15055(list, arg.method_10086(16), arg2, lv3, lv4);
			this.method_15055(list, arg.method_10086(27), arg2, lv4, null);
			if (!list.isEmpty()) {
			}

			class_3471.class_3473[] lvs = new class_3471.class_3473[]{new class_3471.class_3472(), new class_3471.class_3477(), new class_3471.class_3479()};

			for (int lx = 0; lx < 3; lx++) {
				class_2338 lv6 = arg.method_10086(8 * lx + (lx == 2 ? 3 : 0));
				class_3471.class_3478 lv7 = arg3.field_15443[lx];
				class_3471.class_3478 lv8 = lx == 2 ? lv4 : lv3;
				String string = lx == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = lx == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int m = 0; m < lv8.field_15453; m++) {
					for (int n = 0; n < lv8.field_15454; n++) {
						if (lv8.method_15066(n, m) == 1) {
							class_2338 lv9 = lv6.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (m - this.field_15445) * 8);
							lv9 = lv9.method_10079(arg2.method_10503(class_2350.field_11034), (n - this.field_15446) * 8);
							list.add(new class_3471.class_3480(this.field_15444, "corridor_floor", lv9, arg2));
							if (lv8.method_15066(n, m - 1) == 1 || (lv7.method_15066(n, m - 1) & 8388608) == 8388608) {
								list.add(
									new class_3471.class_3480(this.field_15444, "carpet_north", lv9.method_10079(arg2.method_10503(class_2350.field_11034), 1).method_10084(), arg2)
								);
							}

							if (lv8.method_15066(n + 1, m) == 1 || (lv7.method_15066(n + 1, m) & 8388608) == 8388608) {
								list.add(
									new class_3471.class_3480(
										this.field_15444,
										"carpet_east",
										lv9.method_10079(arg2.method_10503(class_2350.field_11035), 1).method_10079(arg2.method_10503(class_2350.field_11034), 5).method_10084(),
										arg2
									)
								);
							}

							if (lv8.method_15066(n, m + 1) == 1 || (lv7.method_15066(n, m + 1) & 8388608) == 8388608) {
								list.add(
									new class_3471.class_3480(
										this.field_15444,
										string,
										lv9.method_10079(arg2.method_10503(class_2350.field_11035), 5).method_10079(arg2.method_10503(class_2350.field_11039), 1),
										arg2
									)
								);
							}

							if (lv8.method_15066(n - 1, m) == 1 || (lv7.method_15066(n - 1, m) & 8388608) == 8388608) {
								list.add(
									new class_3471.class_3480(
										this.field_15444,
										string2,
										lv9.method_10079(arg2.method_10503(class_2350.field_11039), 1).method_10079(arg2.method_10503(class_2350.field_11043), 1),
										arg2
									)
								);
							}
						}
					}
				}

				String string3 = lx == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = lx == 0 ? "indoors_door_1" : "indoors_door_2";
				List<class_2350> list2 = Lists.<class_2350>newArrayList();

				for (int o = 0; o < lv8.field_15453; o++) {
					for (int p = 0; p < lv8.field_15454; p++) {
						boolean bl2 = lx == 2 && lv8.method_15066(p, o) == 3;
						if (lv8.method_15066(p, o) == 2 || bl2) {
							int q = lv7.method_15066(p, o);
							int r = q & 983040;
							int s = q & 65535;
							bl2 = bl2 && (q & 8388608) == 8388608;
							list2.clear();
							if ((q & 2097152) == 2097152) {
								for (class_2350 lv10 : class_2350.class_2353.field_11062) {
									if (lv8.method_15066(p + lv10.method_10148(), o + lv10.method_10165()) == 1) {
										list2.add(lv10);
									}
								}
							}

							class_2350 lv11 = null;
							if (!list2.isEmpty()) {
								lv11 = (class_2350)list2.get(this.field_15447.nextInt(list2.size()));
							} else if ((q & 1048576) == 1048576) {
								lv11 = class_2350.field_11036;
							}

							class_2338 lv12 = lv6.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (o - this.field_15445) * 8);
							lv12 = lv12.method_10079(arg2.method_10503(class_2350.field_11034), -1 + (p - this.field_15446) * 8);
							if (class_3471.class_3474.method_15047(lv8, p - 1, o) && !arg3.method_15039(lv8, p - 1, o, lx, s)) {
								list.add(new class_3471.class_3480(this.field_15444, lv11 == class_2350.field_11039 ? string4 : string3, lv12, arg2));
							}

							if (lv8.method_15066(p + 1, o) == 1 && !bl2) {
								class_2338 lv13 = lv12.method_10079(arg2.method_10503(class_2350.field_11034), 8);
								list.add(new class_3471.class_3480(this.field_15444, lv11 == class_2350.field_11034 ? string4 : string3, lv13, arg2));
							}

							if (class_3471.class_3474.method_15047(lv8, p, o + 1) && !arg3.method_15039(lv8, p, o + 1, lx, s)) {
								class_2338 lv13 = lv12.method_10079(arg2.method_10503(class_2350.field_11035), 7);
								lv13 = lv13.method_10079(arg2.method_10503(class_2350.field_11034), 7);
								list.add(
									new class_3471.class_3480(this.field_15444, lv11 == class_2350.field_11035 ? string4 : string3, lv13, arg2.method_10501(class_2470.field_11463))
								);
							}

							if (lv8.method_15066(p, o - 1) == 1 && !bl2) {
								class_2338 lv13 = lv12.method_10079(arg2.method_10503(class_2350.field_11043), 1);
								lv13 = lv13.method_10079(arg2.method_10503(class_2350.field_11034), 7);
								list.add(
									new class_3471.class_3480(this.field_15444, lv11 == class_2350.field_11043 ? string4 : string3, lv13, arg2.method_10501(class_2470.field_11463))
								);
							}

							if (r == 65536) {
								this.method_15057(list, lv12, arg2, lv11, lvs[lx]);
							} else if (r == 131072 && lv11 != null) {
								class_2350 lv14 = arg3.method_15040(lv8, p, o, lx, s);
								boolean bl3 = (q & 4194304) == 4194304;
								this.method_15059(list, lv12, arg2, lv14, lv11, lvs[lx], bl3);
							} else if (r == 262144 && lv11 != null && lv11 != class_2350.field_11036) {
								class_2350 lv14 = lv11.method_10170();
								if (!arg3.method_15039(lv8, p + lv14.method_10148(), o + lv14.method_10165(), lx, s)) {
									lv14 = lv14.method_10153();
								}

								this.method_15056(list, lv12, arg2, lv14, lv11, lvs[lx]);
							} else if (r == 262144 && lv11 == class_2350.field_11036) {
								this.method_15053(list, lv12, arg2, lvs[lx]);
							}
						}
					}
				}
			}
		}

		private void method_15051(
			List<class_3471.class_3480> list, class_3471.class_3476 arg, class_3471.class_3478 arg2, class_2350 arg3, int i, int j, int k, int l
		) {
			int m = i;
			int n = j;
			class_2350 lv = arg3;

			do {
				if (!class_3471.class_3474.method_15047(arg2, m + arg3.method_10148(), n + arg3.method_10165())) {
					this.method_15058(list, arg);
					arg3 = arg3.method_10170();
					if (m != k || n != l || lv != arg3) {
						this.method_15052(list, arg);
					}
				} else if (class_3471.class_3474.method_15047(arg2, m + arg3.method_10148(), n + arg3.method_10165())
					&& class_3471.class_3474.method_15047(
						arg2, m + arg3.method_10148() + arg3.method_10160().method_10148(), n + arg3.method_10165() + arg3.method_10160().method_10165()
					)) {
					this.method_15060(list, arg);
					m += arg3.method_10148();
					n += arg3.method_10165();
					arg3 = arg3.method_10160();
				} else {
					m += arg3.method_10148();
					n += arg3.method_10165();
					if (m != k || n != l || lv != arg3) {
						this.method_15052(list, arg);
					}
				}
			} while (m != k || n != l || lv != arg3);
		}

		private void method_15055(List<class_3471.class_3480> list, class_2338 arg, class_2470 arg2, class_3471.class_3478 arg3, @Nullable class_3471.class_3478 arg4) {
			for (int i = 0; i < arg3.field_15453; i++) {
				for (int j = 0; j < arg3.field_15454; j++) {
					class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (i - this.field_15445) * 8);
					lv = lv.method_10079(arg2.method_10503(class_2350.field_11034), (j - this.field_15446) * 8);
					boolean bl = arg4 != null && class_3471.class_3474.method_15047(arg4, j, i);
					if (class_3471.class_3474.method_15047(arg3, j, i) && !bl) {
						list.add(new class_3471.class_3480(this.field_15444, "roof", lv.method_10086(3), arg2));
						if (!class_3471.class_3474.method_15047(arg3, j + 1, i)) {
							class_2338 lv2 = lv.method_10079(arg2.method_10503(class_2350.field_11034), 6);
							list.add(new class_3471.class_3480(this.field_15444, "roof_front", lv2, arg2));
						}

						if (!class_3471.class_3474.method_15047(arg3, j - 1, i)) {
							class_2338 lv2 = lv.method_10079(arg2.method_10503(class_2350.field_11034), 0);
							lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 7);
							list.add(new class_3471.class_3480(this.field_15444, "roof_front", lv2, arg2.method_10501(class_2470.field_11464)));
						}

						if (!class_3471.class_3474.method_15047(arg3, j, i - 1)) {
							class_2338 lv2 = lv.method_10079(arg2.method_10503(class_2350.field_11039), 1);
							list.add(new class_3471.class_3480(this.field_15444, "roof_front", lv2, arg2.method_10501(class_2470.field_11465)));
						}

						if (!class_3471.class_3474.method_15047(arg3, j, i + 1)) {
							class_2338 lv2 = lv.method_10079(arg2.method_10503(class_2350.field_11034), 6);
							lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 6);
							list.add(new class_3471.class_3480(this.field_15444, "roof_front", lv2, arg2.method_10501(class_2470.field_11463)));
						}
					}
				}
			}

			if (arg4 != null) {
				for (int i = 0; i < arg3.field_15453; i++) {
					for (int jx = 0; jx < arg3.field_15454; jx++) {
						class_2338 var17 = arg.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (i - this.field_15445) * 8);
						var17 = var17.method_10079(arg2.method_10503(class_2350.field_11034), (jx - this.field_15446) * 8);
						boolean bl = class_3471.class_3474.method_15047(arg4, jx, i);
						if (class_3471.class_3474.method_15047(arg3, jx, i) && bl) {
							if (!class_3471.class_3474.method_15047(arg3, jx + 1, i)) {
								class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11034), 7);
								list.add(new class_3471.class_3480(this.field_15444, "small_wall", lv2, arg2));
							}

							if (!class_3471.class_3474.method_15047(arg3, jx - 1, i)) {
								class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11039), 1);
								lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 6);
								list.add(new class_3471.class_3480(this.field_15444, "small_wall", lv2, arg2.method_10501(class_2470.field_11464)));
							}

							if (!class_3471.class_3474.method_15047(arg3, jx, i - 1)) {
								class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11039), 0);
								lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11043), 1);
								list.add(new class_3471.class_3480(this.field_15444, "small_wall", lv2, arg2.method_10501(class_2470.field_11465)));
							}

							if (!class_3471.class_3474.method_15047(arg3, jx, i + 1)) {
								class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11034), 6);
								lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 7);
								list.add(new class_3471.class_3480(this.field_15444, "small_wall", lv2, arg2.method_10501(class_2470.field_11463)));
							}

							if (!class_3471.class_3474.method_15047(arg3, jx + 1, i)) {
								if (!class_3471.class_3474.method_15047(arg3, jx, i - 1)) {
									class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11034), 7);
									lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11043), 2);
									list.add(new class_3471.class_3480(this.field_15444, "small_wall_corner", lv2, arg2));
								}

								if (!class_3471.class_3474.method_15047(arg3, jx, i + 1)) {
									class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11034), 8);
									lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 7);
									list.add(new class_3471.class_3480(this.field_15444, "small_wall_corner", lv2, arg2.method_10501(class_2470.field_11463)));
								}
							}

							if (!class_3471.class_3474.method_15047(arg3, jx - 1, i)) {
								if (!class_3471.class_3474.method_15047(arg3, jx, i - 1)) {
									class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11039), 2);
									lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11043), 1);
									list.add(new class_3471.class_3480(this.field_15444, "small_wall_corner", lv2, arg2.method_10501(class_2470.field_11465)));
								}

								if (!class_3471.class_3474.method_15047(arg3, jx, i + 1)) {
									class_2338 lv2 = var17.method_10079(arg2.method_10503(class_2350.field_11039), 1);
									lv2 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 8);
									list.add(new class_3471.class_3480(this.field_15444, "small_wall_corner", lv2, arg2.method_10501(class_2470.field_11464)));
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < arg3.field_15453; i++) {
				for (int jxx = 0; jxx < arg3.field_15454; jxx++) {
					class_2338 var19 = arg.method_10079(arg2.method_10503(class_2350.field_11035), 8 + (i - this.field_15445) * 8);
					var19 = var19.method_10079(arg2.method_10503(class_2350.field_11034), (jxx - this.field_15446) * 8);
					boolean bl = arg4 != null && class_3471.class_3474.method_15047(arg4, jxx, i);
					if (class_3471.class_3474.method_15047(arg3, jxx, i) && !bl) {
						if (!class_3471.class_3474.method_15047(arg3, jxx + 1, i)) {
							class_2338 lv2 = var19.method_10079(arg2.method_10503(class_2350.field_11034), 6);
							if (!class_3471.class_3474.method_15047(arg3, jxx, i + 1)) {
								class_2338 lv3 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 6);
								list.add(new class_3471.class_3480(this.field_15444, "roof_corner", lv3, arg2));
							} else if (class_3471.class_3474.method_15047(arg3, jxx + 1, i + 1)) {
								class_2338 lv3 = lv2.method_10079(arg2.method_10503(class_2350.field_11035), 5);
								list.add(new class_3471.class_3480(this.field_15444, "roof_inner_corner", lv3, arg2));
							}

							if (!class_3471.class_3474.method_15047(arg3, jxx, i - 1)) {
								list.add(new class_3471.class_3480(this.field_15444, "roof_corner", lv2, arg2.method_10501(class_2470.field_11465)));
							} else if (class_3471.class_3474.method_15047(arg3, jxx + 1, i - 1)) {
								class_2338 lv3 = var19.method_10079(arg2.method_10503(class_2350.field_11034), 9);
								lv3 = lv3.method_10079(arg2.method_10503(class_2350.field_11043), 2);
								list.add(new class_3471.class_3480(this.field_15444, "roof_inner_corner", lv3, arg2.method_10501(class_2470.field_11463)));
							}
						}

						if (!class_3471.class_3474.method_15047(arg3, jxx - 1, i)) {
							class_2338 lv2x = var19.method_10079(arg2.method_10503(class_2350.field_11034), 0);
							lv2x = lv2x.method_10079(arg2.method_10503(class_2350.field_11035), 0);
							if (!class_3471.class_3474.method_15047(arg3, jxx, i + 1)) {
								class_2338 lv3 = lv2x.method_10079(arg2.method_10503(class_2350.field_11035), 6);
								list.add(new class_3471.class_3480(this.field_15444, "roof_corner", lv3, arg2.method_10501(class_2470.field_11463)));
							} else if (class_3471.class_3474.method_15047(arg3, jxx - 1, i + 1)) {
								class_2338 lv3 = lv2x.method_10079(arg2.method_10503(class_2350.field_11035), 8);
								lv3 = lv3.method_10079(arg2.method_10503(class_2350.field_11039), 3);
								list.add(new class_3471.class_3480(this.field_15444, "roof_inner_corner", lv3, arg2.method_10501(class_2470.field_11465)));
							}

							if (!class_3471.class_3474.method_15047(arg3, jxx, i - 1)) {
								list.add(new class_3471.class_3480(this.field_15444, "roof_corner", lv2x, arg2.method_10501(class_2470.field_11464)));
							} else if (class_3471.class_3474.method_15047(arg3, jxx - 1, i - 1)) {
								class_2338 lv3 = lv2x.method_10079(arg2.method_10503(class_2350.field_11035), 1);
								list.add(new class_3471.class_3480(this.field_15444, "roof_inner_corner", lv3, arg2.method_10501(class_2470.field_11464)));
							}
						}
					}
				}
			}
		}

		private void method_15054(List<class_3471.class_3480> list, class_3471.class_3476 arg) {
			class_2350 lv = arg.field_15450.method_10503(class_2350.field_11039);
			list.add(new class_3471.class_3480(this.field_15444, "entrance", arg.field_15449.method_10079(lv, 9), arg.field_15450));
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11035), 16);
		}

		private void method_15052(List<class_3471.class_3480> list, class_3471.class_3476 arg) {
			list.add(
				new class_3471.class_3480(
					this.field_15444, arg.field_15448, arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11034), 7), arg.field_15450
				)
			);
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11035), 8);
		}

		private void method_15058(List<class_3471.class_3480> list, class_3471.class_3476 arg) {
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11035), -1);
			list.add(new class_3471.class_3480(this.field_15444, "wall_corner", arg.field_15449, arg.field_15450));
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11035), -7);
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11039), -6);
			arg.field_15450 = arg.field_15450.method_10501(class_2470.field_11463);
		}

		private void method_15060(List<class_3471.class_3480> list, class_3471.class_3476 arg) {
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11035), 6);
			arg.field_15449 = arg.field_15449.method_10079(arg.field_15450.method_10503(class_2350.field_11034), 8);
			arg.field_15450 = arg.field_15450.method_10501(class_2470.field_11465);
		}

		private void method_15057(List<class_3471.class_3480> list, class_2338 arg, class_2470 arg2, class_2350 arg3, class_3471.class_3473 arg4) {
			class_2470 lv = class_2470.field_11467;
			String string = arg4.method_15037(this.field_15447);
			if (arg3 != class_2350.field_11034) {
				if (arg3 == class_2350.field_11043) {
					lv = lv.method_10501(class_2470.field_11465);
				} else if (arg3 == class_2350.field_11039) {
					lv = lv.method_10501(class_2470.field_11464);
				} else if (arg3 == class_2350.field_11035) {
					lv = lv.method_10501(class_2470.field_11463);
				} else {
					string = arg4.method_15032(this.field_15447);
				}
			}

			class_2338 lv2 = class_3499.method_15162(new class_2338(1, 0, 0), class_2415.field_11302, lv, 7, 7);
			lv = lv.method_10501(arg2);
			lv2 = lv2.method_10070(arg2);
			class_2338 lv3 = arg.method_10069(lv2.method_10263(), 0, lv2.method_10260());
			list.add(new class_3471.class_3480(this.field_15444, string, lv3, lv));
		}

		private void method_15059(
			List<class_3471.class_3480> list, class_2338 arg, class_2470 arg2, class_2350 arg3, class_2350 arg4, class_3471.class_3473 arg5, boolean bl
		) {
			if (arg4 == class_2350.field_11034 && arg3 == class_2350.field_11035) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2));
			} else if (arg4 == class_2350.field_11034 && arg3 == class_2350.field_11043) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 6);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2, class_2415.field_11300));
			} else if (arg4 == class_2350.field_11039 && arg3 == class_2350.field_11043) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 7);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 6);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11464)));
			} else if (arg4 == class_2350.field_11039 && arg3 == class_2350.field_11035) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 7);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2, class_2415.field_11301));
			} else if (arg4 == class_2350.field_11035 && arg3 == class_2350.field_11034) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				list.add(
					new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11463), class_2415.field_11300)
				);
			} else if (arg4 == class_2350.field_11035 && arg3 == class_2350.field_11039) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 7);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11463)));
			} else if (arg4 == class_2350.field_11043 && arg3 == class_2350.field_11039) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 7);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 6);
				list.add(
					new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11463), class_2415.field_11301)
				);
			} else if (arg4 == class_2350.field_11043 && arg3 == class_2350.field_11034) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 6);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15033(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11465)));
			} else if (arg4 == class_2350.field_11035 && arg3 == class_2350.field_11043) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11043), 8);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15031(this.field_15447, bl), lv, arg2));
			} else if (arg4 == class_2350.field_11043 && arg3 == class_2350.field_11035) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 7);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 14);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15031(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11464)));
			} else if (arg4 == class_2350.field_11039 && arg3 == class_2350.field_11034) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 15);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15031(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11463)));
			} else if (arg4 == class_2350.field_11034 && arg3 == class_2350.field_11039) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11039), 7);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11035), 6);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15031(this.field_15447, bl), lv, arg2.method_10501(class_2470.field_11465)));
			} else if (arg4 == class_2350.field_11036 && arg3 == class_2350.field_11034) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 15);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15035(this.field_15447), lv, arg2.method_10501(class_2470.field_11463)));
			} else if (arg4 == class_2350.field_11036 && arg3 == class_2350.field_11035) {
				class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
				lv = lv.method_10079(arg2.method_10503(class_2350.field_11043), 0);
				list.add(new class_3471.class_3480(this.field_15444, arg5.method_15035(this.field_15447), lv, arg2));
			}
		}

		private void method_15056(List<class_3471.class_3480> list, class_2338 arg, class_2470 arg2, class_2350 arg3, class_2350 arg4, class_3471.class_3473 arg5) {
			int i = 0;
			int j = 0;
			class_2470 lv = arg2;
			class_2415 lv2 = class_2415.field_11302;
			if (arg4 == class_2350.field_11034 && arg3 == class_2350.field_11035) {
				i = -7;
			} else if (arg4 == class_2350.field_11034 && arg3 == class_2350.field_11043) {
				i = -7;
				j = 6;
				lv2 = class_2415.field_11300;
			} else if (arg4 == class_2350.field_11043 && arg3 == class_2350.field_11034) {
				i = 1;
				j = 14;
				lv = arg2.method_10501(class_2470.field_11465);
			} else if (arg4 == class_2350.field_11043 && arg3 == class_2350.field_11039) {
				i = 7;
				j = 14;
				lv = arg2.method_10501(class_2470.field_11465);
				lv2 = class_2415.field_11300;
			} else if (arg4 == class_2350.field_11035 && arg3 == class_2350.field_11039) {
				i = 7;
				j = -8;
				lv = arg2.method_10501(class_2470.field_11463);
			} else if (arg4 == class_2350.field_11035 && arg3 == class_2350.field_11034) {
				i = 1;
				j = -8;
				lv = arg2.method_10501(class_2470.field_11463);
				lv2 = class_2415.field_11300;
			} else if (arg4 == class_2350.field_11039 && arg3 == class_2350.field_11043) {
				i = 15;
				j = 6;
				lv = arg2.method_10501(class_2470.field_11464);
			} else if (arg4 == class_2350.field_11039 && arg3 == class_2350.field_11035) {
				i = 15;
				lv2 = class_2415.field_11301;
			}

			class_2338 lv3 = arg.method_10079(arg2.method_10503(class_2350.field_11034), i);
			lv3 = lv3.method_10079(arg2.method_10503(class_2350.field_11035), j);
			list.add(new class_3471.class_3480(this.field_15444, arg5.method_15034(this.field_15447), lv3, lv, lv2));
		}

		private void method_15053(List<class_3471.class_3480> list, class_2338 arg, class_2470 arg2, class_3471.class_3473 arg3) {
			class_2338 lv = arg.method_10079(arg2.method_10503(class_2350.field_11034), 1);
			list.add(new class_3471.class_3480(this.field_15444, arg3.method_15036(this.field_15447), lv, arg2, class_2415.field_11302));
		}
	}

	static class class_3476 {
		public class_2470 field_15450;
		public class_2338 field_15449;
		public String field_15448;

		private class_3476() {
		}
	}

	static class class_3477 extends class_3471.class_3473 {
		private class_3477() {
		}

		@Override
		public String method_15037(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15032(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15033(Random random, boolean bl) {
			return bl ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15031(Random random, boolean bl) {
			return bl ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15035(Random random) {
			return "1x2_se" + (random.nextInt(1) + 1);
		}

		@Override
		public String method_15034(Random random) {
			return "2x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15036(Random random) {
			return "2x2_s1";
		}
	}

	static class class_3478 {
		private final int[][] field_15451;
		private final int field_15454;
		private final int field_15453;
		private final int field_15452;

		public class_3478(int i, int j, int k) {
			this.field_15454 = i;
			this.field_15453 = j;
			this.field_15452 = k;
			this.field_15451 = new int[i][j];
		}

		public void method_15065(int i, int j, int k) {
			if (i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453) {
				this.field_15451[i][j] = k;
			}
		}

		public void method_15062(int i, int j, int k, int l, int m) {
			for (int n = j; n <= l; n++) {
				for (int o = i; o <= k; o++) {
					this.method_15065(o, n, m);
				}
			}
		}

		public int method_15066(int i, int j) {
			return i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453 ? this.field_15451[i][j] : this.field_15452;
		}

		public void method_15061(int i, int j, int k, int l) {
			if (this.method_15066(i, j) == k) {
				this.method_15065(i, j, l);
			}
		}

		public boolean method_15067(int i, int j, int k) {
			return this.method_15066(i - 1, j) == k || this.method_15066(i + 1, j) == k || this.method_15066(i, j + 1) == k || this.method_15066(i, j - 1) == k;
		}
	}

	static class class_3479 extends class_3471.class_3477 {
		private class_3479() {
		}
	}

	public static class class_3480 extends class_3470 {
		private final String field_15455;
		private final class_2470 field_15457;
		private final class_2415 field_15456;

		public class_3480(class_3485 arg, String string, class_2338 arg2, class_2470 arg3) {
			this(arg, string, arg2, arg3, class_2415.field_11302);
		}

		public class_3480(class_3485 arg, String string, class_2338 arg2, class_2470 arg3, class_2415 arg4) {
			super(class_3773.field_16907, 0);
			this.field_15455 = string;
			this.field_15432 = arg2;
			this.field_15457 = arg3;
			this.field_15456 = arg4;
			this.method_15068(arg);
		}

		public class_3480(class_3485 arg, class_2487 arg2) {
			super(class_3773.field_16907, arg2);
			this.field_15455 = arg2.method_10558("Template");
			this.field_15457 = class_2470.valueOf(arg2.method_10558("Rot"));
			this.field_15456 = class_2415.valueOf(arg2.method_10558("Mi"));
			this.method_15068(arg);
		}

		private void method_15068(class_3485 arg) {
			class_3499 lv = arg.method_15091(new class_2960("woodland_mansion/" + this.field_15455));
			class_3492 lv2 = new class_3492().method_15133(true).method_15123(this.field_15457).method_15125(this.field_15456).method_16184(class_3793.field_16718);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void method_14943(class_2487 arg) {
			super.method_14943(arg);
			arg.method_10582("Template", this.field_15455);
			arg.method_10582("Rot", this.field_15434.method_15113().name());
			arg.method_10582("Mi", this.field_15434.method_15114().name());
		}

		@Override
		protected void method_15026(String string, class_2338 arg, class_1936 arg2, Random random, class_3341 arg3) {
			if (string.startsWith("Chest")) {
				class_2470 lv = this.field_15434.method_15113();
				class_2680 lv2 = class_2246.field_10034.method_9564();
				if ("ChestWest".equals(string)) {
					lv2 = lv2.method_11657(class_2281.field_10768, lv.method_10503(class_2350.field_11039));
				} else if ("ChestEast".equals(string)) {
					lv2 = lv2.method_11657(class_2281.field_10768, lv.method_10503(class_2350.field_11034));
				} else if ("ChestSouth".equals(string)) {
					lv2 = lv2.method_11657(class_2281.field_10768, lv.method_10503(class_2350.field_11035));
				} else if ("ChestNorth".equals(string)) {
					lv2 = lv2.method_11657(class_2281.field_10768, lv.method_10503(class_2350.field_11043));
				}

				this.method_14921(arg2, arg3, random, arg, class_39.field_484, lv2);
			} else if ("Mage".equals(string)) {
				class_1564 lv3 = new class_1564(arg2.method_8410());
				lv3.method_5971();
				lv3.method_5725(arg, 0.0F, 0.0F);
				arg2.method_8649(lv3);
				arg2.method_8652(arg, class_2246.field_10124.method_9564(), 2);
			} else if ("Warrior".equals(string)) {
				class_1632 lv4 = new class_1632(arg2.method_8410());
				lv4.method_5971();
				lv4.method_5725(arg, 0.0F, 0.0F);
				lv4.method_5943(arg2, arg2.method_8404(new class_2338(lv4)), class_3730.field_16474, null, null);
				arg2.method_8649(lv4);
				arg2.method_8652(arg, class_2246.field_10124.method_9564(), 2);
			}
		}
	}
}
