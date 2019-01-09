package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_758 {
	private final FloatBuffer field_4036 = class_311.method_1597(16);
	private final FloatBuffer field_4038 = class_311.method_1597(16);
	private float field_4034;
	private float field_4033;
	private float field_4032;
	private float field_4030 = -1.0F;
	private float field_4040 = -1.0F;
	private float field_4039 = -1.0F;
	private int field_4031 = -1;
	private int field_4041 = -1;
	private long field_4042 = -1L;
	private final class_757 field_4035;
	private final class_310 field_4037;

	public class_758(class_757 arg) {
		this.field_4035 = arg;
		this.field_4037 = arg.method_3204();
		this.field_4036.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
	}

	public void method_3210(float f) {
		class_1937 lv = this.field_4037.field_1687;
		class_1297 lv2 = this.field_4037.method_1560();
		class_2680 lv3 = class_295.method_1376(this.field_4037.field_1687, lv2, f);
		class_3610 lv4 = class_295.method_1374(this.field_4037.field_1687, lv2, f);
		if (lv4.method_15767(class_3486.field_15517)) {
			this.method_3213(lv2, lv, f);
		} else if (lv4.method_15767(class_3486.field_15518)) {
			this.field_4034 = 0.6F;
			this.field_4033 = 0.1F;
			this.field_4032 = 0.0F;
			this.field_4042 = -1L;
		} else {
			this.method_3208(lv2, lv, f);
			this.field_4042 = -1L;
		}

		double d = class_3532.method_16436((double)f, lv2.field_5971, lv2.field_6010) * lv.field_9247.method_12459();
		if (lv2 instanceof class_1309 && ((class_1309)lv2).method_6059(class_1294.field_5919)) {
			int i = ((class_1309)lv2).method_6112(class_1294.field_5919).method_5584();
			if (i < 20) {
				d *= (double)(1.0F - (float)i / 20.0F);
			} else {
				d = 0.0;
			}
		}

		if (d < 1.0) {
			if (d < 0.0) {
				d = 0.0;
			}

			d *= d;
			this.field_4034 = (float)((double)this.field_4034 * d);
			this.field_4033 = (float)((double)this.field_4033 * d);
			this.field_4032 = (float)((double)this.field_4032 * d);
		}

		if (this.field_4035.method_3195(f) > 0.0F) {
			float g = this.field_4035.method_3195(f);
			this.field_4034 = this.field_4034 * (1.0F - g) + this.field_4034 * 0.7F * g;
			this.field_4033 = this.field_4033 * (1.0F - g) + this.field_4033 * 0.6F * g;
			this.field_4032 = this.field_4032 * (1.0F - g) + this.field_4032 * 0.6F * g;
		}

		if (lv4.method_15767(class_3486.field_15517)) {
			float g = 0.0F;
			if (lv2 instanceof class_746) {
				class_746 lv5 = (class_746)lv2;
				g = lv5.method_3140();
			}

			float h = 1.0F / this.field_4034;
			if (h > 1.0F / this.field_4033) {
				h = 1.0F / this.field_4033;
			}

			if (h > 1.0F / this.field_4032) {
				h = 1.0F / this.field_4032;
			}

			this.field_4034 = this.field_4034 * (1.0F - g) + this.field_4034 * h * g;
			this.field_4033 = this.field_4033 * (1.0F - g) + this.field_4033 * h * g;
			this.field_4032 = this.field_4032 * (1.0F - g) + this.field_4032 * h * g;
		} else if (lv2 instanceof class_1309 && ((class_1309)lv2).method_6059(class_1294.field_5925)) {
			float gx = this.field_4035.method_3174((class_1309)lv2, f);
			float hx = 1.0F / this.field_4034;
			if (hx > 1.0F / this.field_4033) {
				hx = 1.0F / this.field_4033;
			}

			if (hx > 1.0F / this.field_4032) {
				hx = 1.0F / this.field_4032;
			}

			this.field_4034 = this.field_4034 * (1.0F - gx) + this.field_4034 * hx * gx;
			this.field_4033 = this.field_4033 * (1.0F - gx) + this.field_4033 * hx * gx;
			this.field_4032 = this.field_4032 * (1.0F - gx) + this.field_4032 * hx * gx;
		}

		GlStateManager.clearColor(this.field_4034, this.field_4033, this.field_4032, 0.0F);
	}

	private void method_3208(class_1297 arg, class_1937 arg2, float f) {
		float g = 0.25F + 0.75F * (float)this.field_4037.field_1690.field_1870 / 32.0F;
		g = 1.0F - (float)Math.pow((double)g, 0.25);
		class_243 lv = arg2.method_8548(this.field_4037.method_1560(), f);
		float h = (float)lv.field_1352;
		float i = (float)lv.field_1351;
		float j = (float)lv.field_1350;
		class_243 lv2 = arg2.method_8464(f);
		this.field_4034 = (float)lv2.field_1352;
		this.field_4033 = (float)lv2.field_1351;
		this.field_4032 = (float)lv2.field_1350;
		if (this.field_4037.field_1690.field_1870 >= 4) {
			double d = class_3532.method_15374(arg2.method_8442(f)) > 0.0F ? -1.0 : 1.0;
			class_243 lv3 = new class_243(d, 0.0, 0.0);
			float k = (float)arg.method_5828(f).method_1026(lv3);
			if (k < 0.0F) {
				k = 0.0F;
			}

			if (k > 0.0F) {
				float[] fs = arg2.field_9247.method_12446(arg2.method_8400(f), f);
				if (fs != null) {
					k *= fs[3];
					this.field_4034 = this.field_4034 * (1.0F - k) + fs[0] * k;
					this.field_4033 = this.field_4033 * (1.0F - k) + fs[1] * k;
					this.field_4032 = this.field_4032 * (1.0F - k) + fs[2] * k;
				}
			}
		}

		this.field_4034 = this.field_4034 + (h - this.field_4034) * g;
		this.field_4033 = this.field_4033 + (i - this.field_4033) * g;
		this.field_4032 = this.field_4032 + (j - this.field_4032) * g;
		float l = arg2.method_8430(f);
		if (l > 0.0F) {
			float m = 1.0F - l * 0.5F;
			float n = 1.0F - l * 0.4F;
			this.field_4034 *= m;
			this.field_4033 *= m;
			this.field_4032 *= n;
		}

		float m = arg2.method_8478(f);
		if (m > 0.0F) {
			float n = 1.0F - m * 0.5F;
			this.field_4034 *= n;
			this.field_4033 *= n;
			this.field_4032 *= n;
		}
	}

	private void method_3213(class_1297 arg, class_1941 arg2, float f) {
		long l = class_156.method_658();
		int i = arg2.method_8310(new class_2338(class_295.method_1379(arg, (double)f))).method_8713();
		if (this.field_4042 < 0L) {
			this.field_4031 = i;
			this.field_4041 = i;
			this.field_4042 = l;
		}

		int j = this.field_4031 >> 16 & 0xFF;
		int k = this.field_4031 >> 8 & 0xFF;
		int m = this.field_4031 & 0xFF;
		int n = this.field_4041 >> 16 & 0xFF;
		int o = this.field_4041 >> 8 & 0xFF;
		int p = this.field_4041 & 0xFF;
		float g = class_3532.method_15363((float)(l - this.field_4042) / 5000.0F, 0.0F, 1.0F);
		float h = class_3532.method_16439(g, (float)n, (float)j);
		float q = class_3532.method_16439(g, (float)o, (float)k);
		float r = class_3532.method_16439(g, (float)p, (float)m);
		this.field_4034 = h / 255.0F;
		this.field_4033 = q / 255.0F;
		this.field_4032 = r / 255.0F;
		if (this.field_4031 != i) {
			this.field_4031 = i;
			this.field_4041 = class_3532.method_15375(h) << 16 | class_3532.method_15375(q) << 8 | class_3532.method_15375(r);
			this.field_4042 = l;
		}
	}

	public void method_3211(int i, float f) {
		class_1297 lv = this.field_4037.method_1560();
		this.method_3212(false);
		GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_3610 lv2 = class_295.method_1374(this.field_4037.field_1687, lv, f);
		if (lv instanceof class_1309 && ((class_1309)lv).method_6059(class_1294.field_5919)) {
			float g = 5.0F;
			int j = ((class_1309)lv).method_6112(class_1294.field_5919).method_5584();
			if (j < 20) {
				g = class_3532.method_16439(1.0F - (float)j / 20.0F, 5.0F, this.field_4035.method_3193());
			}

			GlStateManager.fogMode(GlStateManager.class_1028.field_5095);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(g * 0.8F);
			} else {
				GlStateManager.fogStart(g * 0.25F);
				GlStateManager.fogEnd(g);
			}

			GLX.setupNvFogDistance();
		} else if (lv2.method_15767(class_3486.field_15517)) {
			GlStateManager.fogMode(GlStateManager.class_1028.field_5097);
			if (lv instanceof class_1309) {
				if (lv instanceof class_746) {
					class_746 lv3 = (class_746)lv;
					float h = 0.05F - lv3.method_3140() * lv3.method_3140() * 0.03F;
					class_1959 lv4 = lv3.field_6002.method_8310(new class_2338(lv3));
					if (lv4 == class_1972.field_9471 || lv4 == class_1972.field_9479) {
						h += 0.005F;
					}

					GlStateManager.fogDensity(h);
				} else {
					GlStateManager.fogDensity(0.05F);
				}
			} else {
				GlStateManager.fogDensity(0.1F);
			}
		} else if (lv2.method_15767(class_3486.field_15518)) {
			GlStateManager.fogMode(GlStateManager.class_1028.field_5096);
			GlStateManager.fogDensity(2.0F);
		} else {
			float gx = this.field_4035.method_3193();
			GlStateManager.fogMode(GlStateManager.class_1028.field_5095);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(gx);
			} else {
				GlStateManager.fogStart(gx * 0.75F);
				GlStateManager.fogEnd(gx);
			}

			GLX.setupNvFogDistance();
			if (this.field_4037.field_1687.field_9247.method_12453((int)lv.field_5987, (int)lv.field_6035) || this.field_4037.field_1705.method_1740().method_1800()) {
				GlStateManager.fogStart(gx * 0.05F);
				GlStateManager.fogEnd(Math.min(gx, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	public void method_3212(boolean bl) {
		if (bl) {
			GlStateManager.fog(2918, this.field_4036);
		} else {
			GlStateManager.fog(2918, this.method_3209());
		}
	}

	private FloatBuffer method_3209() {
		if (this.field_4030 != this.field_4034 || this.field_4040 != this.field_4033 || this.field_4039 != this.field_4032) {
			this.field_4038.clear();
			this.field_4038.put(this.field_4034).put(this.field_4033).put(this.field_4032).put(1.0F);
			this.field_4038.flip();
			this.field_4030 = this.field_4034;
			this.field_4040 = this.field_4033;
			this.field_4039 = this.field_4032;
		}

		return this.field_4038;
	}
}
