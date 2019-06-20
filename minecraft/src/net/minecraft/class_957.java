package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_957 extends class_925<class_1701> {
	public class_957(class_898 arg) {
		super(arg);
	}

	protected void method_4137(class_1701 arg, float f, class_2680 arg2) {
		int i = arg.method_7577();
		if (i > -1 && (float)i - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)i - f + 1.0F) / 10.0F;
			g = class_3532.method_15363(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			GlStateManager.scalef(h, h, h);
		}

		super.method_4064(arg, f, arg2);
		if (i > -1 && i / 5 % 2 == 0) {
			class_776 lv = class_310.method_1551().method_1541();
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.DST_ALPHA);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)i - f + 1.0F) / 100.0F) * 0.8F);
			GlStateManager.pushMatrix();
			lv.method_3353(class_2246.field_10375.method_9564(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}
	}
}
