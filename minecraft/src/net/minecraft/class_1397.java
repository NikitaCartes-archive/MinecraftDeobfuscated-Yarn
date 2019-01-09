package net.minecraft;

public class class_1397 extends class_1405 {
	private final class_1439 field_6629;
	private class_1309 field_6630;

	public class_1397(class_1439 arg) {
		super(arg, false, true);
		this.field_6629 = arg;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		class_1415 lv = this.field_6629.method_6500();
		if (lv == null) {
			return false;
		} else {
			this.field_6630 = lv.method_6385(this.field_6629);
			if (this.field_6630 instanceof class_1548) {
				return false;
			} else if (this.method_6328(this.field_6630, false)) {
				return true;
			} else if (this.field_6660.method_6051().nextInt(20) == 0) {
				this.field_6630 = lv.method_6391(this.field_6629);
				return this.method_6328(this.field_6630, false);
			} else {
				return false;
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6629.method_5980(this.field_6630);
		super.method_6269();
	}
}
