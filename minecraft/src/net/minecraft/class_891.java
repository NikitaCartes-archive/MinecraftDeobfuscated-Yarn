package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_891 extends class_897<class_1670> {
	private static final class_2960 field_4661 = new class_2960("textures/entity/enderdragon/dragon_fireball.png");

	public class_891(class_898 arg) {
		super(arg);
	}

	public void method_3906(class_1670 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3925(arg);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		float i = 1.0F;
		float j = 0.5F;
		float k = 0.25F;
		GlStateManager.rotatef(180.0F - this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.field_4676.field_4692.field_1850 == 2 ? -1 : 1) * -this.field_4676.field_4677, 1.0F, 0.0F, 0.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		lv2.method_1328(7, class_290.field_1589);
		lv2.method_1315(-0.5, -0.25, 0.0).method_1312(0.0, 1.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(0.5, -0.25, 0.0).method_1312(1.0, 1.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(0.5, 0.75, 0.0).method_1312(1.0, 0.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(-0.5, 0.75, 0.0).method_1312(0.0, 0.0).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv.method_1350();
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_3905(class_1670 arg) {
		return field_4661;
	}
}
