package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2909 implements class_2596<class_2896> {
	private class_2561 field_13243;

	public class_2909() {
	}

	public class_2909(class_2561 arg) {
		this.field_13243 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13243 = class_2561.class_2562.method_10873(arg.method_10800(32767));
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10805(this.field_13243);
	}

	public void method_12637(class_2896 arg) {
		arg.method_12584(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_12638() {
		return this.field_13243;
	}
}
