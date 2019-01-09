package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_996 extends class_3887<class_1473, class_608<class_1473>> {
	public class_996(class_3883<class_1473, class_608<class_1473>> arg) {
		super(arg);
	}

	public void method_4201(class_1473 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_5767() && arg.method_6643()) {
			GlStateManager.pushMatrix();
			this.method_17165().method_2834().method_2847(0.0625F);
			float m = 0.625F;
			GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(0.625F, -0.625F, -0.625F);
			class_310.method_1551().method_1489().method_3233(arg, new class_1799(class_2246.field_10147), class_809.class_811.field_4316);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
