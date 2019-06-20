package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2724 implements class_2596<class_2602> {
	private class_2874 field_12431;
	private class_1934 field_12434;
	private class_1942 field_12432;

	public class_2724() {
	}

	public class_2724(class_2874 arg, class_1942 arg2, class_1934 arg3) {
		this.field_12431 = arg;
		this.field_12434 = arg3;
		this.field_12432 = arg2;
	}

	public void method_11782(class_2602 arg) {
		arg.method_11117(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12431 = class_2874.method_12490(arg.readInt());
		this.field_12434 = class_1934.method_8384(arg.readUnsignedByte());
		this.field_12432 = class_1942.method_8639(arg.method_10800(16));
		if (this.field_12432 == null) {
			this.field_12432 = class_1942.field_9265;
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12431.method_12484());
		arg.writeByte(this.field_12434.method_8379());
		arg.method_10814(this.field_12432.method_8635());
	}

	@Environment(EnvType.CLIENT)
	public class_2874 method_11779() {
		return this.field_12431;
	}

	@Environment(EnvType.CLIENT)
	public class_1934 method_11780() {
		return this.field_12434;
	}

	@Environment(EnvType.CLIENT)
	public class_1942 method_11781() {
		return this.field_12432;
	}
}
