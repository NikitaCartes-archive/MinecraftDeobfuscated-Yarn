package net.minecraft;

import java.io.IOException;

public class class_2855 implements class_2596<class_2792> {
	private String field_13013;

	public class_2855() {
	}

	public class_2855(String string) {
		this.field_13013 = string;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13013 = arg.method_10800(32767);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10814(this.field_13013);
	}

	public void method_12408(class_2792 arg) {
		arg.method_12060(this);
	}

	public String method_12407() {
		return this.field_13013;
	}
}
