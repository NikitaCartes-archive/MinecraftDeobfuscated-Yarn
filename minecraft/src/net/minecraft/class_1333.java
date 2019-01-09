package net.minecraft;

public class class_1333 {
	protected final class_1308 field_6361;
	protected float field_6359;
	protected float field_6358;
	protected boolean field_6360;
	protected double field_6364;
	protected double field_6363;
	protected double field_6362;

	public class_1333(class_1308 arg) {
		this.field_6361 = arg;
	}

	public void method_6226(class_1297 arg, float f, float g) {
		this.field_6364 = arg.field_5987;
		if (arg instanceof class_1309) {
			this.field_6363 = arg.field_6010 + (double)arg.method_5751();
		} else {
			this.field_6363 = (arg.method_5829().field_1322 + arg.method_5829().field_1325) / 2.0;
		}

		this.field_6362 = arg.field_6035;
		this.field_6359 = f;
		this.field_6358 = g;
		this.field_6360 = true;
	}

	public void method_6230(double d, double e, double f, float g, float h) {
		this.field_6364 = d;
		this.field_6363 = e;
		this.field_6362 = f;
		this.field_6359 = g;
		this.field_6358 = h;
		this.field_6360 = true;
	}

	public void method_6231() {
		this.field_6361.field_5965 = 0.0F;
		if (this.field_6360) {
			this.field_6360 = false;
			double d = this.field_6364 - this.field_6361.field_5987;
			double e = this.field_6363 - (this.field_6361.field_6010 + (double)this.field_6361.method_5751());
			double f = this.field_6362 - this.field_6361.field_6035;
			double g = (double)class_3532.method_15368(d * d + f * f);
			float h = (float)(class_3532.method_15349(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			float i = (float)(-(class_3532.method_15349(e, g) * 180.0F / (float)Math.PI));
			this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, i, this.field_6358);
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, h, this.field_6359);
		} else {
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.field_6361.field_6283, 10.0F);
		}

		float j = class_3532.method_15393(this.field_6361.field_6241 - this.field_6361.field_6283);
		if (!this.field_6361.method_5942().method_6357()) {
			if (j < -75.0F) {
				this.field_6361.field_6241 = this.field_6361.field_6283 - 75.0F;
			}

			if (j > 75.0F) {
				this.field_6361.field_6241 = this.field_6361.field_6283 + 75.0F;
			}
		}
	}

	protected float method_6229(float f, float g, float h) {
		float i = class_3532.method_15393(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	public boolean method_6232() {
		return this.field_6360;
	}

	public double method_6225() {
		return this.field_6364;
	}

	public double method_6227() {
		return this.field_6363;
	}

	public double method_6228() {
		return this.field_6362;
	}
}
