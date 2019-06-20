package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_985<T extends class_1309> extends class_3887<T, class_566<T>> {
	private static final class_2960 field_4876 = new class_2960("textures/entity/enderman/enderman_eyes.png");

	public class_985(class_3883<T, class_566<T>> arg) {
		super(arg);
	}

	public void method_4187(T arg, float f, float g, float h, float i, float j, float k, float l) {
		this.method_17164(field_4876);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!arg.method_5767());
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_757 lv = class_310.method_1551().field_1773;
		lv.method_3201(true);
		this.method_17165().method_17088(arg, f, g, i, j, k, l);
		lv.method_3201(false);
		this.method_17163(arg);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
