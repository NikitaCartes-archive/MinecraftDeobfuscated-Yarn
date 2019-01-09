package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1701 extends class_1688 {
	private int field_7751 = -1;

	public class_1701(class_1937 arg) {
		super(class_1299.field_6053, arg);
	}

	public class_1701(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6053, arg, d, e, f);
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7675;
	}

	@Override
	public class_2680 method_7517() {
		return class_2246.field_10375.method_9564();
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_7751 > 0) {
			this.field_7751--;
			this.field_6002.method_8406(class_2398.field_11251, this.field_5987, this.field_6010 + 0.5, this.field_6035, 0.0, 0.0, 0.0);
		} else if (this.field_7751 == 0) {
			this.method_7576(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		}

		if (this.field_5976) {
			double d = this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006;
			if (d >= 0.01F) {
				this.method_7576(d);
			}
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		class_1297 lv = arg.method_5526();
		if (lv instanceof class_1665) {
			class_1665 lv2 = (class_1665)lv;
			if (lv2.method_5809()) {
				this.method_7576(lv2.field_5967 * lv2.field_5967 + lv2.field_5984 * lv2.field_5984 + lv2.field_6006 * lv2.field_6006);
			}
		}

		return super.method_5643(arg, f);
	}

	@Override
	public void method_7516(class_1282 arg) {
		double d = this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006;
		if (!arg.method_5534() && !arg.method_5535() && !(d >= 0.01F)) {
			super.method_7516(arg);
			if (!arg.method_5535() && this.field_6002.method_8450().method_8355("doEntityDrops")) {
				this.method_5706(class_2246.field_10375);
			}
		} else {
			if (this.field_7751 < 0) {
				this.method_7575();
				this.field_7751 = this.field_5974.nextInt(20) + this.field_5974.nextInt(20);
			}
		}
	}

	protected void method_7576(double d) {
		if (!this.field_6002.field_9236) {
			double e = Math.sqrt(d);
			if (e > 5.0) {
				e = 5.0;
			}

			this.field_6002.method_8437(this, this.field_5987, this.field_6010, this.field_6035, (float)(4.0 + this.field_5974.nextDouble() * 1.5 * e), true);
			this.method_5650();
		}
	}

	@Override
	public void method_5747(float f, float g) {
		if (f >= 3.0F) {
			float h = f / 10.0F;
			this.method_7576((double)(h * h));
		}

		super.method_5747(f, g);
	}

	@Override
	public void method_7506(int i, int j, int k, boolean bl) {
		if (bl && this.field_7751 < 0) {
			this.method_7575();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 10) {
			this.method_7575();
		} else {
			super.method_5711(b);
		}
	}

	public void method_7575() {
		this.field_7751 = 80;
		if (!this.field_6002.field_9236) {
			this.field_6002.method_8421(this, (byte)10);
			if (!this.method_5701()) {
				this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_15079, class_3419.field_15245, 1.0F, 1.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_7577() {
		return this.field_7751;
	}

	public boolean method_7578() {
		return this.field_7751 > -1;
	}

	@Override
	public float method_5774(class_1927 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, class_3610 arg5, float f) {
		return !this.method_7578() || !arg4.method_11602(class_3481.field_15463) && !arg2.method_8320(arg3.method_10084()).method_11602(class_3481.field_15463)
			? super.method_5774(arg, arg2, arg3, arg4, arg5, f)
			: 0.0F;
	}

	@Override
	public boolean method_5853(class_1927 arg, class_1922 arg2, class_2338 arg3, class_2680 arg4, float f) {
		return !this.method_7578() || !arg4.method_11602(class_3481.field_15463) && !arg2.method_8320(arg3.method_10084()).method_11602(class_3481.field_15463)
			? super.method_5853(arg, arg2, arg3, arg4, f)
			: false;
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("TNTFuse", 99)) {
			this.field_7751 = arg.method_10550("TNTFuse");
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("TNTFuse", this.field_7751);
	}
}
