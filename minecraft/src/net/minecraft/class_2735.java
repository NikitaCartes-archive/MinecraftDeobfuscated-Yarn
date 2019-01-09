package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2735 implements class_2596<class_2602> {
	private int field_12463;

	public class_2735() {
	}

	public class_2735(int i) {
		this.field_12463 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12463 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12463);
	}

	public void method_11802(class_2602 arg) {
		arg.method_11135(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11803() {
		return this.field_12463;
	}
}
