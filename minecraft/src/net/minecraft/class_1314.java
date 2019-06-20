package net.minecraft;

public abstract class class_1314 extends class_1308 {
	protected class_1314(class_1299<? extends class_1314> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public float method_6149(class_2338 arg) {
		return this.method_6144(arg, this.field_6002);
	}

	public float method_6144(class_2338 arg, class_1941 arg2) {
		return 0.0F;
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return this.method_6144(new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035), arg) >= 0.0F;
	}

	public boolean method_6150() {
		return !this.method_5942().method_6357();
	}

	@Override
	protected void method_5995() {
		super.method_5995();
		class_1297 lv = this.method_5933();
		if (lv != null && lv.field_6002 == this.field_6002) {
			this.method_18408(new class_2338(lv), 5);
			float f = this.method_5739(lv);
			if (this instanceof class_1321 && ((class_1321)this).method_6172()) {
				if (f > 10.0F) {
					this.method_5932(true, true);
				}

				return;
			}

			this.method_6142(f);
			if (f > 10.0F) {
				this.method_5932(true, true);
				this.field_6201.method_6274(class_1352.class_4134.field_18405);
			} else if (f > 6.0F) {
				double d = (lv.field_5987 - this.field_5987) / (double)f;
				double e = (lv.field_6010 - this.field_6010) / (double)f;
				double g = (lv.field_6035 - this.field_6035) / (double)f;
				this.method_18799(this.method_18798().method_1031(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
			} else {
				this.field_6201.method_6273(class_1352.class_4134.field_18405);
				float h = 2.0F;
				class_243 lv2 = new class_243(lv.field_5987 - this.field_5987, lv.field_6010 - this.field_6010, lv.field_6035 - this.field_6035)
					.method_1029()
					.method_1021((double)Math.max(f - 2.0F, 0.0F));
				this.method_5942().method_6337(this.field_5987 + lv2.field_1352, this.field_6010 + lv2.field_1351, this.field_6035 + lv2.field_1350, this.method_6148());
			}
		}
	}

	protected double method_6148() {
		return 1.0;
	}

	protected void method_6142(float f) {
	}
}
