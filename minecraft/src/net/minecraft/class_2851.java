package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2851 implements class_2596<class_2792> {
	private float field_12995;
	private float field_12994;
	private boolean field_12997;
	private boolean field_12996;

	public class_2851() {
	}

	@Environment(EnvType.CLIENT)
	public class_2851(float f, float g, boolean bl, boolean bl2) {
		this.field_12995 = f;
		this.field_12994 = g;
		this.field_12997 = bl;
		this.field_12996 = bl2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12995 = arg.readFloat();
		this.field_12994 = arg.readFloat();
		byte b = arg.readByte();
		this.field_12997 = (b & 1) > 0;
		this.field_12996 = (b & 2) > 0;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeFloat(this.field_12995);
		arg.writeFloat(this.field_12994);
		byte b = 0;
		if (this.field_12997) {
			b = (byte)(b | 1);
		}

		if (this.field_12996) {
			b = (byte)(b | 2);
		}

		arg.writeByte(b);
	}

	public void method_12369(class_2792 arg) {
		arg.method_12067(this);
	}

	public float method_12372() {
		return this.field_12995;
	}

	public float method_12373() {
		return this.field_12994;
	}

	public boolean method_12371() {
		return this.field_12997;
	}

	public boolean method_12370() {
		return this.field_12996;
	}
}
