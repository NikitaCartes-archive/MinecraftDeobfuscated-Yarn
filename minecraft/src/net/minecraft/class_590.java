package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_590<T extends class_1456> extends class_597<T> {
	public class_590() {
		super(12, 0.0F);
		this.field_17138 = 128;
		this.field_17139 = 64;
		this.field_3535 = new class_630(this, 0, 0);
		this.field_3535.method_2856(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		this.field_3535.method_2851(0.0F, 10.0F, -16.0F);
		this.field_3535.method_2850(0, 44).method_2856(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		this.field_3535.method_2850(26, 0).method_2856(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		class_630 lv = this.field_3535.method_2850(26, 0);
		lv.field_3666 = true;
		lv.method_2856(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		this.field_3538 = new class_630(this);
		this.field_3538.method_2850(0, 19).method_2856(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		this.field_3538.method_2850(39, 0).method_2856(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		this.field_3538.method_2851(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.field_3536 = new class_630(this, 50, 22);
		this.field_3536.method_2856(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.field_3536.method_2851(-3.5F, 14.0F, 6.0F);
		this.field_3534 = new class_630(this, 50, 22);
		this.field_3534.method_2856(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.field_3534.method_2851(3.5F, 14.0F, 6.0F);
		this.field_3533 = new class_630(this, 50, 40);
		this.field_3533.method_2856(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.field_3533.method_2851(-2.5F, 14.0F, -7.0F);
		this.field_3539 = new class_630(this, 50, 40);
		this.field_3539.method_2856(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.field_3539.method_2851(2.5F, 14.0F, -7.0F);
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

	public void method_17113(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17114(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			this.field_3540 = 16.0F;
			this.field_3537 = 4.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.field_3535.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3538.method_2846(k);
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
	}

	public void method_17114(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		float l = h - (float)arg.field_6012;
		float m = arg.method_6601(l);
		m *= m;
		float n = 1.0F - m;
		this.field_3538.field_3654 = (float) (Math.PI / 2) - m * (float) Math.PI * 0.35F;
		this.field_3538.field_3656 = 9.0F * n + 11.0F * m;
		this.field_3533.field_3656 = 14.0F * n - 6.0F * m;
		this.field_3533.field_3655 = -8.0F * n - 4.0F * m;
		this.field_3533.field_3654 -= m * (float) Math.PI * 0.45F;
		this.field_3539.field_3656 = this.field_3533.field_3656;
		this.field_3539.field_3655 = this.field_3533.field_3655;
		this.field_3539.field_3654 -= m * (float) Math.PI * 0.45F;
		if (this.field_3448) {
			this.field_3535.field_3656 = 10.0F * n - 9.0F * m;
			this.field_3535.field_3655 = -16.0F * n - 7.0F * m;
		} else {
			this.field_3535.field_3656 = 10.0F * n - 14.0F * m;
			this.field_3535.field_3655 = -16.0F * n - 3.0F * m;
		}

		this.field_3535.field_3654 += m * (float) Math.PI * 0.15F;
	}
}
