package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1849 extends class_1852 {
	public class_1849(class_2960 arg) {
		super(arg);
	}

	public boolean method_17701(class_1715 arg, class_1937 arg2) {
		class_1799 lv = class_1799.field_8037;
		List<class_1799> list = Lists.<class_1799>newArrayList();

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (!lv2.method_7960()) {
				if (lv2.method_7909() instanceof class_1768) {
					if (!lv.method_7960()) {
						return false;
					}

					lv = lv2;
				} else {
					if (!(lv2.method_7909() instanceof class_1769)) {
						return false;
					}

					list.add(lv2);
				}
			}
		}

		return !lv.method_7960() && !list.isEmpty();
	}

	public class_1799 method_17700(class_1715 arg) {
		List<class_1769> list = Lists.<class_1769>newArrayList();
		class_1799 lv = class_1799.field_8037;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (!lv2.method_7960()) {
				class_1792 lv3 = lv2.method_7909();
				if (lv3 instanceof class_1768) {
					if (!lv.method_7960()) {
						return class_1799.field_8037;
					}

					lv = lv2.method_7972();
				} else {
					if (!(lv3 instanceof class_1769)) {
						return class_1799.field_8037;
					}

					list.add((class_1769)lv3);
				}
			}
		}

		return !lv.method_7960() && !list.isEmpty() ? class_1768.method_19261(lv, list) : class_1799.field_8037;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9028;
	}
}
