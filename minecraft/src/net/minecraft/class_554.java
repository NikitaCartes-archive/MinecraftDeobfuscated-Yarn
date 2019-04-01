package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_554 extends class_583<class_1690> {
	private final class_630[] field_3327 = new class_630[5];
	private final class_630[] field_3325 = new class_630[2];
	private final class_630 field_3326;

	public class_554() {
		this.field_3327[0] = new class_630(this, 0, 0).method_2853(128, 64);
		this.field_3327[1] = new class_630(this, 0, 19).method_2853(128, 64);
		this.field_3327[2] = new class_630(this, 0, 27).method_2853(128, 64);
		this.field_3327[3] = new class_630(this, 0, 35).method_2853(128, 64);
		this.field_3327[4] = new class_630(this, 0, 43).method_2853(128, 64);
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		this.field_3327[0].method_2856(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.field_3327[0].method_2851(0.0F, 3.0F, 1.0F);
		this.field_3327[1].method_2856(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
		this.field_3327[1].method_2851(-15.0F, 4.0F, 4.0F);
		this.field_3327[2].method_2856(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
		this.field_3327[2].method_2851(15.0F, 4.0F, 0.0F);
		this.field_3327[3].method_2856(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.field_3327[3].method_2851(0.0F, 4.0F, -9.0F);
		this.field_3327[4].method_2856(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.field_3327[4].method_2851(0.0F, 4.0F, 9.0F);
		this.field_3327[0].field_3654 = (float) (Math.PI / 2);
		this.field_3327[1].field_3675 = (float) (Math.PI * 3.0 / 2.0);
		this.field_3327[2].field_3675 = (float) (Math.PI / 2);
		this.field_3327[3].field_3675 = (float) Math.PI;
		this.field_3325[0] = this.method_2796(true);
		this.field_3325[0].method_2851(3.0F, -5.0F, 9.0F);
		this.field_3325[1] = this.method_2796(false);
		this.field_3325[1].method_2851(3.0F, -5.0F, -9.0F);
		this.field_3325[1].field_3675 = (float) Math.PI;
		this.field_3325[0].field_3674 = (float) (Math.PI / 16);
		this.field_3325[1].field_3674 = (float) (Math.PI / 16);
		this.field_3326 = new class_630(this, 0, 0).method_2853(128, 64);
		this.field_3326.method_2856(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.field_3326.method_2851(0.0F, -3.0F, 1.0F);
		this.field_3326.field_3654 = (float) (Math.PI / 2);
	}

	public void method_17071(class_1690 arg, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.method_17080(arg, f, g, h, i, j, k);

		for (int l = 0; l < 5; l++) {
			this.field_3327[l].method_2846(k);
		}

		this.method_2797(arg, 0, k, f);
		this.method_2797(arg, 1, k, f);
	}

	public void method_2836(class_1297 arg, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.colorMask(false, false, false, false);
		this.field_3326.method_2846(k);
		GlStateManager.colorMask(true, true, true, true);
	}

	protected class_630 method_2796(boolean bl) {
		class_630 lv = new class_630(this, 62, bl ? 0 : 20).method_2853(128, 64);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		lv.method_2844(-1.0F, 0.0F, -5.0F, 2, 2, 18);
		lv.method_2844(bl ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
		return lv;
	}

	protected void method_2797(class_1690 arg, int i, float f, float g) {
		float h = arg.method_7551(i, g);
		class_630 lv = this.field_3325[i];
		lv.field_3654 = (float)class_3532.method_15390((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((class_3532.method_15374(-h) + 1.0F) / 2.0F));
		lv.field_3675 = (float)class_3532.method_15390((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((class_3532.method_15374(-h + 1.0F) + 1.0F) / 2.0F));
		if (i == 1) {
			lv.field_3675 = (float) Math.PI - lv.field_3675;
		}

		lv.method_2846(f);
	}
}
