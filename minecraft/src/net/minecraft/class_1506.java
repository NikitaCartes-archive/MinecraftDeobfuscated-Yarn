package net.minecraft;

import javax.annotation.Nullable;

public class class_1506 extends class_1496 {
	private final class_1505 field_7003 = new class_1505(this);
	private boolean field_7005;
	private int field_7004;

	public class_1506(class_1299<? extends class_1506> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(15.0);
		this.method_5996(class_1612.field_7357).method_6192(0.2F);
		this.method_5996(field_6974).method_6192(this.method_6774());
	}

	@Override
	protected void method_6764() {
	}

	@Override
	protected class_3414 method_5994() {
		super.method_5994();
		return this.method_5777(class_3486.field_15517) ? class_3417.field_14686 : class_3417.field_14984;
	}

	@Override
	protected class_3414 method_6002() {
		super.method_6002();
		return class_3417.field_14721;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		super.method_6011(arg);
		return class_3417.field_14855;
	}

	@Override
	protected class_3414 method_5737() {
		if (this.field_5952) {
			if (!this.method_5782()) {
				return class_3417.field_15182;
			}

			this.field_6975++;
			if (this.field_6975 > 5 && this.field_6975 % 3 == 0) {
				return class_3417.field_15108;
			}

			if (this.field_6975 <= 5) {
				return class_3417.field_15182;
			}
		}

		return class_3417.field_14617;
	}

	@Override
	protected void method_5734(float f) {
		if (this.field_5952) {
			super.method_5734(0.3F);
		} else {
			super.method_5734(Math.min(0.1F, f * 25.0F));
		}
	}

	@Override
	protected void method_6723() {
		if (this.method_5799()) {
			this.method_5783(class_3417.field_14901, 0.4F, 1.0F);
		} else {
			super.method_6723();
		}
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	public double method_5621() {
		return super.method_5621() - 0.1875;
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.method_6812() && this.field_7004++ >= 18000) {
			this.method_5650();
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("SkeletonTrap", this.method_6812());
		arg.method_10569("SkeletonTrapTime", this.field_7004);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6813(arg.method_10577("SkeletonTrap"));
		this.field_7004 = arg.method_10550("SkeletonTrapTime");
	}

	@Override
	public boolean method_5788() {
		return true;
	}

	@Override
	protected float method_6120() {
		return 0.96F;
	}

	public boolean method_6812() {
		return this.field_7005;
	}

	public void method_6813(boolean bl) {
		if (bl != this.field_7005) {
			this.field_7005 = bl;
			if (bl) {
				this.field_6201.method_6277(1, this.field_7003);
			} else {
				this.field_6201.method_6280(this.field_7003);
			}
		}
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return class_1299.field_6075.method_5883(this.field_6002);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() instanceof class_1826) {
			return super.method_5992(arg, arg2);
		} else if (!this.method_6727()) {
			return false;
		} else if (this.method_6109()) {
			return super.method_5992(arg, arg2);
		} else if (arg.method_5715()) {
			this.method_6722(arg);
			return true;
		} else if (this.method_5782()) {
			return super.method_5992(arg, arg2);
		} else {
			if (!lv.method_7960()) {
				if (lv.method_7909() == class_1802.field_8175 && !this.method_6725()) {
					this.method_6722(arg);
					return true;
				}

				if (lv.method_7920(arg, this, arg2)) {
					return true;
				}
			}

			this.method_6726(arg);
			return true;
		}
	}
}
