package net.minecraft;

import javax.annotation.Nullable;

public class class_1838 {
	protected final class_1657 field_8942;
	protected final float field_8940;
	protected final float field_8939;
	protected final float field_8938;
	protected final class_2350 field_8943;
	protected final class_1937 field_8945;
	protected final class_1799 field_8941;
	protected final class_2338 field_8944;

	public class_1838(class_1657 arg, class_1799 arg2, class_2338 arg3, class_2350 arg4, float f, float g, float h) {
		this(arg.field_6002, arg, arg2, arg3, arg4, f, g, h);
	}

	protected class_1838(class_1937 arg, @Nullable class_1657 arg2, class_1799 arg3, class_2338 arg4, class_2350 arg5, float f, float g, float h) {
		this.field_8942 = arg2;
		this.field_8943 = arg5;
		this.field_8940 = f;
		this.field_8939 = g;
		this.field_8938 = h;
		this.field_8944 = arg4;
		this.field_8941 = arg3;
		this.field_8945 = arg;
	}

	public class_2338 method_8037() {
		return this.field_8944;
	}

	public class_1799 method_8041() {
		return this.field_8941;
	}

	@Nullable
	public class_1657 method_8036() {
		return this.field_8942;
	}

	public class_1937 method_8045() {
		return this.field_8945;
	}

	public class_2350 method_8038() {
		return this.field_8943;
	}

	public float method_8043() {
		return this.field_8940;
	}

	public float method_8039() {
		return this.field_8939;
	}

	public float method_8040() {
		return this.field_8938;
	}

	public class_2350 method_8042() {
		return this.field_8942 == null ? class_2350.field_11043 : this.field_8942.method_5735();
	}

	public boolean method_8046() {
		return this.field_8942 != null && this.field_8942.method_5715();
	}

	public float method_8044() {
		return this.field_8942 == null ? 0.0F : this.field_8942.field_6031;
	}
}
