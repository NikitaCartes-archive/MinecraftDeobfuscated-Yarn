package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_822 extends class_827<class_2580> {
	private static final class_2960 field_4338 = new class_2960("textures/entity/beacon_beam.png");

	public void method_3541(class_2580 arg, double d, double e, double f, float g, int i) {
		this.method_3543(d, e, f, (double)g, (double)arg.method_10933(), arg.method_10937(), arg.method_10997().method_8510());
	}

	private void method_3543(double d, double e, double f, double g, double h, List<class_2580.class_2581> list, long l) {
		GlStateManager.alphaFunc(516, 0.1F);
		this.method_3566(field_4338);
		if (h > 0.0) {
			GlStateManager.disableFog();
			int i = 0;

			for (int j = 0; j < list.size(); j++) {
				class_2580.class_2581 lv = (class_2580.class_2581)list.get(j);
				method_3544(d, e, f, g, h, l, i, lv.method_10943(), lv.method_10944());
				i += lv.method_10943();
			}

			GlStateManager.enableFog();
		}
	}

	private static void method_3544(double d, double e, double f, double g, double h, long l, int i, int j, float[] fs) {
		method_3545(d, e, f, g, h, l, i, j, fs, 0.2, 0.25);
	}

	public static void method_3545(double d, double e, double f, double g, double h, long l, int i, int j, float[] fs, double k, double m) {
		int n = i + j;
		GlStateManager.texParameter(3553, 10242, 10497);
		GlStateManager.texParameter(3553, 10243, 10497);
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5078, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.pushMatrix();
		GlStateManager.translated(d + 0.5, e, f + 0.5);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		double o = (double)Math.floorMod(l, 40L) + g;
		double p = j < 0 ? o : -o;
		double q = class_3532.method_15385(p * 0.2 - (double)class_3532.method_15357(p * 0.1));
		float r = fs[0];
		float s = fs[1];
		float t = fs[2];
		GlStateManager.pushMatrix();
		GlStateManager.rotated(o * 2.25 - 45.0, 0.0, 1.0, 0.0);
		double u = 0.0;
		double x = 0.0;
		double y = -k;
		double z = 0.0;
		double aa = 0.0;
		double ab = -k;
		double ac = 0.0;
		double ad = 1.0;
		double ae = -1.0 + q;
		double af = (double)j * h * (0.5 / k) + ae;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315(0.0, (double)n, k).method_1312(1.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)i, k).method_1312(1.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(k, (double)i, 0.0).method_1312(0.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(k, (double)n, 0.0).method_1312(0.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)n, ab).method_1312(1.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)i, ab).method_1312(1.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(y, (double)i, 0.0).method_1312(0.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(y, (double)n, 0.0).method_1312(0.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(k, (double)n, 0.0).method_1312(1.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(k, (double)i, 0.0).method_1312(1.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)i, ab).method_1312(0.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)n, ab).method_1312(0.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(y, (double)n, 0.0).method_1312(1.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(y, (double)i, 0.0).method_1312(1.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)i, k).method_1312(0.0, ae).method_1336(r, s, t, 1.0F).method_1344();
		lv2.method_1315(0.0, (double)n, k).method_1312(0.0, af).method_1336(r, s, t, 1.0F).method_1344();
		lv.method_1350();
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.depthMask(false);
		u = -m;
		double v = -m;
		x = -m;
		y = -m;
		ac = 0.0;
		ad = 1.0;
		ae = -1.0 + q;
		af = (double)j * h + ae;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315(u, (double)n, v).method_1312(1.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(u, (double)i, v).method_1312(1.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)i, x).method_1312(0.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)n, x).method_1312(0.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)n, m).method_1312(1.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)i, m).method_1312(1.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(y, (double)i, m).method_1312(0.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(y, (double)n, m).method_1312(0.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)n, x).method_1312(1.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)i, x).method_1312(1.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)i, m).method_1312(0.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(m, (double)n, m).method_1312(0.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(y, (double)n, m).method_1312(1.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(y, (double)i, m).method_1312(1.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(u, (double)i, v).method_1312(0.0, ae).method_1336(r, s, t, 0.125F).method_1344();
		lv2.method_1315(u, (double)n, v).method_1312(0.0, af).method_1336(r, s, t, 0.125F).method_1344();
		lv.method_1350();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	public boolean method_3542(class_2580 arg) {
		return true;
	}
}
