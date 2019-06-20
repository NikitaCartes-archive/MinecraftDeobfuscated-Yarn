package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class class_3499 {
	private final List<List<class_3499.class_3501>> field_15586 = Lists.<List<class_3499.class_3501>>newArrayList();
	private final List<class_3499.class_3502> field_15589 = Lists.<class_3499.class_3502>newArrayList();
	private class_2338 field_15587 = class_2338.field_10980;
	private String field_15588 = "?";

	public class_2338 method_15160() {
		return this.field_15587;
	}

	public void method_15161(String string) {
		this.field_15588 = string;
	}

	public String method_15181() {
		return this.field_15588;
	}

	public void method_15174(class_1937 arg, class_2338 arg2, class_2338 arg3, boolean bl, @Nullable class_2248 arg4) {
		if (arg3.method_10263() >= 1 && arg3.method_10264() >= 1 && arg3.method_10260() >= 1) {
			class_2338 lv = arg2.method_10081(arg3).method_10069(-1, -1, -1);
			List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();
			List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();
			List<class_3499.class_3501> list3 = Lists.<class_3499.class_3501>newArrayList();
			class_2338 lv2 = new class_2338(
				Math.min(arg2.method_10263(), lv.method_10263()), Math.min(arg2.method_10264(), lv.method_10264()), Math.min(arg2.method_10260(), lv.method_10260())
			);
			class_2338 lv3 = new class_2338(
				Math.max(arg2.method_10263(), lv.method_10263()), Math.max(arg2.method_10264(), lv.method_10264()), Math.max(arg2.method_10260(), lv.method_10260())
			);
			this.field_15587 = arg3;

			for (class_2338 lv4 : class_2338.method_10097(lv2, lv3)) {
				class_2338 lv5 = lv4.method_10059(lv2);
				class_2680 lv6 = arg.method_8320(lv4);
				if (arg4 == null || arg4 != lv6.method_11614()) {
					class_2586 lv7 = arg.method_8321(lv4);
					if (lv7 != null) {
						class_2487 lv8 = lv7.method_11007(new class_2487());
						lv8.method_10551("x");
						lv8.method_10551("y");
						lv8.method_10551("z");
						list2.add(new class_3499.class_3501(lv5, lv6, lv8));
					} else if (!lv6.method_11598(arg, lv4) && !class_2248.method_9614(lv6.method_11628(arg, lv4))) {
						list3.add(new class_3499.class_3501(lv5, lv6, null));
					} else {
						list.add(new class_3499.class_3501(lv5, lv6, null));
					}
				}
			}

			List<class_3499.class_3501> list4 = Lists.<class_3499.class_3501>newArrayList();
			list4.addAll(list);
			list4.addAll(list2);
			list4.addAll(list3);
			this.field_15586.clear();
			this.field_15586.add(list4);
			if (bl) {
				this.method_15164(arg, lv2, lv3.method_10069(1, 1, 1));
			} else {
				this.field_15589.clear();
			}
		}
	}

	private void method_15164(class_1937 arg, class_2338 arg2, class_2338 arg3) {
		List<class_1297> list = arg.method_8390(class_1297.class, new class_238(arg2, arg3), argx -> !(argx instanceof class_1657));
		this.field_15589.clear();

		for (class_1297 lv : list) {
			class_243 lv2 = new class_243(
				lv.field_5987 - (double)arg2.method_10263(), lv.field_6010 - (double)arg2.method_10264(), lv.field_6035 - (double)arg2.method_10260()
			);
			class_2487 lv3 = new class_2487();
			lv.method_5662(lv3);
			class_2338 lv4;
			if (lv instanceof class_1534) {
				lv4 = ((class_1534)lv).method_6896().method_10059(arg2);
			} else {
				lv4 = new class_2338(lv2);
			}

			this.field_15589.add(new class_3499.class_3502(lv2, lv4, lv3));
		}
	}

	public List<class_3499.class_3501> method_16445(class_2338 arg, class_3492 arg2, class_2248 arg3) {
		return this.method_15165(arg, arg2, arg3, true);
	}

	public List<class_3499.class_3501> method_15165(class_2338 arg, class_3492 arg2, class_2248 arg3, boolean bl) {
		List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();
		class_3341 lv = arg2.method_15124();

		for (class_3499.class_3501 lv2 : arg2.method_15121(this.field_15586, arg)) {
			class_2338 lv3 = bl ? method_15171(arg2, lv2.field_15597).method_10081(arg) : lv2.field_15597;
			if (lv == null || lv.method_14662(lv3)) {
				class_2680 lv4 = lv2.field_15596;
				if (lv4.method_11614() == arg3) {
					list.add(new class_3499.class_3501(lv3, lv4.method_11626(arg2.method_15113()), lv2.field_15595));
				}
			}
		}

		return list;
	}

	public class_2338 method_15180(class_3492 arg, class_2338 arg2, class_3492 arg3, class_2338 arg4) {
		class_2338 lv = method_15171(arg, arg2);
		class_2338 lv2 = method_15171(arg3, arg4);
		return lv.method_10059(lv2);
	}

	public static class_2338 method_15171(class_3492 arg, class_2338 arg2) {
		return method_15168(arg2, arg.method_15114(), arg.method_15113(), arg.method_15134());
	}

	public void method_15182(class_1936 arg, class_2338 arg2, class_3492 arg3) {
		arg3.method_15132();
		this.method_15178(arg, arg2, arg3);
	}

	public void method_15178(class_1936 arg, class_2338 arg2, class_3492 arg3) {
		this.method_15172(arg, arg2, arg3, 2);
	}

	public boolean method_15172(class_1936 arg, class_2338 arg2, class_3492 arg3, int i) {
		if (this.field_15586.isEmpty()) {
			return false;
		} else {
			List<class_3499.class_3501> list = arg3.method_15121(this.field_15586, arg2);
			if ((!list.isEmpty() || !arg3.method_15135() && !this.field_15589.isEmpty())
				&& this.field_15587.method_10263() >= 1
				&& this.field_15587.method_10264() >= 1
				&& this.field_15587.method_10260() >= 1) {
				class_3341 lv = arg3.method_15124();
				List<class_2338> list2 = Lists.<class_2338>newArrayListWithCapacity(arg3.method_15120() ? list.size() : 0);
				List<Pair<class_2338, class_2487>> list3 = Lists.<Pair<class_2338, class_2487>>newArrayListWithCapacity(list.size());
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MAX_VALUE;
				int m = Integer.MIN_VALUE;
				int n = Integer.MIN_VALUE;
				int o = Integer.MIN_VALUE;

				for (class_3499.class_3501 lv2 : method_16446(arg, arg2, arg3, list)) {
					class_2338 lv3 = lv2.field_15597;
					if (lv == null || lv.method_14662(lv3)) {
						class_3610 lv4 = arg3.method_15120() ? arg.method_8316(lv3) : null;
						class_2680 lv5 = lv2.field_15596.method_11605(arg3.method_15114()).method_11626(arg3.method_15113());
						if (lv2.field_15595 != null) {
							class_2586 lv6 = arg.method_8321(lv3);
							class_3829.method_16825(lv6);
							arg.method_8652(lv3, class_2246.field_10499.method_9564(), 20);
						}

						if (arg.method_8652(lv3, lv5, i)) {
							j = Math.min(j, lv3.method_10263());
							k = Math.min(k, lv3.method_10264());
							l = Math.min(l, lv3.method_10260());
							m = Math.max(m, lv3.method_10263());
							n = Math.max(n, lv3.method_10264());
							o = Math.max(o, lv3.method_10260());
							list3.add(Pair.of(lv3, lv2.field_15595));
							if (lv2.field_15595 != null) {
								class_2586 lv6 = arg.method_8321(lv3);
								if (lv6 != null) {
									lv2.field_15595.method_10569("x", lv3.method_10263());
									lv2.field_15595.method_10569("y", lv3.method_10264());
									lv2.field_15595.method_10569("z", lv3.method_10260());
									lv6.method_11014(lv2.field_15595);
									lv6.method_11001(arg3.method_15114());
									lv6.method_11013(arg3.method_15113());
								}
							}

							if (lv4 != null && lv5.method_11614() instanceof class_2402) {
								((class_2402)lv5.method_11614()).method_10311(arg, lv3, lv5, lv4);
								if (!lv4.method_15771()) {
									list2.add(lv3);
								}
							}
						}
					}
				}

				boolean bl = true;
				class_2350[] lvs = new class_2350[]{class_2350.field_11036, class_2350.field_11043, class_2350.field_11034, class_2350.field_11035, class_2350.field_11039};

				while (bl && !list2.isEmpty()) {
					bl = false;
					Iterator<class_2338> iterator = list2.iterator();

					while (iterator.hasNext()) {
						class_2338 lv7 = (class_2338)iterator.next();
						class_2338 lv8 = lv7;
						class_3610 lv9 = arg.method_8316(lv7);

						for (int p = 0; p < lvs.length && !lv9.method_15771(); p++) {
							class_2338 lv10 = lv8.method_10093(lvs[p]);
							class_3610 lv11 = arg.method_8316(lv10);
							if (lv11.method_15763(arg, lv10) > lv9.method_15763(arg, lv8) || lv11.method_15771() && !lv9.method_15771()) {
								lv9 = lv11;
								lv8 = lv10;
							}
						}

						if (lv9.method_15771()) {
							class_2680 lv12 = arg.method_8320(lv7);
							class_2248 lv13 = lv12.method_11614();
							if (lv13 instanceof class_2402) {
								((class_2402)lv13).method_10311(arg, lv7, lv12, lv9);
								bl = true;
								iterator.remove();
							}
						}
					}
				}

				if (j <= m) {
					if (!arg3.method_16444()) {
						class_251 lv14 = new class_244(m - j + 1, n - k + 1, o - l + 1);
						int q = j;
						int r = k;
						int s = l;

						for (Pair<class_2338, class_2487> pair : list3) {
							class_2338 lv15 = pair.getFirst();
							lv14.method_1049(lv15.method_10263() - q, lv15.method_10264() - r, lv15.method_10260() - s, true, true);
						}

						method_20532(arg, i, lv14, q, r, s);
					}

					for (Pair<class_2338, class_2487> pair2 : list3) {
						class_2338 lv8 = pair2.getFirst();
						if (!arg3.method_16444()) {
							class_2680 lv16 = arg.method_8320(lv8);
							class_2680 lv12 = class_2248.method_9510(lv16, arg, lv8);
							if (lv16 != lv12) {
								arg.method_8652(lv8, lv12, i & -2 | 16);
							}

							arg.method_8408(lv8, lv12.method_11614());
						}

						if (pair2.getSecond() != null) {
							class_2586 lv6 = arg.method_8321(lv8);
							if (lv6 != null) {
								lv6.method_5431();
							}
						}
					}
				}

				if (!arg3.method_15135()) {
					this.method_15179(arg, arg2, arg3.method_15114(), arg3.method_15113(), arg3.method_15134(), lv);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public static void method_20532(class_1936 arg, int i, class_251 arg2, int j, int k, int l) {
		arg2.method_1046((arg2x, m, n, o) -> {
			class_2338 lv = new class_2338(j + m, k + n, l + o);
			class_2338 lv2 = lv.method_10093(arg2x);
			class_2680 lv3 = arg.method_8320(lv);
			class_2680 lv4 = arg.method_8320(lv2);
			class_2680 lv5 = lv3.method_11578(arg2x, lv4, arg, lv, lv2);
			if (lv3 != lv5) {
				arg.method_8652(lv, lv5, i & -2 | 16);
			}

			class_2680 lv6 = lv4.method_11578(arg2x.method_10153(), lv5, arg, lv2, lv);
			if (lv4 != lv6) {
				arg.method_8652(lv2, lv6, i & -2 | 16);
			}
		});
	}

	public static List<class_3499.class_3501> method_16446(class_1936 arg, class_2338 arg2, class_3492 arg3, List<class_3499.class_3501> list) {
		List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();

		for (class_3499.class_3501 lv : list) {
			class_2338 lv2 = method_15171(arg3, lv.field_15597).method_10081(arg2);
			class_3499.class_3501 lv3 = new class_3499.class_3501(lv2, lv.field_15596, lv.field_15595);
			Iterator<class_3491> iterator = arg3.method_16182().iterator();

			while (lv3 != null && iterator.hasNext()) {
				lv3 = ((class_3491)iterator.next()).method_15110(arg, arg2, lv, lv3, arg3);
			}

			if (lv3 != null) {
				list2.add(lv3);
			}
		}

		return list2;
	}

	private void method_15179(class_1936 arg, class_2338 arg2, class_2415 arg3, class_2470 arg4, class_2338 arg5, @Nullable class_3341 arg6) {
		for (class_3499.class_3502 lv : this.field_15589) {
			class_2338 lv2 = method_15168(lv.field_15600, arg3, arg4, arg5).method_10081(arg2);
			if (arg6 == null || arg6.method_14662(lv2)) {
				class_2487 lv3 = lv.field_15598;
				class_243 lv4 = method_15176(lv.field_15599, arg3, arg4, arg5);
				class_243 lv5 = lv4.method_1031((double)arg2.method_10263(), (double)arg2.method_10264(), (double)arg2.method_10260());
				class_2499 lv6 = new class_2499();
				lv6.add(new class_2489(lv5.field_1352));
				lv6.add(new class_2489(lv5.field_1351));
				lv6.add(new class_2489(lv5.field_1350));
				lv3.method_10566("Pos", lv6);
				lv3.method_10551("UUIDMost");
				lv3.method_10551("UUIDLeast");
				method_17916(arg, lv3).ifPresent(arg5x -> {
					float f = arg5x.method_5763(arg3);
					f += arg5x.field_6031 - arg5x.method_5832(arg4);
					arg5x.method_5808(lv5.field_1352, lv5.field_1351, lv5.field_1350, f, arg5x.field_5965);
					arg.method_8649(arg5x);
				});
			}
		}
	}

	private static Optional<class_1297> method_17916(class_1936 arg, class_2487 arg2) {
		try {
			return class_1299.method_5892(arg2, arg.method_8410());
		} catch (Exception var3) {
			return Optional.empty();
		}
	}

	public class_2338 method_15166(class_2470 arg) {
		switch (arg) {
			case field_11465:
			case field_11463:
				return new class_2338(this.field_15587.method_10260(), this.field_15587.method_10264(), this.field_15587.method_10263());
			default:
				return this.field_15587;
		}
	}

	public static class_2338 method_15168(class_2338 arg, class_2415 arg2, class_2470 arg3, class_2338 arg4) {
		int i = arg.method_10263();
		int j = arg.method_10264();
		int k = arg.method_10260();
		boolean bl = true;
		switch (arg2) {
			case field_11300:
				k = -k;
				break;
			case field_11301:
				i = -i;
				break;
			default:
				bl = false;
		}

		int l = arg4.method_10263();
		int m = arg4.method_10260();
		switch (arg3) {
			case field_11465:
				return new class_2338(l - m + k, j, l + m - i);
			case field_11463:
				return new class_2338(l + m - k, j, m - l + i);
			case field_11464:
				return new class_2338(l + l - i, j, m + m - k);
			default:
				return bl ? new class_2338(i, j, k) : arg;
		}
	}

	private static class_243 method_15176(class_243 arg, class_2415 arg2, class_2470 arg3, class_2338 arg4) {
		double d = arg.field_1352;
		double e = arg.field_1351;
		double f = arg.field_1350;
		boolean bl = true;
		switch (arg2) {
			case field_11300:
				f = 1.0 - f;
				break;
			case field_11301:
				d = 1.0 - d;
				break;
			default:
				bl = false;
		}

		int i = arg4.method_10263();
		int j = arg4.method_10260();
		switch (arg3) {
			case field_11465:
				return new class_243((double)(i - j) + f, e, (double)(i + j + 1) - d);
			case field_11463:
				return new class_243((double)(i + j + 1) - f, e, (double)(j - i) + d);
			case field_11464:
				return new class_243((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
			default:
				return bl ? new class_243(d, e, f) : arg;
		}
	}

	public class_2338 method_15167(class_2338 arg, class_2415 arg2, class_2470 arg3) {
		return method_15162(arg, arg2, arg3, this.method_15160().method_10263(), this.method_15160().method_10260());
	}

	public static class_2338 method_15162(class_2338 arg, class_2415 arg2, class_2470 arg3, int i, int j) {
		i--;
		j--;
		int k = arg2 == class_2415.field_11301 ? i : 0;
		int l = arg2 == class_2415.field_11300 ? j : 0;
		class_2338 lv = arg;
		switch (arg3) {
			case field_11465:
				lv = arg.method_10069(l, 0, i - k);
				break;
			case field_11463:
				lv = arg.method_10069(j - l, 0, k);
				break;
			case field_11464:
				lv = arg.method_10069(i - k, 0, j - l);
				break;
			case field_11467:
				lv = arg.method_10069(k, 0, l);
		}

		return lv;
	}

	public class_3341 method_16187(class_3492 arg, class_2338 arg2) {
		class_2470 lv = arg.method_15113();
		class_2338 lv2 = arg.method_15134();
		class_2338 lv3 = this.method_15166(lv);
		class_2415 lv4 = arg.method_15114();
		int i = lv2.method_10263();
		int j = lv2.method_10260();
		int k = lv3.method_10263() - 1;
		int l = lv3.method_10264() - 1;
		int m = lv3.method_10260() - 1;
		class_3341 lv5 = new class_3341(0, 0, 0, 0, 0, 0);
		switch (lv) {
			case field_11465:
				lv5 = new class_3341(i - j, 0, i + j - m, i - j + k, l, i + j);
				break;
			case field_11463:
				lv5 = new class_3341(i + j - k, 0, j - i, i + j, l, j - i + m);
				break;
			case field_11464:
				lv5 = new class_3341(i + i - k, 0, j + j - m, i + i, l, j + j);
				break;
			case field_11467:
				lv5 = new class_3341(0, 0, 0, k, l, m);
		}

		switch (lv4) {
			case field_11300:
				this.method_16186(lv, m, k, lv5, class_2350.field_11043, class_2350.field_11035);
				break;
			case field_11301:
				this.method_16186(lv, k, m, lv5, class_2350.field_11039, class_2350.field_11034);
			case field_11302:
		}

		lv5.method_14661(arg2.method_10263(), arg2.method_10264(), arg2.method_10260());
		return lv5;
	}

	private void method_16186(class_2470 arg, int i, int j, class_3341 arg2, class_2350 arg3, class_2350 arg4) {
		class_2338 lv = class_2338.field_10980;
		if (arg == class_2470.field_11463 || arg == class_2470.field_11465) {
			lv = lv.method_10079(arg.method_10503(arg3), j);
		} else if (arg == class_2470.field_11464) {
			lv = lv.method_10079(arg4, i);
		} else {
			lv = lv.method_10079(arg3, i);
		}

		arg2.method_14661(lv.method_10263(), 0, lv.method_10260());
	}

	public class_2487 method_15175(class_2487 arg) {
		if (this.field_15586.isEmpty()) {
			arg.method_10566("blocks", new class_2499());
			arg.method_10566("palette", new class_2499());
		} else {
			List<class_3499.class_3500> list = Lists.<class_3499.class_3500>newArrayList();
			class_3499.class_3500 lv = new class_3499.class_3500();
			list.add(lv);

			for (int i = 1; i < this.field_15586.size(); i++) {
				list.add(new class_3499.class_3500());
			}

			class_2499 lv2 = new class_2499();
			List<class_3499.class_3501> list2 = (List<class_3499.class_3501>)this.field_15586.get(0);

			for (int j = 0; j < list2.size(); j++) {
				class_3499.class_3501 lv3 = (class_3499.class_3501)list2.get(j);
				class_2487 lv4 = new class_2487();
				lv4.method_10566("pos", this.method_15169(lv3.field_15597.method_10263(), lv3.field_15597.method_10264(), lv3.field_15597.method_10260()));
				int k = lv.method_15187(lv3.field_15596);
				lv4.method_10569("state", k);
				if (lv3.field_15595 != null) {
					lv4.method_10566("nbt", lv3.field_15595);
				}

				lv2.add(lv4);

				for (int l = 1; l < this.field_15586.size(); l++) {
					class_3499.class_3500 lv5 = (class_3499.class_3500)list.get(l);
					lv5.method_15186(((class_3499.class_3501)((List)this.field_15586.get(l)).get(j)).field_15596, k);
				}
			}

			arg.method_10566("blocks", lv2);
			if (list.size() == 1) {
				class_2499 lv6 = new class_2499();

				for (class_2680 lv7 : lv) {
					lv6.add(class_2512.method_10686(lv7));
				}

				arg.method_10566("palette", lv6);
			} else {
				class_2499 lv6 = new class_2499();

				for (class_3499.class_3500 lv8 : list) {
					class_2499 lv9 = new class_2499();

					for (class_2680 lv10 : lv8) {
						lv9.add(class_2512.method_10686(lv10));
					}

					lv6.add(lv9);
				}

				arg.method_10566("palettes", lv6);
			}
		}

		class_2499 lv11 = new class_2499();

		for (class_3499.class_3502 lv12 : this.field_15589) {
			class_2487 lv13 = new class_2487();
			lv13.method_10566("pos", this.method_15184(lv12.field_15599.field_1352, lv12.field_15599.field_1351, lv12.field_15599.field_1350));
			lv13.method_10566("blockPos", this.method_15169(lv12.field_15600.method_10263(), lv12.field_15600.method_10264(), lv12.field_15600.method_10260()));
			if (lv12.field_15598 != null) {
				lv13.method_10566("nbt", lv12.field_15598);
			}

			lv11.add(lv13);
		}

		arg.method_10566("entities", lv11);
		arg.method_10566("size", this.method_15169(this.field_15587.method_10263(), this.field_15587.method_10264(), this.field_15587.method_10260()));
		arg.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
		return arg;
	}

	public void method_15183(class_2487 arg) {
		this.field_15586.clear();
		this.field_15589.clear();
		class_2499 lv = arg.method_10554("size", 3);
		this.field_15587 = new class_2338(lv.method_10600(0), lv.method_10600(1), lv.method_10600(2));
		class_2499 lv2 = arg.method_10554("blocks", 10);
		if (arg.method_10573("palettes", 9)) {
			class_2499 lv3 = arg.method_10554("palettes", 9);

			for (int i = 0; i < lv3.size(); i++) {
				this.method_15177(lv3.method_10603(i), lv2);
			}
		} else {
			this.method_15177(arg.method_10554("palette", 10), lv2);
		}

		class_2499 lv3 = arg.method_10554("entities", 10);

		for (int i = 0; i < lv3.size(); i++) {
			class_2487 lv4 = lv3.method_10602(i);
			class_2499 lv5 = lv4.method_10554("pos", 6);
			class_243 lv6 = new class_243(lv5.method_10611(0), lv5.method_10611(1), lv5.method_10611(2));
			class_2499 lv7 = lv4.method_10554("blockPos", 3);
			class_2338 lv8 = new class_2338(lv7.method_10600(0), lv7.method_10600(1), lv7.method_10600(2));
			if (lv4.method_10545("nbt")) {
				class_2487 lv9 = lv4.method_10562("nbt");
				this.field_15589.add(new class_3499.class_3502(lv6, lv8, lv9));
			}
		}
	}

	private void method_15177(class_2499 arg, class_2499 arg2) {
		class_3499.class_3500 lv = new class_3499.class_3500();
		List<class_3499.class_3501> list = Lists.<class_3499.class_3501>newArrayList();

		for (int i = 0; i < arg.size(); i++) {
			lv.method_15186(class_2512.method_10681(arg.method_10602(i)), i);
		}

		for (int i = 0; i < arg2.size(); i++) {
			class_2487 lv2 = arg2.method_10602(i);
			class_2499 lv3 = lv2.method_10554("pos", 3);
			class_2338 lv4 = new class_2338(lv3.method_10600(0), lv3.method_10600(1), lv3.method_10600(2));
			class_2680 lv5 = lv.method_15185(lv2.method_10550("state"));
			class_2487 lv6;
			if (lv2.method_10545("nbt")) {
				lv6 = lv2.method_10562("nbt");
			} else {
				lv6 = null;
			}

			list.add(new class_3499.class_3501(lv4, lv5, lv6));
		}

		list.sort(Comparator.comparingInt(argx -> argx.field_15597.method_10264()));
		this.field_15586.add(list);
	}

	private class_2499 method_15169(int... is) {
		class_2499 lv = new class_2499();

		for (int i : is) {
			lv.add(new class_2497(i));
		}

		return lv;
	}

	private class_2499 method_15184(double... ds) {
		class_2499 lv = new class_2499();

		for (double d : ds) {
			lv.add(new class_2489(d));
		}

		return lv;
	}

	static class class_3500 implements Iterable<class_2680> {
		public static final class_2680 field_15590 = class_2246.field_10124.method_9564();
		private final class_2361<class_2680> field_15591 = new class_2361<>(16);
		private int field_15592;

		private class_3500() {
		}

		public int method_15187(class_2680 arg) {
			int i = this.field_15591.method_10206(arg);
			if (i == -1) {
				i = this.field_15592++;
				this.field_15591.method_10203(arg, i);
			}

			return i;
		}

		@Nullable
		public class_2680 method_15185(int i) {
			class_2680 lv = this.field_15591.method_10200(i);
			return lv == null ? field_15590 : lv;
		}

		public Iterator<class_2680> iterator() {
			return this.field_15591.iterator();
		}

		public void method_15186(class_2680 arg, int i) {
			this.field_15591.method_10203(arg, i);
		}
	}

	public static class class_3501 {
		public final class_2338 field_15597;
		public final class_2680 field_15596;
		public final class_2487 field_15595;

		public class_3501(class_2338 arg, class_2680 arg2, @Nullable class_2487 arg3) {
			this.field_15597 = arg;
			this.field_15596 = arg2;
			this.field_15595 = arg3;
		}

		public String toString() {
			return String.format("<StructureBlockInfo | %s | %s | %s>", this.field_15597, this.field_15596, this.field_15595);
		}
	}

	public static class class_3502 {
		public final class_243 field_15599;
		public final class_2338 field_15600;
		public final class_2487 field_15598;

		public class_3502(class_243 arg, class_2338 arg2, class_2487 arg3) {
			this.field_15599 = arg;
			this.field_15600 = arg2;
			this.field_15598 = arg3;
		}
	}
}
