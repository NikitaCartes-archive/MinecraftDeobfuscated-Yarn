package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2809 implements class_2596<class_2792> {
	private int field_12811;
	private short field_12809;
	private boolean field_12810;

	public class_2809() {
	}

	@Environment(EnvType.CLIENT)
	public class_2809(int i, short s, boolean bl) {
		this.field_12811 = i;
		this.field_12809 = s;
		this.field_12810 = bl;
	}

	public void method_12177(class_2792 arg) {
		arg.method_12079(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12811 = arg.readByte();
		this.field_12809 = arg.readShort();
		this.field_12810 = arg.readByte() != 0;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12811);
		arg.writeShort(this.field_12809);
		arg.writeByte(this.field_12810 ? 1 : 0);
	}

	public int method_12178() {
		return this.field_12811;
	}

	public short method_12176() {
		return this.field_12809;
	}
}
