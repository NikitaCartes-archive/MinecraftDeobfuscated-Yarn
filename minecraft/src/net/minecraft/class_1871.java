package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1871 extends class_1852 {
	public class_1871(class_2960 arg) {
		super(arg);
	}

	public boolean method_17734(class_1715 arg, class_1937 arg2) {
		int i = 0;
		int j = 0;

		for (int k = 0; k < arg.method_5439(); k++) {
			class_1799 lv = arg.method_5438(k);
			if (!lv.method_7960()) {
				if (class_2248.method_9503(lv.method_7909()) instanceof class_2480) {
					i++;
				} else {
					if (!(lv.method_7909() instanceof class_1769)) {
						return false;
					}

					j++;
				}

				if (j > 1 || i > 1) {
					return false;
				}
			}
		}

		return i == 1 && j == 1;
	}

	public class_1799 method_17733(class_1715 arg) {
		class_1799 lv = class_1799.field_8037;
		class_1769 lv2 = (class_1769)class_1802.field_8446;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv3 = arg.method_5438(i);
			if (!lv3.method_7960()) {
				class_1792 lv4 = lv3.method_7909();
				if (class_2248.method_9503(lv4) instanceof class_2480) {
					lv = lv3;
				} else if (lv4 instanceof class_1769) {
					lv2 = (class_1769)lv4;
				}
			}
		}

		class_1799 lv5 = class_2480.method_10529(lv2.method_7802());
		if (lv.method_7985()) {
			lv5.method_7980(lv.method_7969().method_10553());
		}

		return lv5;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9041;
	}
}
