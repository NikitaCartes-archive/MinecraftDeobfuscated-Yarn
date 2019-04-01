package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4294 extends class_437 {
	private static final class_2960 field_19235 = new class_2960("textures/gui/mojang_logo.png");
	private static final class_2960 field_19236 = new class_2960("textures/gui/mojang_text.png");
	private class_4294.class_4295 field_19237 = class_4294.class_4295.field_19243;
	private long field_19238 = -1L;
	private float field_19239 = 15.0F;
	private long field_19240;
	private class_1113 field_19241;

	public class_4294() {
		super(new class_2585("Amazing Logo"));
	}

	@Override
	public void render(int i, int j, float f) {
		GlStateManager.enableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.clear(16640, class_310.field_1703);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		long l = class_156.method_658();
		long m = l - this.field_19240;
		this.field_19240 = l;
		if (this.field_19237 == class_4294.class_4295.field_19243) {
			this.field_19237 = class_4294.class_4295.field_19244;
			this.field_19239 = 10.0F;
			this.field_19238 = -1L;
		} else {
			if (this.field_19237 == class_4294.class_4295.field_19244) {
				this.field_19239 -= (float)m / 1000.0F;
				if (this.field_19239 <= 0.0F) {
					this.field_19237 = class_4294.class_4295.field_19245;
					this.field_19238 = l;
					this.field_19241 = new class_1109(
						class_3417.field_19260.method_14833(), class_3419.field_15250, 0.25F, 1.0F, false, 0, class_1113.class_1114.field_5478, 0.0F, 0.0F, 0.0F, true
					) {
						@Override
						public boolean method_20286() {
							return false;
						}
					};
					this.minecraft.method_1483().method_4873(this.field_19241);
				}
			} else if (!this.minecraft.method_1483().method_4877(this.field_19241)) {
				this.minecraft.method_1507(new class_442());
				this.field_19237 = class_4294.class_4295.field_19243;
			}

			float g = 20.0F * this.field_19239;
			float h = 100.0F * class_3532.method_15374(this.field_19239);
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			lv2.method_1328(7, class_290.field_1575);
			if (this.field_19238 != -1L) {
				this.minecraft.method_1531().method_4618(field_19236);
				int k = class_3532.method_15340((int)(l - this.field_19238), 0, 255);
				this.method_20278(lv2, this.width / 2, this.height - this.height / 8, 208, 38, k);
			}

			lv.method_1350();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)this.width / 2.0F, (float)this.height / 2.0F, this.blitOffset);
			GlStateManager.rotatef(g, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(h, 0.0F, 0.0F);
			float n = 1.0F / (2.0F * this.field_19239 + 1.0F);
			GlStateManager.rotatef(1.5F * this.field_19239, 0.0F, 0.0F, 1.0F);
			GlStateManager.scalef(n, n, 1.0F);
			this.minecraft.method_1531().method_4618(field_19235);
			lv2.method_1328(7, class_290.field_1575);
			this.method_20278(lv2, 0, 0, 78, 76, 255);
			lv.method_1350();
			GlStateManager.popMatrix();
		}
	}

	private void method_20278(class_287 arg, int i, int j, int k, int l, int m) {
		int n = k / 2;
		int o = l / 2;
		arg.method_1315((double)(i - n), (double)(j + o), (double)this.blitOffset).method_1312(0.0, 1.0).method_1323(255, 255, 255, m).method_1344();
		arg.method_1315((double)(i + n), (double)(j + o), (double)this.blitOffset).method_1312(1.0, 1.0).method_1323(255, 255, 255, m).method_1344();
		arg.method_1315((double)(i + n), (double)(j - o), (double)this.blitOffset).method_1312(1.0, 0.0).method_1323(255, 255, 255, m).method_1344();
		arg.method_1315((double)(i - n), (double)(j - o), (double)this.blitOffset).method_1312(0.0, 0.0).method_1323(255, 255, 255, m).method_1344();
	}

	@Override
	public void onClose() {
		this.minecraft.method_1507(new class_442());
	}

	@Environment(EnvType.CLIENT)
	static enum class_4295 {
		field_19243,
		field_19244,
		field_19245;
	}
}
