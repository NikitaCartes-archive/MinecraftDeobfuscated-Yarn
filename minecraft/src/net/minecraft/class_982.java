package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_982 extends class_3887<class_1510, class_625> {
	public class_982(class_3883<class_1510, class_625> arg) {
		super(arg);
	}

	public void method_4184(class_1510 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.field_7031 > 0) {
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			class_308.method_1450();
			float m = ((float)arg.field_7031 + h) / 200.0F;
			float n = 0.0F;
			if (m > 0.8F) {
				n = (m - 0.8F) / 0.2F;
			}

			Random random = new Random(432L);
			GlStateManager.disableTexture();
			GlStateManager.shadeModel(7425);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.disableAlphaTest();
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, -1.0F, -2.0F);

			for (int o = 0; (float)o < (m + m * m) / 2.0F * 60.0F; o++) {
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(random.nextFloat() * 360.0F + m * 90.0F, 0.0F, 0.0F, 1.0F);
				float p = random.nextFloat() * 20.0F + 5.0F + n * 10.0F;
				float q = random.nextFloat() * 2.0F + 1.0F + n * 2.0F;
				lv2.method_1328(6, class_290.field_1576);
				lv2.method_1315(0.0, 0.0, 0.0).method_1323(255, 255, 255, (int)(255.0F * (1.0F - n))).method_1344();
				lv2.method_1315(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).method_1323(255, 0, 255, 0).method_1344();
				lv2.method_1315(0.866 * (double)q, (double)p, (double)(-0.5F * q)).method_1323(255, 0, 255, 0).method_1344();
				lv2.method_1315(0.0, (double)p, (double)(1.0F * q)).method_1323(255, 0, 255, 0).method_1344();
				lv2.method_1315(-0.866 * (double)q, (double)p, (double)(-0.5F * q)).method_1323(255, 0, 255, 0).method_1344();
				lv.method_1350();
			}

			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(7424);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture();
			GlStateManager.enableAlphaTest();
			class_308.method_1452();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
