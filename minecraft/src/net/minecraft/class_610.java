package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_610<T extends class_1297> extends class_583<T> {
	private final class_630 field_3575;
	private final class_630[] field_3574 = new class_630[8];

	public class_610() {
		int i = -16;
		this.field_3575 = new class_630(this, 0, 0);
		this.field_3575.method_2844(-6.0F, -8.0F, -6.0F, 12, 16, 12);
		this.field_3575.field_3656 += 8.0F;

		for (int j = 0; j < this.field_3574.length; j++) {
			this.field_3574[j] = new class_630(this, 48, 0);
			double d = (double)j * Math.PI * 2.0 / (double)this.field_3574.length;
			float f = (float)Math.cos(d) * 5.0F;
			float g = (float)Math.sin(d) * 5.0F;
			this.field_3574[j].method_2844(-1.0F, 0.0F, -1.0F, 2, 18, 2);
			this.field_3574[j].field_3657 = f;
			this.field_3574[j].field_3655 = g;
			this.field_3574[j].field_3656 = 15.0F;
			d = (double)j * Math.PI * -2.0 / (double)this.field_3574.length + (Math.PI / 2);
			this.field_3574[j].field_3675 = (float)d;
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		for (class_630 lv : this.field_3574) {
			lv.field_3654 = h;
		}
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3575.method_2846(k);

		for (class_630 lv : this.field_3574) {
			lv.method_2846(k);
		}
	}
}
