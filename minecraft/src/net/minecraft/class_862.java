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
	public void method_3715(long l) {
		class_4184 lv = this.field_4516.field_1773.method_19418();
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		double g = 0.0 - e;
		double h = 256.0 - e;
		GlStateManager.disableTexture();
		GlStateManager.disableBlend();
		double i = (double)(lv.method_19331().field_6024 << 4) - d;
		double j = (double)(lv.method_19331().field_5980 << 4) - f;
		GlStateManager.lineWidth(1.0F);
		lv3.method_1328(3, class_290.field_1576);

		for (int k = -16; k <= 32; k += 16) {
			for (int m = -16; m <= 32; m += 16) {
				lv3.method_1315(i + (double)k, g, j + (double)m).method_1336(1.0F, 0.0F, 0.0F, 0.0F).method_1344();
				lv3.method_1315(i + (double)k, g, j + (double)m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
				lv3.method_1315(i + (double)k, h, j + (double)m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
				lv3.method_1315(i + (double)k, h, j + (double)m).method_1336(1.0F, 0.0F, 0.0F, 0.0F).method_1344();
			}
		}

		for (int k = 2; k < 16; k += 2) {
			lv3.method_1315(i + (double)k, g, j).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i + (double)k, g, j).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + (double)k, h, j).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + (double)k, h, j).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i + (double)k, g, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i + (double)k, g, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + (double)k, h, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + (double)k, h, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		for (int k = 2; k < 16; k += 2) {
			lv3.method_1315(i, g, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i, g, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i, h, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i, h, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i + 16.0, g, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i + 16.0, g, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, h, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, h, j + (double)k).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		for (int k = 0; k <= 256; k += 2) {
			double n = (double)k - e;
			lv3.method_1315(i, n, j).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, n, j + 16.0).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, n, j).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(1.0F, 1.0F, 0.0F, 0.0F).method_1344();
		}

		lv2.method_1350();
		GlStateManager.lineWidth(2.0F);
		lv3.method_1328(3, class_290.field_1576);

		for (int k = 0; k <= 16; k += 16) {
			for (int m = 0; m <= 16; m += 16) {
				lv3.method_1315(i + (double)k, g, j + (double)m).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
				lv3.method_1315(i + (double)k, g, j + (double)m).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
				lv3.method_1315(i + (double)k, h, j + (double)m).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
				lv3.method_1315(i + (double)k, h, j + (double)m).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
			}
		}

		for (int k = 0; k <= 256; k += 16) {
			double n = (double)k - e;
			lv3.method_1315(i, n, j).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j + 16.0).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, n, j + 16.0).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(i + 16.0, n, j).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(0.25F, 0.25F, 1.0F, 1.0F).method_1344();
			lv3.method_1315(i, n, j).method_1336(0.25F, 0.25F, 1.0F, 0.0F).method_1344();
		}

		lv2.method_1350();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}
}
