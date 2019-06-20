package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2616 implements class_2596<class_2602> {
	private int field_12029;
	private int field_12028;

	public class_2616() {
	}

	public class_2616(class_1297 arg, int i) {
		this.field_12029 = arg.method_5628();
		this.field_12028 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12029 = arg.method_10816();
		this.field_12028 = arg.readUnsignedByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12029);
		arg.writeByte(this.field_12028);
	}

	public void method_11268(class_2602 arg) {
		arg.method_11160(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11269() {
		return this.field_12029;
	}

	@Environment(EnvType.CLIENT)
	public int method_11267() {
		return this.field_12028;
	}
}
