package net.minecraft;

public class class_1330 {
	private final class_1308 field_6356;
	private int field_6355;
	private float field_6354;

	public class_1330(class_1308 arg) {
		this.field_6356 = arg;
	}

	public void method_6224() {
		if (this.method_20247()) {
			this.field_6356.field_6283 = this.field_6356.field_6031;
			this.method_20244();
			this.field_6354 = this.field_6356.field_6241;
			this.field_6355 = 0;
		} else {
			if (this.method_20246()) {
				if (Math.abs(this.field_6356.field_6241 - this.field_6354) > 15.0F) {
					this.field_6355 = 0;
					this.field_6354 = this.field_6356.field_6241;
					this.method_20243();
				} else {
					this.field_6355++;
					if (this.field_6355 > 10) {
						this.method_20245();
					}
				}
			}
		}
	}

	private void method_20243() {
		this.field_6356.field_6283 = class_3532.method_20306(this.field_6356.field_6283, this.field_6356.field_6241, (float)this.field_6356.method_5986());
	}

	private void method_20244() {
		this.field_6356.field_6241 = class_3532.method_20306(this.field_6356.field_6241, this.field_6356.field_6283, (float)this.field_6356.method_5986());
	}

	private void method_20245() {
		int i = this.field_6355 - 10;
		float f = class_3532.method_15363((float)i / 10.0F, 0.0F, 1.0F);
		float g = (float)this.field_6356.method_5986() * (1.0F - f);
		this.field_6356.field_6283 = class_3532.method_20306(this.field_6356.field_6283, this.field_6356.field_6241, g);
	}

	private boolean method_20246() {
		return this.field_6356.method_5685().isEmpty() || !(this.field_6356.method_5685().get(0) instanceof class_1308);
	}

	private boolean method_20247() {
		double d = this.field_6356.field_5987 - this.field_6356.field_6014;
		double e = this.field_6356.field_6035 - this.field_6356.field_5969;
		return d * d + e * e > 2.5000003E-7F;
	}
}
