package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1005<T extends class_1309> extends class_3887<T, class_622<T>> {
	public class_1005(class_3883<T, class_622<T>> arg) {
		super(arg);
	}

	public void method_4208(T arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6047();
		if (!lv.method_7960()) {
			GlStateManager.color3f(1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			if (this.method_17165().field_3448) {
				GlStateManager.translatef(0.0F, 0.625F, 0.0F);
				GlStateManager.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
				float m = 0.5F;
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			}

			this.method_17165().method_2839().method_2847(0.0625F);
			GlStateManager.translatef(-0.0625F, 0.53125F, 0.21875F);
			class_1792 lv2 = lv.method_7909();
			if (class_2248.method_9503(lv2).method_9564().method_11610() == class_2464.field_11456) {
				GlStateManager.translatef(0.0F, 0.0625F, -0.25F);
				GlStateManager.rotatef(30.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-5.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.375F;
				GlStateManager.scalef(0.375F, -0.375F, 0.375F);
			} else if (lv2 == class_1802.field_8102) {
				GlStateManager.translatef(0.0F, 0.125F, -0.125F);
				GlStateManager.rotatef(-45.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.625F;
				GlStateManager.scalef(0.625F, -0.625F, 0.625F);
				GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.translatef(0.1875F, 0.1875F, 0.0F);
				float n = 0.875F;
				GlStateManager.scalef(0.875F, 0.875F, 0.875F);
				GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(-60.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-30.0F, 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(40.0F, 0.0F, 0.0F, 1.0F);
			class_310.method_1551().method_1489().method_3233(arg, lv, class_809.class_811.field_4320);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
