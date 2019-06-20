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
	public void method_3715(long l) {
		class_4184 lv = this.field_4628.field_1773.method_19418();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		class_1922 lv2 = this.field_4628.field_1724.field_6002;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		class_2338 lv3 = new class_2338(lv.method_19326());

		for (class_2338 lv4 : class_2338.method_10097(lv3.method_10069(-6, -6, -6), lv3.method_10069(6, 6, 6))) {
			class_2680 lv5 = lv2.method_8320(lv4);
			if (lv5.method_11614() != class_2246.field_10124) {
				class_265 lv6 = lv5.method_17770(lv2, lv4);

				for (class_238 lv7 : lv6.method_1090()) {
					class_238 lv8 = lv7.method_996(lv4).method_1014(0.002).method_989(-d, -e, -f);
					double g = lv8.field_1323;
					double h = lv8.field_1322;
					double i = lv8.field_1321;
					double j = lv8.field_1320;
					double k = lv8.field_1325;
					double m = lv8.field_1324;
					float n = 1.0F;
					float o = 0.0F;
					float p = 0.0F;
					float q = 0.5F;
					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11039)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(g, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}

					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11035)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(g, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}

					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11034)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(j, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}

					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11043)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(j, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}

					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11033)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(g, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, h, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, h, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}

					if (class_2248.method_20045(lv5, lv2, lv4, class_2350.field_11036)) {
						class_289 lv9 = class_289.method_1348();
						class_287 lv10 = lv9.method_1349();
						lv10.method_1328(5, class_290.field_1576);
						lv10.method_1315(g, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(g, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, k, i).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv10.method_1315(j, k, m).method_1336(1.0F, 0.0F, 0.0F, 0.5F).method_1344();
						lv9.method_1350();
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
