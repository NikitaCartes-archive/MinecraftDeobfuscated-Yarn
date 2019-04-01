package net.minecraft;

public class class_1576 extends class_1642 {
	public class_1576(class_1299<? extends class_1576> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return super.method_5979(arg, arg2) && (arg2 == class_3730.field_16469 || arg.method_8311(new class_2338(this)));
	}

	@Override
	protected boolean method_7216() {
		return false;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14680;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15196;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14892;
	}

	@Override
	protected class_3414 method_7207() {
		return class_3417.field_15046;
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		boolean bl = super.method_6121(arg);
		if (bl && this.method_6047().method_7960() && arg instanceof class_1309) {
			float f = this.field_6002.method_8404(new class_2338(this)).method_5457();
			((class_1309)arg).method_6092(new class_1293(class_1294.field_5903, 140 * (int)f));
		}

		return bl;
	}

	@Override
	protected boolean method_7209() {
		return true;
	}

	@Override
	protected void method_7218() {
		this.method_7200(class_1299.field_6051);
		this.field_6002.method_8444(null, 1041, new class_2338((int)this.field_5987, (int)this.field_6010, (int)this.field_6035), 0);
	}

	@Override
	protected class_1799 method_7215() {
		return class_1799.field_8037;
	}
}
