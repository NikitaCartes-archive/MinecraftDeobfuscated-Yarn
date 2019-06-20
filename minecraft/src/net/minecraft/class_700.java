package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_700 extends class_703 {
	private class_1309 field_3829;

	private class_700(class_1937 arg, double d, double e, double f) {
		super(arg, d, e, f);
		this.field_3844 = 0.0F;
		this.field_3847 = 30;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17831;
	}

	@Override
	public void method_3070() {
		super.method_3070();
		if (this.field_3829 == null) {
			class_1550 lv = class_1299.field_6086.method_5883(this.field_3851);
			lv.method_7010();
			this.field_3829 = lv;
		}
	}

	@Override
	public void method_3074(class_287 arg, class_4184 arg2, float f, float g, float h, float i, float j, float k) {
		if (this.field_3829 != null) {
			class_898 lv = class_310.method_1551().method_1561();
			lv.method_3952(class_703.field_3873, class_703.field_3853, class_703.field_3870);
			float l = 1.0F / class_1550.field_17492;
			float m = ((float)this.field_3866 + f) / (float)this.field_3847;
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.enableDepthTest();
			GlStateManager.blendFunc(GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA);
			float n = 240.0F;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
			GlStateManager.pushMatrix();
			float o = 0.05F + 0.5F * class_3532.method_15374(m * (float) Math.PI);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, o);
			GlStateManager.translatef(0.0F, 1.8F, 0.0F);
			GlStateManager.rotatef(180.0F - arg2.method_19330(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(60.0F - 150.0F * m - arg2.method_19329(), 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.4F, -1.5F);
			GlStateManager.scalef(l, l, l);
			this.field_3829.field_6031 = 0.0F;
			this.field_3829.field_6241 = 0.0F;
			this.field_3829.field_5982 = 0.0F;
			this.field_3829.field_6259 = 0.0F;
			lv.method_3954(this.field_3829, 0.0, 0.0, 0.0, 0.0F, f, false);
			GlStateManager.popMatrix();
			GlStateManager.enableDepthTest();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_701 implements class_707<class_2400> {
		public class_703 method_3042(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_700(arg2, d, e, f);
		}
	}
}
