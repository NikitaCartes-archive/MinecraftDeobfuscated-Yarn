package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2696 implements class_2596<class_2602> {
	private boolean field_12339;
	private boolean field_12338;
	private boolean field_12337;
	private boolean field_12336;
	private float field_12335;
	private float field_12334;

	public class_2696() {
	}

	public class_2696(class_1656 arg) {
		this.method_11689(arg.field_7480);
		this.method_11694(arg.field_7479);
		this.method_11687(arg.field_7478);
		this.method_11693(arg.field_7477);
		this.method_11688(arg.method_7252());
		this.method_11692(arg.method_7253());
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		byte b = arg.readByte();
		this.method_11689((b & 1) > 0);
		this.method_11694((b & 2) > 0);
		this.method_11687((b & 4) > 0);
		this.method_11693((b & 8) > 0);
		this.method_11688(arg.readFloat());
		this.method_11692(arg.readFloat());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		byte b = 0;
		if (this.method_11695()) {
			b = (byte)(b | 1);
		}

		if (this.method_11698()) {
			b = (byte)(b | 2);
		}

		if (this.method_11699()) {
			b = (byte)(b | 4);
		}

		if (this.method_11696()) {
			b = (byte)(b | 8);
		}

		arg.writeByte(b);
		arg.writeFloat(this.field_12335);
		arg.writeFloat(this.field_12334);
	}

	public void method_11697(class_2602 arg) {
		arg.method_11154(this);
	}

	public boolean method_11695() {
		return this.field_12339;
	}

	public void method_11689(boolean bl) {
		this.field_12339 = bl;
	}

	public boolean method_11698() {
		return this.field_12338;
	}

	public void method_11694(boolean bl) {
		this.field_12338 = bl;
	}

	public boolean method_11699() {
		return this.field_12337;
	}

	public void method_11687(boolean bl) {
		this.field_12337 = bl;
	}

	public boolean method_11696() {
		return this.field_12336;
	}

	public void method_11693(boolean bl) {
		this.field_12336 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float method_11690() {
		return this.field_12335;
	}

	public void method_11688(float f) {
		this.field_12335 = f;
	}

	@Environment(EnvType.CLIENT)
	public float method_11691() {
		return this.field_12334;
	}

	public void method_11692(float f) {
		this.field_12334 = f;
	}
}
