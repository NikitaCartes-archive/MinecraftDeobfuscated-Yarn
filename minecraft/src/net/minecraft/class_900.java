package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_900 extends class_897<class_1669> {
	private static final class_2960 field_4699 = new class_2960("textures/entity/illager/evoker_fangs.png");
	private final class_568<class_1669> field_4700 = new class_568<>();

	public class_900(class_898 arg) {
		super(arg);
	}

	public void method_3962(class_1669 arg, double d, double e, double f, float g, float h) {
		float i = arg.method_7472(h);
		if (i != 0.0F) {
			float j = 2.0F;
			if (i > 0.9F) {
				j = (float)((double)j * ((1.0 - (double)i) / 0.1F));
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.enableAlphaTest();
			this.method_3925(arg);
			GlStateManager.translatef((float)d, (float)e, (float)f);
			GlStateManager.rotatef(90.0F - arg.field_6031, 0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(-j, -j, j);
			float k = 0.03125F;
			GlStateManager.translatef(0.0F, -0.626F, 0.0F);
			this.field_4700.method_2819(arg, i, 0.0F, 0.0F, arg.field_6031, arg.field_5965, 0.03125F);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
			super.method_3936(arg, d, e, f, g, h);
		}
	}

	protected class_2960 method_3963(class_1669 arg) {
		return field_4699;
	}
}
