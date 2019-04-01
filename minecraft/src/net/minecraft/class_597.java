package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_597<T extends class_1297> extends class_583<T> {
	protected class_630 field_3535;
	protected class_630 field_3538;
	protected class_630 field_3536;
	protected class_630 field_3534;
	protected class_630 field_3533;
	protected class_630 field_3539;
	protected float field_3540 = 8.0F;
	protected float field_3537 = 4.0F;

	public class_597(int i, float f) {
		this.field_3535 = new class_630(this, 0, 0);
		this.field_3535.method_2856(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
		this.field_3535.method_2851(0.0F, (float)(18 - i), -6.0F);
		this.field_3538 = new class_630(this, 28, 8);
		this.field_3538.method_2856(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
		this.field_3538.method_2851(0.0F, (float)(17 - i), 2.0F);
		this.field_3536 = new class_630(this, 0, 16);
		this.field_3536.method_2856(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3536.method_2851(-3.0F, (float)(24 - i), 7.0F);
		this.field_3534 = new class_630(this, 0, 16);
		this.field_3534.method_2856(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3534.method_2851(3.0F, (float)(24 - i), 7.0F);
		this.field_3533 = new class_630(this, 0, 16);
		this.field_3533.method_2856(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3533.method_2851(-3.0F, (float)(24 - i), -5.0F);
		this.field_3539 = new class_630(this, 0, 16);
		this.field_3539.method_2856(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.field_3539.method_2851(3.0F, (float)(24 - i), -5.0F);
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17080(arg, f, g, h, i, j, k);
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
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

	@Override
	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
		this.field_3535.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3535.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3538.field_3654 = (float) (Math.PI / 2);
		this.field_3536.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
		this.field_3534.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3533.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3539.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g;
	}
}
