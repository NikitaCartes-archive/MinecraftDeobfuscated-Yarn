package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_558<T extends class_1297> extends class_583<T> {
	private final class_630 field_3344;
	private final class_630 field_3346;
	private final class_630 field_3345;
	private final class_630 field_3343;
	private final class_630 field_3341;
	private final class_630 field_3347;
	private final class_630 field_3340;
	private final class_630 field_3342;

	public class_558() {
		int i = 16;
		this.field_3344 = new class_630(this, 0, 0);
		this.field_3344.method_2856(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.field_3344.method_2851(0.0F, 15.0F, -4.0F);
		this.field_3340 = new class_630(this, 14, 0);
		this.field_3340.method_2856(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.field_3340.method_2851(0.0F, 15.0F, -4.0F);
		this.field_3342 = new class_630(this, 14, 4);
		this.field_3342.method_2856(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.field_3342.method_2851(0.0F, 15.0F, -4.0F);
		this.field_3346 = new class_630(this, 0, 9);
		this.field_3346.method_2856(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_3346.method_2851(0.0F, 16.0F, 0.0F);
		this.field_3345 = new class_630(this, 26, 0);
		this.field_3345.method_2844(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_3345.method_2851(-2.0F, 19.0F, 1.0F);
		this.field_3343 = new class_630(this, 26, 0);
		this.field_3343.method_2844(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_3343.method_2851(1.0F, 19.0F, 1.0F);
		this.field_3341 = new class_630(this, 24, 13);
		this.field_3341.method_2844(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_3341.method_2851(-4.0F, 13.0F, 0.0F);
		this.field_3347 = new class_630(this, 24, 13);
		this.field_3347.method_2844(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_3347.method_2851(4.0F, 13.0F, 0.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.field_3344.method_2846(k);
			this.field_3340.method_2846(k);
			this.field_3342.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3346.method_2846(k);
			this.field_3345.method_2846(k);
			this.field_3343.method_2846(k);
			this.field_3341.method_2846(k);
			this.field_3347.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3344.method_2846(k);
			this.field_3340.method_2846(k);
			this.field_3342.method_2846(k);
			this.field_3346.method_2846(k);
			this.field_3345.method_2846(k);
			this.field_3343.method_2846(k);
			this.field_3341.method_2846(k);
			this.field_3347.method_2846(k);
		}
	}

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3344.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3344.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3340.field_3654 = this.field_3344.field_3654;
		this.field_3340.field_3675 = this.field_3344.field_3675;
		this.field_3342.field_3654 = this.field_3344.field_3654;
		this.field_3342.field_3675 = this.field_3344.field_3675;
		this.field_3346.field_3654 = (float) (Math.PI / 2);
		this.field_3345.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		this.field_3343.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3341.field_3674 = h;
		this.field_3347.field_3674 = -h;
	}
}
