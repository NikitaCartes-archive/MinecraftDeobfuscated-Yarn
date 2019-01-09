package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_579<T extends class_1297> extends class_583<T> {
	private final class_630 field_3431;

	public class_579() {
		this(0, 0, 32, 32);
	}

	public class_579(int i, int j, int k, int l) {
		this.field_17138 = k;
		this.field_17139 = l;
		this.field_3431 = new class_630(this, i, j);
		this.field_3431.method_2856(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_3431.method_2851(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3431.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3431.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3431.field_3654 = j * (float) (Math.PI / 180.0);
	}
}
