package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_907 extends class_927<class_1577, class_570> {
	private static final class_2960 field_4708 = new class_2960("textures/entity/guardian.png");
	private static final class_2960 field_4709 = new class_2960("textures/entity/guardian_beam.png");

	public class_907(class_898 arg) {
		this(arg, 0.5F);
	}

	protected class_907(class_898 arg, float f) {
		super(arg, new class_570(), f);
	}

	public boolean method_3978(class_1577 arg, class_856 arg2, double d, double e, double f) {
		if (super.method_4068(arg, arg2, d, e, f)) {
			return true;
		} else {
			if (arg.method_7063()) {
				class_1309 lv = arg.method_7052();
				if (lv != null) {
					class_243 lv2 = this.method_3979(lv, (double)lv.method_17682() * 0.5, 1.0F);
					class_243 lv3 = this.method_3979(arg, (double)arg.method_5751(), 1.0F);
					if (arg2.method_3699(new class_238(lv3.field_1352, lv3.field_1351, lv3.field_1350, lv2.field_1352, lv2.field_1351, lv2.field_1350))) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private class_243 method_3979(class_1309 arg, double d, float f) {
		double e = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		double g = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010) + d;
		double h = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		return new class_243(e, g, h);
	}

	public void method_3977(class_1577 arg, double d, double e, double f, float g, float h) {
		super.method_4072(arg, d, e, f, g, h);
		class_1309 lv = arg.method_7052();
		if (lv != null) {
			float i = arg.method_7061(h);
			class_289 lv2 = class_289.method_1348();
			class_287 lv3 = lv2.method_1349();
			this.method_3924(field_4709);
			GlStateManager.texParameter(3553, 10242, 10497);
			GlStateManager.texParameter(3553, 10243, 10497);
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			float j = 240.0F;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
			);
			float k = (float)arg.field_6002.method_8510() + h;
			float l = k * 0.5F % 1.0F;
			float m = arg.method_5751();
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)d, (float)e + m, (float)f);
			class_243 lv4 = this.method_3979(lv, (double)lv.method_17682() * 0.5, h);
			class_243 lv5 = this.method_3979(arg, (double)m, h);
			class_243 lv6 = lv4.method_1020(lv5);
			double n = lv6.method_1033() + 1.0;
			lv6 = lv6.method_1029();
			float o = (float)Math.acos(lv6.field_1351);
			float p = (float)Math.atan2(lv6.field_1350, lv6.field_1352);
			GlStateManager.rotatef(((float) (Math.PI / 2) - p) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(o * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			int q = 1;
			double r = (double)k * 0.05 * -1.5;
			lv3.method_1328(7, class_290.field_1575);
			float s = i * i;
			int t = 64 + (int)(s * 191.0F);
			int u = 32 + (int)(s * 191.0F);
			int v = 128 - (int)(s * 64.0F);
			double w = 0.2;
			double x = 0.282;
			double y = 0.0 + Math.cos(r + (Math.PI * 3.0 / 4.0)) * 0.282;
			double z = 0.0 + Math.sin(r + (Math.PI * 3.0 / 4.0)) * 0.282;
			double aa = 0.0 + Math.cos(r + (Math.PI / 4)) * 0.282;
			double ab = 0.0 + Math.sin(r + (Math.PI / 4)) * 0.282;
			double ac = 0.0 + Math.cos(r + (Math.PI * 5.0 / 4.0)) * 0.282;
			double ad = 0.0 + Math.sin(r + (Math.PI * 5.0 / 4.0)) * 0.282;
			double ae = 0.0 + Math.cos(r + (Math.PI * 7.0 / 4.0)) * 0.282;
			double af = 0.0 + Math.sin(r + (Math.PI * 7.0 / 4.0)) * 0.282;
			double ag = 0.0 + Math.cos(r + Math.PI) * 0.2;
			double ah = 0.0 + Math.sin(r + Math.PI) * 0.2;
			double ai = 0.0 + Math.cos(r + 0.0) * 0.2;
			double aj = 0.0 + Math.sin(r + 0.0) * 0.2;
			double ak = 0.0 + Math.cos(r + (Math.PI / 2)) * 0.2;
			double al = 0.0 + Math.sin(r + (Math.PI / 2)) * 0.2;
			double am = 0.0 + Math.cos(r + (Math.PI * 3.0 / 2.0)) * 0.2;
			double an = 0.0 + Math.sin(r + (Math.PI * 3.0 / 2.0)) * 0.2;
			double ap = 0.0;
			double aq = 0.4999;
			double ar = (double)(-1.0F + l);
			double as = n * 2.5 + ar;
			lv3.method_1315(ag, n, ah).method_1312(0.4999, as).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ag, 0.0, ah).method_1312(0.4999, ar).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ai, 0.0, aj).method_1312(0.0, ar).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ai, n, aj).method_1312(0.0, as).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ak, n, al).method_1312(0.4999, as).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ak, 0.0, al).method_1312(0.4999, ar).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(am, 0.0, an).method_1312(0.0, ar).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(am, n, an).method_1312(0.0, as).method_1323(t, u, v, 255).method_1344();
			double at = 0.0;
			if (arg.field_6012 % 2 == 0) {
				at = 0.5;
			}

			lv3.method_1315(y, n, z).method_1312(0.5, at + 0.5).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(aa, n, ab).method_1312(1.0, at + 0.5).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ae, n, af).method_1312(1.0, at).method_1323(t, u, v, 255).method_1344();
			lv3.method_1315(ac, n, ad).method_1312(0.5, at).method_1323(t, u, v, 255).method_1344();
			lv2.method_1350();
			GlStateManager.popMatrix();
		}
	}

	protected class_2960 method_3976(class_1577 arg) {
		return field_4708;
	}
}
