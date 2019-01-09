package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2724 implements class_2596<class_2602> {
	private class_2874 field_12431;
	private class_1267 field_12433;
	private class_1934 field_12434;
	private class_1942 field_12432;

	public class_2724() {
	}

	public class_2724(class_2874 arg, class_1267 arg2, class_1942 arg3, class_1934 arg4) {
		this.field_12431 = arg;
		this.field_12433 = arg2;
		this.field_12434 = arg4;
		this.field_12432 = arg3;
	}

	public void method_11782(class_2602 arg) {
		arg.method_11117(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12431 = class_2874.method_12490(arg.readInt());
		this.field_12433 = class_1267.method_5462(arg.readUnsignedByte());
		this.field_12434 = class_1934.method_8384(arg.readUnsignedByte());
		this.field_12432 = class_1942.method_8639(arg.method_10800(16));
		if (this.field_12432 == null) {
			this.field_12432 = class_1942.field_9265;
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12431.method_12484());
		arg.writeByte(this.field_12433.method_5461());
		arg.writeByte(this.field_12434.method_8379());
		arg.method_10814(this.field_12432.method_8635());
	}

	@Environment(EnvType.CLIENT)
	public class_2874 method_11779() {
		return this.field_12431;
	}

	@Environment(EnvType.CLIENT)
	public class_1267 method_11783() {
		return this.field_12433;
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
