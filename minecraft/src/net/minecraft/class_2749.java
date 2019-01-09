package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2749 implements class_2596<class_2602> {
	private float field_12584;
	private int field_12585;
	private float field_12583;

	public class_2749() {
	}

	public class_2749(float f, int i, float g) {
		this.field_12584 = f;
		this.field_12585 = i;
		this.field_12583 = g;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12584 = arg.readFloat();
		this.field_12585 = arg.method_10816();
		this.field_12583 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeFloat(this.field_12584);
		arg.method_10804(this.field_12585);
		arg.writeFloat(this.field_12583);
	}

	public void method_11832(class_2602 arg) {
		arg.method_11122(this);
	}

	@Environment(EnvType.CLIENT)
	public float method_11833() {
		return this.field_12584;
	}

	@Environment(EnvType.CLIENT)
	public int method_11831() {
		return this.field_12585;
	}

	@Environment(EnvType.CLIENT)
	public float method_11834() {
		return this.field_12583;
	}
}
