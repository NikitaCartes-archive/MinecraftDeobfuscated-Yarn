package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1872 extends class_1852 {
	public class_1872(class_2960 arg) {
		super(arg);
	}

	public boolean method_17732(class_1715 arg, class_1937 arg2) {
		class_1799 lv = class_1799.field_8037;
		class_1799 lv2 = class_1799.field_8037;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv3 = arg.method_5438(i);
			if (!lv3.method_7960()) {
				if (lv3.method_7909() instanceof class_1746) {
					if (!lv2.method_7960()) {
						return false;
					}

					lv2 = lv3;
				} else {
					if (lv3.method_7909() != class_1802.field_8255) {
						return false;
					}

					if (!lv.method_7960()) {
						return false;
					}

					if (lv3.method_7941("BlockEntityTag") != null) {
						return false;
					}

					lv = lv3;
				}
			}
		}

		return !lv.method_7960() && !lv2.method_7960();
	}

	public class_1799 method_17731(class_1715 arg) {
		class_1799 lv = class_1799.field_8037;
		class_1799 lv2 = class_1799.field_8037;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv3 = arg.method_5438(i);
			if (!lv3.method_7960()) {
				if (lv3.method_7909() instanceof class_1746) {
					lv = lv3;
				} else if (lv3.method_7909() == class_1802.field_8255) {
					lv2 = lv3.method_7972();
				}
			}
		}

		if (lv2.method_7960()) {
			return lv2;
		} else {
			class_2487 lv4 = lv.method_7941("BlockEntityTag");
			class_2487 lv5 = lv4 == null ? new class_2487() : lv4.method_10553();
			lv5.method_10569("Base", ((class_1746)lv.method_7909()).method_7706().method_7789());
			lv2.method_7959("BlockEntityTag", lv5);
			return lv2;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9040;
	}
}
