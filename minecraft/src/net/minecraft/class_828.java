package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_828 extends class_827<class_2605> {
	private static final class_2960 field_4369 = new class_2960("textures/entity/enchanting_table_book.png");
	private final class_557 field_4370 = new class_557();

	public void method_3571(class_2605 arg, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
		float h = (float)arg.field_11961 + g;
		GlStateManager.translatef(0.0F, 0.1F + class_3532.method_15374(h * 0.1F) * 0.01F, 0.0F);
		float j = arg.field_11964 - arg.field_11963;

		while (j >= (float) Math.PI) {
			j -= (float) (Math.PI * 2);
		}

		while (j < (float) -Math.PI) {
			j += (float) (Math.PI * 2);
		}

		float k = arg.field_11963 + j * g;
		GlStateManager.rotatef(-k * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
		this.method_3566(field_4369);
		float l = class_3532.method_16439(g, arg.field_11960, arg.field_11958) + 0.25F;
		float m = class_3532.method_16439(g, arg.field_11960, arg.field_11958) + 0.75F;
		l = (l - (float)class_3532.method_15365((double)l)) * 1.6F - 0.3F;
		m = (m - (float)class_3532.method_15365((double)m)) * 1.6F - 0.3F;
		if (l < 0.0F) {
			l = 0.0F;
		}

		if (m < 0.0F) {
			m = 0.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		float n = class_3532.method_16439(g, arg.field_11965, arg.field_11966);
		GlStateManager.enableCull();
		this.field_4370.method_17072(h, l, m, n, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
