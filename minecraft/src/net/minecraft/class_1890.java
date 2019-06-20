package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class class_1890 {
	public static int method_8225(class_1887 arg, class_1799 arg2) {
		if (arg2.method_7960()) {
			return 0;
		} else {
			class_2960 lv = class_2378.field_11160.method_10221(arg);
			class_2499 lv2 = arg2.method_7921();

			for (int i = 0; i < lv2.size(); i++) {
				class_2487 lv3 = lv2.method_10602(i);
				class_2960 lv4 = class_2960.method_12829(lv3.method_10558("id"));
				if (lv4 != null && lv4.equals(lv)) {
					return lv3.method_10550("lvl");
				}
			}

			return 0;
		}
	}

	public static Map<class_1887, Integer> method_8222(class_1799 arg) {
		Map<class_1887, Integer> map = Maps.<class_1887, Integer>newLinkedHashMap();
		class_2499 lv = arg.method_7909() == class_1802.field_8598 ? class_1772.method_7806(arg) : arg.method_7921();

		for (int i = 0; i < lv.size(); i++) {
			class_2487 lv2 = lv.method_10602(i);
			class_2378.field_11160.method_17966(class_2960.method_12829(lv2.method_10558("id"))).ifPresent(arg2 -> {
				Integer var10000 = (Integer)map.put(arg2, lv2.method_10550("lvl"));
			});
		}

		return map;
	}

	public static void method_8214(Map<class_1887, Integer> map, class_1799 arg) {
		class_2499 lv = new class_2499();

		for (Entry<class_1887, Integer> entry : map.entrySet()) {
			class_1887 lv2 = (class_1887)entry.getKey();
			if (lv2 != null) {
				int i = (Integer)entry.getValue();
				class_2487 lv3 = new class_2487();
				lv3.method_10582("id", String.valueOf(class_2378.field_11160.method_10221(lv2)));
				lv3.method_10575("lvl", (short)i);
				lv.add(lv3);
				if (arg.method_7909() == class_1802.field_8598) {
					class_1772.method_7807(arg, new class_1889(lv2, i));
				}
			}
		}

		if (lv.isEmpty()) {
			arg.method_7983("Enchantments");
		} else if (arg.method_7909() != class_1802.field_8598) {
			arg.method_7959("Enchantments", lv);
		}
	}

	private static void method_8220(class_1890.class_1891 arg, class_1799 arg2) {
		if (!arg2.method_7960()) {
			class_2499 lv = arg2.method_7921();

			for (int i = 0; i < lv.size(); i++) {
				String string = lv.method_10602(i).method_10558("id");
				int j = lv.method_10602(i).method_10550("lvl");
				class_2378.field_11160.method_17966(class_2960.method_12829(string)).ifPresent(arg2x -> arg.accept(arg2x, j));
			}
		}
	}

	private static void method_8209(class_1890.class_1891 arg, Iterable<class_1799> iterable) {
		for (class_1799 lv : iterable) {
			method_8220(arg, lv);
		}
	}

	public static int method_8219(Iterable<class_1799> iterable, class_1282 arg) {
		MutableInt mutableInt = new MutableInt();
		method_8209((arg2, i) -> mutableInt.add(arg2.method_8181(i, arg)), iterable);
		return mutableInt.intValue();
	}

	public static float method_8218(class_1799 arg, class_1310 arg2) {
		MutableFloat mutableFloat = new MutableFloat();
		method_8220((arg2x, i) -> mutableFloat.add(arg2x.method_8196(i, arg2)), arg);
		return mutableFloat.floatValue();
	}

	public static float method_8217(class_1309 arg) {
		int i = method_8203(class_1893.field_9115, arg);
		return i > 0 ? class_1903.method_8241(i) : 0.0F;
	}

	public static void method_8210(class_1309 arg, class_1297 arg2) {
		class_1890.class_1891 lv = (arg3, i) -> arg3.method_8178(arg, arg2, i);
		if (arg != null) {
			method_8209(lv, arg.method_5743());
		}

		if (arg2 instanceof class_1657) {
			method_8220(lv, arg.method_6047());
		}
	}

	public static void method_8213(class_1309 arg, class_1297 arg2) {
		class_1890.class_1891 lv = (arg3, i) -> arg3.method_8189(arg, arg2, i);
		if (arg != null) {
			method_8209(lv, arg.method_5743());
		}

		if (arg instanceof class_1657) {
			method_8220(lv, arg.method_6047());
		}
	}

	public static int method_8203(class_1887 arg, class_1309 arg2) {
		Iterable<class_1799> iterable = arg.method_8185(arg2).values();
		if (iterable == null) {
			return 0;
		} else {
			int i = 0;

			for (class_1799 lv : iterable) {
				int j = method_8225(arg, lv);
				if (j > i) {
					i = j;
				}
			}

			return i;
		}
	}

	public static int method_8205(class_1309 arg) {
		return method_8203(class_1893.field_9121, arg);
	}

	public static int method_8199(class_1309 arg) {
		return method_8203(class_1893.field_9124, arg);
	}

	public static int method_8211(class_1309 arg) {
		return method_8203(class_1893.field_9127, arg);
	}

	public static int method_8232(class_1309 arg) {
		return method_8203(class_1893.field_9128, arg);
	}

	public static int method_8234(class_1309 arg) {
		return method_8203(class_1893.field_9131, arg);
	}

	public static int method_8223(class_1799 arg) {
		return method_8225(class_1893.field_9114, arg);
	}

	public static int method_8215(class_1799 arg) {
		return method_8225(class_1893.field_9100, arg);
	}

	public static int method_8226(class_1309 arg) {
		return method_8203(class_1893.field_9110, arg);
	}

	public static boolean method_8200(class_1309 arg) {
		return method_8203(class_1893.field_9105, arg) > 0;
	}

	public static boolean method_8216(class_1309 arg) {
		return method_8203(class_1893.field_9122, arg) > 0;
	}

	public static boolean method_8224(class_1799 arg) {
		return method_8225(class_1893.field_9113, arg) > 0;
	}

	public static boolean method_8221(class_1799 arg) {
		return method_8225(class_1893.field_9109, arg) > 0;
	}

	public static int method_8206(class_1799 arg) {
		return method_8225(class_1893.field_9120, arg);
	}

	public static int method_8202(class_1799 arg) {
		return method_8225(class_1893.field_9104, arg);
	}

	public static boolean method_8228(class_1799 arg) {
		return method_8225(class_1893.field_9117, arg) > 0;
	}

	@Nullable
	public static Entry<class_1304, class_1799> method_8204(class_1887 arg, class_1309 arg2) {
		Map<class_1304, class_1799> map = arg.method_8185(arg2);
		if (map.isEmpty()) {
			return null;
		} else {
			List<Entry<class_1304, class_1799>> list = Lists.<Entry<class_1304, class_1799>>newArrayList();

			for (Entry<class_1304, class_1799> entry : map.entrySet()) {
				class_1799 lv = (class_1799)entry.getValue();
				if (!lv.method_7960() && method_8225(arg, lv) > 0) {
					list.add(entry);
				}
			}

			return list.isEmpty() ? null : (Entry)list.get(arg2.method_6051().nextInt(list.size()));
		}
	}

	public static int method_8227(Random random, int i, int j, class_1799 arg) {
		class_1792 lv = arg.method_7909();
		int k = lv.method_7837();
		if (k <= 0) {
			return 0;
		} else {
			if (j > 15) {
				j = 15;
			}

			int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);
			if (i == 0) {
				return Math.max(l / 3, 1);
			} else {
				return i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2);
			}
		}
	}

	public static class_1799 method_8233(Random random, class_1799 arg, int i, boolean bl) {
		List<class_1889> list = method_8230(random, arg, i, bl);
		boolean bl2 = arg.method_7909() == class_1802.field_8529;
		if (bl2) {
			arg = new class_1799(class_1802.field_8598);
		}

		for (class_1889 lv : list) {
			if (bl2) {
				class_1772.method_7807(arg, lv);
			} else {
				arg.method_7978(lv.field_9093, lv.field_9094);
			}
		}

		return arg;
	}

	public static List<class_1889> method_8230(Random random, class_1799 arg, int i, boolean bl) {
		List<class_1889> list = Lists.<class_1889>newArrayList();
		class_1792 lv = arg.method_7909();
		int j = lv.method_7837();
		if (j <= 0) {
			return list;
		} else {
			i += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			i = class_3532.method_15340(Math.round((float)i + (float)i * f), 1, Integer.MAX_VALUE);
			List<class_1889> list2 = method_8229(i, arg, bl);
			if (!list2.isEmpty()) {
				list.add(class_3549.method_15446(random, list2));

				while (random.nextInt(50) <= i) {
					method_8231(list2, class_156.method_20793(list));
					if (list2.isEmpty()) {
						break;
					}

					list.add(class_3549.method_15446(random, list2));
					i /= 2;
				}
			}

			return list;
		}
	}

	public static void method_8231(List<class_1889> list, class_1889 arg) {
		Iterator<class_1889> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (!arg.field_9093.method_8188(((class_1889)iterator.next()).field_9093)) {
				iterator.remove();
			}
		}
	}

	public static boolean method_8201(Collection<class_1887> collection, class_1887 arg) {
		for (class_1887 lv : collection) {
			if (!lv.method_8188(arg)) {
				return false;
			}
		}

		return true;
	}

	public static List<class_1889> method_8229(int i, class_1799 arg, boolean bl) {
		List<class_1889> list = Lists.<class_1889>newArrayList();
		class_1792 lv = arg.method_7909();
		boolean bl2 = arg.method_7909() == class_1802.field_8529;

		for (class_1887 lv2 : class_2378.field_11160) {
			if ((!lv2.method_8193() || bl) && (lv2.field_9083.method_8177(lv) || bl2)) {
				for (int j = lv2.method_8183(); j > lv2.method_8187() - 1; j--) {
					if (i >= lv2.method_8182(j) && i <= lv2.method_20742(j)) {
						list.add(new class_1889(lv2, j));
						break;
					}
				}
			}
		}

		return list;
	}

	@FunctionalInterface
	interface class_1891 {
		void accept(class_1887 arg, int i);
	}
}
