package net.minecraft;

import java.io.IOException;

public class class_2856 implements class_2596<class_2792> {
	private class_2856.class_2857 field_13014;

	public class_2856() {
	}

	public class_2856(class_2856.class_2857 arg) {
		this.field_13014 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13014 = arg.method_10818(class_2856.class_2857.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13014);
	}

	public void method_12409(class_2792 arg) {
		arg.method_12081(this);
	}

	public static enum class_2857 {
		field_13017,
		field_13018,
		field_13015,
		field_13016;
	}
}
