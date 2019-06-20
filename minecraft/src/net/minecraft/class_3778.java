package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3778 {
	private static final Logger field_16665 = LogManager.getLogger();
	public static final class_3787 field_16666 = new class_3787();

	public static void method_16605(
		class_2960 arg, int i, class_3778.class_3779 arg2, class_2794<?> arg3, class_3485 arg4, class_2338 arg5, List<class_3443> list, Random random
	) {
		class_3420.method_16651();
		new class_3778.class_4182(arg, i, arg2, arg3, arg4, arg5, list, random);
	}

	static {
		field_16666.method_16640(class_3785.field_16679);
	}

	public interface class_3779 {
		class_3790 create(class_3485 arg, class_3784 arg2, class_2338 arg3, int i, class_2470 arg4, class_3341 arg5);
	}

	static final class class_4181 {
		private final class_3790 field_18696;
		private final AtomicReference<class_265> field_18697;
		private final int field_18698;
		private final int field_18699;

		private class_4181(class_3790 arg, AtomicReference<class_265> atomicReference, int i, int j) {
			this.field_18696 = arg;
			this.field_18697 = atomicReference;
			this.field_18698 = i;
			this.field_18699 = j;
		}
	}

	static final class class_4182 {
		private final int field_18700;
		private final class_3778.class_3779 field_18701;
		private final class_2794<?> field_18702;
		private final class_3485 field_18703;
		private final List<class_3443> field_18704;
		private final Random field_18705;
		private final Deque<class_3778.class_4181> field_18706 = Queues.<class_3778.class_4181>newArrayDeque();

		public class_4182(
			class_2960 arg, int i, class_3778.class_3779 arg2, class_2794<?> arg3, class_3485 arg4, class_2338 arg5, List<class_3443> list, Random random
		) {
			this.field_18700 = i;
			this.field_18701 = arg2;
			this.field_18702 = arg3;
			this.field_18703 = arg4;
			this.field_18704 = list;
			this.field_18705 = random;
			class_2470 lv = class_2470.method_16548(random);
			class_3785 lv2 = class_3778.field_16666.method_16639(arg);
			class_3784 lv3 = lv2.method_16631(random);
			class_3790 lv4 = arg2.create(arg4, lv3, arg5, lv3.method_19308(), lv, lv3.method_16628(arg4, arg5, lv));
			class_3341 lv5 = lv4.method_14935();
			int j = (lv5.field_14378 + lv5.field_14381) / 2;
			int k = (lv5.field_14376 + lv5.field_14379) / 2;
			int l = arg3.method_20402(j, k, class_2902.class_2903.field_13194);
			lv4.method_14922(0, l - (lv5.field_14380 + lv4.method_16646()), 0);
			list.add(lv4);
			if (i > 0) {
				int m = 80;
				class_238 lv6 = new class_238((double)(j - 80), (double)(l - 80), (double)(k - 80), (double)(j + 80 + 1), (double)(l + 80 + 1), (double)(k + 80 + 1));
				this.field_18706
					.addLast(
						new class_3778.class_4181(
							lv4,
							new AtomicReference(class_259.method_1072(class_259.method_1078(lv6), class_259.method_1078(class_238.method_19316(lv5)), class_247.field_16886)),
							l + 80,
							0
						)
					);

				while (!this.field_18706.isEmpty()) {
					class_3778.class_4181 lv7 = (class_3778.class_4181)this.field_18706.removeFirst();
					this.method_19306(lv7.field_18696, lv7.field_18697, lv7.field_18698, lv7.field_18699);
				}
			}
		}

		private void method_19306(class_3790 arg, AtomicReference<class_265> atomicReference, int i, int j) {
			class_3784 lv = arg.method_16644();
			class_2338 lv2 = arg.method_16648();
			class_2470 lv3 = arg.method_16888();
			class_3785.class_3786 lv4 = lv.method_16624();
			boolean bl = lv4 == class_3785.class_3786.field_16687;
			AtomicReference<class_265> atomicReference2 = new AtomicReference();
			class_3341 lv5 = arg.method_14935();
			int k = lv5.field_14380;

			label121:
			for (class_3499.class_3501 lv6 : lv.method_16627(this.field_18703, lv2, lv3, this.field_18705)) {
				class_2350 lv7 = lv6.field_15596.method_11654(class_3748.field_10927);
				class_2338 lv8 = lv6.field_15597;
				class_2338 lv9 = lv8.method_10093(lv7);
				int l = lv8.method_10264() - k;
				int m = -1;
				class_3785 lv10 = class_3778.field_16666.method_16639(new class_2960(lv6.field_15595.method_10558("target_pool")));
				class_3785 lv11 = class_3778.field_16666.method_16639(lv10.method_16634());
				if (lv10 != class_3785.field_16746 && (lv10.method_16632() != 0 || lv10 == class_3785.field_16679)) {
					boolean bl2 = lv5.method_14662(lv9);
					AtomicReference<class_265> atomicReference3;
					int n;
					if (bl2) {
						atomicReference3 = atomicReference2;
						n = k;
						if (atomicReference2.get() == null) {
							atomicReference2.set(class_259.method_1078(class_238.method_19316(lv5)));
						}
					} else {
						atomicReference3 = atomicReference;
						n = i;
					}

					List<class_3784> list = Lists.<class_3784>newArrayList();
					if (j != this.field_18700) {
						list.addAll(lv10.method_16633(this.field_18705));
					}

					list.addAll(lv11.method_16633(this.field_18705));

					for (class_3784 lv12 : list) {
						if (lv12 == class_3777.field_16663) {
							break;
						}

						for (class_2470 lv13 : class_2470.method_16547(this.field_18705)) {
							List<class_3499.class_3501> list2 = lv12.method_16627(this.field_18703, class_2338.field_10980, lv13, this.field_18705);
							class_3341 lv14 = lv12.method_16628(this.field_18703, class_2338.field_10980, lv13);
							int o;
							if (lv14.method_14663() > 16) {
								o = 0;
							} else {
								o = list2.stream().mapToInt(arg2 -> {
									if (!lv14.method_14662(arg2.field_15597.method_10093(arg2.field_15596.method_11654(class_3748.field_10927)))) {
										return 0;
									} else {
										class_2960 lvx = new class_2960(arg2.field_15595.method_10558("target_pool"));
										class_3785 lv2x = class_3778.field_16666.method_16639(lvx);
										class_3785 lv3x = class_3778.field_16666.method_16639(lv2x.method_16634());
										return Math.max(lv2x.method_19309(this.field_18703), lv3x.method_19309(this.field_18703));
									}
								}).max().orElse(0);
							}

							for (class_3499.class_3501 lv15 : list2) {
								if (class_3748.method_16546(lv6, lv15)) {
									class_2338 lv16 = lv15.field_15597;
									class_2338 lv17 = new class_2338(
										lv9.method_10263() - lv16.method_10263(), lv9.method_10264() - lv16.method_10264(), lv9.method_10260() - lv16.method_10260()
									);
									class_3341 lv18 = lv12.method_16628(this.field_18703, lv17, lv13);
									int p = lv18.field_14380;
									class_3785.class_3786 lv19 = lv12.method_16624();
									boolean bl3 = lv19 == class_3785.class_3786.field_16687;
									int q = lv16.method_10264();
									int r = l - q + ((class_2350)lv6.field_15596.method_11654(class_3748.field_10927)).method_10164();
									int s;
									if (bl && bl3) {
										s = k + r;
									} else {
										if (m == -1) {
											m = this.field_18702.method_20402(lv8.method_10263(), lv8.method_10260(), class_2902.class_2903.field_13194);
										}

										s = m - q;
									}

									int t = s - p;
									class_3341 lv20 = lv18.method_19311(0, t, 0);
									class_2338 lv21 = lv17.method_10069(0, t, 0);
									if (o > 0) {
										int u = Math.max(o + 1, lv20.field_14377 - lv20.field_14380);
										lv20.field_14377 = lv20.field_14380 + u;
									}

									if (!class_259.method_1074(
										(class_265)atomicReference3.get(), class_259.method_1078(class_238.method_19316(lv20).method_1011(0.25)), class_247.field_16893
									)) {
										atomicReference3.set(
											class_259.method_1082((class_265)atomicReference3.get(), class_259.method_1078(class_238.method_19316(lv20)), class_247.field_16886)
										);
										int u = arg.method_16646();
										int v;
										if (bl3) {
											v = u - r;
										} else {
											v = lv12.method_19308();
										}

										class_3790 lv22 = this.field_18701.create(this.field_18703, lv12, lv21, v, lv13, lv20);
										int w;
										if (bl) {
											w = k + l;
										} else if (bl3) {
											w = s + q;
										} else {
											if (m == -1) {
												m = this.field_18702.method_20402(lv8.method_10263(), lv8.method_10260(), class_2902.class_2903.field_13194);
											}

											w = m + r / 2;
										}

										arg.method_16647(new class_3780(lv9.method_10263(), w - l + u, lv9.method_10260(), r, lv19));
										lv22.method_16647(new class_3780(lv8.method_10263(), w - q + v, lv8.method_10260(), -r, lv4));
										this.field_18704.add(lv22);
										if (j + 1 <= this.field_18700) {
											this.field_18706.addLast(new class_3778.class_4181(lv22, atomicReference3, n, j + 1));
										}
										continue label121;
									}
								}
							}
						}
					}
				} else {
					class_3778.field_16665.warn("Empty or none existent pool: {}", lv6.field_15595.method_10558("target_pool"));
				}
			}
		}
	}
}
