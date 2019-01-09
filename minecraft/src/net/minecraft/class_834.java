package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_834 extends class_827<class_2627> {
	private final class_602<?> field_4387;

	public class_834(class_602<?> arg) {
		this.field_4387 = arg;
	}

	public void method_3574(class_2627 arg, double d, double e, double f, float g, int i) {
		class_2350 lv = class_2350.field_11036;
		if (arg.method_11002()) {
			class_2680 lv2 = this.method_3565().method_8320(arg.method_11016());
			if (lv2.method_11614() instanceof class_2480) {
				lv = lv2.method_11654(class_2480.field_11496);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		if (i >= 0) {
			this.method_3566(field_4368[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 4.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			class_1767 lv3 = arg.method_11320();
			if (lv3 == null) {
				this.method_3566(class_943.field_4781);
			} else {
				this.method_3566(class_943.field_4780[lv3.method_7789()]);
			}
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		if (i < 0) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.translatef((float)d + 0.5F, (float)e + 1.5F, (float)f + 0.5F);
		GlStateManager.scalef(1.0F, -1.0F, -1.0F);
		GlStateManager.translatef(0.0F, 1.0F, 0.0F);
		float h = 0.9995F;
		GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
		GlStateManager.translatef(0.0F, -1.0F, 0.0F);
		switch (lv) {
			case field_11033:
				GlStateManager.translatef(0.0F, 2.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			case field_11036:
			default:
				break;
			case field_11043:
				GlStateManager.translatef(0.0F, 1.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				break;
			case field_11035:
				GlStateManager.translatef(0.0F, 1.0F, -1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case field_11039:
				GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;
			case field_11034:
				GlStateManager.translatef(1.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}

		this.field_4387.method_2831().method_2846(0.0625F);
		GlStateManager.translatef(0.0F, -arg.method_11312(g) * 0.5F, 0.0F);
		GlStateManager.rotatef(270.0F * arg.method_11312(g), 0.0F, 1.0F, 0.0F);
		this.field_4387.method_2829().method_2846(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}
