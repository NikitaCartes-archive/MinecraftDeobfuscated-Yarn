package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_570 extends class_583<class_1577> {
	private static final float[] field_17131 = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
	private static final float[] field_17132 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
	private static final float[] field_17133 = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
	private static final float[] field_17134 = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
	private static final float[] field_17135 = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
	private static final float[] field_17136 = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
	private final class_630 field_3379;
	private final class_630 field_3381;
	private final class_630[] field_3380;
	private final class_630[] field_3378;

	public class_570() {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3380 = new class_630[12];
		this.field_3379 = new class_630(this);
		this.field_3379.method_2850(0, 0).method_2844(-6.0F, 10.0F, -8.0F, 12, 12, 16);
		this.field_3379.method_2850(0, 28).method_2844(-8.0F, 10.0F, -6.0F, 2, 12, 12);
		this.field_3379.method_2850(0, 28).method_2854(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
		this.field_3379.method_2850(16, 40).method_2844(-6.0F, 8.0F, -6.0F, 12, 2, 12);
		this.field_3379.method_2850(16, 40).method_2844(-6.0F, 22.0F, -6.0F, 12, 2, 12);

		for (int i = 0; i < this.field_3380.length; i++) {
			this.field_3380[i] = new class_630(this, 0, 0);
			this.field_3380[i].method_2844(-1.0F, -4.5F, -1.0F, 2, 9, 2);
			this.field_3379.method_2845(this.field_3380[i]);
		}

		this.field_3381 = new class_630(this, 8, 0);
		this.field_3381.method_2844(-1.0F, 15.0F, 0.0F, 2, 2, 1);
		this.field_3379.method_2845(this.field_3381);
		this.field_3378 = new class_630[3];
		this.field_3378[0] = new class_630(this, 40, 0);
		this.field_3378[0].method_2844(-2.0F, 14.0F, 7.0F, 4, 4, 8);
		this.field_3378[1] = new class_630(this, 0, 54);
		this.field_3378[1].method_2844(0.0F, 14.0F, 0.0F, 3, 3, 7);
		this.field_3378[2] = new class_630(this);
		this.field_3378[2].method_2850(41, 32).method_2844(0.0F, 14.0F, 0.0F, 2, 2, 6);
		this.field_3378[2].method_2850(25, 19).method_2844(1.0F, 10.5F, 3.0F, 1, 9, 9);
		this.field_3379.method_2845(this.field_3378[0]);
		this.field_3378[0].method_2845(this.field_3378[1]);
		this.field_3378[1].method_2845(this.field_3378[2]);
	}

	public void method_17082(class_1577 arg, float f, float g, float h, float i, float j, float k) {
		this.method_17083(arg, f, g, h, i, j, k);
		this.field_3379.method_2846(k);
	}

	public void method_17083(class_1577 arg, float f, float g, float h, float i, float j, float k) {
		float l = h - (float)arg.field_6012;
		this.field_3379.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3379.field_3654 = j * (float) (Math.PI / 180.0);
		float m = (1.0F - arg.method_7053(l)) * 0.55F;

		for (int n = 0; n < 12; n++) {
			this.field_3380[n].field_3654 = (float) Math.PI * field_17131[n];
			this.field_3380[n].field_3675 = (float) Math.PI * field_17132[n];
			this.field_3380[n].field_3674 = (float) Math.PI * field_17133[n];
			this.field_3380[n].field_3657 = field_17134[n] * (1.0F + class_3532.method_15362(h * 1.5F + (float)n) * 0.01F - m);
			this.field_3380[n].field_3656 = 16.0F + field_17135[n] * (1.0F + class_3532.method_15362(h * 1.5F + (float)n) * 0.01F - m);
			this.field_3380[n].field_3655 = field_17136[n] * (1.0F + class_3532.method_15362(h * 1.5F + (float)n) * 0.01F - m);
		}

		this.field_3381.field_3655 = -8.25F;
		class_1297 lv = class_310.method_1551().method_1560();
		if (arg.method_7063()) {
			lv = arg.method_7052();
		}

		if (lv != null) {
			class_243 lv2 = lv.method_5836(0.0F);
			class_243 lv3 = arg.method_5836(0.0F);
			double d = lv2.field_1351 - lv3.field_1351;
			if (d > 0.0) {
				this.field_3381.field_3656 = 0.0F;
			} else {
				this.field_3381.field_3656 = 1.0F;
			}

			class_243 lv4 = arg.method_5828(0.0F);
			lv4 = new class_243(lv4.field_1352, 0.0, lv4.field_1350);
			class_243 lv5 = new class_243(lv3.field_1352 - lv2.field_1352, 0.0, lv3.field_1350 - lv2.field_1350).method_1029().method_1024((float) (Math.PI / 2));
			double e = lv4.method_1026(lv5);
			this.field_3381.field_3657 = class_3532.method_15355((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.field_3381.field_3665 = true;
		float o = arg.method_7057(l);
		this.field_3378[0].field_3675 = class_3532.method_15374(o) * (float) Math.PI * 0.05F;
		this.field_3378[1].field_3675 = class_3532.method_15374(o) * (float) Math.PI * 0.1F;
		this.field_3378[1].field_3657 = -1.5F;
		this.field_3378[1].field_3656 = 0.5F;
		this.field_3378[1].field_3655 = 14.0F;
		this.field_3378[2].field_3675 = class_3532.method_15374(o) * (float) Math.PI * 0.15F;
		this.field_3378[2].field_3657 = 0.5F;
		this.field_3378[2].field_3656 = 0.5F;
		this.field_3378[2].field_3655 = 6.0F;
	}
}
