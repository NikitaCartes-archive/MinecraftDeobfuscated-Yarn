package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2653 implements class_2596<class_2602> {
	private int field_12152;
	private int field_12151;
	private class_1799 field_12153 = class_1799.field_8037;

	public class_2653() {
	}

	public class_2653(int i, int j, class_1799 arg) {
		this.field_12152 = i;
		this.field_12151 = j;
		this.field_12153 = arg.method_7972();
	}

	public void method_11451(class_2602 arg) {
		arg.method_11109(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12152 = arg.readByte();
		this.field_12151 = arg.readShort();
		this.field_12153 = arg.method_10819();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12152);
		arg.writeShort(this.field_12151);
		arg.method_10793(this.field_12153);
	}

	@Environment(EnvType.CLIENT)
	public int method_11452() {
		return this.field_12152;
	}

	@Environment(EnvType.CLIENT)
	public int method_11450() {
		return this.field_12151;
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_11449() {
		return this.field_12153;
	}
}
