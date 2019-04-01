package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_555<T extends class_1297> extends class_583<T> {
	private final class_630[] field_3328 = new class_630[12];
	private final class_630 field_3329;

	public class_555() {
		for (int i = 0; i < this.field_3328.length; i++) {
			this.field_3328[i] = new class_630(this, 0, 16);
			this.field_3328[i].method_2844(0.0F, 0.0F, 0.0F, 2, 8, 2);
		}

		this.field_3329 = new class_630(this, 0, 0);
		this.field_3329.method_2844(-4.0F, -4.0F, -4.0F, 8, 8, 8);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3329.method_2846(k);

		for (class_630 lv : this.field_3328) {
			lv.method_2846(k);
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		float l = h * (float) Math.PI * -0.1F;

		for (int m = 0; m < 4; m++) {
			this.field_3328[m].field_3656 = -2.0F + class_3532.method_15362(((float)(m * 2) + h) * 0.25F);
			this.field_3328[m].field_3657 = class_3532.method_15362(l) * 9.0F;
			this.field_3328[m].field_3655 = class_3532.method_15374(l) * 9.0F;
			l++;
		}

		l = (float) (Math.PI / 4) + h * (float) Math.PI * 0.03F;

		for (int m = 4; m < 8; m++) {
			this.field_3328[m].field_3656 = 2.0F + class_3532.method_15362(((float)(m * 2) + h) * 0.25F);
			this.field_3328[m].field_3657 = class_3532.method_15362(l) * 7.0F;
			this.field_3328[m].field_3655 = class_3532.method_15374(l) * 7.0F;
			l++;
		}

		l = 0.47123894F + h * (float) Math.PI * -0.05F;

		for (int m = 8; m < 12; m++) {
			this.field_3328[m].field_3656 = 11.0F + class_3532.method_15362(((float)m * 1.5F + h) * 0.5F);
			this.field_3328[m].field_3657 = class_3532.method_15362(l) * 5.0F;
			this.field_3328[m].field_3655 = class_3532.method_15374(l) * 5.0F;
			l++;
		}

		this.field_3329.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3329.field_3654 = j * (float) (Math.PI / 180.0);
	}
}
