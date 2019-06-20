package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_627<T extends class_1297> extends class_583<T> {
	private final class_630 field_3640;
	private final class_630 field_3642 = new class_630(this, "glass");
	private final class_630 field_3641;

	public class_627(float f, boolean bl) {
		this.field_3642.method_2850(0, 0).method_2844(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		this.field_3640 = new class_630(this, "cube");
		this.field_3640.method_2850(32, 0).method_2844(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		if (bl) {
			this.field_3641 = new class_630(this, "base");
			this.field_3641.method_2850(0, 16).method_2844(-6.0F, 0.0F, -6.0F, 12, 4, 12);
		} else {
			this.field_3641 = null;
		}
	}

	@Override
	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		GlStateManager.translatef(0.0F, -0.5F, 0.0F);
		if (this.field_3641 != null) {
			this.field_3641.method_2846(k);
		}

		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.8F + h, 0.0F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.field_3642.method_2846(k);
		float l = 0.875F;
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.field_3642.method_2846(k);
		GlStateManager.scalef(0.875F, 0.875F, 0.875F);
		GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		this.field_3640.method_2846(k);
		GlStateManager.popMatrix();
	}
}
