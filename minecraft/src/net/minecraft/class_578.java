package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_578<T extends class_1492> extends class_597<T> {
	private final class_630 field_3430;
	private final class_630 field_3429;

	public class_578(float f) {
		super(15, f);
		this.field_17138 = 128;
		this.field_17139 = 64;
		this.field_3535 = new class_630(this, 0, 0);
		this.field_3535.method_2856(-2.0F, -14.0F, -10.0F, 4, 4, 9, f);
		this.field_3535.method_2851(0.0F, 7.0F, -6.0F);
		this.field_3535.method_2850(0, 14).method_2856(-4.0F, -16.0F, -6.0F, 8, 18, 6, f);
		this.field_3535.method_2850(17, 0).method_2856(-4.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.field_3535.method_2850(17, 0).method_2856(1.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.field_3538 = new class_630(this, 29, 0);
		this.field_3538.method_2856(-6.0F, -10.0F, -7.0F, 12, 18, 10, f);
		this.field_3538.method_2851(0.0F, 5.0F, 2.0F);
		this.field_3430 = new class_630(this, 45, 28);
		this.field_3430.method_2856(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3430.method_2851(-8.5F, 3.0F, 3.0F);
		this.field_3430.field_3675 = (float) (Math.PI / 2);
		this.field_3429 = new class_630(this, 45, 41);
		this.field_3429.method_2856(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3429.method_2851(5.5F, 3.0F, 3.0F);
		this.field_3429.field_3675 = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.field_3536 = new class_630(this, 29, 29);
		this.field_3536.method_2856(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3536.method_2851(-2.5F, 10.0F, 6.0F);
		this.field_3534 = new class_630(this, 29, 29);
		this.field_3534.method_2856(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3534.method_2851(2.5F, 10.0F, 6.0F);
		this.field_3533 = new class_630(this, 29, 29);
		this.field_3533.method_2856(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3533.method_2851(-2.5F, 10.0F, -4.0F);
		this.field_3539 = new class_630(this, 29, 29);
		this.field_3539.method_2856(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3539.method_2851(2.5F, 10.0F, -4.0F);
		this.field_3536.field_3657--;
		this.field_3534.field_3657++;
		this.field_3536.field_3655 += 0.0F;
		this.field_3534.field_3655 += 0.0F;
		this.field_3533.field_3657--;
		this.field_3539.field_3657++;
		this.field_3533.field_3655--;
		this.field_3539.field_3655--;
		this.field_3537 += 2.0F;
	}

	public void method_17100(T arg, float f, float g, float h, float i, float j, float k) {
		boolean bl = !arg.method_6109() && arg.method_6703();
		this.method_17080(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.7F;
			GlStateManager.scalef(0.71428573F, 0.64935064F, 0.7936508F);
			GlStateManager.translatef(0.0F, 21.0F * k, 0.22F);
			this.field_3535.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float n = 1.1F;
			GlStateManager.scalef(0.625F, 0.45454544F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
			this.field_3538.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.45454544F, 0.41322312F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
			this.field_3536.method_2846(k);
			this.field_3534.method_2846(k);
			this.field_3533.method_2846(k);
			this.field_3539.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3535.method_2846(k);
			this.field_3538.method_2846(k);
			this.field_3536.method_2846(k);
			this.field_3534.method_2846(k);
			this.field_3533.method_2846(k);
			this.field_3539.method_2846(k);
		}

		if (bl) {
			this.field_3430.method_2846(k);
			this.field_3429.method_2846(k);
		}
	}
}
