package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2759 implements class_2596<class_2602> {
	private class_2338 field_12615;

	public class_2759() {
	}

	public class_2759(class_2338 arg) {
		this.field_12615 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12615 = arg.method_10811();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_12615);
	}

	public void method_11869(class_2602 arg) {
		arg.method_11142(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11870() {
		return this.field_12615;
	}
}
