package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1850 extends class_1852 {
	public class_1850(class_2960 arg) {
		super(arg);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		if (!(arg instanceof class_1715)) {
			return false;
		} else {
			int i = 0;
			class_1799 lv = class_1799.field_8037;

			for (int j = 0; j < arg.method_5439(); j++) {
				class_1799 lv2 = arg.method_5438(j);
				if (!lv2.method_7960()) {
					if (lv2.method_7909() == class_1802.field_8360) {
						if (!lv.method_7960()) {
							return false;
						}

						lv = lv2;
					} else {
						if (lv2.method_7909() != class_1802.field_8674) {
							return false;
						}

						i++;
					}
				}
			}

			return !lv.method_7960() && lv.method_7985() && i > 0;
		}
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		int i = 0;
		class_1799 lv = class_1799.field_8037;

		for (int j = 0; j < arg.method_5439(); j++) {
			class_1799 lv2 = arg.method_5438(j);
			if (!lv2.method_7960()) {
				if (lv2.method_7909() == class_1802.field_8360) {
					if (!lv.method_7960()) {
						return class_1799.field_8037;
					}

					lv = lv2;
				} else {
					if (lv2.method_7909() != class_1802.field_8674) {
						return class_1799.field_8037;
					}

					i++;
				}
			}
		}

		if (!lv.method_7960() && lv.method_7985() && i >= 1 && class_1843.method_8052(lv) < 2) {
			class_1799 lv3 = new class_1799(class_1802.field_8360, i);
			class_2487 lv4 = lv.method_7969().method_10553();
			lv4.method_10569("generation", class_1843.method_8052(lv) + 1);
			lv3.method_7980(lv4);
			return lv3;
		} else {
			return class_1799.field_8037;
		}
	}

	@Override
	public class_2371<class_1799> method_8111(class_1263 arg) {
		class_2371<class_1799> lv = class_2371.method_10213(arg.method_5439(), class_1799.field_8037);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (lv2.method_7909().method_7857()) {
				lv.set(i, new class_1799(lv2.method_7909().method_7858()));
			} else if (lv2.method_7909() instanceof class_1843) {
				class_1799 lv3 = lv2.method_7972();
				lv3.method_7939(1);
				lv.set(i, lv3);
				break;
			}
		}

		return lv;
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9029;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i >= 3 && j >= 3;
	}
}
