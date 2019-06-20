package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_837 extends class_827<class_2625> {
	private static final class_2960 field_4396 = new class_2960("textures/entity/signs/oak.png");
	private static final class_2960 field_4397 = new class_2960("textures/entity/signs/spruce.png");
	private static final class_2960 field_4395 = new class_2960("textures/entity/signs/birch.png");
	private static final class_2960 field_4394 = new class_2960("textures/entity/signs/acacia.png");
	private static final class_2960 field_4399 = new class_2960("textures/entity/signs/jungle.png");
	private static final class_2960 field_4400 = new class_2960("textures/entity/signs/dark_oak.png");
	private final class_605 field_4398 = new class_605();

	public void method_3582(class_2625 arg, double d, double e, double f, float g, int i) {
		class_2680 lv = arg.method_11010();
		GlStateManager.pushMatrix();
		float h = 0.6666667F;
		if (lv.method_11614() instanceof class_2508) {
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotatef(-((float)((Integer)lv.method_11654(class_2508.field_11559) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
			this.field_4398.method_2832().field_3665 = true;
		} else {
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotatef(-((class_2350)lv.method_11654(class_2551.field_11726)).method_10144(), 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
			this.field_4398.method_2832().field_3665 = false;
		}

		if (i >= 0) {
			this.method_3566(field_4368[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 2.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.method_3566(this.method_3584(lv.method_11614()));
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
		this.field_4398.method_2833();
		GlStateManager.popMatrix();
		class_327 lv2 = this.method_3564();
		float j = 0.010416667F;
		GlStateManager.translatef(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scalef(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.normal3f(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);
		int k = arg.method_16126().method_16357();
		if (i < 0) {
			for (int l = 0; l < 4; l++) {
				String string = arg.method_11300(l, arg2 -> {
					List<class_2561> list = class_341.method_1850(arg2, 90, lv2, false, true);
					return list.isEmpty() ? "" : ((class_2561)list.get(0)).method_10863();
				});
				if (string != null) {
					lv2.method_1729(string, (float)(-lv2.method_1727(string) / 2), (float)(l * 10 - arg.field_12050.length * 5), k);
					if (l == arg.method_16334() && arg.method_16336() >= 0) {
						int m = lv2.method_1727(string.substring(0, Math.max(Math.min(arg.method_16336(), string.length()), 0)));
						int n = lv2.method_1726() ? -1 : 1;
						int o = (m - lv2.method_1727(string) / 2) * n;
						int p = l * 10 - arg.field_12050.length * 5;
						if (arg.method_16331()) {
							if (arg.method_16336() < string.length()) {
								class_332.fill(o, p - 1, o + 1, p + 9, 0xFF000000 | k);
							} else {
								lv2.method_1729("_", (float)o, (float)p, k);
							}
						}

						if (arg.method_16333() != arg.method_16336()) {
							int q = Math.min(arg.method_16336(), arg.method_16333());
							int r = Math.max(arg.method_16336(), arg.method_16333());
							int s = (lv2.method_1727(string.substring(0, q)) - lv2.method_1727(string) / 2) * n;
							int t = (lv2.method_1727(string.substring(0, r)) - lv2.method_1727(string) / 2) * n;
							this.method_16210(Math.min(s, t), p, Math.max(s, t), p + 9);
						}
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private class_2960 method_3584(class_2248 arg) {
		if (arg == class_2246.field_10121 || arg == class_2246.field_10187) {
			return field_4396;
		} else if (arg == class_2246.field_10411 || arg == class_2246.field_10088) {
			return field_4397;
		} else if (arg == class_2246.field_10231 || arg == class_2246.field_10391) {
			return field_4395;
		} else if (arg == class_2246.field_10284 || arg == class_2246.field_10401) {
			return field_4394;
		} else if (arg == class_2246.field_10544 || arg == class_2246.field_10587) {
			return field_4399;
		} else {
			return arg != class_2246.field_10330 && arg != class_2246.field_10265 ? field_4396 : field_4400;
		}
	}

	private void method_16210(int i, int j, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.class_1030.field_5110);
		lv2.method_1328(7, class_290.field_1592);
		lv2.method_1315((double)i, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)j, 0.0).method_1344();
		lv2.method_1315((double)i, (double)j, 0.0).method_1344();
		lv.method_1350();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}
}
