package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_561<T extends class_1297> extends class_583<T> {
	private final class_630 field_3353;
	private final class_630 field_3355;
	private final class_630 field_3354;
	private final class_630 field_3352;
	private final class_630 field_3351;
	private final class_630 field_3356;
	private final class_630 field_3350;

	public class_561() {
		this.field_17138 = 32;
		this.field_17139 = 32;
		int i = 22;
		this.field_3353 = new class_630(this, 0, 0);
		this.field_3353.method_2844(-1.0F, -2.0F, 0.0F, 2, 4, 7);
		this.field_3353.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3354 = new class_630(this, 11, 0);
		this.field_3354.method_2844(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_3354.method_2851(0.0F, 22.0F, 0.0F);
		this.field_3352 = new class_630(this, 0, 0);
		this.field_3352.method_2844(-1.0F, -2.0F, -1.0F, 2, 3, 1);
		this.field_3352.method_2851(0.0F, 22.0F, -3.0F);
		this.field_3351 = new class_630(this, 22, 1);
		this.field_3351.method_2844(-2.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_3351.method_2851(-1.0F, 23.0F, 0.0F);
		this.field_3351.field_3674 = (float) (-Math.PI / 4);
		this.field_3356 = new class_630(this, 22, 4);
		this.field_3356.method_2844(0.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_3356.method_2851(1.0F, 23.0F, 0.0F);
		this.field_3356.field_3674 = (float) (Math.PI / 4);
		this.field_3350 = new class_630(this, 22, 3);
		this.field_3350.method_2844(0.0F, -2.0F, 0.0F, 0, 4, 4);
		this.field_3350.method_2851(0.0F, 22.0F, 7.0F);
		this.field_3355 = new class_630(this, 20, -6);
		this.field_3355.method_2844(0.0F, -1.0F, -1.0F, 0, 1, 6);
		this.field_3355.method_2851(0.0F, 20.0F, 0.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3353.method_2846(k);
		this.field_3354.method_2846(k);
		this.field_3352.method_2846(k);
		this.field_3351.method_2846(k);
		this.field_3356.method_2846(k);
		this.field_3350.method_2846(k);
		this.field_3355.method_2846(k);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		if (!arg.method_5799()) {
			l = 1.5F;
		}

		this.field_3350.field_3675 = -l * 0.45F * class_3532.method_15374(0.6F * h);
	}
}
