package net.minecraft;

public class class_4048 {
	public final float field_18067;
	public final float field_18068;
	public final boolean field_18069;

	public class_4048(float f, float g, boolean bl) {
		this.field_18067 = f;
		this.field_18068 = g;
		this.field_18069 = bl;
	}

	public class_4048 method_18383(float f) {
		return !this.field_18069 && f != 1.0F ? method_18384(this.field_18067 * f, this.field_18068 * f) : this;
	}

	public static class_4048 method_18384(float f, float g) {
		return new class_4048(f, g, false);
	}

	public static class_4048 method_18385(float f, float g) {
		return new class_4048(f, g, true);
	}
}
