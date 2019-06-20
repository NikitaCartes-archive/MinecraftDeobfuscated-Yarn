package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_974 extends class_3887<class_1548, class_562<class_1548>> {
	private static final class_2960 field_4842 = new class_2960("textures/entity/creeper/creeper_armor.png");
	private final class_562<class_1548> field_4844 = new class_562<>(2.0F);

	public class_974(class_3883<class_1548, class_562<class_1548>> arg) {
		super(arg);
	}

	public void method_4178(class_1548 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_7009()) {
			boolean bl = arg.method_5767();
			GlStateManager.depthMask(!bl);
			this.method_17164(field_4842);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)arg.field_6012 + h;
			GlStateManager.translatef(m * 0.01F, m * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float n = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.class_5119.ONE, GlStateManager.class_5118.ONE);
			this.method_17165().method_17081(this.field_4844);
			class_757 lv = class_310.method_1551().field_1773;
			lv.method_3201(true);
			this.field_4844.method_2819(arg, f, g, i, j, k, l);
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
