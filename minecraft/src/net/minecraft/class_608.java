package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_608<T extends class_1297> extends class_583<T> {
	private final class_630 field_3567;
	private final class_630 field_3569;
	private final class_630 field_3568;
	private final class_630 field_3566;
	private final class_630 field_3565;

	public class_608() {
		float f = 4.0F;
		float g = 0.0F;
		this.field_3568 = new class_630(this, 0, 0).method_2853(64, 64);
		this.field_3568.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
		this.field_3568.method_2851(0.0F, 4.0F, 0.0F);
		this.field_3566 = new class_630(this, 32, 0).method_2853(64, 64);
		this.field_3566.method_2856(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_3566.method_2851(0.0F, 6.0F, 0.0F);
		this.field_3565 = new class_630(this, 32, 0).method_2853(64, 64);
		this.field_3565.method_2856(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_3565.method_2851(0.0F, 6.0F, 0.0F);
		this.field_3567 = new class_630(this, 0, 16).method_2853(64, 64);
		this.field_3567.method_2856(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
		this.field_3567.method_2851(0.0F, 13.0F, 0.0F);
		this.field_3569 = new class_630(this, 0, 36).method_2853(64, 64);
		this.field_3569.method_2856(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
		this.field_3569.method_2851(0.0F, 24.0F, 0.0F);
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3568.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3568.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3567.field_3675 = i * (float) (Math.PI / 180.0) * 0.25F;
		float l = class_3532.method_15374(this.field_3567.field_3675);
		float m = class_3532.method_15362(this.field_3567.field_3675);
		this.field_3566.field_3674 = 1.0F;
		this.field_3565.field_3674 = -1.0F;
		this.field_3566.field_3675 = 0.0F + this.field_3567.field_3675;
		this.field_3565.field_3675 = (float) Math.PI + this.field_3567.field_3675;
		this.field_3566.field_3657 = m * 5.0F;
		this.field_3566.field_3655 = -l * 5.0F;
		this.field_3565.field_3657 = -m * 5.0F;
		this.field_3565.field_3655 = l * 5.0F;
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		this.field_3567.method_2846(k);
		this.field_3569.method_2846(k);
		this.field_3568.method_2846(k);
		this.field_3566.method_2846(k);
		this.field_3565.method_2846(k);
	}

	public class_630 method_2834() {
		return this.field_3568;
	}
}
