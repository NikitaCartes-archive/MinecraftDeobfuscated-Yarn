package net.minecraft;

import javax.annotation.Nullable;

public class class_1379 extends class_1352 {
	protected final class_1314 field_6566;
	protected double field_6563;
	protected double field_6562;
	protected double field_6561;
	protected final double field_6567;
	protected int field_6564;
	protected boolean field_6565;

	public class_1379(class_1314 arg, double d) {
		this(arg, d, 120);
	}

	public class_1379(class_1314 arg, double d, int i) {
		this.field_6566 = arg;
		this.field_6567 = d;
		this.field_6564 = i;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6566.method_5782()) {
			return false;
		} else {
			if (!this.field_6565) {
				if (this.field_6566.method_6131() >= 100) {
					return false;
				}

				if (this.field_6566.method_6051().nextInt(this.field_6564) != 0) {
					return false;
				}
			}

			class_243 lv = this.method_6302();
			if (lv == null) {
				return false;
			} else {
				this.field_6563 = lv.field_1352;
				this.field_6562 = lv.field_1351;
				this.field_6561 = lv.field_1350;
				this.field_6565 = false;
				return true;
			}
		}
	}

	@Nullable
	protected class_243 method_6302() {
		return class_1414.method_6375(this.field_6566, 10, 7);
	}

	@Override
	public boolean method_6266() {
		return !this.field_6566.method_5942().method_6357();
	}

	@Override
	public void method_6269() {
		this.field_6566.method_5942().method_6337(this.field_6563, this.field_6562, this.field_6561, this.field_6567);
	}

	public void method_6304() {
		this.field_6565 = true;
	}

	public void method_6303(int i) {
		this.field_6564 = i;
	}
}
