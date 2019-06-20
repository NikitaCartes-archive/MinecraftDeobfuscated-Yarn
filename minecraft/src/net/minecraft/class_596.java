package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_596<T extends class_1463> extends class_583<T> {
	private final class_630 field_3525 = new class_630(this, 26, 24);
	private final class_630 field_3532;
	private final class_630 field_3526;
	private final class_630 field_3522;
	private final class_630 field_3528;
	private final class_630 field_3527;
	private final class_630 field_3521;
	private final class_630 field_3529;
	private final class_630 field_3523;
	private final class_630 field_3520;
	private final class_630 field_3524;
	private final class_630 field_3530;
	private float field_3531;

	public class_596() {
		this.field_3525.method_2844(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_3525.method_2851(3.0F, 17.5F, 3.7F);
		this.field_3525.field_3666 = true;
		this.method_2827(this.field_3525, 0.0F, 0.0F, 0.0F);
		this.field_3532 = new class_630(this, 8, 24);
		this.field_3532.method_2844(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_3532.method_2851(-3.0F, 17.5F, 3.7F);
		this.field_3532.field_3666 = true;
		this.method_2827(this.field_3532, 0.0F, 0.0F, 0.0F);
		this.field_3526 = new class_630(this, 30, 15);
		this.field_3526.method_2844(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_3526.method_2851(3.0F, 17.5F, 3.7F);
		this.field_3526.field_3666 = true;
		this.method_2827(this.field_3526, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3522 = new class_630(this, 16, 15);
		this.field_3522.method_2844(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_3522.method_2851(-3.0F, 17.5F, 3.7F);
		this.field_3522.field_3666 = true;
		this.method_2827(this.field_3522, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3528 = new class_630(this, 0, 0);
		this.field_3528.method_2844(-3.0F, -2.0F, -10.0F, 6, 5, 10);
		this.field_3528.method_2851(0.0F, 19.0F, 8.0F);
		this.field_3528.field_3666 = true;
		this.method_2827(this.field_3528, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_3527 = new class_630(this, 8, 15);
		this.field_3527.method_2844(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_3527.method_2851(3.0F, 17.0F, -1.0F);
		this.field_3527.field_3666 = true;
		this.method_2827(this.field_3527, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_3521 = new class_630(this, 0, 15);
		this.field_3521.method_2844(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_3521.method_2851(-3.0F, 17.0F, -1.0F);
		this.field_3521.field_3666 = true;
		this.method_2827(this.field_3521, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_3529 = new class_630(this, 32, 0);
		this.field_3529.method_2844(-2.5F, -4.0F, -5.0F, 5, 4, 5);
		this.field_3529.method_2851(0.0F, 16.0F, -1.0F);
		this.field_3529.field_3666 = true;
		this.method_2827(this.field_3529, 0.0F, 0.0F, 0.0F);
		this.field_3523 = new class_630(this, 52, 0);
		this.field_3523.method_2844(-2.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_3523.method_2851(0.0F, 16.0F, -1.0F);
		this.field_3523.field_3666 = true;
		this.method_2827(this.field_3523, 0.0F, (float) (-Math.PI / 12), 0.0F);
		this.field_3520 = new class_630(this, 58, 0);
		this.field_3520.method_2844(0.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_3520.method_2851(0.0F, 16.0F, -1.0F);
		this.field_3520.field_3666 = true;
		this.method_2827(this.field_3520, 0.0F, (float) (Math.PI / 12), 0.0F);
		this.field_3524 = new class_630(this, 52, 6);
		this.field_3524.method_2844(-1.5F, -1.5F, 0.0F, 3, 3, 2);
		this.field_3524.method_2851(0.0F, 20.0F, 7.0F);
		this.field_3524.field_3666 = true;
		this.method_2827(this.field_3524, -0.3490659F, 0.0F, 0.0F);
		this.field_3530 = new class_630(this, 32, 9);
		this.field_3530.method_2844(-0.5F, -2.5F, -5.5F, 1, 1, 1);
		this.field_3530.method_2851(0.0F, 16.0F, -1.0F);
		this.field_3530.field_3666 = true;
		this.method_2827(this.field_3530, 0.0F, 0.0F, 0.0F);
	}

	private void method_2827(class_630 arg, float f, float g, float h) {
		arg.field_3654 = f;
		arg.field_3675 = g;
		arg.field_3674 = h;
	}

	public void method_17116(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17117(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 1.5F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.56666666F, 0.56666666F, 0.56666666F);
			GlStateManager.translatef(0.0F, 22.0F * k, 2.0F * k);
			this.field_3529.method_2846(k);
			this.field_3520.method_2846(k);
			this.field_3523.method_2846(k);
			this.field_3530.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.4F, 0.4F, 0.4F);
			GlStateManager.translatef(0.0F, 36.0F * k, 0.0F);
			this.field_3525.method_2846(k);
			this.field_3532.method_2846(k);
			this.field_3526.method_2846(k);
			this.field_3522.method_2846(k);
			this.field_3528.method_2846(k);
			this.field_3527.method_2846(k);
			this.field_3521.method_2846(k);
			this.field_3524.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6F, 0.6F, 0.6F);
			GlStateManager.translatef(0.0F, 16.0F * k, 0.0F);
			this.field_3525.method_2846(k);
			this.field_3532.method_2846(k);
			this.field_3526.method_2846(k);
			this.field_3522.method_2846(k);
			this.field_3528.method_2846(k);
			this.field_3527.method_2846(k);
			this.field_3521.method_2846(k);
			this.field_3529.method_2846(k);
			this.field_3523.method_2846(k);
			this.field_3520.method_2846(k);
			this.field_3524.method_2846(k);
			this.field_3530.method_2846(k);
			GlStateManager.popMatrix();
		}
	}

	public void method_17117(T arg, float f, float g, float h, float i, float j, float k) {
		float l = h - (float)arg.field_6012;
		this.field_3530.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3529.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3523.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3520.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3530.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3529.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3523.field_3675 = this.field_3530.field_3675 - (float) (Math.PI / 12);
		this.field_3520.field_3675 = this.field_3530.field_3675 + (float) (Math.PI / 12);
		this.field_3531 = class_3532.method_15374(arg.method_6605(l) * (float) Math.PI);
		this.field_3526.field_3654 = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.field_3522.field_3654 = (this.field_3531 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.field_3525.field_3654 = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.field_3532.field_3654 = this.field_3531 * 50.0F * (float) (Math.PI / 180.0);
		this.field_3527.field_3654 = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
		this.field_3521.field_3654 = (this.field_3531 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
	}

	public void method_17115(T arg, float f, float g, float h) {
		super.method_2816(arg, f, g, h);
		this.field_3531 = class_3532.method_15374(arg.method_6605(h) * (float) Math.PI);
	}
}
