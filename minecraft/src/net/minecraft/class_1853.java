package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1853 extends class_1852 {
	private static final class_1856 field_9011 = class_1856.method_8091(
		class_1802.field_8814,
		class_1802.field_8153,
		class_1802.field_8397,
		class_1802.field_8398,
		class_1802.field_8791,
		class_1802.field_8681,
		class_1802.field_8575,
		class_1802.field_8712,
		class_1802.field_8470
	);
	private static final class_1856 field_9010 = class_1856.method_8091(class_1802.field_8477);
	private static final class_1856 field_9014 = class_1856.method_8091(class_1802.field_8601);
	private static final Map<class_1792, class_1781.class_1782> field_9013 = class_156.method_654(
		Maps.<class_1792, class_1781.class_1782>newHashMap(), hashMap -> {
			hashMap.put(class_1802.field_8814, class_1781.class_1782.field_7977);
			hashMap.put(class_1802.field_8153, class_1781.class_1782.field_7970);
			hashMap.put(class_1802.field_8397, class_1781.class_1782.field_7973);
			hashMap.put(class_1802.field_8398, class_1781.class_1782.field_7974);
			hashMap.put(class_1802.field_8791, class_1781.class_1782.field_7974);
			hashMap.put(class_1802.field_8681, class_1781.class_1782.field_7974);
			hashMap.put(class_1802.field_8575, class_1781.class_1782.field_7974);
			hashMap.put(class_1802.field_8712, class_1781.class_1782.field_7974);
			hashMap.put(class_1802.field_8470, class_1781.class_1782.field_7974);
		}
	);
	private static final class_1856 field_9012 = class_1856.method_8091(class_1802.field_8054);

	public class_1853(class_2960 arg) {
		super(arg);
	}

	public boolean method_17713(class_1715 arg, class_1937 arg2) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960()) {
				if (field_9011.method_8093(lv)) {
					if (bl3) {
						return false;
					}

					bl3 = true;
				} else if (field_9014.method_8093(lv)) {
					if (bl5) {
						return false;
					}

					bl5 = true;
				} else if (field_9010.method_8093(lv)) {
					if (bl4) {
						return false;
					}

					bl4 = true;
				} else if (field_9012.method_8093(lv)) {
					if (bl) {
						return false;
					}

					bl = true;
				} else {
					if (!(lv.method_7909() instanceof class_1769)) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl && bl2;
	}

	public class_1799 method_17712(class_1715 arg) {
		class_1799 lv = new class_1799(class_1802.field_8450);
		class_2487 lv2 = lv.method_7911("Explosion");
		class_1781.class_1782 lv3 = class_1781.class_1782.field_7976;
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv4 = arg.method_5438(i);
			if (!lv4.method_7960()) {
				if (field_9011.method_8093(lv4)) {
					lv3 = (class_1781.class_1782)field_9013.get(lv4.method_7909());
				} else if (field_9014.method_8093(lv4)) {
					lv2.method_10556("Flicker", true);
				} else if (field_9010.method_8093(lv4)) {
					lv2.method_10556("Trail", true);
				} else if (lv4.method_7909() instanceof class_1769) {
					list.add(((class_1769)lv4.method_7909()).method_7802().method_7790());
				}
			}
		}

		lv2.method_10572("Colors", list);
		lv2.method_10567("Type", (byte)lv3.method_7816());
		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1799 method_8110() {
		return new class_1799(class_1802.field_8450);
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9036;
	}
}
