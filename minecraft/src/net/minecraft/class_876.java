package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_876<T extends class_1665> extends class_897<T> {
	public class_876(class_898 arg) {
		super(arg);
	}

	public void method_3875(T arg, double d, double e, double f, float g, float h) {
		this.method_3925(arg);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_5982, arg.field_6031) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_6004, arg.field_5965), 0.0F, 0.0F, 1.0F);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		int i = 0;
		float j = 0.0F;
		float k = 0.5F;
		float l = 0.0F;
		float m = 0.15625F;
		float n = 0.0F;
		float o = 0.15625F;
		float p = 0.15625F;
		float q = 0.3125F;
		float r = 0.05625F;
		GlStateManager.enableRescaleNormal();
		float s = (float)arg.field_7574 - h;
		if (s > 0.0F) {
			float t = -class_3532.method_15374(s * 3.0F) * s;
			GlStateManager.rotatef(t, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
		GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(-7.0, -2.0, -2.0).method_1312(0.0, 0.15625).method_1344();
		lv2.method_1315(-7.0, -2.0, 2.0).method_1312(0.15625, 0.15625).method_1344();
		lv2.method_1315(-7.0, 2.0, 2.0).method_1312(0.15625, 0.3125).method_1344();
		lv2.method_1315(-7.0, 2.0, -2.0).method_1312(0.0, 0.3125).method_1344();
		lv.method_1350();
		GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(-7.0, 2.0, -2.0).method_1312(0.0, 0.15625).method_1344();
		lv2.method_1315(-7.0, 2.0, 2.0).method_1312(0.15625, 0.15625).method_1344();
		lv2.method_1315(-7.0, -2.0, 2.0).method_1312(0.15625, 0.3125).method_1344();
		lv2.method_1315(-7.0, -2.0, -2.0).method_1312(0.0, 0.3125).method_1344();
		lv.method_1350();

		for (int u = 0; u < 4; u++) {
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1315(-8.0, -2.0, 0.0).method_1312(0.0, 0.0).method_1344();
			lv2.method_1315(8.0, -2.0, 0.0).method_1312(0.5, 0.0).method_1344();
			lv2.method_1315(8.0, 2.0, 0.0).method_1312(0.5, 0.15625).method_1344();
			lv2.method_1315(-8.0, 2.0, 0.0).method_1312(0.0, 0.15625).method_1344();
			lv.method_1350();
		}

		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}
}
