package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2693 implements class_2596<class_2602> {
	private class_2338 field_12325;

	public class_2693() {
	}

	public class_2693(class_2338 arg) {
		this.field_12325 = arg;
	}

	public void method_11676(class_2602 arg) {
		arg.method_11108(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12325 = arg.method_10811();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_12325);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11677() {
		return this.field_12325;
	}
}
