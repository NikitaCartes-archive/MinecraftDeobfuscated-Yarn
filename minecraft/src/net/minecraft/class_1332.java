package net.minecraft;

public class class_1332 extends class_1333 {
	private final int field_6357;

	public class_1332(class_1308 arg, int i) {
		super(arg);
		this.field_6357 = i;
	}

	@Override
	public void method_6231() {
		if (this.field_6360) {
			this.field_6360 = false;
			double d = this.field_6364 - this.field_6361.field_5987;
			double e = this.field_6363 - (this.field_6361.field_6010 + (double)this.field_6361.method_5751());
			double f = this.field_6362 - this.field_6361.field_6035;
			double g = (double)class_3532.method_15368(d * d + f * f);
			float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F + 20.0F;
			float i = (float)(-(class_3532.method_15349(e, g) * 180.0F / (float)Math.PI)) + 10.0F;
			this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, i, this.field_6358);
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, h, this.field_6359);
		} else {
			if (this.field_6361.method_5942().method_6357()) {
				this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, 0.0F, 5.0F);
			}

			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.field_6361.field_6283, this.field_6359);
		}

		float j = class_3532.method_15393(this.field_6361.field_6241 - this.field_6361.field_6283);
		if (j < (float)(-this.field_6357)) {
			this.field_6361.field_6283 -= 4.0F;
		} else if (j > (float)this.field_6357) {
			this.field_6361.field_6283 += 4.0F;
		}
	}
}
