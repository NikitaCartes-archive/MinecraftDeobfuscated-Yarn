package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1004 extends class_3887<class_1528, class_621<class_1528>> {
	private static final class_2960 field_4910 = new class_2960("textures/entity/wither/wither_armor.png");
	private final class_621<class_1528> field_4909 = new class_621<>(0.5F);

	public class_1004(class_3883<class_1528, class_621<class_1528>> arg) {
		super(arg);
	}

	public void method_4207(class_1528 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6872()) {
			GlStateManager.depthMask(!arg.method_5767());
			this.method_17164(field_4910);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)arg.field_6012 + h;
			float n = class_3532.method_15362(m * 0.02F) * 3.0F;
			float o = m * 0.01F;
			GlStateManager.translatef(n, o, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float p = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ONE);
			this.field_4909.method_17128(arg, f, g, h);
			this.method_17165().method_17081(this.field_4909);
			class_757 lv = class_310.method_1551().field_1773;
			lv.method_3201(true);
			this.field_4909.method_17129(arg, f, g, i, j, k, l);
			lv.method_3201(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
