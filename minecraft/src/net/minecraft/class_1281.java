package net.minecraft;

import javax.annotation.Nullable;

public class class_1281 {
	private final class_1282 field_5837;
	private final int field_5836;
	private final float field_5835;
	private final float field_5834;
	private final String field_5838;
	private final float field_5833;

	public class_1281(class_1282 arg, int i, float f, float g, String string, float h) {
		this.field_5837 = arg;
		this.field_5836 = i;
		this.field_5835 = g;
		this.field_5834 = f;
		this.field_5838 = string;
		this.field_5833 = h;
	}

	public class_1282 method_5499() {
		return this.field_5837;
	}

	public float method_5503() {
		return this.field_5835;
	}

	public boolean method_5502() {
		return this.field_5837.method_5529() instanceof class_1309;
	}

	@Nullable
	public String method_5500() {
		return this.field_5838;
	}

	@Nullable
	public class_2561 method_5498() {
		return this.method_5499().method_5529() == null ? null : this.method_5499().method_5529().method_5476();
	}

	public float method_5501() {
		return this.field_5837 == class_1282.field_5849 ? Float.MAX_VALUE : this.field_5833;
	}
}
