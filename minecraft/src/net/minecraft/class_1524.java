package net.minecraft;

import javax.annotation.Nullable;

public class class_1524 extends class_1512 {
	private boolean field_7056;
	private class_11 field_7054;
	private class_243 field_7055;

	public class_1524(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6855() {
		if (!this.field_7056 && this.field_7054 != null) {
			class_2338 lv = this.field_7036.field_6002.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600);
			double d = this.field_7036.method_5677(lv);
			if (d > 100.0) {
				this.field_7036.method_6831().method_6863(class_1527.field_7069);
			}
		} else {
			this.field_7056 = false;
			this.method_6858();
		}
	}

	@Override
	public void method_6856() {
		this.field_7056 = true;
		this.field_7054 = null;
		this.field_7055 = null;
	}

	private void method_6858() {
		int i = this.field_7036.method_6818();
		class_243 lv = this.field_7036.method_6834(1.0F);
		int j = this.field_7036.method_6822(-lv.field_1352 * 40.0, 105.0, -lv.field_1350 * 40.0);
		if (this.field_7036.method_6829() != null && this.field_7036.method_6829().method_12517() > 0) {
			j %= 12;
			if (j < 0) {
				j += 12;
			}
		} else {
			j -= 12;
			j &= 7;
			j += 12;
		}

		this.field_7054 = this.field_7036.method_6833(i, j, null);
		if (this.field_7054 != null) {
			this.field_7054.method_44();
			this.method_6859();
		}
	}

	private void method_6859() {
		class_243 lv = this.field_7054.method_35();
		this.field_7054.method_44();

		double d;
		do {
			d = lv.field_1351 + (double)(this.field_7036.method_6051().nextFloat() * 20.0F);
		} while (d < lv.field_1351);

		this.field_7055 = new class_243(lv.field_1352, d, lv.field_1350);
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7055;
	}

	@Override
	public class_1527<class_1524> method_6849() {
		return class_1527.field_7077;
	}
}
