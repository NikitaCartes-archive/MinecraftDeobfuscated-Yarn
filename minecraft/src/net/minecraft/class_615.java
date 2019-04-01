package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_615<T extends class_1297> extends class_583<T> {
	private final class_630 field_3597;
	private final class_630 field_3599;
	private final class_630 field_3598;
	private final class_630 field_3596;
	private final class_630 field_3595;
	private final class_630 field_3600;

	public class_615() {
		this(0.0F);
	}

	public class_615(float f) {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 19;
		this.field_3597 = new class_630(this, 0, 20);
		this.field_3597.method_2856(-1.0F, -3.0F, -3.0F, 2, 6, 6, f);
		this.field_3597.method_2851(0.0F, 19.0F, 0.0F);
		this.field_3599 = new class_630(this, 21, 16);
		this.field_3599.method_2856(0.0F, -3.0F, 0.0F, 0, 6, 5, f);
		this.field_3599.method_2851(0.0F, 19.0F, 3.0F);
		this.field_3598 = new class_630(this, 2, 16);
		this.field_3598.method_2856(-2.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_3598.method_2851(-1.0F, 20.0F, 0.0F);
		this.field_3598.field_3675 = (float) (Math.PI / 4);
		this.field_3596 = new class_630(this, 2, 12);
		this.field_3596.method_2856(0.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_3596.method_2851(1.0F, 20.0F, 0.0F);
		this.field_3596.field_3675 = (float) (-Math.PI / 4);
		this.field_3595 = new class_630(this, 20, 11);
		this.field_3595.method_2856(0.0F, -4.0F, 0.0F, 0, 4, 6, f);
		this.field_3595.method_2851(0.0F, 16.0F, -3.0F);
		this.field_3600 = new class_630(this, 20, 21);
		this.field_3600.method_2856(0.0F, 0.0F, 0.0F, 0, 4, 6, f);
		this.field_3600.method_2851(0.0F, 22.0F, -3.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3597.method_2846(k);
		this.field_3599.method_2846(k);
		this.field_3598.method_2846(k);
		this.field_3596.method_2846(k);
		this.field_3595.method_2846(k);
		this.field_3600.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!arg.method_5799()) {
			l = 1.5F;
		}

		this.field_3599.field_3675 = -l * 0.45F * class_3532.method_15374(0.6F * h);
	}
}
