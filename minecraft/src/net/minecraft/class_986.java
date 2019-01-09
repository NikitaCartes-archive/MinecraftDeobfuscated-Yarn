package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_986 extends class_3887<class_1439, class_574<class_1439>> {
	public class_986(class_3883<class_1439, class_574<class_1439>> arg) {
		super(arg);
	}

	public void method_4188(class_1439 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6502() != 0) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(5.0F + 180.0F * this.method_17165().method_2809().field_3654 / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(-0.9375F, -0.625F, -0.9375F);
			float m = 0.5F;
			GlStateManager.scalef(0.5F, -0.5F, 0.5F);
			int n = arg.method_5635();
			int o = n % 65536;
			int p = n / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)o, (float)p);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_17164(class_1059.field_5275);
			class_310.method_1551().method_1541().method_3353(class_2246.field_10449.method_9564(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
