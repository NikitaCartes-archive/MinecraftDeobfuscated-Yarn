package net.minecraft;

public class class_1330 {
	private final class_1309 field_6356;
	private int field_6355;
	private float field_6354;

	public class_1330(class_1309 arg) {
		this.field_6356 = arg;
	}

	public void method_6224() {
		double d = this.field_6356.field_5987 - this.field_6356.field_6014;
		double e = this.field_6356.field_6035 - this.field_6356.field_5969;
		if (d * d + e * e > 2.5000003E-7F) {
			this.field_6356.field_6283 = this.field_6356.field_6031;
			this.field_6356.field_6241 = this.method_6223(this.field_6356.field_6283, this.field_6356.field_6241, (float)((class_1308)this.field_6356).method_5986());
			this.field_6354 = this.field_6356.field_6241;
			this.field_6355 = 0;
		} else {
			if (this.field_6356.method_5685().isEmpty() || !(this.field_6356.method_5685().get(0) instanceof class_1308)) {
				float f = 75.0F;
				if (Math.abs(this.field_6356.field_6241 - this.field_6354) > 15.0F) {
					this.field_6355 = 0;
					this.field_6354 = this.field_6356.field_6241;
				} else {
					this.field_6355++;
					int i = 10;
					if (this.field_6355 > 10) {
						f = Math.max(1.0F - (float)(this.field_6355 - 10) / 10.0F, 0.0F) * (float)((class_1308)this.field_6356).method_5986();
					}
				}

				this.field_6356.field_6283 = this.method_6223(this.field_6356.field_6241, this.field_6356.field_6283, f);
			}
		}
	}

	private float method_6223(float f, float g, float h) {
		float i = class_3532.method_15393(f - g);
		if (i < -h) {
			i = -h;
		}

		if (i >= h) {
			i = h;
		}

		return f - i;
	}
}
