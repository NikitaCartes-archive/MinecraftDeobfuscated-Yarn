package net.minecraft;

import javax.annotation.Nullable;

public class class_1507 extends class_1496 {
	public class_1507(class_1299<? extends class_1507> arg, class_1937 arg2) {
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
	public class_1310 method_6046() {
		return class_1310.field_6289;
	}

	@Override
	protected class_3414 method_5994() {
		super.method_5994();
		return class_3417.field_15154;
	}

	@Override
	protected class_3414 method_6002() {
		super.method_6002();
		return class_3417.field_14543;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		super.method_6011(arg);
		return class_3417.field_15179;
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return class_1299.field_6048.method_5883(this.field_6002);
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
				if (!this.method_6725() && lv.method_7909() == class_1802.field_8175) {
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

	@Override
	protected void method_6764() {
	}
}
