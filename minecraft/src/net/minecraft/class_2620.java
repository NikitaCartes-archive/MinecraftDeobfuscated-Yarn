package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2620 implements class_2596<class_2602> {
	private int field_12033;
	private class_2338 field_12034;
	private int field_12032;

	public class_2620() {
	}

	public class_2620(int i, class_2338 arg, int j) {
		this.field_12033 = i;
		this.field_12034 = arg;
		this.field_12032 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12033 = arg.method_10816();
		this.field_12034 = arg.method_10811();
		this.field_12032 = arg.readUnsignedByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12033);
		arg.method_10807(this.field_12034);
		arg.writeByte(this.field_12032);
	}

	public void method_11279(class_2602 arg) {
		arg.method_11116(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11280() {
		return this.field_12033;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11277() {
		return this.field_12034;
	}

	@Environment(EnvType.CLIENT)
	public int method_11278() {
		return this.field_12032;
	}
}
