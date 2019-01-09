package net.minecraft;

public class class_1403 extends class_1405 {
	private final class_1321 field_6654;
	private class_1309 field_6655;
	private int field_6653;

	public class_1403(class_1321 arg) {
		super(arg, false);
		this.field_6654 = arg;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6654.method_6181()) {
			return false;
		} else {
			class_1309 lv = this.field_6654.method_6177();
			if (lv == null) {
				return false;
			} else {
				this.field_6655 = lv.method_6065();
				int i = lv.method_6117();
				return i != this.field_6653 && this.method_6328(this.field_6655, false) && this.field_6654.method_6178(this.field_6655, lv);
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6660.method_5980(this.field_6655);
		class_1309 lv = this.field_6654.method_6177();
		if (lv != null) {
			this.field_6653 = lv.method_6117();
		}

		super.method_6269();
	}
}
