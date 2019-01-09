package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1000<T extends class_1297, M extends class_611<T>> extends class_3887<T, M> {
	private static final class_2960 field_4902 = new class_2960("textures/entity/spider_eyes.png");

	public class_1000(class_3883<T, M> arg) {
		super(arg);
	}

	@Override
	public void method_4199(T arg, float f, float g, float h, float i, float j, float k, float l) {
		this.method_17164(field_4902);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5078);
		if (arg.method_5767()) {
			GlStateManager.depthMask(false);
		} else {
			GlStateManager.depthMask(true);
		}

		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_757 lv = class_310.method_1551().field_1773;
		lv.method_3201(true);
		this.method_17165().method_2819(arg, f, g, i, j, k, l);
		lv.method_3201(false);
		m = arg.method_5635();
		n = m % 65536;
		o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
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
