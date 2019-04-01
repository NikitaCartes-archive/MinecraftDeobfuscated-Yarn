package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_580<T extends class_1297> extends class_583<T> {
	private final class_630[] field_3432 = new class_630[7];

	public class_580() {
		this.field_3432[0] = new class_630(this, 0, 10);
		this.field_3432[1] = new class_630(this, 0, 0);
		this.field_3432[2] = new class_630(this, 0, 0);
		this.field_3432[3] = new class_630(this, 0, 0);
		this.field_3432[4] = new class_630(this, 0, 0);
		this.field_3432[5] = new class_630(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.field_3432[0].method_2856(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
		this.field_3432[0].method_2851(0.0F, 4.0F, 0.0F);
		this.field_3432[5].method_2856(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.field_3432[5].method_2851(0.0F, 4.0F, 0.0F);
		this.field_3432[1].method_2856(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[1].method_2851(-9.0F, 4.0F, 0.0F);
		this.field_3432[2].method_2856(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[2].method_2851(9.0F, 4.0F, 0.0F);
		this.field_3432[3].method_2856(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[3].method_2851(0.0F, 4.0F, -7.0F);
		this.field_3432[4].method_2856(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_3432[4].method_2851(0.0F, 4.0F, 7.0F);
		this.field_3432[0].field_3654 = (float) (Math.PI / 2);
		this.field_3432[1].field_3675 = (float) (Math.PI * 3.0 / 2.0);
		this.field_3432[2].field_3675 = (float) (Math.PI / 2);
		this.field_3432[3].field_3675 = (float) Math.PI;
		this.field_3432[5].field_3654 = (float) (-Math.PI / 2);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3432[5].field_3656 = 4.0F - h;

		for (int l = 0; l < 6; l++) {
			this.field_3432[l].method_2846(k);
		}
	}
}
