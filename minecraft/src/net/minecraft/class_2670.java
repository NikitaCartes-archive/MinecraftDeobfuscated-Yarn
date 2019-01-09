package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2670 implements class_2596<class_2602> {
	private long field_12211;

	public class_2670() {
	}

	public class_2670(long l) {
		this.field_12211 = l;
	}

	public void method_11518(class_2602 arg) {
		arg.method_11147(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12211 = arg.readLong();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeLong(this.field_12211);
	}

	@Environment(EnvType.CLIENT)
	public long method_11517() {
		return this.field_12211;
	}
}
