package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2695 implements class_2596<class_2602> {
	private int field_12333;
	private class_2960 field_12332;

	public class_2695() {
	}

	public class_2695(int i, class_1860 arg) {
		this.field_12333 = i;
		this.field_12332 = arg.method_8114();
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_11684() {
		return this.field_12332;
	}

	@Environment(EnvType.CLIENT)
	public int method_11685() {
		return this.field_12333;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12333 = arg.readByte();
		this.field_12332 = arg.method_10810();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12333);
		arg.method_10812(this.field_12332);
	}

	public void method_11686(class_2602 arg) {
		arg.method_11090(this);
	}
}
