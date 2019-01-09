package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2790 implements class_2596<class_2602> {
	private class_3505 field_12757;

	public class_2790() {
	}

	public class_2790(class_3505 arg) {
		this.field_12757 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12757 = class_3505.method_15200(arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		this.field_12757.method_15204(arg);
	}

	public void method_12001(class_2602 arg) {
		arg.method_11126(this);
	}

	@Environment(EnvType.CLIENT)
	public class_3505 method_12000() {
		return this.field_12757;
	}
}
