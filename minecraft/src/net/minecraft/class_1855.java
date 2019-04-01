package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1855 extends class_1852 {
	public class_1855(class_2960 arg) {
		super(arg);
	}

	public boolean method_17715(class_1715 arg, class_1937 arg2) {
		int i = 0;
		class_1799 lv = class_1799.field_8037;

		for (int j = 0; j < arg.method_5439(); j++) {
			class_1799 lv2 = arg.method_5438(j);
			if (!lv2.method_7960()) {
				if (lv2.method_7909() == class_1802.field_8204) {
					if (!lv.method_7960()) {
						return false;
					}

					lv = lv2;
				} else {
					if (lv2.method_7909() != class_1802.field_8895) {
						return false;
					}

					i++;
				}
			}
		}

		return !lv.method_7960() && i > 0;
	}

	public class_1799 method_17714(class_1715 arg) {
		int i = 0;
		class_1799 lv = class_1799.field_8037;

		for (int j = 0; j < arg.method_5439(); j++) {
			class_1799 lv2 = arg.method_5438(j);
			if (!lv2.method_7960()) {
				if (lv2.method_7909() == class_1802.field_8204) {
					if (!lv.method_7960()) {
						return class_1799.field_8037;
					}

					lv = lv2;
				} else {
					if (lv2.method_7909() != class_1802.field_8895) {
						return class_1799.field_8037;
					}

					i++;
				}
			}
		}

		if (!lv.method_7960() && i >= 1) {
			class_1799 lv3 = lv.method_7972();
			lv3.method_7939(i + 1);
			return lv3;
		} else {
			return class_1799.field_8037;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i >= 3 && j >= 3;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9044;
	}
}
