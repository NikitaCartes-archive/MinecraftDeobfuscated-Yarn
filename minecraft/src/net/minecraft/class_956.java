package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_956 extends class_897<class_1541> {
	public class_956(class_898 arg) {
		super(arg);
		this.field_4673 = 0.5F;
	}

	public void method_4135(class_1541 arg, double d, double e, double f, float g, float h) {
		class_776 lv = class_310.method_1551().method_1541();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.5F, (float)f);
		if ((float)arg.method_6968() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)arg.method_6968() - h + 1.0F) / 10.0F;
			i = class_3532.method_15363(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			GlStateManager.scalef(j, j, j);
		}

		float i = (1.0F - ((float)arg.method_6968() - h + 1.0F) / 100.0F) * 0.8F;
		this.method_3925(arg);
		GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
		lv.method_3353(class_2246.field_10375.method_9564(), arg.method_5718());
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
			lv.method_3353(class_2246.field_10375.method_9564(), 1.0F);
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		} else if (arg.method_6968() / 5 % 2 == 0) {
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, i);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			lv.method_3353(class_2246.field_10375.method_9564(), 1.0F);
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}

		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4136(class_1541 arg) {
		return class_1059.field_5275;
	}
}
