package net.minecraft;

import javax.annotation.Nullable;

public class class_1519 extends class_1512 {
	private class_11 field_7047;
	private class_243 field_7048;

	public class_1519(class_1510 arg) {
		super(arg);
	}

	@Override
	public class_1527<class_1519> method_6849() {
		return class_1527.field_7071;
	}

	@Override
	public void method_6856() {
		this.field_7047 = null;
		this.field_7048 = null;
	}

	@Override
	public void method_6855() {
		double d = this.field_7048 == null ? 0.0 : this.field_7048.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
		if (d < 100.0 || d > 22500.0 || this.field_7036.field_5976 || this.field_7036.field_5992) {
			this.method_6844();
		}
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7048;
	}

	private void method_6844() {
		if (this.field_7047 == null || this.field_7047.method_46()) {
			int i = this.field_7036.method_6818();
			class_2338 lv = this.field_7036.field_6002.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600);
			class_1657 lv2 = this.field_7036.field_6002.method_8483(lv, 128.0, 128.0);
			int j;
			if (lv2 != null) {
				class_243 lv3 = new class_243(lv2.field_5987, 0.0, lv2.field_6035).method_1029();
				j = this.field_7036.method_6822(-lv3.field_1352 * 40.0, 105.0, -lv3.field_1350 * 40.0);
			} else {
				j = this.field_7036.method_6822(40.0, (double)lv.method_10264(), 0.0);
			}

			class_9 lv4 = new class_9(lv.method_10263(), lv.method_10264(), lv.method_10260());
			this.field_7047 = this.field_7036.method_6833(i, j, lv4);
			if (this.field_7047 != null) {
				this.field_7047.method_44();
			}
		}

		this.method_6845();
		if (this.field_7047 != null && this.field_7047.method_46()) {
			this.field_7036.method_6831().method_6863(class_1527.field_7067);
		}
	}

	private void method_6845() {
		if (this.field_7047 != null && !this.field_7047.method_46()) {
			class_243 lv = this.field_7047.method_35();
			this.field_7047.method_44();
			double d = lv.field_1352;
			double e = lv.field_1350;

			double f;
			do {
				f = lv.field_1351 + (double)(this.field_7036.method_6051().nextFloat() * 20.0F);
			} while (f < lv.field_1351);

			this.field_7048 = new class_243(d, f, e);
		}
	}
}
