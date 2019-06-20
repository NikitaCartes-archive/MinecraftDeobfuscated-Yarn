package net.minecraft;

import java.io.IOException;

public class class_2842 implements class_2596<class_2792> {
	private boolean field_12949;
	private boolean field_12948;
	private boolean field_12947;
	private boolean field_12946;
	private float field_12945;
	private float field_12944;

	public class_2842() {
	}

	public class_2842(class_1656 arg) {
		this.method_12340(arg.field_7480);
		this.method_12343(arg.field_7479);
		this.method_12337(arg.field_7478);
		this.method_12342(arg.field_7477);
		this.method_12338(arg.method_7252());
		this.method_12341(arg.method_7253());
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		byte b = arg.readByte();
		this.method_12340((b & 1) > 0);
		this.method_12343((b & 2) > 0);
		this.method_12337((b & 4) > 0);
		this.method_12342((b & 8) > 0);
		this.method_12338(arg.readFloat());
		this.method_12341(arg.readFloat());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		byte b = 0;
		if (this.method_12344()) {
			b = (byte)(b | 1);
		}

		if (this.method_12346()) {
			b = (byte)(b | 2);
		}

		if (this.method_12347()) {
			b = (byte)(b | 4);
		}

		if (this.method_12345()) {
			b = (byte)(b | 8);
		}

		arg.writeByte(b);
		arg.writeFloat(this.field_12945);
		arg.writeFloat(this.field_12944);
	}

	public void method_12339(class_2792 arg) {
		arg.method_12083(this);
	}

	public boolean method_12344() {
		return this.field_12949;
	}

	public void method_12340(boolean bl) {
		this.field_12949 = bl;
	}

	public boolean method_12346() {
		return this.field_12948;
	}

	public void method_12343(boolean bl) {
		this.field_12948 = bl;
	}

	public boolean method_12347() {
		return this.field_12947;
	}

	public void method_12337(boolean bl) {
		this.field_12947 = bl;
	}

	public boolean method_12345() {
		return this.field_12946;
	}

	public void method_12342(boolean bl) {
		this.field_12946 = bl;
	}

	public void method_12338(float f) {
		this.field_12945 = f;
	}

	public void method_12341(float f) {
		this.field_12944 = f;
	}
}
