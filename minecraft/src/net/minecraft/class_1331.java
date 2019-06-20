package net.minecraft;

public class class_1331 extends class_1335 {
	public class_1331(class_1308 arg) {
		super(arg);
	}

	@Override
	public void method_6240() {
		if (this.field_6374 == class_1335.class_1336.field_6378) {
			this.field_6374 = class_1335.class_1336.field_6377;
			this.field_6371.method_5875(true);
			double d = this.field_6370 - this.field_6371.field_5987;
			double e = this.field_6369 - this.field_6371.field_6010;
			double f = this.field_6367 - this.field_6371.field_6035;
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.field_6371.method_5976(0.0F);
				this.field_6371.method_5930(0.0F);
				return;
			}

			float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.field_6371.field_6031 = this.method_6238(this.field_6371.field_6031, h, 10.0F);
			float i;
			if (this.field_6371.field_5952) {
				i = (float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7357).method_6194());
			} else {
				i = (float)(this.field_6372 * this.field_6371.method_5996(class_1612.field_7355).method_6194());
			}

			this.field_6371.method_6125(i);
			double j = (double)class_3532.method_15368(d * d + f * f);
			float k = (float)(-(class_3532.method_15349(e, j) * 180.0F / (float)Math.PI));
			this.field_6371.field_5965 = this.method_6238(this.field_6371.field_5965, k, 10.0F);
			this.field_6371.method_5976(e > 0.0 ? i : -i);
		} else {
			this.field_6371.method_5875(false);
			this.field_6371.method_5976(0.0F);
			this.field_6371.method_5930(0.0F);
		}
	}
}
