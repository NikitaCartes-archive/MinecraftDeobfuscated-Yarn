package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_574<T extends class_1439> extends class_583<T> {
	private final class_630 field_3415;
	private final class_630 field_3413;
	public final class_630 field_3414;
	private final class_630 field_3412;
	private final class_630 field_3411;
	private final class_630 field_3416;

	public class_574() {
		this(0.0F);
	}

	public class_574(float f) {
		this(f, -7.0F);
	}

	public class_574(float f, float g) {
		int i = 128;
		int j = 128;
		this.field_3415 = new class_630(this).method_2853(128, 128);
		this.field_3415.method_2851(0.0F, 0.0F + g, -2.0F);
		this.field_3415.method_2850(0, 0).method_2856(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
		this.field_3415.method_2850(24, 0).method_2856(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
		this.field_3413 = new class_630(this).method_2853(128, 128);
		this.field_3413.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3413.method_2850(0, 40).method_2856(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
		this.field_3413.method_2850(0, 70).method_2856(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
		this.field_3414 = new class_630(this).method_2853(128, 128);
		this.field_3414.method_2851(0.0F, -7.0F, 0.0F);
		this.field_3414.method_2850(60, 21).method_2856(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_3412 = new class_630(this).method_2853(128, 128);
		this.field_3412.method_2851(0.0F, -7.0F, 0.0F);
		this.field_3412.method_2850(60, 58).method_2856(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_3411 = new class_630(this, 0, 22).method_2853(128, 128);
		this.field_3411.method_2851(-4.0F, 18.0F + g, 0.0F);
		this.field_3411.method_2850(37, 0).method_2856(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
		this.field_3416 = new class_630(this, 0, 22).method_2853(128, 128);
		this.field_3416.field_3666 = true;
		this.field_3416.method_2850(60, 0).method_2851(5.0F, 18.0F + g, 0.0F);
		this.field_3416.method_2856(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
	}

	public void method_17096(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17097(arg, f, g, h, i, j, k);
		this.field_3415.method_2846(k);
		this.field_3413.method_2846(k);
		this.field_3411.method_2846(k);
		this.field_3416.method_2846(k);
		this.field_3414.method_2846(k);
		this.field_3412.method_2846(k);
	}

	public void method_17097(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3415.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3415.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3411.field_3654 = -1.5F * this.method_2810(f, 13.0F) * g;
		this.field_3416.field_3654 = 1.5F * this.method_2810(f, 13.0F) * g;
		this.field_3411.field_3675 = 0.0F;
		this.field_3416.field_3675 = 0.0F;
	}

	public void method_17095(T arg, float f, float g, float h) {
		int i = arg.method_6501();
		if (i > 0) {
			this.field_3414.field_3654 = -2.0F + 1.5F * this.method_2810((float)i - h, 10.0F);
			this.field_3412.field_3654 = -2.0F + 1.5F * this.method_2810((float)i - h, 10.0F);
		} else {
			int j = arg.method_6502();
			if (j > 0) {
				this.field_3414.field_3654 = -0.8F + 0.025F * this.method_2810((float)j, 70.0F);
				this.field_3412.field_3654 = 0.0F;
			} else {
				this.field_3414.field_3654 = (-0.2F + 1.5F * this.method_2810(f, 13.0F)) * g;
				this.field_3412.field_3654 = (-0.2F - 1.5F * this.method_2810(f, 13.0F)) * g;
			}
		}
	}

	private float method_2810(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}

	public class_630 method_2809() {
		return this.field_3414;
	}
}
