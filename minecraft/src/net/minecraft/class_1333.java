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

	public void method_19615(class_243 arg) {
		this.method_20248(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public void method_6226(class_1297 arg, float f, float g) {
		this.method_6230(arg.field_5987, method_20249(arg), arg.field_6035, f, g);
	}

	public void method_20248(double d, double e, double f) {
		this.method_6230(d, e, f, (float)this.field_6361.method_20240(), (float)this.field_6361.method_5978());
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
		if (this.method_20433()) {
			this.field_6361.field_5965 = 0.0F;
		}

		if (this.field_6360) {
			this.field_6360 = false;
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.method_20251(), this.field_6359);
			this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, this.method_20250(), this.field_6358);
		} else {
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.field_6361.field_6283, 10.0F);
		}

		if (!this.field_6361.method_5942().method_6357()) {
			this.field_6361.field_6241 = class_3532.method_20306(this.field_6361.field_6241, this.field_6361.field_6283, (float)this.field_6361.method_5986());
		}
	}

	protected boolean method_20433() {
		return true;
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

	protected float method_20250() {
		double d = this.field_6364 - this.field_6361.field_5987;
		double e = this.field_6363 - (this.field_6361.field_6010 + (double)this.field_6361.method_5751());
		double f = this.field_6362 - this.field_6361.field_6035;
		double g = (double)class_3532.method_15368(d * d + f * f);
		return (float)(-(class_3532.method_15349(e, g) * 180.0F / (float)Math.PI));
	}

	protected float method_20251() {
		double d = this.field_6364 - this.field_6361.field_5987;
		double e = this.field_6362 - this.field_6361.field_6035;
		return (float)(class_3532.method_15349(e, d) * 180.0F / (float)Math.PI) - 90.0F;
	}

	protected float method_6229(float f, float g, float h) {
		float i = class_3532.method_15381(f, g);
		float j = class_3532.method_15363(i, -h, h);
		return f + j;
	}

	private static double method_20249(class_1297 arg) {
		return arg instanceof class_1309 ? arg.field_6010 + (double)arg.method_5751() : (arg.method_5829().field_1322 + arg.method_5829().field_1325) / 2.0;
	}
}
