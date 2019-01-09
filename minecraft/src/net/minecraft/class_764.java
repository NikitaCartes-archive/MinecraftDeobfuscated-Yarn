package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_764 implements class_760 {
	@Override
	public class_1011 method_3237(class_1011 arg) {
		boolean bl = arg.method_4323() == 32;
		if (bl) {
			class_1011 lv = new class_1011(64, 64, true);
			lv.method_4317(arg);
			arg.close();
			arg = lv;
			lv.method_4326(0, 32, 64, 32, 0);
			lv.method_4304(4, 16, 16, 32, 4, 4, true, false);
			lv.method_4304(8, 16, 16, 32, 4, 4, true, false);
			lv.method_4304(0, 20, 24, 32, 4, 12, true, false);
			lv.method_4304(4, 20, 16, 32, 4, 12, true, false);
			lv.method_4304(8, 20, 8, 32, 4, 12, true, false);
			lv.method_4304(12, 20, 16, 32, 4, 12, true, false);
			lv.method_4304(44, 16, -8, 32, 4, 4, true, false);
			lv.method_4304(48, 16, -8, 32, 4, 4, true, false);
			lv.method_4304(40, 20, 0, 32, 4, 12, true, false);
			lv.method_4304(44, 20, -8, 32, 4, 12, true, false);
			lv.method_4304(48, 20, -16, 32, 4, 12, true, false);
			lv.method_4304(52, 20, -8, 32, 4, 12, true, false);
		}

		method_3312(arg, 0, 0, 32, 16);
		if (bl) {
			method_3311(arg, 32, 0, 64, 32);
		}

		method_3312(arg, 0, 16, 64, 32);
		method_3312(arg, 16, 48, 48, 64);
		return arg;
	}

	@Override
	public void method_3238() {
	}

	private static void method_3311(class_1011 arg, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				int o = arg.method_4315(m, n);
				if ((o >> 24 & 0xFF) < 128) {
					return;
				}
			}
		}

		for (int m = i; m < k; m++) {
			for (int nx = j; nx < l; nx++) {
				arg.method_4305(m, nx, arg.method_4315(m, nx) & 16777215);
			}
		}
	}

	private static void method_3312(class_1011 arg, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				arg.method_4305(m, n, arg.method_4315(m, n) | 0xFF000000);
			}
		}
	}
}
