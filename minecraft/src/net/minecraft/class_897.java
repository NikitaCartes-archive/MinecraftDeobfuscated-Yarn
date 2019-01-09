package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_897<T extends class_1297> {
	private static final class_2960 field_4675 = new class_2960("textures/misc/shadow.png");
	protected final class_898 field_4676;
	protected float field_4673;
	protected float field_4672 = 1.0F;
	protected boolean field_4674;

	protected class_897(class_898 arg) {
		this.field_4676 = arg;
	}

	public void method_3927(boolean bl) {
		this.field_4674 = bl;
	}

	public boolean method_3933(T arg, class_856 arg2, double d, double e, double f) {
		class_238 lv = arg.method_5830().method_1014(0.5);
		if (lv.method_1013() || lv.method_995() == 0.0) {
			lv = new class_238(arg.field_5987 - 2.0, arg.field_6010 - 2.0, arg.field_6035 - 2.0, arg.field_5987 + 2.0, arg.field_6010 + 2.0, arg.field_6035 + 2.0);
		}

		return arg.method_5727(d, e, f) && (arg.field_5985 || arg2.method_3699(lv));
	}

	public void method_3936(T arg, double d, double e, double f, float g, float h) {
		if (!this.field_4674) {
			this.method_3926(arg, d, e, f);
		}
	}

	protected int method_3929(T arg) {
		class_268 lv = (class_268)arg.method_5781();
		return lv != null && lv.method_1202().method_532() != null ? lv.method_1202().method_532() : 16777215;
	}

	protected void method_3926(T arg, double d, double e, double f) {
		if (this.method_3921(arg)) {
			this.method_3923(arg, arg.method_5476().method_10863(), d, e, f, 64);
		}
	}

	protected boolean method_3921(T arg) {
		return arg.method_5733() && arg.method_16914();
	}

	protected void method_3930(T arg, double d, double e, double f, String string, double g) {
		this.method_3923(arg, string, d, e, f, 64);
	}

	@Nullable
	protected abstract class_2960 method_3931(T arg);

	protected boolean method_3925(T arg) {
		class_2960 lv = this.method_3931(arg);
		if (lv == null) {
			return false;
		} else {
			this.method_3924(lv);
			return true;
		}
	}

	public void method_3924(class_2960 arg) {
		this.field_4676.field_4685.method_4618(arg);
	}

	private void method_3938(class_1297 arg, double d, double e, double f, float g) {
		GlStateManager.disableLighting();
		class_1059 lv = class_310.method_1551().method_1549();
		class_1058 lv2 = lv.method_4608(class_1088.field_5397);
		class_1058 lv3 = lv.method_4608(class_1088.field_5370);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float h = arg.field_5998 * 1.4F;
		GlStateManager.scalef(h, h, h);
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		float i = 0.5F;
		float j = 0.0F;
		float k = arg.field_6019 / h;
		float l = (float)(arg.field_6010 - arg.method_5829().field_1322);
		GlStateManager.rotatef(-this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, -0.3F + (float)((int)k) * 0.02F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float m = 0.0F;
		int n = 0;
		lv5.method_1328(7, class_290.field_1585);

		while (k > 0.0F) {
			class_1058 lv6 = n % 2 == 0 ? lv2 : lv3;
			this.method_3924(class_1059.field_5275);
			float o = lv6.method_4594();
			float p = lv6.method_4593();
			float q = lv6.method_4577();
			float r = lv6.method_4575();
			if (n / 2 % 2 == 0) {
				float s = q;
				q = o;
				o = s;
			}

			lv5.method_1315((double)(i - 0.0F), (double)(0.0F - l), (double)m).method_1312((double)q, (double)r).method_1344();
			lv5.method_1315((double)(-i - 0.0F), (double)(0.0F - l), (double)m).method_1312((double)o, (double)r).method_1344();
			lv5.method_1315((double)(-i - 0.0F), (double)(1.4F - l), (double)m).method_1312((double)o, (double)p).method_1344();
			lv5.method_1315((double)(i - 0.0F), (double)(1.4F - l), (double)m).method_1312((double)q, (double)p).method_1344();
			k -= 0.45F;
			l -= 0.45F;
			i *= 0.9F;
			m += 0.03F;
			n++;
		}

		lv4.method_1350();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}

	private void method_3934(class_1297 arg, double d, double e, double f, float g, float h) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
		this.field_4676.field_4685.method_4618(field_4675);
		class_1941 lv = this.method_3935();
		GlStateManager.depthMask(false);
		float i = this.field_4673;
		if (arg instanceof class_1308) {
			class_1308 lv2 = (class_1308)arg;
			i *= lv2.method_5967();
			if (lv2.method_6109()) {
				i *= 0.5F;
			}
		}

		double j = class_3532.method_16436((double)h, arg.field_6038, arg.field_5987);
		double k = class_3532.method_16436((double)h, arg.field_5971, arg.field_6010);
		double l = class_3532.method_16436((double)h, arg.field_5989, arg.field_6035);
		int m = class_3532.method_15357(j - (double)i);
		int n = class_3532.method_15357(j + (double)i);
		int o = class_3532.method_15357(k - (double)i);
		int p = class_3532.method_15357(k);
		int q = class_3532.method_15357(l - (double)i);
		int r = class_3532.method_15357(l + (double)i);
		double s = d - j;
		double t = e - k;
		double u = f - l;
		class_289 lv3 = class_289.method_1348();
		class_287 lv4 = lv3.method_1349();
		lv4.method_1328(7, class_290.field_1575);

		for (class_2338 lv5 : class_2338.method_10082(new class_2338(m, o, q), new class_2338(n, p, r))) {
			class_2338 lv6 = lv5.method_10074();
			class_2680 lv7 = lv.method_8320(lv6);
			if (lv7.method_11610() != class_2464.field_11455 && lv.method_8602(lv5) > 3) {
				this.method_3928(lv7, lv, lv6, d, e, f, lv5, g, i, s, t, u);
			}
		}

		lv3.method_1350();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	private class_1941 method_3935() {
		return this.field_4676.field_4684;
	}

	private void method_3928(
		class_2680 arg, class_1941 arg2, class_2338 arg3, double d, double e, double f, class_2338 arg4, float g, float h, double i, double j, double k
	) {
		if (arg.method_11604(arg2, arg3)) {
			class_265 lv = arg.method_11606(this.method_3935(), arg4.method_10074());
			if (!lv.method_1110()) {
				class_289 lv2 = class_289.method_1348();
				class_287 lv3 = lv2.method_1349();
				double l = ((double)g - (e - ((double)arg4.method_10264() + j)) / 2.0) * 0.5 * (double)this.method_3935().method_8610(arg4);
				if (!(l < 0.0)) {
					if (l > 1.0) {
						l = 1.0;
					}

					class_238 lv4 = lv.method_1107();
					double m = (double)arg4.method_10263() + lv4.field_1323 + i;
					double n = (double)arg4.method_10263() + lv4.field_1320 + i;
					double o = (double)arg4.method_10264() + lv4.field_1322 + j + 0.015625;
					double p = (double)arg4.method_10260() + lv4.field_1321 + k;
					double q = (double)arg4.method_10260() + lv4.field_1324 + k;
					float r = (float)((d - m) / 2.0 / (double)h + 0.5);
					float s = (float)((d - n) / 2.0 / (double)h + 0.5);
					float t = (float)((f - p) / 2.0 / (double)h + 0.5);
					float u = (float)((f - q) / 2.0 / (double)h + 0.5);
					lv3.method_1315(m, o, p).method_1312((double)r, (double)t).method_1336(1.0F, 1.0F, 1.0F, (float)l).method_1344();
					lv3.method_1315(m, o, q).method_1312((double)r, (double)u).method_1336(1.0F, 1.0F, 1.0F, (float)l).method_1344();
					lv3.method_1315(n, o, q).method_1312((double)s, (double)u).method_1336(1.0F, 1.0F, 1.0F, (float)l).method_1344();
					lv3.method_1315(n, o, p).method_1312((double)s, (double)t).method_1336(1.0F, 1.0F, 1.0F, (float)l).method_1344();
				}
			}
		}
	}

	public static void method_3922(class_238 arg, double d, double e, double f) {
		GlStateManager.disableTexture();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		lv2.method_1331(d, e, f);
		lv2.method_1328(7, class_290.field_1588);
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1321).method_1318(0.0F, 0.0F, -1.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1321).method_1318(0.0F, 0.0F, -1.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1321).method_1318(0.0F, 0.0F, -1.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1321).method_1318(0.0F, 0.0F, -1.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1324).method_1318(0.0F, 0.0F, 1.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1324).method_1318(0.0F, 0.0F, 1.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1324).method_1318(0.0F, 0.0F, 1.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1324).method_1318(0.0F, 0.0F, 1.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1321).method_1318(0.0F, -1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1321).method_1318(0.0F, -1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1324).method_1318(0.0F, -1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1324).method_1318(0.0F, -1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1324).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1324).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1321).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1321).method_1318(0.0F, 1.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1324).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1324).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1325, arg.field_1321).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1323, arg.field_1322, arg.field_1321).method_1318(-1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1321).method_1318(1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1321).method_1318(1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1325, arg.field_1324).method_1318(1.0F, 0.0F, 0.0F).method_1344();
		lv2.method_1315(arg.field_1320, arg.field_1322, arg.field_1324).method_1318(1.0F, 0.0F, 0.0F).method_1344();
		lv.method_1350();
		lv2.method_1331(0.0, 0.0, 0.0);
		GlStateManager.enableTexture();
	}

	public void method_3939(class_1297 arg, double d, double e, double f, float g, float h) {
		if (this.field_4676.field_4692 != null) {
			if (this.field_4676.field_4692.field_1888 && this.field_4673 > 0.0F && !arg.method_5767() && this.field_4676.method_3951()) {
				double i = this.field_4676.method_3959(arg.field_5987, arg.field_6010, arg.field_6035);
				float j = (float)((1.0 - i / 256.0) * (double)this.field_4672);
				if (j > 0.0F) {
					this.method_3934(arg, d, e, f, j, h);
				}
			}

			if (arg.method_5862() && (!(arg instanceof class_1657) || !((class_1657)arg).method_7325())) {
				this.method_3938(arg, d, e, f, h);
			}
		}
	}

	public class_327 method_3932() {
		return this.field_4676.method_3949();
	}

	protected void method_3923(T arg, String string, double d, double e, double f, int i) {
		double g = arg.method_5858(this.field_4676.field_4686);
		if (!(g > (double)(i * i))) {
			boolean bl = arg.method_5715();
			float h = this.field_4676.field_4679;
			float j = this.field_4676.field_4677;
			boolean bl2 = this.field_4676.field_4692.field_1850 == 2;
			float k = arg.field_6019 + 0.5F - (bl ? 0.25F : 0.0F);
			int l = "deadmau5".equals(string) ? -10 : 0;
			class_757.method_3179(this.method_3932(), string, (float)d, (float)e + k, (float)f, l, h, j, bl2, bl);
		}
	}

	public class_898 method_3940() {
		return this.field_4676;
	}

	public boolean method_16894() {
		return false;
	}

	public void method_3937(T arg, double d, double e, double f, float g, float h) {
	}

	public void method_17146(T arg) {
		int i = arg.method_5635();
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
	}
}
