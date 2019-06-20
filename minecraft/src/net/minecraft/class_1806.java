package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1806 extends class_1762 {
	public class_1806(class_1792.class_1793 arg) {
		super(arg);
	}

	public static class_1799 method_8005(class_1937 arg, int i, int j, byte b, boolean bl, boolean bl2) {
		class_1799 lv = new class_1799(class_1802.field_8204);
		method_8000(lv, arg, i, j, b, bl, bl2, arg.field_9247.method_12460());
		return lv;
	}

	@Nullable
	public static class_22 method_7997(class_1799 arg, class_1937 arg2) {
		return arg2.method_17891(method_17440(method_8003(arg)));
	}

	@Nullable
	public static class_22 method_8001(class_1799 arg, class_1937 arg2) {
		class_22 lv = method_7997(arg, arg2);
		if (lv == null && !arg2.field_9236) {
			lv = method_8000(arg, arg2, arg2.method_8401().method_215(), arg2.method_8401().method_166(), 3, false, false, arg2.field_9247.method_12460());
		}

		return lv;
	}

	public static int method_8003(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		return lv != null && lv.method_10573("map", 99) ? lv.method_10550("map") : 0;
	}

	private static class_22 method_8000(class_1799 arg, class_1937 arg2, int i, int j, int k, boolean bl, boolean bl2, class_2874 arg3) {
		int l = arg2.method_17889();
		class_22 lv = new class_22(method_17440(l));
		lv.method_105(i, j, k, bl, bl2, arg3);
		arg2.method_17890(lv);
		arg.method_7948().method_10569("map", l);
		return lv;
	}

	public static String method_17440(int i) {
		return "map_" + i;
	}

	public void method_7998(class_1937 arg, class_1297 arg2, class_22 arg3) {
		if (arg.field_9247.method_12460() == arg3.field_118 && arg2 instanceof class_1657) {
			int i = 1 << arg3.field_119;
			int j = arg3.field_116;
			int k = arg3.field_115;
			int l = class_3532.method_15357(arg2.field_5987 - (double)j) / i + 64;
			int m = class_3532.method_15357(arg2.field_6035 - (double)k) / i + 64;
			int n = 128 / i;
			if (arg.field_9247.method_12467()) {
				n /= 2;
			}

			class_22.class_23 lv = arg3.method_101((class_1657)arg2);
			lv.field_131++;
			boolean bl = false;

			for (int o = l - n + 1; o < l + n; o++) {
				if ((o & 15) == (lv.field_131 & 15) || bl) {
					bl = false;
					double d = 0.0;

					for (int p = m - n - 1; p < m + n; p++) {
						if (o >= 0 && p >= -1 && o < 128 && p < 128) {
							int q = o - l;
							int r = p - m;
							boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
							int s = (j / i + o - 64) * i;
							int t = (k / i + p - 64) * i;
							Multiset<class_3620> multiset = LinkedHashMultiset.create();
							class_2818 lv2 = arg.method_8500(new class_2338(s, 0, t));
							if (!lv2.method_12223()) {
								class_1923 lv3 = lv2.method_12004();
								int u = s & 15;
								int v = t & 15;
								int w = 0;
								double e = 0.0;
								if (arg.field_9247.method_12467()) {
									int x = s + t * 231871;
									x = x * x * 31287121 + x * 11;
									if ((x >> 20 & 1) == 0) {
										multiset.add(class_2246.field_10566.method_9564().method_11625(arg, class_2338.field_10980), 10);
									} else {
										multiset.add(class_2246.field_10340.method_9564().method_11625(arg, class_2338.field_10980), 100);
									}

									e = 100.0;
								} else {
									class_2338.class_2339 lv4 = new class_2338.class_2339();
									class_2338.class_2339 lv5 = new class_2338.class_2339();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = lv2.method_12005(class_2902.class_2903.field_13202, y + u, z + v) + 1;
											class_2680 lv6;
											if (aa <= 1) {
												lv6 = class_2246.field_9987.method_9564();
											} else {
												do {
													lv4.method_10103(lv3.method_8326() + y + u, --aa, lv3.method_8328() + z + v);
													lv6 = lv2.method_8320(lv4);
												} while (lv6.method_11625(arg, lv4) == class_3620.field_16008 && aa > 0);

												if (aa > 0 && !lv6.method_11618().method_15769()) {
													int ab = aa - 1;
													lv5.method_10101(lv4);

													class_2680 lv7;
													do {
														lv5.method_10099(ab--);
														lv7 = lv2.method_8320(lv5);
														w++;
													} while (ab > 0 && !lv7.method_11618().method_15769());

													lv6 = this.method_7995(arg, lv6, lv4);
												}
											}

											arg3.method_109(arg, lv3.method_8326() + y + u, lv3.method_8328() + z + v);
											e += (double)aa / (double)(i * i);
											multiset.add(lv6.method_11625(arg, lv4));
										}
									}
								}

								w /= i * i;
								double f = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
								int y = 1;
								if (f > 0.6) {
									y = 2;
								}

								if (f < -0.6) {
									y = 0;
								}

								class_3620 lv8 = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), class_3620.field_16008);
								if (lv8 == class_3620.field_16019) {
									f = (double)w * 0.1 + (double)(o + p & 1) * 0.2;
									y = 1;
									if (f < 0.5) {
										y = 2;
									}

									if (f > 0.9) {
										y = 0;
									}
								}

								d = e;
								if (p >= 0 && q * q + r * r < n * n && (!bl2 || (o + p & 1) != 0)) {
									byte b = arg3.field_122[o + p * 128];
									byte c = (byte)(lv8.field_16021 * 4 + y);
									if (b != c) {
										arg3.field_122[o + p * 128] = c;
										arg3.method_103(o, p);
										bl = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private class_2680 method_7995(class_1937 arg, class_2680 arg2, class_2338 arg3) {
		class_3610 lv = arg2.method_11618();
		return !lv.method_15769() && !class_2248.method_20045(arg2, arg, arg3, class_2350.field_11036) ? lv.method_15759() : arg2;
	}

	private static boolean method_8004(class_1959[] args, int i, int j, int k) {
		return args[j * i + k * i * 128 * i].method_8695() >= 0.0F;
	}

	public static void method_8002(class_1937 arg, class_1799 arg2) {
		class_22 lv = method_8001(arg2, arg);
		if (lv != null) {
			if (arg.field_9247.method_12460() == lv.field_118) {
				int i = 1 << lv.field_119;
				int j = lv.field_116;
				int k = lv.field_115;
				class_1959[] lvs = arg.method_8398().method_12129().method_12098().method_8760((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

				for (int l = 0; l < 128; l++) {
					for (int m = 0; m < 128; m++) {
						if (l > 0 && m > 0 && l < 127 && m < 127) {
							class_1959 lv2 = lvs[l * i + m * i * 128 * i];
							int n = 8;
							if (method_8004(lvs, i, l - 1, m - 1)) {
								n--;
							}

							if (method_8004(lvs, i, l - 1, m + 1)) {
								n--;
							}

							if (method_8004(lvs, i, l - 1, m)) {
								n--;
							}

							if (method_8004(lvs, i, l + 1, m - 1)) {
								n--;
							}

							if (method_8004(lvs, i, l + 1, m + 1)) {
								n--;
							}

							if (method_8004(lvs, i, l + 1, m)) {
								n--;
							}

							if (method_8004(lvs, i, l, m - 1)) {
								n--;
							}

							if (method_8004(lvs, i, l, m + 1)) {
								n--;
							}

							int o = 3;
							class_3620 lv3 = class_3620.field_16008;
							if (lv2.method_8695() < 0.0F) {
								lv3 = class_3620.field_15987;
								if (n > 7 && m % 2 == 0) {
									o = (l + (int)(class_3532.method_15374((float)m + 0.0F) * 7.0F)) / 8 % 5;
									if (o == 3) {
										o = 1;
									} else if (o == 4) {
										o = 0;
									}
								} else if (n > 7) {
									lv3 = class_3620.field_16008;
								} else if (n > 5) {
									o = 1;
								} else if (n > 3) {
									o = 0;
								} else if (n > 1) {
									o = 0;
								}
							} else if (n > 0) {
								lv3 = class_3620.field_15977;
								if (n > 3) {
									o = 1;
								} else {
									o = 3;
								}
							}

							if (lv3 != class_3620.field_16008) {
								lv.field_122[l + m * 128] = (byte)(lv3.field_16021 * 4 + o);
								lv.method_103(l, m);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void method_7888(class_1799 arg, class_1937 arg2, class_1297 arg3, int i, boolean bl) {
		if (!arg2.field_9236) {
			class_22 lv = method_8001(arg, arg2);
			if (lv != null) {
				if (arg3 instanceof class_1657) {
					class_1657 lv2 = (class_1657)arg3;
					lv.method_102(lv2, arg);
				}

				if (!lv.field_17403 && (bl || arg3 instanceof class_1657 && ((class_1657)arg3).method_6079() == arg)) {
					this.method_7998(arg2, arg3, lv);
				}
			}
		}
	}

	@Nullable
	@Override
	public class_2596<?> method_7757(class_1799 arg, class_1937 arg2, class_1657 arg3) {
		return method_8001(arg, arg2).method_100(arg, arg2, arg3);
	}

	@Override
	public void method_7843(class_1799 arg, class_1937 arg2, class_1657 arg3) {
		class_2487 lv = arg.method_7969();
		if (lv != null && lv.method_10573("map_scale_direction", 99)) {
			method_7996(arg, arg2, lv.method_10550("map_scale_direction"));
			lv.method_10551("map_scale_direction");
		}
	}

	protected static void method_7996(class_1799 arg, class_1937 arg2, int i) {
		class_22 lv = method_8001(arg, arg2);
		if (lv != null) {
			method_8000(arg, arg2, lv.field_116, lv.field_115, class_3532.method_15340(lv.field_119 + i, 0, 4), lv.field_114, lv.field_113, lv.field_118);
		}
	}

	@Nullable
	public static class_1799 method_17442(class_1937 arg, class_1799 arg2) {
		class_22 lv = method_8001(arg2, arg);
		if (lv != null) {
			class_1799 lv2 = arg2.method_7972();
			class_22 lv3 = method_8000(lv2, arg, 0, 0, lv.field_119, lv.field_114, lv.field_113, lv.field_118);
			lv3.method_18818(lv);
			return lv2;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_22 lv = arg2 == null ? null : method_8001(arg, arg2);
		if (lv != null && lv.field_17403) {
			list.add(new class_2588("filled_map.locked", method_8003(arg)).method_10854(class_124.field_1080));
		}

		if (arg3.method_8035()) {
			if (lv != null) {
				list.add(new class_2588("filled_map.id", method_8003(arg)).method_10854(class_124.field_1080));
				list.add(new class_2588("filled_map.scale", 1 << lv.field_119).method_10854(class_124.field_1080));
				list.add(new class_2588("filled_map.level", lv.field_119, 4).method_10854(class_124.field_1080));
			} else {
				list.add(new class_2588("filled_map.unknown").method_10854(class_124.field_1080));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static int method_7999(class_1799 arg) {
		class_2487 lv = arg.method_7941("display");
		if (lv != null && lv.method_10573("MapColor", 99)) {
			int i = lv.method_10550("MapColor");
			return 0xFF000000 | i & 16777215;
		} else {
			return -12173266;
		}
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_2680 lv = arg.method_8045().method_8320(arg.method_8037());
		if (lv.method_11602(class_3481.field_15501)) {
			if (!arg.field_8945.field_9236) {
				class_22 lv2 = method_8001(arg.method_8041(), arg.method_8045());
				lv2.method_108(arg.method_8045(), arg.method_8037());
			}

			return class_1269.field_5812;
		} else {
			return super.method_7884(arg);
		}
	}
}
