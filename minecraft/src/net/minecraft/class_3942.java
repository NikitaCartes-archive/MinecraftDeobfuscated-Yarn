package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3942 extends class_827<class_3722> {
	private static final class_2960 field_17427 = new class_2960("textures/entity/enchanting_table_book.png");
	private final class_557 field_17428 = new class_557();

	public void method_17582(class_3722 arg, double d, double e, double f, float g, int i) {
		class_2680 lv = arg.method_11010();
		if ((Boolean)lv.method_11654(class_3715.field_17366)) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d + 0.5F, (float)e + 1.0F + 0.0625F, (float)f + 0.5F);
			float h = ((class_2350)lv.method_11654(class_3715.field_16404)).method_10170().method_10144();
			GlStateManager.rotatef(-h, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(67.5F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(0.0F, -0.125F, 0.0F);
			this.method_3566(field_17427);
			GlStateManager.enableCull();
			this.field_17428.method_17072(0.0F, 0.1F, 0.9F, 1.2F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		}
	}
}
