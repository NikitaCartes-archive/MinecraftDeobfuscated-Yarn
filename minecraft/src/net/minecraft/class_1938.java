package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_1938 {
	void method_8570(class_1922 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4, int i);

	default void method_16111(int i, int j, int k, int l, int m, int n) {
		for (int o = k - 1; o <= n + 1; o++) {
			for (int p = i - 1; p <= l + 1; p++) {
				for (int q = j - 1; q <= m + 1; q++) {
					this.method_8571(p >> 4, q >> 4, o >> 4);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	default void method_16110(int i, int j, int k) {
		for (int l = k - 1; l <= k + 1; l++) {
			for (int m = i - 1; m <= i + 1; m++) {
				for (int n = j - 1; n <= j + 1; n++) {
					this.method_8571(m, n, l);
				}
			}
		}
	}

	default void method_8571(int i, int j, int k) {
	}

	void method_8572(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, double d, double e, double f, float g, float h);

	void method_8565(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, class_1297 arg4, float f, float g);

	void method_8562(class_3414 arg, class_2338 arg2);

	void method_8568(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i);

	void method_8563(class_2394 arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i);

	void method_8561(class_1297 arg);

	void method_8566(class_1297 arg);

	void method_8564(int i, class_2338 arg, int j);

	void method_8567(class_1657 arg, int i, class_2338 arg2, int j);

	void method_8569(int i, class_2338 arg, int j);
}
