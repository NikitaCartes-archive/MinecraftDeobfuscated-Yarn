package net.minecraft;

import java.util.List;

public class class_1700 extends class_1693 implements class_2615 {
	private boolean field_7749 = true;
	private int field_7748 = -1;
	private final class_2338 field_7750 = class_2338.field_10980;

	public class_1700(class_1299<? extends class_1700> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1700(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6058, d, e, f, arg);
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7677;
	}

	@Override
	public class_2680 method_7517() {
		return class_2246.field_10312.method_9564();
	}

	@Override
	public int method_7526() {
		return 1;
	}

	@Override
	public int method_5439() {
		return 5;
	}

	@Override
	public void method_7506(int i, int j, int k, boolean bl) {
		boolean bl2 = !bl;
		if (bl2 != this.method_7572()) {
			this.method_7570(bl2);
		}
	}

	public boolean method_7572() {
		return this.field_7749;
	}

	public void method_7570(boolean bl) {
		this.field_7749 = bl;
	}

	@Override
	public class_1937 method_10997() {
		return this.field_6002;
	}

	@Override
	public double method_11266() {
		return this.field_5987;
	}

	@Override
	public double method_11264() {
		return this.field_6010 + 0.5;
	}

	@Override
	public double method_11265() {
		return this.field_6035;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.field_6002.field_9236 && this.method_5805() && this.method_7572()) {
			class_2338 lv = new class_2338(this);
			if (lv.equals(this.field_7750)) {
				this.field_7748--;
			} else {
				this.method_7571(0);
			}

			if (!this.method_7573()) {
				this.method_7571(0);
				if (this.method_7574()) {
					this.method_7571(4);
					this.method_5431();
				}
			}
		}
	}

	public boolean method_7574() {
		if (class_2614.method_11241(this)) {
			return true;
		} else {
			List<class_1542> list = this.field_6002.method_8390(class_1542.class, this.method_5829().method_1009(0.25, 0.0, 0.25), class_1301.field_6154);
			if (!list.isEmpty()) {
				class_2614.method_11247(this, (class_1542)list.get(0));
			}

			return false;
		}
	}

	@Override
	public void method_7516(class_1282 arg) {
		super.method_7516(arg);
		if (this.field_6002.method_8450().method_8355("doEntityDrops")) {
			this.method_5706(class_2246.field_10312);
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("TransferCooldown", this.field_7748);
		arg.method_10556("Enabled", this.field_7749);
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7748 = arg.method_10550("TransferCooldown");
		this.field_7749 = arg.method_10545("Enabled") ? arg.method_10577("Enabled") : true;
	}

	public void method_7571(int i) {
		this.field_7748 = i;
	}

	public boolean method_7573() {
		return this.field_7748 > 0;
	}

	@Override
	public class_1703 method_17357(int i, class_1661 arg) {
		return new class_1722(i, arg, this);
	}
}
