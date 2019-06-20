package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_981 extends class_3887<class_1510, class_625> {
	private static final class_2960 field_4857 = new class_2960("textures/entity/enderdragon/dragon_eyes.png");

	public class_981(class_3883<class_1510, class_625> arg) {
		super(arg);
	}

	public void method_4183(class_1510 arg, float f, float g, float h, float i, float j, float k, float l) {
		this.method_17164(field_4857);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(514);
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_757 lv = class_310.method_1551().field_1773;
		lv.method_3201(true);
		this.method_17165().method_17137(arg, f, g, i, j, k, l);
		lv.method_3201(false);
		this.method_17163(arg);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthFunc(515);
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
