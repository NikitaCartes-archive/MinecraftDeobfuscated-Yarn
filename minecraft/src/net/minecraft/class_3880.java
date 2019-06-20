package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3880 extends class_827<class_3721> {
	private static final class_2960 field_17145 = new class_2960("textures/entity/bell/bell_body.png");
	private final class_3878 field_17146 = new class_3878();

	public void method_17139(class_3721 arg, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		this.method_3566(field_17145);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float h = (float)arg.field_17095 + g;
		float j = 0.0F;
		float k = 0.0F;
		if (arg.field_17096) {
			float l = class_3532.method_15374(h / (float) Math.PI) / (4.0F + h / 3.0F);
			if (arg.field_17097 == class_2350.field_11043) {
				j = -l;
			} else if (arg.field_17097 == class_2350.field_11035) {
				j = l;
			} else if (arg.field_17097 == class_2350.field_11034) {
				k = -l;
			} else if (arg.field_17097 == class_2350.field_11039) {
				k = l;
			}
		}

		this.field_17146.method_17070(j, k, 0.0625F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
