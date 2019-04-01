package net.minecraft;

import java.io.IOException;

public class class_2797 implements class_2596<class_2792> {
	private String field_12764;

	public class_2797() {
	}

	public class_2797(String string) {
		if (string.length() > 256) {
			string = string.substring(0, 256);
		}

		this.field_12764 = string;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12764 = arg.method_10800(256);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_12764);
	}

	public void method_12115(class_2792 arg) {
		arg.method_12048(this);
	}

	public String method_12114() {
		return this.field_12764;
	}
}
