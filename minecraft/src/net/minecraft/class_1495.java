package net.minecraft;

public class class_1495 extends class_1492 {
	public class_1495(class_1937 arg) {
		super(class_1299.field_6067, arg);
	}

	@Override
	protected class_3414 method_5994() {
		super.method_5994();
		return class_3417.field_15094;
	}

	@Override
	protected class_3414 method_6002() {
		super.method_6002();
		return class_3417.field_14827;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		super.method_6011(arg);
		return class_3417.field_14781;
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		if (arg == this) {
			return false;
		} else {
			return !(arg instanceof class_1495) && !(arg instanceof class_1498) ? false : this.method_6734() && ((class_1496)arg).method_6734();
		}
	}

	@Override
	public class_1296 method_5613(class_1296 arg) {
		class_1496 lv = (class_1496)(arg instanceof class_1498 ? new class_1500(this.field_6002) : new class_1495(this.field_6002));
		this.method_6743(arg, lv);
		return lv;
	}
}
