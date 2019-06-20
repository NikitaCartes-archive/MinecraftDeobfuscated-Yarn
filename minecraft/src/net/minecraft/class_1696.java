package net.minecraft;

public class class_1696 extends class_1688 {
	private static final class_2940<Boolean> field_7740 = class_2945.method_12791(class_1696.class, class_2943.field_13323);
	private int field_7739;
	public double field_7737;
	public double field_7736;
	private static final class_1856 field_7738 = class_1856.method_8091(class_1802.field_8713, class_1802.field_8665);

	public class_1696(class_1299<? extends class_1696> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1696(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6080, arg, d, e, f);
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7679;
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7740, false);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7739 > 0) {
			this.field_7739--;
		}

		if (this.field_7739 <= 0) {
			this.field_7737 = 0.0;
			this.field_7736 = 0.0;
		}

		this.method_7564(this.field_7739 > 0);
		if (this.method_7565() && this.field_5974.nextInt(4) == 0) {
			this.field_6002.method_8406(class_2398.field_11237, this.field_5987, this.field_6010 + 0.8, this.field_6035, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double method_7504() {
		return 0.2;
	}

	@Override
	public void method_7516(class_1282 arg) {
		super.method_7516(arg);
		if (!arg.method_5535() && this.field_6002.method_8450().method_8355(class_1928.field_19393)) {
			this.method_5706(class_2246.field_10181);
		}
	}

	@Override
	protected void method_7513(class_2338 arg, class_2680 arg2) {
		super.method_7513(arg, arg2);
		double d = this.field_7737 * this.field_7737 + this.field_7736 * this.field_7736;
		class_243 lv = this.method_18798();
		if (d > 1.0E-4 && method_17996(lv) > 0.001) {
			d = (double)class_3532.method_15368(d);
			this.field_7737 /= d;
			this.field_7736 /= d;
			if (this.field_7737 * lv.field_1352 + this.field_7736 * lv.field_1350 < 0.0) {
				this.field_7737 = 0.0;
				this.field_7736 = 0.0;
			} else {
				double e = d / this.method_7504();
				this.field_7737 *= e;
				this.field_7736 *= e;
			}
		}
	}

	@Override
	protected void method_7525() {
		double d = this.field_7737 * this.field_7737 + this.field_7736 * this.field_7736;
		if (d > 1.0E-7) {
			d = (double)class_3532.method_15368(d);
			this.field_7737 /= d;
			this.field_7736 /= d;
			this.method_18799(this.method_18798().method_18805(0.8, 0.0, 0.8).method_1031(this.field_7737, 0.0, this.field_7736));
		} else {
			this.method_18799(this.method_18798().method_18805(0.98, 0.0, 0.98));
		}

		super.method_7525();
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (field_7738.method_8093(lv) && this.field_7739 + 3600 <= 32000) {
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			this.field_7739 += 3600;
		}

		this.field_7737 = this.field_5987 - arg.field_5987;
		this.field_7736 = this.field_6035 - arg.field_6035;
		return true;
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10549("PushX", this.field_7737);
		arg.method_10549("PushZ", this.field_7736);
		arg.method_10575("Fuel", (short)this.field_7739);
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7737 = arg.method_10574("PushX");
		this.field_7736 = arg.method_10574("PushZ");
		this.field_7739 = arg.method_10568("Fuel");
	}

	protected boolean method_7565() {
		return this.field_6011.method_12789(field_7740);
	}

	protected void method_7564(boolean bl) {
		this.field_6011.method_12778(field_7740, bl);
	}

	@Override
	public class_2680 method_7517() {
		return class_2246.field_10181
			.method_9564()
			.method_11657(class_3865.field_11104, class_2350.field_11043)
			.method_11657(class_3865.field_11105, Boolean.valueOf(this.method_7565()));
	}
}
