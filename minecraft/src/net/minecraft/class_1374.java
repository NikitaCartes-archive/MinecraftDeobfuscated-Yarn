package net.minecraft;

import javax.annotation.Nullable;

public class class_1374 extends class_1352 {
	protected final class_1314 field_6549;
	protected final double field_6548;
	protected double field_6547;
	protected double field_6546;
	protected double field_6550;

	public class_1374(class_1314 arg, double d) {
		this.field_6549 = arg;
		this.field_6548 = d;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6549.method_6065() == null && !this.field_6549.method_5809()) {
			return false;
		} else {
			if (this.field_6549.method_5809()) {
				class_2338 lv = this.method_6300(this.field_6549.field_6002, this.field_6549, 5, 4);
				if (lv != null) {
					this.field_6547 = (double)lv.method_10263();
					this.field_6546 = (double)lv.method_10264();
					this.field_6550 = (double)lv.method_10260();
					return true;
				}
			}

			return this.method_6301();
		}
	}

	protected boolean method_6301() {
		class_243 lv = class_1414.method_6375(this.field_6549, 5, 4);
		if (lv == null) {
			return false;
		} else {
			this.field_6547 = lv.field_1352;
			this.field_6546 = lv.field_1351;
			this.field_6550 = lv.field_1350;
			return true;
		}
	}

	@Override
	public void method_6269() {
		this.field_6549.method_5942().method_6337(this.field_6547, this.field_6546, this.field_6550, this.field_6548);
	}

	@Override
	public boolean method_6266() {
		return !this.field_6549.method_5942().method_6357();
	}

	@Nullable
	protected class_2338 method_6300(class_1922 arg, class_1297 arg2, int i, int j) {
		class_2338 lv = new class_2338(arg2);
		int k = lv.method_10263();
		int l = lv.method_10264();
		int m = lv.method_10260();
		float f = (float)(i * i * j * 2);
		class_2338 lv2 = null;
		class_2338.class_2339 lv3 = new class_2338.class_2339();

		for (int n = k - i; n <= k + i; n++) {
			for (int o = l - j; o <= l + j; o++) {
				for (int p = m - i; p <= m + i; p++) {
					lv3.method_10103(n, o, p);
					if (arg.method_8316(lv3).method_15767(class_3486.field_15517)) {
						float g = (float)((n - k) * (n - k) + (o - l) * (o - l) + (p - m) * (p - m));
						if (g < f) {
							f = g;
							lv2 = new class_2338(lv3);
						}
					}
				}
			}
		}

		return lv2;
	}
}
