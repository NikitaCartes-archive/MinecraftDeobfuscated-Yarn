package net.minecraft;

import javax.annotation.Nullable;

public class class_1541 extends class_1297 {
	private static final class_2940<Integer> field_7197 = class_2945.method_12791(class_1541.class, class_2943.field_13327);
	@Nullable
	private class_1309 field_7198;
	private int field_7196 = 80;

	public class_1541(class_1937 arg) {
		super(class_1299.field_6063, arg);
		this.field_6033 = true;
		this.field_5977 = true;
		this.method_5835(0.98F, 0.98F);
	}

	public class_1541(class_1937 arg, double d, double e, double f, @Nullable class_1309 arg2) {
		this(arg);
		this.method_5814(d, e, f);
		float g = (float)(Math.random() * (float) (Math.PI * 2));
		this.field_5967 = (double)(-((float)Math.sin((double)g)) * 0.02F);
		this.field_5984 = 0.2F;
		this.field_6006 = (double)(-((float)Math.cos((double)g)) * 0.02F);
		this.method_6967(80);
		this.field_6014 = d;
		this.field_6036 = e;
		this.field_5969 = f;
		this.field_7198 = arg2;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7197, 80);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public boolean method_5863() {
		return !this.field_5988;
	}

	@Override
	public void method_5773() {
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		if (!this.method_5740()) {
			this.field_5984 -= 0.04F;
		}

		this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
		this.field_5967 *= 0.98F;
		this.field_5984 *= 0.98F;
		this.field_6006 *= 0.98F;
		if (this.field_5952) {
			this.field_5967 *= 0.7F;
			this.field_6006 *= 0.7F;
			this.field_5984 *= -0.5;
		}

		this.field_7196--;
		if (this.field_7196 <= 0) {
			this.method_5650();
			if (!this.field_6002.field_9236) {
				this.method_6971();
			}
		} else {
			this.method_5713();
			this.field_6002.method_8406(class_2398.field_11251, this.field_5987, this.field_6010 + 0.5, this.field_6035, 0.0, 0.0, 0.0);
		}
	}

	private void method_6971() {
		float f = 4.0F;
		this.field_6002.method_8437(this, this.field_5987, this.field_6010 + (double)(this.field_6019 / 16.0F), this.field_6035, 4.0F, true);
	}

	@Override
	protected void method_5652(class_2487 arg) {
		arg.method_10575("Fuse", (short)this.method_6968());
	}

	@Override
	protected void method_5749(class_2487 arg) {
		this.method_6967(arg.method_10568("Fuse"));
	}

	@Nullable
	public class_1309 method_6970() {
		return this.field_7198;
	}

	@Override
	public float method_5751() {
		return 0.0F;
	}

	public void method_6967(int i) {
		this.field_6011.method_12778(field_7197, i);
		this.field_7196 = i;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7197.equals(arg)) {
			this.field_7196 = this.method_6969();
		}
	}

	public int method_6969() {
		return this.field_6011.method_12789(field_7197);
	}

	public int method_6968() {
		return this.field_7196;
	}
}
