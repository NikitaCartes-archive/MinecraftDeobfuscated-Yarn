package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_940 extends class_897<class_1678> {
	private static final class_2960 field_4776 = new class_2960("textures/entity/shulker/spark.png");
	private final class_603<class_1678> field_4777 = new class_603<>();

	public class_940(class_898 arg) {
		super(arg);
	}

	private float method_4104(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void method_4103(class_1678 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		float i = this.method_4104(arg.field_5982, arg.field_6031, h);
		float j = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
		float k = (float)arg.field_6012 + h;
		GlStateManager.translatef((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotatef(class_3532.method_15374(k * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(class_3532.method_15362(k * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(class_3532.method_15374(k * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
		float l = 0.03125F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.method_3925(arg);
		this.field_4777.method_2819(arg, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.scalef(1.5F, 1.5F, 1.5F);
		this.field_4777.method_2819(arg, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4105(class_1678 arg) {
		return field_4776;
	}
}
