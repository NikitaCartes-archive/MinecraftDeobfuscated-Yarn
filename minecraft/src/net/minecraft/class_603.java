package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_603<T extends class_1297> extends class_583<T> {
	private final class_630 field_3556;

	public class_603() {
		this.field_17138 = 64;
		this.field_17139 = 32;
		this.field_3556 = new class_630(this);
		this.field_3556.method_2850(0, 0).method_2856(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
		this.field_3556.method_2850(0, 10).method_2856(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
		this.field_3556.method_2850(20, 0).method_2856(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
		this.field_3556.method_2851(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3556.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3556.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3556.field_3654 = j * (float) (Math.PI / 180.0);
	}
}
