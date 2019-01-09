package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_902 extends class_897<class_1303> {
	private static final class_2960 field_4701 = new class_2960("textures/entity/experience_orb.png");

	public class_902(class_898 arg) {
		super(arg);
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	public void method_3966(class_1303 arg, double d, double e, double f, float g, float h) {
		if (!this.field_4674) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e, (float)f);
			this.method_3925(arg);
			class_308.method_1452();
			int i = arg.method_5920();
			float j = (float)(i % 4 * 16 + 0) / 64.0F;
			float k = (float)(i % 4 * 16 + 16) / 64.0F;
			float l = (float)(i / 4 * 16 + 0) / 64.0F;
			float m = (float)(i / 4 * 16 + 16) / 64.0F;
			float n = 1.0F;
			float o = 0.5F;
			float p = 0.25F;
			int q = arg.method_5635();
			int r = q % 65536;
			int s = q / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)r, (float)s);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float t = 255.0F;
			float u = ((float)arg.field_6165 + h) / 2.0F;
			int v = (int)((class_3532.method_15374(u + 0.0F) + 1.0F) * 0.5F * 255.0F);
			int w = 255;
			int x = (int)((class_3532.method_15374(u + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
			GlStateManager.translatef(0.0F, 0.1F, 0.0F);
			GlStateManager.rotatef(180.0F - this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float)(this.field_4676.field_4692.field_1850 == 2 ? -1 : 1) * -this.field_4676.field_4677, 1.0F, 0.0F, 0.0F);
			float y = 0.3F;
			GlStateManager.scalef(0.3F, 0.3F, 0.3F);
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			lv2.method_1328(7, class_290.field_1577);
			lv2.method_1315(-0.5, -0.25, 0.0).method_1312((double)j, (double)m).method_1323(v, 255, x, 128).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv2.method_1315(0.5, -0.25, 0.0).method_1312((double)k, (double)m).method_1323(v, 255, x, 128).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv2.method_1315(0.5, 0.75, 0.0).method_1312((double)k, (double)l).method_1323(v, 255, x, 128).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv2.method_1315(-0.5, 0.75, 0.0).method_1312((double)j, (double)l).method_1323(v, 255, x, 128).method_1318(0.0F, 1.0F, 0.0F).method_1344();
			lv.method_1350();
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.method_3936(arg, d, e, f, g, h);
		}
	}

	protected class_2960 method_3967(class_1303 arg) {
		return field_4701;
	}
}
