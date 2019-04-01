package net.minecraft;

import java.util.EnumSet;

public class class_1406 extends class_1405 {
	private final class_1321 field_6666;
	private class_1309 field_6667;
	private int field_6665;

	public class_1406(class_1321 arg) {
		super(arg, false);
		this.field_6666 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6666.method_6181()) {
			return false;
		} else {
			class_1309 lv = this.field_6666.method_6177();
			if (lv == null) {
				return false;
			} else {
				this.field_6667 = lv.method_6052();
				int i = lv.method_6083();
				return i != this.field_6665 && this.method_6328(this.field_6667, class_4051.field_18092) && this.field_6666.method_6178(this.field_6667, lv);
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6660.method_5980(this.field_6667);
		class_1309 lv = this.field_6666.method_6177();
		if (lv != null) {
			this.field_6665 = lv.method_6083();
		}

		super.method_6269();
	}
}
