package net.minecraft;

import javax.annotation.Nullable;

public class class_1517 extends class_1512 {
	private static final class_4051 field_18121 = new class_4051().method_18418(64.0);
	private class_11 field_7043;
	private class_243 field_7045;
	private boolean field_7044;

	public class_1517(class_1510 arg) {
		super(arg);
	}

	@Override
	public class_1527<class_1517> method_6849() {
		return class_1527.field_7069;
	}

	@Override
	public void method_6855() {
		double d = this.field_7045 == null ? 0.0 : this.field_7045.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
		if (d < 100.0 || d > 22500.0 || this.field_7036.field_5976 || this.field_7036.field_5992) {
			this.method_6841();
		}
	}

	@Override
	public void method_6856() {
		this.field_7043 = null;
		this.field_7045 = null;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7045;
	}

	private void method_6841() {
		if (this.field_7043 != null && this.field_7043.method_46()) {
			class_2338 lv = this.field_7036.field_6002.method_8598(class_2902.class_2903.field_13203, new class_2338(class_3033.field_13600));
			int i = this.field_7036.method_6829() == null ? 0 : this.field_7036.method_6829().method_12517();
			if (this.field_7036.method_6051().nextInt(i + 3) == 0) {
				this.field_7036.method_6831().method_6863(class_1527.field_7071);
				return;
			}

			double d = 64.0;
			class_1657 lv2 = this.field_7036.field_6002.method_18461(field_18121, (double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
			if (lv2 != null) {
				d = lv.method_19770(lv2.method_19538(), true) / 512.0;
			}

			if (lv2 != null && (this.field_7036.method_6051().nextInt(class_3532.method_15382((int)d) + 2) == 0 || this.field_7036.method_6051().nextInt(i + 2) == 0)) {
				this.method_6843(lv2);
				return;
			}
		}

		if (this.field_7043 == null || this.field_7043.method_46()) {
			int j = this.field_7036.method_6818();
			int ix = j;
			if (this.field_7036.method_6051().nextInt(8) == 0) {
				this.field_7044 = !this.field_7044;
				ix = j + 6;
			}

			if (this.field_7044) {
				ix++;
			} else {
				ix--;
			}

			if (this.field_7036.method_6829() != null && this.field_7036.method_6829().method_12517() >= 0) {
				ix %= 12;
				if (ix < 0) {
					ix += 12;
				}
			} else {
				ix -= 12;
				ix &= 7;
				ix += 12;
			}

			this.field_7043 = this.field_7036.method_6833(j, ix, null);
			if (this.field_7043 != null) {
				this.field_7043.method_44();
			}
		}

		this.method_6842();
	}

	private void method_6843(class_1657 arg) {
		this.field_7036.method_6831().method_6863(class_1527.field_7076);
		this.field_7036.method_6831().method_6865(class_1527.field_7076).method_6862(arg);
	}

	private void method_6842() {
		if (this.field_7043 != null && !this.field_7043.method_46()) {
			class_243 lv = this.field_7043.method_35();
			this.field_7043.method_44();
			double d = lv.field_1352;
			double e = lv.field_1350;

			double f;
			do {
				f = lv.field_1351 + (double)(this.field_7036.method_6051().nextFloat() * 20.0F);
			} while (f < lv.field_1351);

			this.field_7045 = new class_243(d, f, e);
		}
	}

	@Override
	public void method_6850(class_1511 arg, class_2338 arg2, class_1282 arg3, @Nullable class_1657 arg4) {
		if (arg4 != null && !arg4.field_7503.field_7480) {
			this.method_6843(arg4);
		}
	}
}
