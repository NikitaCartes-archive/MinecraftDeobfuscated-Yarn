package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_975 extends class_3887<class_1560, class_566<class_1560>> {
	public class_975(class_3883<class_1560, class_566<class_1560>> arg) {
		super(arg);
	}

	public void method_4179(class_1560 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_2680 lv = arg.method_7027();
		if (lv != null) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.25F, 0.1875F, 0.25F);
			float m = 0.5F;
			GlStateManager.scalef(-0.5F, -0.5F, 0.5F);
			int n = arg.method_5635();
			int o = n % 65536;
			int p = n / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)o, (float)p);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_17164(class_1059.field_5275);
			class_310.method_1551().method_1541().method_3353(lv, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
