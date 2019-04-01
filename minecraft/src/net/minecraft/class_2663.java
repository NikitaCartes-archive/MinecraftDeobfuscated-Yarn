package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2663 implements class_2596<class_2602> {
	private int field_12175;
	private byte field_12174;

	public class_2663() {
	}

	public class_2663(class_1297 arg, byte b) {
		this.field_12175 = arg.method_5628();
		this.field_12174 = b;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12175 = arg.readInt();
		this.field_12174 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12175);
		arg.writeByte(this.field_12174);
	}

	public void method_11471(class_2602 arg) {
		arg.method_11148(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1297 method_11469(class_1937 arg) {
		return arg.method_8469(this.field_12175);
	}

	@Environment(EnvType.CLIENT)
	public byte method_11470() {
		return this.field_12174;
	}
}
