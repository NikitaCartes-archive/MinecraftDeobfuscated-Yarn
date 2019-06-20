package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2843 {
	private static final Logger field_12956 = LogManager.getLogger();
	public static final class_2843 field_12950 = new class_2843();
	private static final class_2355[] field_12952 = class_2355.values();
	private final EnumSet<class_2355> field_12951 = EnumSet.noneOf(class_2355.class);
	private final int[][] field_12955 = new int[16][];
	private static final Map<class_2248, class_2843.class_2844> field_12953 = new IdentityHashMap();
	private static final Set<class_2843.class_2844> field_12954 = Sets.<class_2843.class_2844>newHashSet();

	private class_2843() {
	}

	public class_2843(class_2487 arg) {
		this();
		if (arg.method_10573("Indices", 10)) {
			class_2487 lv = arg.method_10562("Indices");

			for (int i = 0; i < this.field_12955.length; i++) {
				String string = String.valueOf(i);
				if (lv.method_10573(string, 11)) {
					this.field_12955[i] = lv.method_10561(string);
				}
			}
		}

		int j = arg.method_10550("Sides");

		for (class_2355 lv2 : class_2355.values()) {
			if ((j & 1 << lv2.ordinal()) != 0) {
				this.field_12951.add(lv2);
			}
		}
	}

	public void method_12356(class_2818 arg) {
		this.method_12348(arg);

		for (class_2355 lv : field_12952) {
			method_12352(arg, lv);
		}

		class_1937 lv2 = arg.method_12200();
		field_12954.forEach(arg2 -> arg2.method_12357(lv2));
	}

	private static void method_12352(class_2818 arg, class_2355 arg2) {
		class_1937 lv = arg.method_12200();
		if (arg.method_12003().field_12951.remove(arg2)) {
			Set<class_2350> set = arg2.method_10186();
			int i = 0;
			int j = 15;
			boolean bl = set.contains(class_2350.field_11034);
			boolean bl2 = set.contains(class_2350.field_11039);
			boolean bl3 = set.contains(class_2350.field_11035);
			boolean bl4 = set.contains(class_2350.field_11043);
			boolean bl5 = set.size() == 1;
			class_1923 lv2 = arg.method_12004();
			int k = lv2.method_8326() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 1);
			int l = lv2.method_8326() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 14);
			int m = lv2.method_8328() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 1);
			int n = lv2.method_8328() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 14);
			class_2350[] lvs = class_2350.values();
			class_2338.class_2339 lv3 = new class_2338.class_2339();

			for (class_2338 lv4 : class_2338.method_10094(k, 0, m, l, lv.method_8322() - 1, n)) {
				class_2680 lv5 = lv.method_8320(lv4);
				class_2680 lv6 = lv5;

				for (class_2350 lv7 : lvs) {
					lv3.method_10101(lv4).method_10098(lv7);
					lv6 = method_12351(lv6, lv7, lv, lv4, lv3);
				}

				class_2248.method_9611(lv5, lv6, lv, lv4, 18);
			}
		}
	}

	private static class_2680 method_12351(class_2680 arg, class_2350 arg2, class_1936 arg3, class_2338 arg4, class_2338 arg5) {
		return ((class_2843.class_2844)field_12953.getOrDefault(arg.method_11614(), class_2843.class_2845.field_12962))
			.method_12358(arg, arg2, arg3.method_8320(arg5), arg3, arg4, arg5);
	}

	private void method_12348(class_2818 arg) {
		try (
			class_2338.class_2340 lv = class_2338.class_2340.method_10109();
			class_2338.class_2340 lv2 = class_2338.class_2340.method_10109();
		) {
			class_1923 lv3 = arg.method_12004();
			class_1936 lv4 = arg.method_12200();

			for (int i = 0; i < 16; i++) {
				class_2826 lv5 = arg.method_12006()[i];
				int[] is = this.field_12955[i];
				this.field_12955[i] = null;
				if (lv5 != null && is != null && is.length > 0) {
					class_2350[] lvs = class_2350.values();
					class_2841<class_2680> lv6 = lv5.method_12265();

					for (int j : is) {
						int k = j & 15;
						int l = j >> 8 & 15;
						int m = j >> 4 & 15;
						lv.method_10113(lv3.method_8326() + k, (i << 4) + l, lv3.method_8328() + m);
						class_2680 lv7 = lv6.method_12331(j);
						class_2680 lv8 = lv7;

						for (class_2350 lv9 : lvs) {
							lv2.method_10114(lv).method_10118(lv9);
							if (lv.method_10263() >> 4 == lv3.field_9181 && lv.method_10260() >> 4 == lv3.field_9180) {
								lv8 = method_12351(lv8, lv9, lv4, lv, lv2);
							}
						}

						class_2248.method_9611(lv7, lv8, lv4, lv, 18);
					}
				}
			}

			for (int ix = 0; ix < this.field_12955.length; ix++) {
				if (this.field_12955[ix] != null) {
					field_12956.warn("Discarding update data for section {} for chunk ({} {})", ix, lv3.field_9181, lv3.field_9180);
				}

				this.field_12955[ix] = null;
			}
		}
	}

	public boolean method_12349() {
		for (int[] is : this.field_12955) {
			if (is != null) {
				return false;
			}
		}

		return this.field_12951.isEmpty();
	}

	public class_2487 method_12350() {
		class_2487 lv = new class_2487();
		class_2487 lv2 = new class_2487();

		for (int i = 0; i < this.field_12955.length; i++) {
			String string = String.valueOf(i);
			if (this.field_12955[i] != null && this.field_12955[i].length != 0) {
				lv2.method_10539(string, this.field_12955[i]);
			}
		}

		if (!lv2.isEmpty()) {
			lv.method_10566("Indices", lv2);
		}

		int ix = 0;

		for (class_2355 lv3 : this.field_12951) {
			ix |= 1 << lv3.ordinal();
		}

		lv.method_10567("Sides", (byte)ix);
		return lv;
	}

	public interface class_2844 {
		class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6);

		default void method_12357(class_1936 arg) {
		}
	}

	static enum class_2845 implements class_2843.class_2844 {
		field_12957(
			class_2246.field_10282,
			class_2246.field_10316,
			class_2246.field_10197,
			class_2246.field_10022,
			class_2246.field_10300,
			class_2246.field_10321,
			class_2246.field_10145,
			class_2246.field_10133,
			class_2246.field_10522,
			class_2246.field_10353,
			class_2246.field_10628,
			class_2246.field_10233,
			class_2246.field_10404,
			class_2246.field_10456,
			class_2246.field_10023,
			class_2246.field_10529,
			class_2246.field_10287,
			class_2246.field_10506,
			class_2246.field_10535,
			class_2246.field_10105,
			class_2246.field_10414,
			class_2246.field_10081,
			class_2246.field_10255,
			class_2246.field_10102,
			class_2246.field_10534,
			class_2246.field_10121,
			class_2246.field_10411,
			class_2246.field_10231,
			class_2246.field_10284,
			class_2246.field_10544,
			class_2246.field_10330,
			class_2246.field_10187,
			class_2246.field_10088,
			class_2246.field_10391,
			class_2246.field_10401,
			class_2246.field_10587,
			class_2246.field_10265
		) {
			@Override
			public class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
				return arg;
			}
		},
		field_12962 {
			@Override
			public class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
				return arg.method_11578(arg2, arg4.method_8320(arg6), arg4, arg5, arg6);
			}
		},
		field_12960(class_2246.field_10034, class_2246.field_10380) {
			@Override
			public class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
				if (arg3.method_11614() == arg.method_11614()
					&& arg2.method_10166().method_10179()
					&& arg.method_11654(class_2281.field_10770) == class_2745.field_12569
					&& arg3.method_11654(class_2281.field_10770) == class_2745.field_12569) {
					class_2350 lv = arg.method_11654(class_2281.field_10768);
					if (arg2.method_10166() != lv.method_10166() && lv == arg3.method_11654(class_2281.field_10768)) {
						class_2745 lv2 = arg2 == lv.method_10170() ? class_2745.field_12574 : class_2745.field_12571;
						arg4.method_8652(arg6, arg3.method_11657(class_2281.field_10770, lv2.method_11824()), 18);
						if (lv == class_2350.field_11043 || lv == class_2350.field_11034) {
							class_2586 lv3 = arg4.method_8321(arg5);
							class_2586 lv4 = arg4.method_8321(arg6);
							if (lv3 instanceof class_2595 && lv4 instanceof class_2595) {
								class_2595.method_11047((class_2595)lv3, (class_2595)lv4);
							}
						}

						return arg.method_11657(class_2281.field_10770, lv2);
					}
				}

				return arg;
			}
		},
		field_12963(
			true, class_2246.field_10098, class_2246.field_10539, class_2246.field_10035, class_2246.field_10335, class_2246.field_10503, class_2246.field_9988
		) {
			private final ThreadLocal<List<ObjectSet<class_2338>>> field_12964 = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

			@Override
			public class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
				class_2680 lv = arg.method_11578(arg2, arg4.method_8320(arg6), arg4, arg5, arg6);
				if (arg != lv) {
					int i = (Integer)lv.method_11654(class_2741.field_12541);
					List<ObjectSet<class_2338>> list = (List<ObjectSet<class_2338>>)this.field_12964.get();
					if (list.isEmpty()) {
						for (int j = 0; j < 7; j++) {
							list.add(new ObjectOpenHashSet());
						}
					}

					((ObjectSet)list.get(i)).add(arg5.method_10062());
				}

				return arg;
			}

			@Override
			public void method_12357(class_1936 arg) {
				class_2338.class_2339 lv = new class_2338.class_2339();
				List<ObjectSet<class_2338>> list = (List<ObjectSet<class_2338>>)this.field_12964.get();

				for (int i = 2; i < list.size(); i++) {
					int j = i - 1;
					ObjectSet<class_2338> objectSet = (ObjectSet<class_2338>)list.get(j);
					ObjectSet<class_2338> objectSet2 = (ObjectSet<class_2338>)list.get(i);

					for (class_2338 lv2 : objectSet) {
						class_2680 lv3 = arg.method_8320(lv2);
						if ((Integer)lv3.method_11654(class_2741.field_12541) >= j) {
							arg.method_8652(lv2, lv3.method_11657(class_2741.field_12541, Integer.valueOf(j)), 18);
							if (i != 7) {
								for (class_2350 lv4 : field_12959) {
									lv.method_10101(lv2).method_10098(lv4);
									class_2680 lv5 = arg.method_8320(lv);
									if (lv5.method_11570(class_2741.field_12541) && (Integer)lv3.method_11654(class_2741.field_12541) > i) {
										objectSet2.add(lv.method_10062());
									}
								}
							}
						}
					}
				}

				list.clear();
			}
		},
		field_12958(class_2246.field_10168, class_2246.field_9984) {
			@Override
			public class_2680 method_12358(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
				if ((Integer)arg.method_11654(class_2513.field_11584) == 7) {
					class_2511 lv = ((class_2513)arg.method_11614()).method_10694();
					if (arg3.method_11614() == lv) {
						return lv.method_10680().method_9564().method_11657(class_2383.field_11177, arg2);
					}
				}

				return arg;
			}
		};

		public static final class_2350[] field_12959 = class_2350.values();

		private class_2845(class_2248... args) {
			this(false, args);
		}

		private class_2845(boolean bl, class_2248... args) {
			for (class_2248 lv : args) {
				class_2843.field_12953.put(lv, this);
			}

			if (bl) {
				class_2843.field_12954.add(this);
			}
		}
	}
}
