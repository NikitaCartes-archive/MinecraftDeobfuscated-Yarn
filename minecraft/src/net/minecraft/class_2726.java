package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2726 implements class_2596<class_2602> {
	private int field_12437;
	private byte field_12436;

	public class_2726() {
	}

	public class_2726(class_1297 arg, byte b) {
		this.field_12437 = arg.method_5628();
		this.field_12436 = b;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12437 = arg.method_10816();
		this.field_12436 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12437);
		arg.writeByte(this.field_12436);
	}

	public void method_11788(class_2602 arg) {
		arg.method_11139(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1297 method_11786(class_1937 arg) {
		return arg.method_8469(this.field_12437);
	}

	@Environment(EnvType.CLIENT)
	public byte method_11787() {
		return this.field_12436;
	}
}
