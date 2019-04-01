package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_565<T extends class_1297> extends class_583<T> {
	private static final int[][] field_3366 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] field_3369 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private static final int field_3367 = field_3366.length;
	private final class_630[] field_3368 = new class_630[field_3367];

	public class_565() {
		float f = -3.5F;

		for (int i = 0; i < this.field_3368.length; i++) {
			this.field_3368[i] = new class_630(this, field_3369[i][0], field_3369[i][1]);
			this.field_3368[i].method_2844((float)field_3366[i][0] * -0.5F, 0.0F, (float)field_3366[i][2] * -0.5F, field_3366[i][0], field_3366[i][1], field_3366[i][2]);
			this.field_3368[i].method_2851(0.0F, (float)(24 - field_3366[i][1]), f);
			if (i < this.field_3368.length - 1) {
				f += (float)(field_3366[i][2] + field_3366[i + 1][2]) * 0.5F;
			}
		}
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);

		for (class_630 lv : this.field_3368) {
			lv.method_2846(k);
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3368.length; l++) {
			this.field_3368[l].field_3675 = class_3532.method_15362(h * 0.9F + (float)l * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.01F
				* (float)(1 + Math.abs(l - 2));
			this.field_3368[l].field_3657 = class_3532.method_15374(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(l - 2);
		}
	}
}
