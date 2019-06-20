package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_916 extends class_897<class_1542> {
	private final class_918 field_4726;
	private final Random field_4725 = new Random();

	public class_916(class_898 arg, class_918 arg2) {
		super(arg);
		this.field_4726 = arg2;
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	private int method_3997(class_1542 arg, double d, double e, double f, float g, class_1087 arg2) {
		class_1799 lv = arg.method_6983();
		class_1792 lv2 = lv.method_7909();
		if (lv2 == null) {
			return 0;
		} else {
			boolean bl = arg2.method_4712();
			int i = this.method_3998(lv);
			float h = 0.25F;
			float j = class_3532.method_15374(((float)arg.method_6985() + g) / 10.0F + arg.field_7203) * 0.1F + 0.1F;
			float k = arg2.method_4709().method_3503(class_809.class_811.field_4318).field_4285.method_4945();
			GlStateManager.translatef((float)d, (float)e + j + 0.25F * k, (float)f);
			if (bl || this.field_4676.field_4692 != null) {
				float l = (((float)arg.method_6985() + g) / 20.0F + arg.field_7203) * (180.0F / (float)Math.PI);
				GlStateManager.rotatef(l, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			return i;
		}
	}

	private int method_3998(class_1799 arg) {
		int i = 1;
		if (arg.method_7947() > 48) {
			i = 5;
		} else if (arg.method_7947() > 32) {
			i = 4;
		} else if (arg.method_7947() > 16) {
			i = 3;
		} else if (arg.method_7947() > 1) {
			i = 2;
		}

		return i;
	}

	public void method_3996(class_1542 arg, double d, double e, double f, float g, float h) {
		class_1799 lv = arg.method_6983();
		int i = lv.method_7960() ? 187 : class_1792.method_7880(lv.method_7909()) + lv.method_7919();
		this.field_4725.setSeed((long)i);
		boolean bl = false;
		if (this.method_3925(arg)) {
			this.field_4676.field_4685.method_4619(this.method_3999(arg)).method_4626(false, false);
			bl = true;
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		class_308.method_1452();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.pushMatrix();
		class_1087 lv2 = this.field_4726.method_4028(lv, arg.field_6002, null);
		int j = this.method_3997(arg, d, e, f, h, lv2);
		float k = lv2.method_4709().field_4303.field_4285.method_4943();
		float l = lv2.method_4709().field_4303.field_4285.method_4945();
		float m = lv2.method_4709().field_4303.field_4285.method_4947();
		boolean bl2 = lv2.method_4712();
		if (!bl2) {
			float n = -0.0F * (float)(j - 1) * 0.5F * k;
			float o = -0.0F * (float)(j - 1) * 0.5F * l;
			float p = -0.09375F * (float)(j - 1) * 0.5F * m;
			GlStateManager.translatef(n, o, p);
		}

		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		for (int q = 0; q < j; q++) {
			if (bl2) {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float o = (this.field_4725.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float p = (this.field_4725.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float r = (this.field_4725.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translatef(o, p, r);
				}

				lv2.method_4709().method_3500(class_809.class_811.field_4318);
				this.field_4726.method_4006(lv, lv2);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float o = (this.field_4725.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float p = (this.field_4725.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					GlStateManager.translatef(o, p, 0.0F);
				}

				lv2.method_4709().method_3500(class_809.class_811.field_4318);
				this.field_4726.method_4006(lv, lv2);
				GlStateManager.popMatrix();
				GlStateManager.translatef(0.0F * k, 0.0F * l, 0.09375F * m);
			}
		}

		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		this.method_3925(arg);
		if (bl) {
			this.field_4676.field_4685.method_4619(this.method_3999(arg)).method_4627();
		}

		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_3999(class_1542 arg) {
		return class_1059.field_5275;
	}
}
