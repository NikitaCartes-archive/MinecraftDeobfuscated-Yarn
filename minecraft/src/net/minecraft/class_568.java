package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_568<T extends class_1297> extends class_583<T> {
	private final class_630 field_3374 = new class_630(this, 0, 0);
	private final class_630 field_3376;
	private final class_630 field_3375;

	public class_568() {
		this.field_3374.method_2851(-5.0F, 22.0F, -5.0F);
		this.field_3374.method_2844(0.0F, 0.0F, 0.0F, 10, 12, 10);
		this.field_3376 = new class_630(this, 40, 0);
		this.field_3376.method_2851(1.5F, 22.0F, -4.0F);
		this.field_3376.method_2844(0.0F, 0.0F, 0.0F, 4, 14, 8);
		this.field_3375 = new class_630(this, 40, 0);
		this.field_3375.method_2851(-1.5F, 22.0F, 4.0F);
		this.field_3375.method_2844(0.0F, 0.0F, 0.0F, 4, 14, 8);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		float l = f * 2.0F;
		if (l > 1.0F) {
			l = 1.0F;
		}

		l = 1.0F - l * l * l;
		this.field_3376.field_3674 = (float) Math.PI - l * 0.35F * (float) Math.PI;
		this.field_3375.field_3674 = (float) Math.PI + l * 0.35F * (float) Math.PI;
		this.field_3375.field_3675 = (float) Math.PI;
		float m = (f + class_3532.method_15374(f * 2.7F)) * 0.6F * 12.0F;
		this.field_3376.field_3656 = 24.0F - m;
		this.field_3375.field_3656 = this.field_3376.field_3656;
		this.field_3374.field_3656 = this.field_3376.field_3656;
		this.field_3374.method_2846(k);
		this.field_3376.method_2846(k);
		this.field_3375.method_2846(k);
	}
}
