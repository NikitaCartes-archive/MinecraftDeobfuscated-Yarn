package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_621<T extends class_1528> extends class_583<T> {
	private final class_630[] field_3613;
	private final class_630[] field_3612;

	public class_621(float f) {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3613 = new class_630[3];
		this.field_3613[0] = new class_630(this, 0, 16);
		this.field_3613[0].method_2856(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
		this.field_3613[1] = new class_630(this).method_2853(this.field_17138, this.field_17139);
		this.field_3613[1].method_2851(-2.0F, 6.9F, -0.5F);
		this.field_3613[1].method_2850(0, 22).method_2856(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
		this.field_3613[1].method_2850(24, 22).method_2856(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
		this.field_3613[1].method_2850(24, 22).method_2856(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
		this.field_3613[1].method_2850(24, 22).method_2856(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
		this.field_3613[2] = new class_630(this, 12, 22);
		this.field_3613[2].method_2856(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
		this.field_3612 = new class_630[3];
		this.field_3612[0] = new class_630(this, 0, 0);
		this.field_3612[0].method_2856(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
		this.field_3612[1] = new class_630(this, 32, 0);
		this.field_3612[1].method_2856(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_3612[1].field_3657 = -8.0F;
		this.field_3612[1].field_3656 = 4.0F;
		this.field_3612[2] = new class_630(this, 32, 0);
		this.field_3612[2].method_2856(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_3612[2].field_3657 = 10.0F;
		this.field_3612[2].field_3656 = 4.0F;
	}

	public void method_17129(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17130(arg, f, g, h, i, j, k);

		for (class_630 lv : this.field_3612) {
			lv.method_2846(k);
		}

		for (class_630 lv : this.field_3613) {
			lv.method_2846(k);
		}
	}

	public void method_17130(T arg, float f, float g, float h, float i, float j, float k) {
		float l = class_3532.method_15362(h * 0.1F);
		this.field_3613[1].field_3654 = (0.065F + 0.05F * l) * (float) Math.PI;
		this.field_3613[2]
			.method_2851(
				-2.0F, 6.9F + class_3532.method_15362(this.field_3613[1].field_3654) * 10.0F, -0.5F + class_3532.method_15374(this.field_3613[1].field_3654) * 10.0F
			);
		this.field_3613[2].field_3654 = (0.265F + 0.1F * l) * (float) Math.PI;
		this.field_3612[0].field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3612[0].field_3654 = j * (float) (Math.PI / 180.0);
	}

	public void method_17128(T arg, float f, float g, float h) {
		for (int i = 1; i < 3; i++) {
			this.field_3612[i].field_3675 = (arg.method_6879(i - 1) - arg.field_6283) * (float) (Math.PI / 180.0);
			this.field_3612[i].field_3654 = arg.method_6887(i - 1) * (float) (Math.PI / 180.0);
		}
	}
}
