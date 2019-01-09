package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_862 implements class_863.class_864 {
	private final class_310 field_4516;

	public class_862(class_310 arg) {
		this.field_4516 = arg;
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4516.field_1724;
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		double h = 0.0 - e;
		double i = 256.0 - e;
		GlStateManager.disableTexture();
		GlStateManager.disableBlend();
		double j = (double)(lv.field_6024 << 4) - d;
		double k = (double)(lv.field_5980 << 4) - g;
		GlStateManager.lineWidth(1.0F);
		lv3.method_1328(3, class_290.field_1576);

		for (int m = -16; m <= 32; m += 16) {
			for (int n = -16; n <= 32; n += 16) {
				lv3.method_1315(j + (double)m, h, k + (double)n).method_1336(1.0F, 0.0F, 0.0F, 0.0F).method_1344();
				lv3.method_1315(j + (double)m, h, k + (double)n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
				lv3.method_1315(j + (double)m, i, k + (double)n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
				lv3.method_1315(j + (double)m, i, k + (double)n).method_1336(1.0F, 0.0F, 0.0F, 0.0F).method_1344();
			}
		}

		for (int m = 2; m < 16; m += 2) {
			lv3.method_1315(j + (double)m, h, k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j + (double)m, h, k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + (double)m, i, k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + (double)m, i, k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j + (double)m, h, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j + (double)m, h, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + (double)m, i, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + (double)m, i, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		for (int m = 2; m < 16; m += 2) {
			lv3.method_1315(j, h, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j, h, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j, i, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j, i, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j + 16.0, h, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j + 16.0, h, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, i, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, i, k + (double)m).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		for (int m = 0; m <= 256; m += 2) {
			double o = (double)m - e;
			lv3.method_1315(j, o, k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, o, k + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, o, k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		lv2.method_1350();
		GlStateManager.lineWidth(2.0F);
		lv3.method_1328(3, class_290.field_1576);

		for (int m = 0; m <= 16; m += 16) {
			for (int n = 0; n <= 16; n += 16) {
				lv3.method_1315(j + (double)m, h, k + (double)n).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
				lv3.method_1315(j + (double)m, h, k + (double)n).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
				lv3.method_1315(j + (double)m, i, k + (double)n).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
				lv3.method_1315(j + (double)m, i, k + (double)n).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
			}
		}

		for (int m = 0; m <= 256; m += 16) {
			double o = (double)m - e;
			lv3.method_1315(j, o, k).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k + 16.0).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, o, k + 16.0).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(j + 16.0, o, k).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(j, o, k).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
		}

		lv2.method_1350();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}
}
