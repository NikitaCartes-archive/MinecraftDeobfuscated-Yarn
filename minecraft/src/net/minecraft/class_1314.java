package net.minecraft;

public abstract class class_1314 extends class_1308 {
	private class_2338 field_6312 = class_2338.field_10980;
	private float field_6311 = -1.0F;

	protected class_1314(class_1299<?> arg, class_1937 arg2) {
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
		return super.method_5979(arg, arg2) && this.method_6144(new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035), arg) >= 0.0F;
	}

	public boolean method_6150() {
		return !this.method_5942().method_6357();
	}

	public boolean method_6152() {
		return this.method_6146(new class_2338(this));
	}

	public boolean method_6146(class_2338 arg) {
		return this.field_6311 == -1.0F ? true : this.field_6312.method_10262(arg) < (double)(this.field_6311 * this.field_6311);
	}

	public void method_6145(class_2338 arg, int i) {
		this.field_6312 = arg;
		this.field_6311 = (float)i;
	}

	public class_2338 method_6141() {
		return this.field_6312;
	}

	public float method_6143() {
		return this.field_6311;
	}

	public void method_6147() {
		this.field_6311 = -1.0F;
	}

	public boolean method_6151() {
		return this.field_6311 != -1.0F;
	}

	@Override
	protected void method_5995() {
		super.method_5995();
		if (this.method_5934() && this.method_5933() != null && this.method_5933().field_6002 == this.field_6002) {
			class_1297 lv = this.method_5933();
			this.method_6145(new class_2338((int)lv.field_5987, (int)lv.field_6010, (int)lv.field_6035), 5);
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
				this.field_6201.method_6274(1);
			} else if (f > 6.0F) {
				double d = (lv.field_5987 - this.field_5987) / (double)f;
				double e = (lv.field_6010 - this.field_6010) / (double)f;
				double g = (lv.field_6035 - this.field_6035) / (double)f;
				this.field_5967 = this.field_5967 + d * Math.abs(d) * 0.4;
				this.field_5984 = this.field_5984 + e * Math.abs(e) * 0.4;
				this.field_6006 = this.field_6006 + g * Math.abs(g) * 0.4;
			} else {
				this.field_6201.method_6273(1);
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
