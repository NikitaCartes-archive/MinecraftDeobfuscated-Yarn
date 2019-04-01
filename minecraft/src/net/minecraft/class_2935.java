package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2935 implements class_2596<class_2933> {
	private long field_13292;

	public class_2935() {
	}

	@Environment(EnvType.CLIENT)
	public class_2935(long l) {
		this.field_13292 = l;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13292 = arg.readLong();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeLong(this.field_13292);
	}

	public void method_12699(class_2933 arg) {
		arg.method_12697(this);
	}

	public long method_12700() {
		return this.field_13292;
	}
}
