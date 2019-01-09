package net.minecraft;

import java.io.IOException;

public class class_2879 implements class_2596<class_2792> {
	private class_1268 field_13102;

	public class_2879() {
	}

	public class_2879(class_1268 arg) {
		this.field_13102 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13102 = arg.method_10818(class_1268.class);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13102);
	}

	public void method_12511(class_2792 arg) {
		arg.method_12052(this);
	}

	public class_1268 method_12512() {
		return this.field_13102;
	}
}
