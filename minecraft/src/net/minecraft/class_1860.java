package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_1860 {
	boolean method_8115(class_1263 arg, class_1937 arg2);

	class_1799 method_8116(class_1263 arg);

	@Environment(EnvType.CLIENT)
	boolean method_8113(int i, int j);

	class_1799 method_8110();

	default class_2371<class_1799> method_8111(class_1263 arg) {
		class_2371<class_1799> lv = class_2371.method_10213(arg.method_5439(), class_1799.field_8037);

		for (int i = 0; i < lv.size(); i++) {
			class_1792 lv2 = arg.method_5438(i).method_7909();
			if (lv2.method_7857()) {
				lv.set(i, new class_1799(lv2.method_7858()));
			}
		}

		return lv;
	}

	default class_2371<class_1856> method_8117() {
		return class_2371.method_10211();
	}

	default boolean method_8118() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default String method_8112() {
		return "";
	}

	@Environment(EnvType.CLIENT)
	default class_1799 method_17447() {
		return new class_1799(class_2246.field_9980);
	}

	class_2960 method_8114();

	class_1862<?> method_8119();
}
