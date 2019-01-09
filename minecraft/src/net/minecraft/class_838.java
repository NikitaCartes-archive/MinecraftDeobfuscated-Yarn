package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_838 extends class_827<class_2633> {
	public void method_3587(class_2633 arg, double d, double e, double f, float g, int i) {
		if (class_310.method_1551().field_1724.method_7338() || class_310.method_1551().field_1724.method_7325()) {
			super.method_3569(arg, d, e, f, g, i);
			class_2338 lv = arg.method_11359();
			class_2338 lv2 = arg.method_11349();
			if (lv2.method_10263() >= 1 && lv2.method_10264() >= 1 && lv2.method_10260() >= 1) {
				if (arg.method_11374() == class_2776.field_12695 || arg.method_11374() == class_2776.field_12697) {
					double h = 0.01;
					double j = (double)lv.method_10263();
					double k = (double)lv.method_10260();
					double l = e + (double)lv.method_10264() - 0.01;
					double m = l + (double)lv2.method_10264() + 0.02;
					double n;
					double o;
					switch (arg.method_11345()) {
						case field_11300:
							n = (double)lv2.method_10263() + 0.02;
							o = -((double)lv2.method_10260() + 0.02);
							break;
						case field_11301:
							n = -((double)lv2.method_10263() + 0.02);
							o = (double)lv2.method_10260() + 0.02;
							break;
						default:
							n = (double)lv2.method_10263() + 0.02;
							o = (double)lv2.method_10260() + 0.02;
					}

					double p;
					double q;
					double r;
					double s;
					switch (arg.method_11353()) {
						case field_11463:
							p = d + (o < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
							q = f + (n < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							r = p - o;
							s = q + n;
							break;
						case field_11464:
							p = d + (n < 0.0 ? j - 0.01 : j + 1.0 + 0.01);
							q = f + (o < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							r = p - n;
							s = q - o;
							break;
						case field_11465:
							p = d + (o < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
							q = f + (n < 0.0 ? k - 0.01 : k + 1.0 + 0.01);
							r = p + o;
							s = q - n;
							break;
						default:
							p = d + (n < 0.0 ? j + 1.0 + 0.01 : j - 0.01);
							q = f + (o < 0.0 ? k + 1.0 + 0.01 : k - 0.01);
							r = p + n;
							s = q + o;
					}

					int t = 255;
					int u = 223;
					int v = 127;
					class_289 lv3 = class_289.method_1348();
					class_287 lv4 = lv3.method_1349();
					GlStateManager.disableFog();
					GlStateManager.disableLighting();
					GlStateManager.disableTexture();
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(
						GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
					);
					this.method_3570(true);
					if (arg.method_11374() == class_2776.field_12695 || arg.method_11357()) {
						this.method_3586(lv3, lv4, p, l, q, r, m, s, 255, 223, 127);
					}

					if (arg.method_11374() == class_2776.field_12695 && arg.method_11375()) {
						this.method_3585(arg, d, e, f, lv, lv3, lv4, true);
						this.method_3585(arg, d, e, f, lv, lv3, lv4, false);
					}

					this.method_3570(false);
					GlStateManager.lineWidth(1.0F);
					GlStateManager.enableLighting();
					GlStateManager.enableTexture();
					GlStateManager.enableDepthTest();
					GlStateManager.depthMask(true);
					GlStateManager.enableFog();
				}
			}
		}
	}

	private void method_3585(class_2633 arg, double d, double e, double f, class_2338 arg2, class_289 arg3, class_287 arg4, boolean bl) {
		GlStateManager.lineWidth(bl ? 3.0F : 1.0F);
		arg4.method_1328(3, class_290.field_1576);
		class_1922 lv = arg.method_10997();
		class_2338 lv2 = arg.method_11016();
		class_2338 lv3 = lv2.method_10081(arg2);

		for (class_2338 lv4 : class_2338.method_10097(lv3, lv3.method_10081(arg.method_11349()).method_10069(-1, -1, -1))) {
			class_2680 lv5 = lv.method_8320(lv4);
			boolean bl2 = lv5.method_11588();
			boolean bl3 = lv5.method_11614() == class_2246.field_10369;
			if (bl2 || bl3) {
				float g = bl2 ? 0.05F : 0.0F;
				double h = (double)((float)(lv4.method_10263() - lv2.method_10263()) + 0.45F) + d - (double)g;
				double i = (double)((float)(lv4.method_10264() - lv2.method_10264()) + 0.45F) + e - (double)g;
				double j = (double)((float)(lv4.method_10260() - lv2.method_10260()) + 0.45F) + f - (double)g;
				double k = (double)((float)(lv4.method_10263() - lv2.method_10263()) + 0.55F) + d + (double)g;
				double l = (double)((float)(lv4.method_10264() - lv2.method_10264()) + 0.55F) + e + (double)g;
				double m = (double)((float)(lv4.method_10260() - lv2.method_10260()) + 0.55F) + f + (double)g;
				if (bl) {
					class_761.method_3258(arg4, h, i, j, k, l, m, 0.0F, 0.0F, 0.0F, 1.0F);
				} else if (bl2) {
					class_761.method_3258(arg4, h, i, j, k, l, m, 0.5F, 0.5F, 1.0F, 1.0F);
				} else {
					class_761.method_3258(arg4, h, i, j, k, l, m, 1.0F, 0.25F, 0.25F, 1.0F);
				}
			}
		}

		arg3.method_1350();
	}

	private void method_3586(class_289 arg, class_287 arg2, double d, double e, double f, double g, double h, double i, int j, int k, int l) {
		GlStateManager.lineWidth(2.0F);
		arg2.method_1328(3, class_290.field_1576);
		arg2.method_1315(d, e, f).method_1336((float)k, (float)k, (float)k, 0.0F).method_1344();
		arg2.method_1315(d, e, f).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, e, f).method_1323(k, l, l, j).method_1344();
		arg2.method_1315(g, e, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, e, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, e, f).method_1323(l, l, k, j).method_1344();
		arg2.method_1315(d, h, f).method_1323(l, k, l, j).method_1344();
		arg2.method_1315(g, h, f).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, h, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, h, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, h, f).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, h, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(d, e, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, e, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, h, i).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, h, f).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, e, f).method_1323(k, k, k, j).method_1344();
		arg2.method_1315(g, e, f).method_1336((float)k, (float)k, (float)k, 0.0F).method_1344();
		arg.method_1350();
		GlStateManager.lineWidth(1.0F);
	}

	public boolean method_3588(class_2633 arg) {
		return true;
	}
}
