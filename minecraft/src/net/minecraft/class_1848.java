package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1848 extends class_1852 {
	public class_1848(class_2960 arg) {
		super(arg);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		if (!(arg instanceof class_1715)) {
			return false;
		} else {
			class_1767 lv = null;
			class_1799 lv2 = null;
			class_1799 lv3 = null;

			for (int i = 0; i < arg.method_5439(); i++) {
				class_1799 lv4 = arg.method_5438(i);
				class_1792 lv5 = lv4.method_7909();
				if (lv5 instanceof class_1746) {
					class_1746 lv6 = (class_1746)lv5;
					if (lv == null) {
						lv = lv6.method_7706();
					} else if (lv != lv6.method_7706()) {
						return false;
					}

					boolean bl = class_2573.method_10910(lv4) > 0;
					if (bl) {
						if (lv2 != null) {
							return false;
						}

						lv2 = lv4;
					} else {
						if (lv3 != null) {
							return false;
						}

						lv3 = lv4;
					}
				}
			}

			return lv2 != null && lv3 != null;
		}
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960() && class_2573.method_10910(lv) > 0) {
				class_1799 lv2 = lv.method_7972();
				lv2.method_7939(1);
				return lv2;
			}
		}

		return class_1799.field_8037;
	}

	@Override
	public class_2371<class_1799> method_8111(class_1263 arg) {
		class_2371<class_1799> lv = class_2371.method_10213(arg.method_5439(), class_1799.field_8037);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (!lv2.method_7960()) {
				if (lv2.method_7909().method_7857()) {
					lv.set(i, new class_1799(lv2.method_7909().method_7858()));
				} else if (lv2.method_7985() && class_2573.method_10910(lv2) > 0) {
					class_1799 lv3 = lv2.method_7972();
					lv3.method_7939(1);
					lv.set(i, lv3);
				}
			}
		}

		return lv;
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9038;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}
}
