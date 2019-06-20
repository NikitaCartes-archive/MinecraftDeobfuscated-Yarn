package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1854 extends class_1852 {
	private static final class_1856 field_9015 = class_1856.method_8091(class_1802.field_8450);

	public class_1854(class_2960 arg) {
		super(arg);
	}

	public boolean method_17711(class_1715 arg, class_1937 arg2) {
		boolean bl = false;
		boolean bl2 = false;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv = arg.method_5438(i);
			if (!lv.method_7960()) {
				if (lv.method_7909() instanceof class_1769) {
					bl = true;
				} else {
					if (!field_9015.method_8093(lv)) {
						return false;
					}

					if (bl2) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl2 && bl;
	}

	public class_1799 method_17710(class_1715 arg) {
		List<Integer> list = Lists.<Integer>newArrayList();
		class_1799 lv = null;

		for (int i = 0; i < arg.method_5439(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			class_1792 lv3 = lv2.method_7909();
			if (lv3 instanceof class_1769) {
				list.add(((class_1769)lv3).method_7802().method_7790());
			} else if (field_9015.method_8093(lv2)) {
				lv = lv2.method_7972();
				lv.method_7939(1);
			}
		}

		if (lv != null && !list.isEmpty()) {
			lv.method_7911("Explosion").method_10572("FadeColors", list);
			return lv;
		} else {
			return class_1799.field_8037;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9034;
	}
}
