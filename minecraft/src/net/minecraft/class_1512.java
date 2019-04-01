package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1512 implements class_1521 {
	protected final class_1510 field_7036;

	public class_1512(class_1510 arg) {
		this.field_7036 = arg;
	}

	@Override
	public boolean method_6848() {
		return false;
	}

	@Override
	public void method_6853() {
	}

	@Override
	public void method_6855() {
	}

	@Override
	public void method_6850(class_1511 arg, class_2338 arg2, class_1282 arg3, @Nullable class_1657 arg4) {
	}

	@Override
	public void method_6856() {
	}

	@Override
	public void method_6854() {
	}

	@Override
	public float method_6846() {
		return 0.6F;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return null;
	}

	@Override
	public float method_6852(class_1282 arg, float f) {
		return f;
	}

	@Override
	public float method_6847() {
		float f = class_3532.method_15368(class_1297.method_17996(this.field_7036.method_18798())) + 1.0F;
		float g = Math.min(f, 40.0F);
		return 0.7F / g / f;
	}
}
