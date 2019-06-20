package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4317 extends class_1852 {
	public class_4317(class_2960 arg) {
		super(arg);
	}

	public boolean method_20808(class_1715 arg, class_1937 arg2) {
		List<class_1799> list = Lists.<class_1799>newArrayList();

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960()) {
				list.add(lv);
				if (list.size() > 1) {
					class_1799 lv2 = (class_1799)list.get(0);
					if (lv.method_7909() != lv2.method_7909() || lv2.method_7947() != 1 || lv.method_7947() != 1 || !lv2.method_7909().method_7846()) {
						return false;
					}
				}
			}
		}

		return list.size() == 2;
	}

	public class_1799 method_20807(class_1715 arg) {
		List<class_1799> list = Lists.<class_1799>newArrayList();

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960()) {
				list.add(lv);
				if (list.size() > 1) {
					class_1799 lv2 = (class_1799)list.get(0);
					if (lv.method_7909() != lv2.method_7909() || lv2.method_7947() != 1 || lv.method_7947() != 1 || !lv2.method_7909().method_7846()) {
						return class_1799.field_8037;
					}
				}
			}
		}

		if (list.size() == 2) {
			class_1799 lv3 = (class_1799)list.get(0);
			class_1799 lv = (class_1799)list.get(1);
			if (lv3.method_7909() == lv.method_7909() && lv3.method_7947() == 1 && lv.method_7947() == 1 && lv3.method_7909().method_7846()) {
				class_1792 lv4 = lv3.method_7909();
				int j = lv4.method_7841() - lv3.method_7919();
				int k = lv4.method_7841() - lv.method_7919();
				int l = j + k + lv4.method_7841() * 5 / 100;
				int m = lv4.method_7841() - l;
				if (m < 0) {
					m = 0;
				}

				class_1799 lv5 = new class_1799(lv3.method_7909());
				lv5.method_7974(m);
				return lv5;
			}
		}

		return class_1799.field_8037;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_19421;
	}
}
