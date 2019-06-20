package net.minecraft;

import java.io.IOException;

public class class_4210 implements class_2596<class_2792> {
	private class_1267 field_18805;

	public class_4210() {
	}

	public class_4210(class_1267 arg) {
		this.field_18805 = arg;
	}

	public void method_19477(class_2792 arg) {
		arg.method_19475(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_18805 = class_1267.method_5462(arg.readUnsignedByte());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_18805.method_5461());
	}

	public class_1267 method_19478() {
		return this.field_18805;
	}
}
