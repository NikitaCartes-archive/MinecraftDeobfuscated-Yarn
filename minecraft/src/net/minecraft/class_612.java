package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_612<T extends class_1297> extends class_583<T> {
	private final class_630 field_3589;
	private final class_630 field_3591;
	private final class_630 field_3590;
	private final class_630 field_3588;
	private final class_630 field_3587;

	public class_612() {
		this(0.0F);
	}

	public class_612(float f) {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 22;
		this.field_3589 = new class_630(this, 0, 0);
		this.field_3589.method_2856(-1.0F, -1.5F, -3.0F, 2, 3, 6, f);
		this.field_3589.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3591 = new class_630(this, 22, -6);
		this.field_3591.method_2856(0.0F, -1.5F, 0.0F, 0, 3, 6, f);
		this.field_3591.method_2851(0.0F, 22.0F, 3.0F);
		this.field_3590 = new class_630(this, 2, 16);
		this.field_3590.method_2856(-2.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_3590.method_2851(-1.0F, 22.5F, 0.0F);
		this.field_3590.field_3675 = (float) (Math.PI / 4);
		this.field_3588 = new class_630(this, 2, 12);
		this.field_3588.method_2856(0.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_3588.method_2851(1.0F, 22.5F, 0.0F);
		this.field_3588.field_3675 = (float) (-Math.PI / 4);
		this.field_3587 = new class_630(this, 10, -5);
		this.field_3587.method_2856(0.0F, -3.0F, 0.0F, 0, 3, 6, f);
		this.field_3587.method_2851(0.0F, 20.5F, -3.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3589.method_2846(k);
		this.field_3591.method_2846(k);
		this.field_3590.method_2846(k);
		this.field_3588.method_2846(k);
		this.field_3587.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!arg.method_5799()) {
			l = 1.5F;
		}

		this.field_3591.field_3675 = -l * 0.45F * class_3532.method_15374(0.6F * h);
	}
}
