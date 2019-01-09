package net.minecraft;

import java.io.IOException;

public class class_2886 implements class_2596<class_2792> {
	private class_1268 field_13136;

	public class_2886() {
	}

	public class_2886(class_1268 arg) {
		this.field_13136 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13136 = arg.method_10818(class_1268.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13136);
	}

	public void method_12550(class_2792 arg) {
		arg.method_12065(this);
	}

	public class_1268 method_12551() {
		return this.field_13136;
	}
}
