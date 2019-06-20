package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_604<T extends class_1297> extends class_583<T> {
	private final class_630[] field_3560;
	private final class_630[] field_3557;
	private final float[] field_3561 = new float[7];
	private static final int[][] field_3558 = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] field_3559 = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public class_604() {
		this.field_3560 = new class_630[7];
		float f = -3.5F;

		for (int i = 0; i < this.field_3560.length; i++) {
			this.field_3560[i] = new class_630(this, field_3559[i][0], field_3559[i][1]);
			this.field_3560[i].method_2844((float)field_3558[i][0] * -0.5F, 0.0F, (float)field_3558[i][2] * -0.5F, field_3558[i][0], field_3558[i][1], field_3558[i][2]);
			this.field_3560[i].method_2851(0.0F, (float)(24 - field_3558[i][1]), f);
			this.field_3561[i] = f;
			if (i < this.field_3560.length - 1) {
				f += (float)(field_3558[i][2] + field_3558[i + 1][2]) * 0.5F;
			}
		}

		this.field_3557 = new class_630[3];
		this.field_3557[0] = new class_630(this, 20, 0);
		this.field_3557[0].method_2844(-5.0F, 0.0F, (float)field_3558[2][2] * -0.5F, 10, 8, field_3558[2][2]);
		this.field_3557[0].method_2851(0.0F, 16.0F, this.field_3561[2]);
		this.field_3557[1] = new class_630(this, 20, 11);
		this.field_3557[1].method_2844(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6, 4, field_3558[4][2]);
		this.field_3557[1].method_2851(0.0F, 20.0F, this.field_3561[4]);
		this.field_3557[2] = new class_630(this, 20, 18);
		this.field_3557[2].method_2844(-3.0F, 0.0F, (float)field_3558[4][2] * -0.5F, 6, 5, field_3558[1][2]);
		this.field_3557[2].method_2851(0.0F, 19.0F, this.field_3561[1]);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);

		for (class_630 lv : this.field_3560) {
			lv.method_2846(k);
		}

		for (class_630 lv : this.field_3557) {
			lv.method_2846(k);
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		for (int l = 0; l < this.field_3560.length; l++) {
			this.field_3560[l].field_3675 = class_3532.method_15362(h * 0.9F + (float)l * 0.15F * (float) Math.PI)
				* (float) Math.PI
				* 0.05F
				* (float)(1 + Math.abs(l - 2));
			this.field_3560[l].field_3657 = class_3532.method_15374(h * 0.9F + (float)l * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(l - 2);
		}

		this.field_3557[0].field_3675 = this.field_3560[2].field_3675;
		this.field_3557[1].field_3675 = this.field_3560[4].field_3675;
		this.field_3557[1].field_3657 = this.field_3560[4].field_3657;
		this.field_3557[2].field_3675 = this.field_3560[1].field_3675;
		this.field_3557[2].field_3657 = this.field_3560[1].field_3657;
	}
}
