package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2623 implements class_2596<class_2602> {
	private class_2338 field_12044;
	private int field_12042;
	private int field_12041;
	private class_2248 field_12043;

	public class_2623() {
	}

	public class_2623(class_2338 arg, class_2248 arg2, int i, int j) {
		this.field_12044 = arg;
		this.field_12043 = arg2;
		this.field_12042 = i;
		this.field_12041 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12044 = arg.method_10811();
		this.field_12042 = arg.readUnsignedByte();
		this.field_12041 = arg.readUnsignedByte();
		this.field_12043 = class_2378.field_11146.method_10200(arg.method_10816());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_12044);
		arg.writeByte(this.field_12042);
		arg.writeByte(this.field_12041);
		arg.method_10804(class_2378.field_11146.method_10249(this.field_12043));
	}

	public void method_11297(class_2602 arg) {
		arg.method_11158(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11298() {
		return this.field_12044;
	}

	@Environment(EnvType.CLIENT)
	public int method_11294() {
		return this.field_12042;
	}

	@Environment(EnvType.CLIENT)
	public int method_11296() {
		return this.field_12041;
	}

	@Environment(EnvType.CLIENT)
	public class_2248 method_11295() {
		return this.field_12043;
	}
}
