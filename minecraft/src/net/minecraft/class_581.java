package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_581<T extends class_1297> extends class_583<T> {
	private final class_630 field_3433 = new class_630(this);

	public class_581() {
		this(0.0F);
	}

	public class_581(float f) {
		int i = 2;
		this.field_3433.method_2850(0, 0).method_2856(-4.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(0.0F, -4.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(0.0F, 0.0F, -4.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(0.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(2.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(0.0F, 2.0F, 0.0F, 2, 2, 2, f);
		this.field_3433.method_2850(0, 0).method_2856(0.0F, 0.0F, 2.0F, 2, 2, 2, f);
		this.field_3433.method_2851(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3433.method_2846(k);
	}
}
