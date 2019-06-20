package net.minecraft;

public abstract class class_1307 extends class_1308 {
	protected class_1307(class_1299<? extends class_1307> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
	}

	@Override
	public void method_6091(class_243 arg) {
		if (this.method_5799()) {
			this.method_5724(0.02F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.8F));
		} else if (this.method_5771()) {
			this.method_5724(0.02F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021(0.5));
		} else {
			float f = 0.91F;
			if (this.field_5952) {
				f = this.field_6002.method_8320(new class_2338(this.field_5987, this.method_5829().field_1322 - 1.0, this.field_6035)).method_11614().method_9499() * 0.91F;
			}

			float g = 0.16277137F / (f * f * f);
			f = 0.91F;
			if (this.field_5952) {
				f = this.field_6002.method_8320(new class_2338(this.field_5987, this.method_5829().field_1322 - 1.0, this.field_6035)).method_11614().method_9499() * 0.91F;
			}

			this.method_5724(this.field_5952 ? 0.1F * g : 0.02F, arg);
			this.method_5784(class_1313.field_6308, this.method_18798());
			this.method_18799(this.method_18798().method_1021((double)f));
		}

		this.field_6211 = this.field_6225;
		double d = this.field_5987 - this.field_6014;
		double e = this.field_6035 - this.field_5969;
		float h = class_3532.method_15368(d * d + e * e) * 4.0F;
		if (h > 1.0F) {
			h = 1.0F;
		}

		this.field_6225 = this.field_6225 + (h - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public boolean method_6101() {
		return false;
	}
}
