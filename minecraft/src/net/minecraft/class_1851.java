package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1851 extends class_1852 {
	private static final class_1856 field_9007 = class_1856.method_8091(class_1802.field_8407);
	private static final class_1856 field_9006 = class_1856.method_8091(class_1802.field_8054);
	private static final class_1856 field_9008 = class_1856.method_8091(class_1802.field_8450);

	public class_1851(class_2960 arg) {
		super(arg);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		if (!(arg instanceof class_1715)) {
			return false;
		} else {
			boolean bl = false;
			int i = 0;

			for (int j = 0; j < arg.method_5439(); j++) {
				class_1799 lv = arg.method_5438(j);
				if (!lv.method_7960()) {
					if (field_9007.method_8093(lv)) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (field_9006.method_8093(lv)) {
						if (++i > 3) {
							return false;
						}
					} else if (!field_9008.method_8093(lv)) {
						return false;
					}
				}
			}

			return bl && i >= 1;
		}
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		class_1799 lv = new class_1799(class_1802.field_8639, 3);
		class_2487 lv2 = lv.method_7911("Fireworks");
		class_2499 lv3 = new class_2499();
		int i = 0;

		for (int j = 0; j < arg.method_5439(); j++) {
			class_1799 lv4 = arg.method_5438(j);
			if (!lv4.method_7960()) {
				if (field_9006.method_8093(lv4)) {
					i++;
				} else if (field_9008.method_8093(lv4)) {
					class_2487 lv5 = lv4.method_7941("Explosion");
					if (lv5 != null) {
						lv3.method_10606(lv5);
					}
				}
			}
		}

		lv2.method_10567("Flight", (byte)i);
		if (!lv3.isEmpty()) {
			lv2.method_10566("Explosions", lv3);
		}

		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public class_1799 method_8110() {
		return new class_1799(class_1802.field_8639);
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9043;
	}
}
