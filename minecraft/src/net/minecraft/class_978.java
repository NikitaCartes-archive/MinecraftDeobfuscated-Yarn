package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_978 extends class_3887<class_742, class_591<class_742>> {
	public class_978(class_3883<class_742, class_591<class_742>> arg) {
		super(arg);
	}

	public void method_4181(class_742 arg, float f, float g, float h, float i, float j, float k, float l) {
		if ("deadmau5".equals(arg.method_5477().getString()) && arg.method_3127() && !arg.method_5767()) {
			this.method_17164(arg.method_3117());

			for (int m = 0; m < 2; m++) {
				float n = class_3532.method_16439(h, arg.field_5982, arg.field_6031) - class_3532.method_16439(h, arg.field_6220, arg.field_6283);
				float o = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(n, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(o, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.375F * (float)(m * 2 - 1), 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -0.375F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-n, 0.0F, 1.0F, 0.0F);
				float p = 1.3333334F;
				GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
				this.method_17165().method_2824(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
