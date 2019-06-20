package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1844 {
	public static List<class_1293> method_8067(class_1799 arg) {
		return method_8066(arg.method_7969());
	}

	public static List<class_1293> method_8059(class_1842 arg, Collection<class_1293> collection) {
		List<class_1293> list = Lists.<class_1293>newArrayList();
		list.addAll(arg.method_8049());
		list.addAll(collection);
		return list;
	}

	public static List<class_1293> method_8066(@Nullable class_2487 arg) {
		List<class_1293> list = Lists.<class_1293>newArrayList();
		list.addAll(method_8057(arg).method_8049());
		method_8058(arg, list);
		return list;
	}

	public static List<class_1293> method_8068(class_1799 arg) {
		return method_8060(arg.method_7969());
	}

	public static List<class_1293> method_8060(@Nullable class_2487 arg) {
		List<class_1293> list = Lists.<class_1293>newArrayList();
		method_8058(arg, list);
		return list;
	}

	public static void method_8058(@Nullable class_2487 arg, List<class_1293> list) {
		if (arg != null && arg.method_10573("CustomPotionEffects", 9)) {
			class_2499 lv = arg.method_10554("CustomPotionEffects", 10);

			for (int i = 0; i < lv.size(); i++) {
				class_2487 lv2 = lv.method_10602(i);
				class_1293 lv3 = class_1293.method_5583(lv2);
				if (lv3 != null) {
					list.add(lv3);
				}
			}
		}
	}

	public static int method_8064(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		if (lv != null && lv.method_10573("CustomPotionColor", 99)) {
			return lv.method_10550("CustomPotionColor");
		} else {
			return method_8063(arg) == class_1847.field_8984 ? 16253176 : method_8055(method_8067(arg));
		}
	}

	public static int method_8062(class_1842 arg) {
		return arg == class_1847.field_8984 ? 16253176 : method_8055(arg.method_8049());
	}

	public static int method_8055(Collection<class_1293> collection) {
		int i = 3694022;
		if (collection.isEmpty()) {
			return 3694022;
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int j = 0;

			for (class_1293 lv : collection) {
				if (lv.method_5581()) {
					int k = lv.method_5579().method_5556();
					int l = lv.method_5578() + 1;
					f += (float)(l * (k >> 16 & 0xFF)) / 255.0F;
					g += (float)(l * (k >> 8 & 0xFF)) / 255.0F;
					h += (float)(l * (k >> 0 & 0xFF)) / 255.0F;
					j += l;
				}
			}

			if (j == 0) {
				return 0;
			} else {
				f = f / (float)j * 255.0F;
				g = g / (float)j * 255.0F;
				h = h / (float)j * 255.0F;
				return (int)f << 16 | (int)g << 8 | (int)h;
			}
		}
	}

	public static class_1842 method_8063(class_1799 arg) {
		return method_8057(arg.method_7969());
	}

	public static class_1842 method_8057(@Nullable class_2487 arg) {
		return arg == null ? class_1847.field_8984 : class_1842.method_8048(arg.method_10558("Potion"));
	}

	public static class_1799 method_8061(class_1799 arg, class_1842 arg2) {
		class_2960 lv = class_2378.field_11143.method_10221(arg2);
		if (arg2 == class_1847.field_8984) {
			arg.method_7983("Potion");
		} else {
			arg.method_7948().method_10582("Potion", lv.toString());
		}

		return arg;
	}

	public static class_1799 method_8056(class_1799 arg, Collection<class_1293> collection) {
		if (collection.isEmpty()) {
			return arg;
		} else {
			class_2487 lv = arg.method_7948();
			class_2499 lv2 = lv.method_10554("CustomPotionEffects", 9);

			for (class_1293 lv3 : collection) {
				lv2.add(lv3.method_5582(new class_2487()));
			}

			lv.method_10566("CustomPotionEffects", lv2);
			return arg;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_8065(class_1799 arg, List<class_2561> list, float f) {
		List<class_1293> list2 = method_8067(arg);
		List<class_3545<String, class_1322>> list3 = Lists.<class_3545<String, class_1322>>newArrayList();
		if (list2.isEmpty()) {
			list.add(new class_2588("effect.none").method_10854(class_124.field_1080));
		} else {
			for (class_1293 lv : list2) {
				class_2561 lv2 = new class_2588(lv.method_5586());
				class_1291 lv3 = lv.method_5579();
				Map<class_1320, class_1322> map = lv3.method_5565();
				if (!map.isEmpty()) {
					for (Entry<class_1320, class_1322> entry : map.entrySet()) {
						class_1322 lv4 = (class_1322)entry.getValue();
						class_1322 lv5 = new class_1322(lv4.method_6185(), lv3.method_5563(lv.method_5578(), lv4), lv4.method_6182());
						list3.add(new class_3545<>(((class_1320)entry.getKey()).method_6167(), lv5));
					}
				}

				if (lv.method_5578() > 0) {
					lv2.method_10864(" ").method_10852(new class_2588("potion.potency." + lv.method_5578()));
				}

				if (lv.method_5584() > 20) {
					lv2.method_10864(" (").method_10864(class_1292.method_5577(lv, f)).method_10864(")");
				}

				list.add(lv2.method_10854(lv3.method_18792().method_18793()));
			}
		}

		if (!list3.isEmpty()) {
			list.add(new class_2585(""));
			list.add(new class_2588("potion.whenDrank").method_10854(class_124.field_1064));

			for (class_3545<String, class_1322> lv6 : list3) {
				class_1322 lv7 = lv6.method_15441();
				double d = lv7.method_6186();
				double e;
				if (lv7.method_6182() != class_1322.class_1323.field_6330 && lv7.method_6182() != class_1322.class_1323.field_6331) {
					e = lv7.method_6186();
				} else {
					e = lv7.method_6186() * 100.0;
				}

				if (d > 0.0) {
					list.add(
						new class_2588(
								"attribute.modifier.plus." + lv7.method_6182().method_6191(), class_1799.field_8029.format(e), new class_2588("attribute.name." + lv6.method_15442())
							)
							.method_10854(class_124.field_1078)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					list.add(
						new class_2588(
								"attribute.modifier.take." + lv7.method_6182().method_6191(), class_1799.field_8029.format(e), new class_2588("attribute.name." + lv6.method_15442())
							)
							.method_10854(class_124.field_1061)
					);
				}
			}
		}
	}
}
