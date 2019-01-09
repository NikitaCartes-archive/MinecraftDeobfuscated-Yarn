package net.minecraft;

public abstract class class_1307 extends class_1308 {
	protected class_1307(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.method_5799()) {
			this.method_5724(f, g, h, 0.02F);
			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			this.field_5967 *= 0.8F;
			this.field_5984 *= 0.8F;
			this.field_6006 *= 0.8F;
		} else if (this.method_5771()) {
			this.method_5724(f, g, h, 0.02F);
			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			this.field_5967 *= 0.5;
			this.field_5984 *= 0.5;
			this.field_6006 *= 0.5;
		} else {
			float i = 0.91F;
			if (this.field_5952) {
				i = this.field_6002
						.method_8320(
							new class_2338(
								class_3532.method_15357(this.field_5987), class_3532.method_15357(this.method_5829().field_1322) - 1, class_3532.method_15357(this.field_6035)
							)
						)
						.method_11614()
						.method_9499()
					* 0.91F;
			}

			float j = 0.16277137F / (i * i * i);
			this.method_5724(f, g, h, this.field_5952 ? 0.1F * j : 0.02F);
			i = 0.91F;
			if (this.field_5952) {
				i = this.field_6002
						.method_8320(
							new class_2338(
								class_3532.method_15357(this.field_5987), class_3532.method_15357(this.method_5829().field_1322) - 1, class_3532.method_15357(this.field_6035)
							)
						)
						.method_11614()
						.method_9499()
					* 0.91F;
			}

			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			this.field_5967 *= (double)i;
			this.field_5984 *= (double)i;
			this.field_6006 *= (double)i;
		}

		this.field_6211 = this.field_6225;
		double d = this.field_5987 - this.field_6014;
		double e = this.field_6035 - this.field_5969;
		float k = class_3532.method_15368(d * d + e * e) * 4.0F;
		if (k > 1.0F) {
			k = 1.0F;
		}

		this.field_6225 = this.field_6225 + (k - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public boolean method_6101() {
		return false;
	}
}
