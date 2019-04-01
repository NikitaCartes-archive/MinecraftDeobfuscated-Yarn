package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1876 extends class_1852 {
	public class_1876(class_2960 arg) {
		super(arg);
	}

	public boolean method_17741(class_1715 arg, class_1937 arg2) {
		if (arg.method_17398() == 3 && arg.method_17397() == 3) {
			for (int i = 0; i < arg.method_17398(); i++) {
				for (int j = 0; j < arg.method_17397(); j++) {
					class_1799 lv = arg.method_5438(i + j * arg.method_17398());
					if (lv.method_7960()) {
						return false;
					}

					class_1792 lv2 = lv.method_7909();
					if (i == 1 && j == 1) {
						if (lv2 != class_1802.field_8150) {
							return false;
						}
					} else if (lv2 != class_1802.field_8107) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public class_1799 method_17740(class_1715 arg) {
		class_1799 lv = arg.method_5438(1 + arg.method_17398());
		if (lv.method_7909() != class_1802.field_8150) {
			return class_1799.field_8037;
		} else {
			class_1799 lv2 = new class_1799(class_1802.field_8087, 8);
			class_1844.method_8061(lv2, class_1844.method_8063(lv));
			class_1844.method_8056(lv2, class_1844.method_8068(lv));
			return lv2;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9037;
	}
}
