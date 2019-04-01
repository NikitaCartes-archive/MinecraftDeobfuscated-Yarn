package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_586<T extends class_1440> extends class_597<T> {
	private float field_3470;
	private float field_3469;
	private float field_3468;

	public class_586(int i, float f) {
		super(i, f);
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3535 = new class_630(this, 0, 6);
		this.field_3535.method_2844(-6.5F, -5.0F, -4.0F, 13, 10, 9);
		this.field_3535.method_2851(0.0F, 11.5F, -17.0F);
		this.field_3535.method_2850(45, 16).method_2844(-3.5F, 0.0F, -6.0F, 7, 5, 2);
		this.field_3535.method_2850(52, 25).method_2844(-8.5F, -8.0F, -1.0F, 5, 4, 1);
		this.field_3535.method_2850(52, 25).method_2844(3.5F, -8.0F, -1.0F, 5, 4, 1);
		this.field_3538 = new class_630(this, 0, 25);
		this.field_3538.method_2844(-9.5F, -13.0F, -6.5F, 19, 26, 13);
		this.field_3538.method_2851(0.0F, 10.0F, 0.0F);
		int j = 9;
		int k = 6;
		this.field_3536 = new class_630(this, 40, 0);
		this.field_3536.method_2844(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3536.method_2851(-5.5F, 15.0F, 9.0F);
		this.field_3534 = new class_630(this, 40, 0);
		this.field_3534.method_2844(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3534.method_2851(5.5F, 15.0F, 9.0F);
		this.field_3533 = new class_630(this, 40, 0);
		this.field_3533.method_2844(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3533.method_2851(-5.5F, 15.0F, -9.0F);
		this.field_3539 = new class_630(this, 40, 0);
		this.field_3539.method_2844(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.field_3539.method_2851(5.5F, 15.0F, -9.0F);
	}

	public void method_17102(T arg, float f, float g, float h) {
		super.method_2816(arg, f, g, h);
		this.field_3470 = arg.method_6534(h);
		this.field_3469 = arg.method_6555(h);
		this.field_3468 = arg.method_6109() ? 0.0F : arg.method_6560(h);
	}

	public void method_17103(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		boolean bl = arg.method_6521() > 0;
		boolean bl2 = arg.method_6545();
		int l = arg.method_6532();
		boolean bl3 = arg.method_6527();
		boolean bl4 = arg.method_6524();
		if (bl) {
			this.field_3535.field_3675 = 0.35F * class_3532.method_15374(0.6F * h);
			this.field_3535.field_3674 = 0.35F * class_3532.method_15374(0.6F * h);
			this.field_3533.field_3654 = -0.75F * class_3532.method_15374(0.3F * h);
			this.field_3539.field_3654 = 0.75F * class_3532.method_15374(0.3F * h);
		} else {
			this.field_3535.field_3674 = 0.0F;
		}

		if (bl2) {
			if (l < 15) {
				this.field_3535.field_3654 = (float) (-Math.PI / 4) * (float)l / 14.0F;
			} else if (l < 20) {
				float m = (float)((l - 15) / 5);
				this.field_3535.field_3654 = (float) (-Math.PI / 4) + (float) (Math.PI / 4) * m;
			}
		}

		if (this.field_3470 > 0.0F) {
			this.field_3538.field_3654 = this.method_2822(this.field_3538.field_3654, 1.7407963F, this.field_3470);
			this.field_3535.field_3654 = this.method_2822(this.field_3535.field_3654, (float) (Math.PI / 2), this.field_3470);
			this.field_3533.field_3674 = -0.27079642F;
			this.field_3539.field_3674 = 0.27079642F;
			this.field_3536.field_3674 = 0.5707964F;
			this.field_3534.field_3674 = -0.5707964F;
			if (bl3) {
				this.field_3535.field_3654 = (float) (Math.PI / 2) + 0.2F * class_3532.method_15374(h * 0.6F);
				this.field_3533.field_3654 = -0.4F - 0.2F * class_3532.method_15374(h * 0.6F);
				this.field_3539.field_3654 = -0.4F - 0.2F * class_3532.method_15374(h * 0.6F);
			}

			if (bl4) {
				this.field_3535.field_3654 = 2.1707964F;
				this.field_3533.field_3654 = -0.9F;
				this.field_3539.field_3654 = -0.9F;
			}
		} else {
			this.field_3536.field_3674 = 0.0F;
			this.field_3534.field_3674 = 0.0F;
			this.field_3533.field_3674 = 0.0F;
			this.field_3539.field_3674 = 0.0F;
		}

		if (this.field_3469 > 0.0F) {
			this.field_3536.field_3654 = -0.6F * class_3532.method_15374(h * 0.15F);
			this.field_3534.field_3654 = 0.6F * class_3532.method_15374(h * 0.15F);
			this.field_3533.field_3654 = 0.3F * class_3532.method_15374(h * 0.25F);
			this.field_3539.field_3654 = -0.3F * class_3532.method_15374(h * 0.25F);
			this.field_3535.field_3654 = this.method_2822(this.field_3535.field_3654, (float) (Math.PI / 2), this.field_3469);
		}

		if (this.field_3468 > 0.0F) {
			this.field_3535.field_3654 = this.method_2822(this.field_3535.field_3654, 2.0561945F, this.field_3468);
			this.field_3536.field_3654 = -0.5F * class_3532.method_15374(h * 0.5F);
			this.field_3534.field_3654 = 0.5F * class_3532.method_15374(h * 0.5F);
			this.field_3533.field_3654 = 0.5F * class_3532.method_15374(h * 0.5F);
			this.field_3539.field_3654 = -0.5F * class_3532.method_15374(h * 0.5F);
		}
	}

	protected float method_2822(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}

	public void method_17104(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17103(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 3.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.6F;
			GlStateManager.scalef(0.5555555F, 0.5555555F, 0.5555555F);
			GlStateManager.translatef(0.0F, 23.0F * k, 0.3F);
			this.field_3535.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.translatef(0.0F, 49.0F * k, 0.0F);
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
}
