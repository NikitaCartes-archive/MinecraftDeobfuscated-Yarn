package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_871 implements class_863.class_864 {
	private final class_310 field_4628;

	public class_871(class_310 arg) {
		this.field_4628 = arg;
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4628.field_1724;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		class_1922 lv2 = this.field_4628.field_1724.field_6002;
		Iterable<class_2338> iterable = class_2338.method_10094(
			class_3532.method_15357(lv.field_5987 - 6.0),
			class_3532.method_15357(lv.field_6010 - 6.0),
			class_3532.method_15357(lv.field_6035 - 6.0),
			class_3532.method_15357(lv.field_5987 + 6.0),
			class_3532.method_15357(lv.field_6010 + 6.0),
			class_3532.method_15357(lv.field_6035 + 6.0)
		);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);

		for (class_2338 lv3 : iterable) {
			class_2680 lv4 = lv2.method_8320(lv3);
			if (lv4.method_11614() != class_2246.field_10124) {
				class_265 lv5 = lv4.method_11606(lv2, lv3);

				for (class_238 lv6 : lv5.method_1090()) {
					class_238 lv7 = lv6.method_996(lv3).method_1014(0.002).method_989(-d, -e, -g);
					double h = lv7.field_1323;
					double i = lv7.field_1322;
					double j = lv7.field_1321;
					double k = lv7.field_1320;
					double m = lv7.field_1325;
					double n = lv7.field_1324;
					float o = 1.0F;
					float p = 0.0F;
					float q = 0.0F;
					float r = 0.5F;
					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11039)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(h, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}

					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11035)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(h, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}

					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11034)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(k, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}

					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11043)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(k, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}

					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11033)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(h, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, i, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, i, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}

					if (class_2248.method_9501(lv4.method_11628(lv2, lv3), class_2350.field_11036)) {
						class_289 lv8 = class_289.method_1348();
						class_287 lv9 = lv8.method_1349();
						lv9.method_1328(5, class_290.field_1576);
						lv9.method_1315(h, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(h, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, m, j).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1315(k, m, n).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv8.method_1350();
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
