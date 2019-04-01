package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2661 implements class_2596<class_2602> {
	private class_2561 field_12173;

	public class_2661() {
	}

	public class_2661(class_2561 arg) {
		this.field_12173 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12173 = arg.method_10808();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10805(this.field_12173);
	}

	public void method_11467(class_2602 arg) {
		arg.method_11083(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11468() {
		return this.field_12173;
	}
}
