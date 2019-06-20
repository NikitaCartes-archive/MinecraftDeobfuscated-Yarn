package net.minecraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3642 {
	private static final Logger field_16592 = LogManager.getLogger();
	private final class_3626 field_16516;

	public class_3642(class_3627<class_3626> arg) {
		this.field_16516 = arg.make();
	}

	public class_1959[] method_15842(int i, int j, int k, int l) {
		class_1959[] lvs = new class_1959[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = this.field_16516.method_15825(i + n, j + m);
				class_1959 lv = this.method_16459(o);
				lvs[n + m * k] = lv;
			}
		}

		return lvs;
	}

	private class_1959 method_16459(int i) {
		class_1959 lv = class_2378.field_11153.method_10200(i);
		if (lv == null) {
			if (class_155.field_1125) {
				throw new IllegalStateException("Unknown biome id: " + i);
			} else {
				field_16592.warn("Unknown biome id: ", i);
				return class_1972.field_9469;
			}
		} else {
			return lv;
		}
	}

	public class_1959 method_16341(int i, int j) {
		return this.method_16459(this.field_16516.method_15825(i, j));
	}
}
