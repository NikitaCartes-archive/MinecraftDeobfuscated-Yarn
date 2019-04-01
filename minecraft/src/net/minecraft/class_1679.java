package net.minecraft;

public class class_1679 extends class_1665 {
	private int field_7636 = 200;

	public class_1679(class_1299<? extends class_1679> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1679(class_1937 arg, class_1309 arg2) {
		super(class_1299.field_6135, arg2, arg);
	}

	public class_1679(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6135, d, e, f, arg);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236 && !this.field_7588) {
			this.field_6002.method_8406(class_2398.field_11213, this.field_5987, this.field_6010, this.field_6035, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected class_1799 method_7445() {
		return new class_1799(class_1802.field_8236);
	}

	@Override
	protected void method_7450(class_1309 arg) {
		super.method_7450(arg);
		class_1293 lv = new class_1293(class_1294.field_5912, this.field_7636, 0);
		arg.method_6092(lv);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("Duration")) {
			this.field_7636 = arg.method_10550("Duration");
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Duration", this.field_7636);
	}
}
