package net.minecraft;

import java.io.IOException;

public class class_2645 implements class_2596<class_2602> {
	private int field_12137;

	public class_2645() {
	}

	public class_2645(int i) {
		this.field_12137 = i;
	}

	public void method_11427(class_2602 arg) {
		arg.method_11102(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12137 = arg.readUnsignedByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12137);
	}
}
