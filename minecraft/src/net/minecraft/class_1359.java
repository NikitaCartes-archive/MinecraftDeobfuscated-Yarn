package net.minecraft;

public class class_1359 extends class_1352 {
	private final class_1308 field_6476;
	private class_1309 field_6477;
	private final float field_6475;

	public class_1359(class_1308 arg, float f) {
		this.field_6476 = arg;
		this.field_6475 = f;
		this.method_6265(5);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6476.method_5782()) {
			return false;
		} else {
			this.field_6477 = this.field_6476.method_5968();
			if (this.field_6477 == null) {
				return false;
			} else {
				double d = this.field_6476.method_5858(this.field_6477);
				if (d < 4.0 || d > 16.0) {
					return false;
				} else {
					return !this.field_6476.field_5952 ? false : this.field_6476.method_6051().nextInt(5) == 0;
				}
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6476.field_5952;
	}

	@Override
	public void method_6269() {
		double d = this.field_6477.field_5987 - this.field_6476.field_5987;
		double e = this.field_6477.field_6035 - this.field_6476.field_6035;
		float f = class_3532.method_15368(d * d + e * e);
		if ((double)f >= 1.0E-4) {
			this.field_6476.field_5967 = this.field_6476.field_5967 + d / (double)f * 0.5 * 0.8F + this.field_6476.field_5967 * 0.2F;
			this.field_6476.field_6006 = this.field_6476.field_6006 + e / (double)f * 0.5 * 0.8F + this.field_6476.field_6006 * 0.2F;
		}

		this.field_6476.field_5984 = (double)this.field_6475;
	}
}
