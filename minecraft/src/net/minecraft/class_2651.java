package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2651 implements class_2596<class_2602> {
	private int field_12150;
	private int field_12149;
	private int field_12148;

	public class_2651() {
	}

	public class_2651(int i, int j, int k) {
		this.field_12150 = i;
		this.field_12149 = j;
		this.field_12148 = k;
	}

	public void method_11447(class_2602 arg) {
		arg.method_11131(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12150 = arg.readUnsignedByte();
		this.field_12149 = arg.readShort();
		this.field_12148 = arg.readShort();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12150);
		arg.writeShort(this.field_12149);
		arg.writeShort(this.field_12148);
	}

	@Environment(EnvType.CLIENT)
	public int method_11448() {
		return this.field_12150;
	}

	@Environment(EnvType.CLIENT)
	public int method_11445() {
		return this.field_12149;
	}

	@Environment(EnvType.CLIENT)
	public int method_11446() {
		return this.field_12148;
	}
}
