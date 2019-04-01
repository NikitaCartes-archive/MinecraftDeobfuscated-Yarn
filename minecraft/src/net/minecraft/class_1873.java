package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1873 extends class_1852 {
	public class_1873(class_2960 arg) {
		super(arg);
	}

	public boolean method_17739(class_1715 arg, class_1937 arg2) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960()) {
				if (lv.method_7909() == class_2246.field_10251.method_8389() && !bl3) {
					bl3 = true;
				} else if (lv.method_7909() == class_2246.field_10559.method_8389() && !bl2) {
					bl2 = true;
				} else if (lv.method_7909().method_7855(class_3489.field_15543) && !bl) {
					bl = true;
				} else {
					if (lv.method_7909() != class_1802.field_8428 || bl4) {
						return false;
					}

					bl4 = true;
				}
			}
		}

		return bl && bl3 && bl2 && bl4;
	}

	public class_1799 method_17738(class_1715 arg) {
		class_1799 lv = class_1799.field_8037;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (!lv2.method_7960() && lv2.method_7909().method_7855(class_3489.field_15543)) {
				lv = lv2;
				break;
			}
		}

		class_1799 lv3 = new class_1799(class_1802.field_8766, 1);
		if (lv.method_7909() instanceof class_1747 && ((class_1747)lv.method_7909()).method_7711() instanceof class_2356) {
			class_2356 lv4 = (class_2356)((class_1747)lv.method_7909()).method_7711();
			class_1291 lv5 = lv4.method_10188();
			class_1830.method_8021(lv3, lv5, lv4.method_10187());
		}

		return lv3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9030;
	}
}
