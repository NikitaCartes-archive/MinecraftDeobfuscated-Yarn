package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4286 extends class_4264 {
	private static final class_2960 field_19231 = new class_2960("textures/gui/checkbox.png");
	boolean field_19230;

	public class_4286(int i, int j, int k, int l, String string, boolean bl) {
		super(i, j, k, l, string);
		this.field_19230 = bl;
	}

	@Override
	public void onPress() {
		this.field_19230 = !this.field_19230;
	}

	public boolean method_20372() {
		return this.field_19230;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		class_310 lv = class_310.method_1551();
		lv.method_1531().method_4618(field_19231);
		GlStateManager.enableDepthTest();
		class_327 lv2 = lv.field_1772;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.blendFunc(GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA);
		blit(this.field_23658, this.field_23659, 0.0F, this.field_19230 ? 20.0F : 0.0F, 20, this.height, 32, 64);
		this.renderBg(lv, i, j);
		int k = 14737632;
		this.drawString(
			lv2, this.getMessage(), this.field_23658 + 24, this.field_23659 + (this.height - 8) / 2, 14737632 | class_3532.method_15386(this.alpha * 255.0F) << 24
		);
	}
}
