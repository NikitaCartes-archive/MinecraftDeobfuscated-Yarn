package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_626 extends class_607 {
	private final class_630 field_3638;
	private final class_630 field_3639;

	public class_626(float f) {
		this.field_17138 = 256;
		this.field_17139 = 256;
		float g = -16.0F;
		this.field_3638 = new class_630(this, "head");
		this.field_3638.method_2848("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.field_3638.method_2848("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.field_3638.field_3666 = true;
		this.field_3638.method_2848("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3638.method_2848("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3638.field_3666 = false;
		this.field_3638.method_2848("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3638.method_2848("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3639 = new class_630(this, "jaw");
		this.field_3639.method_2851(0.0F, 4.0F, -8.0F);
		this.field_3639.method_2848("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.field_3638.method_2845(this.field_3639);
	}

	@Override
	public void method_2821(float f, float g, float h, float i, float j, float k) {
		this.field_3639.field_3654 = (float)(Math.sin((double)(f * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.field_3638.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3638.field_3654 = j * (float) (Math.PI / 180.0);
		GlStateManager.translatef(0.0F, -0.374375F, 0.0F);
		GlStateManager.scalef(0.75F, 0.75F, 0.75F);
		this.field_3638.method_2846(k);
	}
}
