package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_614<T extends class_1481> extends class_597<T> {
	private final class_630 field_3594;

	public class_614(float f) {
		super(12, f);
		this.field_17138 = 128;
		this.field_17139 = 64;
		this.field_3535 = new class_630(this, 3, 0);
		this.field_3535.method_2856(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
		this.field_3535.method_2851(0.0F, 19.0F, -10.0F);
		this.field_3538 = new class_630(this);
		this.field_3538.method_2850(7, 37).method_2856(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
		this.field_3538.method_2850(31, 1).method_2856(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
		this.field_3538.method_2851(0.0F, 11.0F, -10.0F);
		this.field_3594 = new class_630(this);
		this.field_3594.method_2850(70, 33).method_2856(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
		this.field_3594.method_2851(0.0F, 11.0F, -10.0F);
		int i = 1;
		this.field_3536 = new class_630(this, 1, 23);
		this.field_3536.method_2856(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.field_3536.method_2851(-3.5F, 22.0F, 11.0F);
		this.field_3534 = new class_630(this, 1, 12);
		this.field_3534.method_2856(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.field_3534.method_2851(3.5F, 22.0F, 11.0F);
		this.field_3533 = new class_630(this, 27, 30);
		this.field_3533.method_2856(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.field_3533.method_2851(-5.0F, 21.0F, -4.0F);
		this.field_3539 = new class_630(this, 27, 24);
		this.field_3539.method_2856(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.field_3539.method_2851(5.0F, 21.0F, -4.0F);
	}

	public void method_17124(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17125(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 6.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
			GlStateManager.translatef(0.0F, 120.0F * k, 0.0F);
			this.field_3535.method_2846(k);
			this.field_3538.method_2846(k);
			this.field_3536.method_2846(k);
			this.field_3534.method_2846(k);
			this.field_3533.method_2846(k);
			this.field_3539.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			if (arg.method_6679()) {
				GlStateManager.translatef(0.0F, -0.08F, 0.0F);
			}

			this.field_3535.method_2846(k);
			this.field_3538.method_2846(k);
			GlStateManager.pushMatrix();
			this.field_3536.method_2846(k);
			this.field_3534.method_2846(k);
			GlStateManager.popMatrix();
			this.field_3533.method_2846(k);
			this.field_3539.method_2846(k);
			if (arg.method_6679()) {
				this.field_3594.method_2846(k);
			}

			GlStateManager.popMatrix();
		}
	}

	public void method_17125(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		this.field_3536.field_3654 = class_3532.method_15362(f * 0.6662F * 0.6F) * 0.5F * g;
		this.field_3534.field_3654 = class_3532.method_15362(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.field_3533.field_3674 = class_3532.method_15362(f * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * g;
		this.field_3539.field_3674 = class_3532.method_15362(f * 0.6662F * 0.6F) * 0.5F * g;
		this.field_3533.field_3654 = 0.0F;
		this.field_3539.field_3654 = 0.0F;
		this.field_3533.field_3675 = 0.0F;
		this.field_3539.field_3675 = 0.0F;
		this.field_3536.field_3675 = 0.0F;
		this.field_3534.field_3675 = 0.0F;
		this.field_3594.field_3654 = (float) (Math.PI / 2);
		if (!arg.method_5799() && arg.field_5952) {
			float l = arg.method_6695() ? 4.0F : 1.0F;
			float m = arg.method_6695() ? 2.0F : 1.0F;
			float n = 5.0F;
			this.field_3533.field_3675 = class_3532.method_15362(l * f * 5.0F + (float) Math.PI) * 8.0F * g * m;
			this.field_3533.field_3674 = 0.0F;
			this.field_3539.field_3675 = class_3532.method_15362(l * f * 5.0F) * 8.0F * g * m;
			this.field_3539.field_3674 = 0.0F;
			this.field_3536.field_3675 = class_3532.method_15362(f * 5.0F + (float) Math.PI) * 3.0F * g;
			this.field_3536.field_3654 = 0.0F;
			this.field_3534.field_3675 = class_3532.method_15362(f * 5.0F) * 3.0F * g;
			this.field_3534.field_3654 = 0.0F;
		}
	}
}
