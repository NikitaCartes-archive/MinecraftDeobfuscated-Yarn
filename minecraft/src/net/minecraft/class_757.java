package net.minecraft;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_757 implements AutoCloseable, class_3302 {
	private static final Logger field_3993 = LogManager.getLogger();
	private static final class_2960 field_4011 = new class_2960("textures/environment/rain.png");
	private static final class_2960 field_4008 = new class_2960("textures/environment/snow.png");
	private final class_310 field_4015;
	private final class_3300 field_4018;
	private final Random field_3994 = new Random();
	private float field_4025;
	public final class_759 field_4012;
	private final class_330 field_4026;
	private int field_4027;
	private class_1297 field_4014;
	private final float field_4020 = 4.0F;
	private float field_4000 = 4.0F;
	private float field_4019;
	private float field_3999;
	private float field_4016;
	private float field_3997;
	private boolean field_3992 = true;
	private boolean field_4009 = true;
	private long field_4017;
	private long field_3998 = class_156.method_658();
	private final class_765 field_4028;
	private int field_3995;
	private final float[] field_3991 = new float[1024];
	private final float[] field_3989 = new float[1024];
	private final class_758 field_3990;
	private boolean field_4001;
	private double field_4005 = 1.0;
	private double field_3988;
	private double field_4004;
	private class_1799 field_4006;
	private int field_4007;
	private float field_4029;
	private float field_4003;
	private class_279 field_4024;
	private float field_4002;
	private float field_4022;
	private static final class_2960[] field_3996 = new class_2960[]{
		new class_2960("shaders/post/notch.json"),
		new class_2960("shaders/post/fxaa.json"),
		new class_2960("shaders/post/art.json"),
		new class_2960("shaders/post/bumpy.json"),
		new class_2960("shaders/post/blobs2.json"),
		new class_2960("shaders/post/pencil.json"),
		new class_2960("shaders/post/color_convolve.json"),
		new class_2960("shaders/post/deconverge.json"),
		new class_2960("shaders/post/flip.json"),
		new class_2960("shaders/post/invert.json"),
		new class_2960("shaders/post/ntsc.json"),
		new class_2960("shaders/post/outline.json"),
		new class_2960("shaders/post/phosphor.json"),
		new class_2960("shaders/post/scan_pincushion.json"),
		new class_2960("shaders/post/sobel.json"),
		new class_2960("shaders/post/bits.json"),
		new class_2960("shaders/post/desaturate.json"),
		new class_2960("shaders/post/green.json"),
		new class_2960("shaders/post/blur.json"),
		new class_2960("shaders/post/wobble.json"),
		new class_2960("shaders/post/blobs.json"),
		new class_2960("shaders/post/antialias.json"),
		new class_2960("shaders/post/creeper.json"),
		new class_2960("shaders/post/spider.json")
	};
	public static final int field_4010 = field_3996.length;
	private int field_4023 = field_4010;
	private boolean field_4013;
	private int field_4021;

	public class_757(class_310 arg, class_3300 arg2) {
		this.field_4015 = arg;
		this.field_4018 = arg2;
		this.field_4012 = arg.method_1489();
		this.field_4026 = new class_330(arg.method_1531());
		this.field_4028 = new class_765(this);
		this.field_3990 = new class_758(this);
		this.field_4024 = null;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = class_3532.method_15355(f * f + g * g);
				this.field_3991[i << 5 | j] = -g / h;
				this.field_3989[i << 5 | j] = f / h;
			}
		}
	}

	public void close() {
		this.field_4028.close();
		this.field_4026.close();
		this.method_3207();
	}

	public boolean method_3175() {
		return GLX.usePostProcess && this.field_4024 != null;
	}

	public void method_3207() {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		this.field_4023 = field_4010;
	}

	public void method_3184() {
		this.field_4013 = !this.field_4013;
	}

	public void method_3167(@Nullable class_1297 arg) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.close();
			}

			this.field_4024 = null;
			if (arg instanceof class_1548) {
				this.method_3168(new class_2960("shaders/post/creeper.json"));
			} else if (arg instanceof class_1628) {
				this.method_3168(new class_2960("shaders/post/spider.json"));
			} else if (arg instanceof class_1560) {
				this.method_3168(new class_2960("shaders/post/invert.json"));
			}
		}
	}

	private void method_3168(class_2960 arg) {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		try {
			this.field_4024 = new class_279(this.field_4015.method_1531(), this.field_4018, this.field_4015.method_1522(), arg);
			this.field_4024.method_1259(this.field_4015.field_1704.method_4489(), this.field_4015.field_1704.method_4506());
			this.field_4013 = true;
		} catch (IOException var3) {
			field_3993.warn("Failed to load shader: {}", arg, var3);
			this.field_4023 = field_4010;
			this.field_4013 = false;
		} catch (JsonSyntaxException var4) {
			field_3993.warn("Failed to load shader: {}", arg, var4);
			this.field_4023 = field_4010;
			this.field_4013 = false;
		}
	}

	@Override
	public void method_14491(class_3300 arg) {
		if (this.field_4024 != null) {
			this.field_4024.close();
		}

		this.field_4024 = null;
		if (this.field_4023 == field_4010) {
			this.method_3167(this.field_4015.method_1560());
		} else {
			this.method_3168(field_3996[this.field_4023]);
		}
	}

	public void method_3182() {
		if (GLX.usePostProcess && class_285.method_1308() == null) {
			class_285.method_1305();
		}

		this.method_3199();
		this.field_4028.method_3314();
		this.field_4000 = 4.0F;
		if (this.field_4015.method_1560() == null) {
			this.field_4015.method_1504(this.field_4015.field_1724);
		}

		this.field_4022 = this.field_4002;
		this.field_4002 = this.field_4002 + (this.field_4015.method_1560().method_5751() - this.field_4002) * 0.5F;
		this.field_4027++;
		this.field_4012.method_3220();
		this.method_3177();
		this.field_3997 = this.field_4016;
		if (this.field_4015.field_1705.method_1740().method_1797()) {
			this.field_4016 += 0.05F;
			if (this.field_4016 > 1.0F) {
				this.field_4016 = 1.0F;
			}
		} else if (this.field_4016 > 0.0F) {
			this.field_4016 -= 0.0125F;
		}

		if (this.field_4007 > 0) {
			this.field_4007--;
			if (this.field_4007 == 0) {
				this.field_4006 = null;
			}
		}
	}

	public class_279 method_3183() {
		return this.field_4024;
	}

	public void method_3169(int i, int j) {
		if (GLX.usePostProcess) {
			if (this.field_4024 != null) {
				this.field_4024.method_1259(i, j);
			}

			this.field_4015.field_1769.method_3242(i, j);
		}
	}

	public void method_3190(float f) {
		class_1297 lv = this.field_4015.method_1560();
		if (lv != null) {
			if (this.field_4015.field_1687 != null) {
				this.field_4015.method_16011().method_15396("pick");
				this.field_4015.field_1692 = null;
				double d = (double)this.field_4015.field_1761.method_2904();
				this.field_4015.field_1765 = lv.method_5745(d, f, class_242.field_1348);
				class_243 lv2 = lv.method_5836(f);
				boolean bl = false;
				int i = 3;
				double e = d;
				if (this.field_4015.field_1761.method_2926()) {
					e = 6.0;
					d = e;
				} else {
					if (d > 3.0) {
						bl = true;
					}

					d = d;
				}

				if (this.field_4015.field_1765 != null) {
					e = this.field_4015.field_1765.field_1329.method_1022(lv2);
				}

				class_243 lv3 = lv.method_5828(1.0F);
				class_243 lv4 = lv2.method_1031(lv3.field_1352 * d, lv3.field_1351 * d, lv3.field_1350 * d);
				this.field_4014 = null;
				class_243 lv5 = null;
				float g = 1.0F;
				List<class_1297> list = this.field_4015
					.field_1687
					.method_8333(
						lv,
						lv.method_5829().method_1012(lv3.field_1352 * d, lv3.field_1351 * d, lv3.field_1350 * d).method_1009(1.0, 1.0, 1.0),
						class_1301.field_6155.and(class_1297::method_5863)
					);
				double h = e;

				for (int j = 0; j < list.size(); j++) {
					class_1297 lv6 = (class_1297)list.get(j);
					class_238 lv7 = lv6.method_5829().method_1014((double)lv6.method_5871());
					class_239 lv8 = lv7.method_1004(lv2, lv4);
					if (lv7.method_1006(lv2)) {
						if (h >= 0.0) {
							this.field_4014 = lv6;
							lv5 = lv8 == null ? lv2 : lv8.field_1329;
							h = 0.0;
						}
					} else if (lv8 != null) {
						double k = lv2.method_1022(lv8.field_1329);
						if (k < h || h == 0.0) {
							if (lv6.method_5668() == lv.method_5668()) {
								if (h == 0.0) {
									this.field_4014 = lv6;
									lv5 = lv8.field_1329;
								}
							} else {
								this.field_4014 = lv6;
								lv5 = lv8.field_1329;
								h = k;
							}
						}
					}
				}

				if (this.field_4014 != null && bl && lv2.method_1022(lv5) > 3.0) {
					this.field_4014 = null;
					this.field_4015.field_1765 = new class_239(class_239.class_240.field_1333, lv5, null, new class_2338(lv5));
				}

				if (this.field_4014 != null && (h < e || this.field_4015.field_1765 == null)) {
					this.field_4015.field_1765 = new class_239(this.field_4014, lv5);
					if (this.field_4014 instanceof class_1309 || this.field_4014 instanceof class_1533) {
						this.field_4015.field_1692 = this.field_4014;
					}
				}

				this.field_4015.method_16011().method_15407();
			}
		}
	}

	private void method_3199() {
		float f = 1.0F;
		if (this.field_4015.method_1560() instanceof class_742) {
			class_742 lv = (class_742)this.field_4015.method_1560();
			f = lv.method_3118();
		}

		this.field_3999 = this.field_4019;
		this.field_4019 = this.field_4019 + (f - this.field_4019) * 0.5F;
		if (this.field_4019 > 1.5F) {
			this.field_4019 = 1.5F;
		}

		if (this.field_4019 < 0.1F) {
			this.field_4019 = 0.1F;
		}
	}

	private double method_3196(float f, boolean bl) {
		if (this.field_4001) {
			return 90.0;
		} else {
			class_1297 lv = this.field_4015.method_1560();
			double d = 70.0;
			if (bl) {
				d = this.field_4015.field_1690.field_1826;
				d *= (double)class_3532.method_16439(f, this.field_3999, this.field_4019);
			}

			if (lv instanceof class_1309 && ((class_1309)lv).method_6032() <= 0.0F) {
				float g = (float)((class_1309)lv).field_6213 + f;
				d /= (double)((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
			}

			class_3610 lv2 = class_295.method_1374(this.field_4015.field_1687, lv, f);
			if (!lv2.method_15769()) {
				d = d * 60.0 / 70.0;
			}

			return d;
		}
	}

	private void method_3198(float f) {
		if (this.field_4015.method_1560() instanceof class_1309) {
			class_1309 lv = (class_1309)this.field_4015.method_1560();
			float g = (float)lv.field_6235 - f;
			if (lv.method_6032() <= 0.0F) {
				float h = (float)lv.field_6213 + f;
				GlStateManager.rotatef(40.0F - 8000.0F / (h + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (g < 0.0F) {
				return;
			}

			g /= (float)lv.field_6254;
			g = class_3532.method_15374(g * g * g * g * (float) Math.PI);
			float h = lv.field_6271;
			GlStateManager.rotatef(-h, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-g * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
		}
	}

	private void method_3186(float f) {
		if (this.field_4015.method_1560() instanceof class_1657) {
			class_1657 lv = (class_1657)this.field_4015.method_1560();
			float g = lv.field_5973 - lv.field_6039;
			float h = -(lv.field_5973 + g * f);
			float i = class_3532.method_16439(f, lv.field_7505, lv.field_7483);
			float j = class_3532.method_16439(f, lv.field_6286, lv.field_6223);
			GlStateManager.translatef(class_3532.method_15374(h * (float) Math.PI) * i * 0.5F, -Math.abs(class_3532.method_15362(h * (float) Math.PI) * i), 0.0F);
			GlStateManager.rotatef(class_3532.method_15374(h * (float) Math.PI) * i * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(Math.abs(class_3532.method_15362(h * (float) Math.PI - 0.2F) * i) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(j, 1.0F, 0.0F, 0.0F);
		}
	}

	private void method_3197(float f) {
		class_1297 lv = this.field_4015.method_1560();
		float g = class_3532.method_16439(f, this.field_4022, this.field_4002);
		double d = class_3532.method_16436((double)f, lv.field_6014, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_6036, lv.field_6010) + (double)lv.method_5751();
		double h = class_3532.method_16436((double)f, lv.field_5969, lv.field_6035);
		if (lv instanceof class_1309 && ((class_1309)lv).method_6113()) {
			g = (float)((double)g + 1.0);
			GlStateManager.translatef(0.0F, 0.3F, 0.0F);
			if (!this.field_4015.field_1690.field_1821) {
				class_2338 lv2 = new class_2338(lv);
				class_2680 lv3 = this.field_4015.field_1687.method_8320(lv2);
				class_2248 lv4 = lv3.method_11614();
				if (lv4 instanceof class_2244) {
					GlStateManager.rotatef(((class_2350)lv3.method_11654(class_2244.field_11177)).method_10144(), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotatef(class_3532.method_16439(f, lv.field_5982, lv.field_6031) + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(class_3532.method_16439(f, lv.field_6004, lv.field_5965), -1.0F, 0.0F, 0.0F);
			}
		} else if (this.field_4015.field_1690.field_1850 > 0) {
			double i = (double)class_3532.method_16439(f, this.field_4000, 4.0F);
			if (this.field_4015.field_1690.field_1821) {
				GlStateManager.translatef(0.0F, 0.0F, (float)(-i));
			} else {
				float j = lv.field_6031;
				float k = lv.field_5965;
				if (this.field_4015.field_1690.field_1850 == 2) {
					k += 180.0F;
				}

				double l = (double)(-class_3532.method_15374(j * (float) (Math.PI / 180.0)) * class_3532.method_15362(k * (float) (Math.PI / 180.0))) * i;
				double m = (double)(class_3532.method_15362(j * (float) (Math.PI / 180.0)) * class_3532.method_15362(k * (float) (Math.PI / 180.0))) * i;
				double n = (double)(-class_3532.method_15374(k * (float) (Math.PI / 180.0))) * i;

				for (int o = 0; o < 8; o++) {
					float p = (float)((o & 1) * 2 - 1);
					float q = (float)((o >> 1 & 1) * 2 - 1);
					float r = (float)((o >> 2 & 1) * 2 - 1);
					p *= 0.1F;
					q *= 0.1F;
					r *= 0.1F;
					class_239 lv5 = this.field_4015
						.field_1687
						.method_8549(
							new class_243(d + (double)p, e + (double)q, h + (double)r), new class_243(d - l + (double)p + (double)r, e - n + (double)q, h - m + (double)r)
						);
					if (lv5 != null) {
						double s = lv5.field_1329.method_1022(new class_243(d, e, h));
						if (s < i) {
							i = s;
						}
					}
				}

				if (this.field_4015.field_1690.field_1850 == 2) {
					GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotatef(lv.field_5965 - k, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(lv.field_6031 - j, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(0.0F, 0.0F, (float)(-i));
				GlStateManager.rotatef(j - lv.field_6031, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(k - lv.field_5965, 1.0F, 0.0F, 0.0F);
			}
		} else if (!this.field_4001) {
			GlStateManager.translatef(0.0F, 0.0F, 0.05F);
		}

		if (!this.field_4015.field_1690.field_1821) {
			GlStateManager.rotatef(lv.method_5695(f), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(lv.method_5705(f) + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.translatef(0.0F, -g, 0.0F);
	}

	private void method_3185(float f) {
		this.field_4025 = (float)(this.field_4015.field_1690.field_1870 * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		if (this.field_4005 != 1.0) {
			GlStateManager.translatef((float)this.field_3988, (float)(-this.field_4004), 0.0F);
			GlStateManager.scaled(this.field_4005, this.field_4005, 1.0);
		}

		GlStateManager.multMatrix(
			class_1159.method_4929(
				this.method_3196(f, true),
				(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
				0.05F,
				this.field_4025 * class_3532.field_15724
			)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.method_3198(f);
		if (this.field_4015.field_1690.field_1891) {
			this.method_3186(f);
		}

		float g = class_3532.method_16439(f, this.field_4015.field_1724.field_3911, this.field_4015.field_1724.field_3929);
		if (g > 0.0F) {
			int i = 20;
			if (this.field_4015.field_1724.method_6059(class_1294.field_5916)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			GlStateManager.rotatef(((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scalef(1.0F / h, 1.0F, 1.0F);
			GlStateManager.rotatef(-((float)this.field_4027 + f) * (float)i, 0.0F, 1.0F, 1.0F);
		}

		this.method_3197(f);
	}

	private void method_3172(float f) {
		if (!this.field_4001) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				class_1159.method_4929(
					this.method_3196(f, false),
					(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
					0.05F,
					this.field_4025 * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.pushMatrix();
			this.method_3198(f);
			if (this.field_4015.field_1690.field_1891) {
				this.method_3186(f);
			}

			boolean bl = this.field_4015.method_1560() instanceof class_1309 && ((class_1309)this.field_4015.method_1560()).method_6113();
			if (this.field_4015.field_1690.field_1850 == 0
				&& !bl
				&& !this.field_4015.field_1690.field_1842
				&& this.field_4015.field_1761.method_2920() != class_1934.field_9219) {
				this.method_3180();
				this.field_4012.method_3225(f);
				this.method_3187();
			}

			GlStateManager.popMatrix();
			if (this.field_4015.field_1690.field_1850 == 0 && !bl) {
				this.field_4012.method_3232(f);
				this.method_3198(f);
			}

			if (this.field_4015.field_1690.field_1891) {
				this.method_3186(f);
			}
		}
	}

	public void method_3187() {
		this.field_4028.method_3315();
	}

	public void method_3180() {
		this.field_4028.method_3316();
	}

	public float method_3174(class_1309 arg, float f) {
		int i = arg.method_6112(class_1294.field_5925).method_5584();
		return i > 200 ? 1.0F : 0.7F + class_3532.method_15374(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void method_3192(float f, long l, boolean bl) {
		if (!this.field_4015.method_1569()
			&& this.field_4015.field_1690.field_1837
			&& (!this.field_4015.field_1690.field_1854 || !this.field_4015.field_1729.method_1609())) {
			if (class_156.method_658() - this.field_3998 > 500L) {
				this.field_4015.method_16889();
			}
		} else {
			this.field_3998 = class_156.method_658();
		}

		if (!this.field_4015.field_1743) {
			int i = (int)(this.field_4015.field_1729.method_1603() * (double)this.field_4015.field_1704.method_4486() / (double)this.field_4015.field_1704.method_4480());
			int j = (int)(this.field_4015.field_1729.method_1604() * (double)this.field_4015.field_1704.method_4502() / (double)this.field_4015.field_1704.method_4507());
			int k = this.field_4015.field_1690.field_1909;
			if (bl && this.field_4015.field_1687 != null) {
				this.field_4015.method_16011().method_15396("level");
				int m = Math.min(class_310.method_1497(), k);
				m = Math.max(m, 60);
				long n = class_156.method_648() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.method_3188(f, class_156.method_648() + o);
				if (this.field_4015.method_1496() && this.field_4017 < class_156.method_658() - 1000L) {
					this.field_4017 = class_156.method_658();
					if (!this.field_4015.method_1576().method_3771()) {
						this.method_3176();
					}
				}

				if (GLX.usePostProcess) {
					this.field_4015.field_1769.method_3254();
					if (this.field_4024 != null && this.field_4013) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.field_4024.method_1258(f);
						GlStateManager.popMatrix();
					}

					this.field_4015.method_1522().method_1235(true);
				}

				this.field_4015.method_16011().method_15405("gui");
				if (!this.field_4015.field_1690.field_1842 || this.field_4015.field_1755 != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.field_4015.field_1704.method_4493(class_310.field_1703);
					this.method_3171(this.field_4015.field_1704.method_4486(), this.field_4015.field_1704.method_4502(), f);
					this.field_4015.field_1705.method_1753(f);
				}

				this.field_4015.method_16011().method_15407();
			} else {
				GlStateManager.viewport(0, 0, this.field_4015.field_1704.method_4489(), this.field_4015.field_1704.method_4506());
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.field_4015.field_1704.method_4493(class_310.field_1703);
			}

			if (this.field_4015.field_1755 != null) {
				GlStateManager.clear(256, class_310.field_1703);

				try {
					this.field_4015.field_1755.method_2214(i, j, this.field_4015.method_1534());
				} catch (Throwable var13) {
					class_128 lv = class_128.method_560(var13, "Rendering screen");
					class_129 lv2 = lv.method_562("Screen render details");
					lv2.method_577("Screen name", () -> this.field_4015.field_1755.getClass().getCanonicalName());
					lv2.method_577(
						"Mouse location",
						() -> String.format(
								Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.field_4015.field_1729.method_1603(), this.field_4015.field_1729.method_1604()
							)
					);
					lv2.method_577(
						"Screen size",
						() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.field_4015.field_1704.method_4486(),
								this.field_4015.field_1704.method_4502(),
								this.field_4015.field_1704.method_4489(),
								this.field_4015.field_1704.method_4506(),
								this.field_4015.field_1704.method_4495()
							)
					);
					throw new class_148(lv);
				}
			}
		}
	}

	private void method_3176() {
		if (this.field_4015.field_1769.method_3246() > 10 && this.field_4015.field_1769.method_3281() && !this.field_4015.method_1576().method_3771()) {
			class_1011 lv = class_318.method_1663(this.field_4015.field_1704.method_4489(), this.field_4015.field_1704.method_4506(), this.field_4015.method_1522());
			class_3306.field_14301.execute(() -> {
				int i = lv.method_4307();
				int j = lv.method_4323();
				int k = 0;
				int l = 0;
				if (i > j) {
					k = (i - j) / 2;
					i = j;
				} else {
					l = (j - i) / 2;
					j = i;
				}

				try (class_1011 lvx = new class_1011(64, 64, false)) {
					lv.method_4300(k, l, i, j, lvx);
					lvx.method_4325(this.field_4015.method_1576().method_3725());
				} catch (IOException var27) {
					field_3993.warn("Couldn't save auto screenshot", (Throwable)var27);
				} finally {
					lv.close();
				}
			});
		}
	}

	public void method_3200(float f) {
		this.field_4015.field_1704.method_4493(class_310.field_1703);
	}

	private boolean method_3202() {
		if (!this.field_4009) {
			return false;
		} else {
			class_1297 lv = this.field_4015.method_1560();
			boolean bl = lv instanceof class_1657 && !this.field_4015.field_1690.field_1842;
			if (bl && !((class_1657)lv).field_7503.field_7476) {
				class_1799 lv2 = ((class_1657)lv).method_6047();
				if (this.field_4015.field_1765 != null && this.field_4015.field_1765.field_1330 == class_239.class_240.field_1332) {
					class_2338 lv3 = this.field_4015.field_1765.method_1015();
					class_2680 lv4 = this.field_4015.field_1687.method_8320(lv3);
					if (this.field_4015.field_1761.method_2920() == class_1934.field_9219) {
						bl = lv4.method_17526(this.field_4015.field_1687, lv3) != null;
					} else {
						class_2694 lv5 = new class_2694(this.field_4015.field_1687, lv3, false);
						bl = !lv2.method_7960()
							&& (lv2.method_7940(this.field_4015.field_1687.method_8514(), lv5) || lv2.method_7944(this.field_4015.field_1687.method_8514(), lv5));
					}
				}
			}

			return bl;
		}
	}

	public void method_3188(float f, long l) {
		this.field_4028.method_3313(f);
		if (this.field_4015.method_1560() == null) {
			this.field_4015.method_1504(this.field_4015.field_1724);
		}

		this.method_3190(f);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.5F);
		this.field_4015.method_16011().method_15396("center");
		this.method_3178(f, l);
		this.field_4015.method_16011().method_15407();
	}

	private void method_3178(float f, long l) {
		class_761 lv = this.field_4015.field_1769;
		class_702 lv2 = this.field_4015.field_1713;
		boolean bl = this.method_3202();
		GlStateManager.enableCull();
		this.field_4015.method_16011().method_15405("clear");
		GlStateManager.viewport(0, 0, this.field_4015.field_1704.method_4489(), this.field_4015.field_1704.method_4506());
		this.field_3990.method_3210(f);
		GlStateManager.clear(16640, class_310.field_1703);
		this.field_4015.method_16011().method_15405("camera");
		this.method_3185(f);
		class_857 lv3 = class_855.method_3696();
		class_295.method_1373(this.field_4015.field_1724, this.field_4015.field_1690.field_1850 == 2, this.field_4025, lv3);
		this.field_4015.method_16011().method_15405("culling");
		class_856 lv4 = new class_858(lv3);
		class_1297 lv5 = this.field_4015.method_1560();
		double d = class_3532.method_16436((double)f, lv5.field_6038, lv5.field_5987);
		double e = class_3532.method_16436((double)f, lv5.field_5971, lv5.field_6010);
		double g = class_3532.method_16436((double)f, lv5.field_5989, lv5.field_6035);
		lv4.method_3700(d, e, g);
		if (this.field_4015.field_1690.field_1870 >= 4) {
			this.field_3990.method_3211(-1, f);
			this.field_4015.method_16011().method_15405("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				class_1159.method_4929(
					this.method_3196(f, true),
					(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
					0.05F,
					this.field_4025 * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			lv.method_3257(f);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				class_1159.method_4929(
					this.method_3196(f, true),
					(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
					0.05F,
					this.field_4025 * class_3532.field_15724
				)
			);
			GlStateManager.matrixMode(5888);
		}

		this.field_3990.method_3211(0, f);
		GlStateManager.shadeModel(7425);
		if (lv5.field_6010 + (double)lv5.method_5751() < 128.0) {
			this.method_3206(lv, f, d, e, g);
		}

		this.field_4015.method_16011().method_15405("prepareterrain");
		this.field_3990.method_3211(0, f);
		this.field_4015.method_1531().method_4618(class_1059.field_5275);
		class_308.method_1450();
		this.field_4015.method_16011().method_15405("terrain_setup");
		this.field_4015.field_1687.method_2935().method_12130().method_15563(Integer.MAX_VALUE, true, true);
		lv.method_3273(lv5, f, lv4, this.field_4021++, this.field_4015.field_1724.method_7325());
		this.field_4015.method_16011().method_15405("updatechunks");
		this.field_4015.field_1769.method_3269(l);
		this.field_4015.method_16011().method_15405("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		lv.method_3251(class_1921.field_9178, (double)f, lv5);
		GlStateManager.enableAlphaTest();
		lv.method_3251(class_1921.field_9175, (double)f, lv5);
		this.field_4015.method_1531().method_4619(class_1059.field_5275).method_4626(false, false);
		lv.method_3251(class_1921.field_9174, (double)f, lv5);
		this.field_4015.method_1531().method_4619(class_1059.field_5275).method_4627();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		class_308.method_1452();
		this.field_4015.method_16011().method_15405("entities");
		lv.method_3271(lv5, lv4, f);
		class_308.method_1450();
		this.method_3187();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.field_4015.field_1765 != null) {
			class_1657 lv6 = (class_1657)lv5;
			GlStateManager.disableAlphaTest();
			this.field_4015.method_16011().method_15405("outline");
			lv.method_3294(lv6, this.field_4015.field_1765, 0, f);
			GlStateManager.enableAlphaTest();
		}

		if (this.field_4015.field_1709.method_3710()) {
			this.field_4015.field_1709.method_3709(f, l);
		}

		this.field_4015.method_16011().method_15405("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5078, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		this.field_4015.method_1531().method_4619(class_1059.field_5275).method_4626(false, false);
		lv.method_3256(class_289.method_1348(), class_289.method_1348().method_1349(), lv5, f);
		this.field_4015.method_1531().method_4619(class_1059.field_5275).method_4627();
		GlStateManager.disableBlend();
		this.method_3180();
		this.field_4015.method_16011().method_15405("litParticles");
		lv2.method_3060(lv5, f);
		class_308.method_1450();
		this.field_3990.method_3211(0, f);
		this.field_4015.method_16011().method_15405("particles");
		lv2.method_3049(lv5, f);
		this.method_3187();
		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.field_4015.method_16011().method_15405("weather");
		this.method_3170(f);
		GlStateManager.depthMask(true);
		lv.method_3243(lv5, f);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.field_3990.method_3211(0, f);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.field_4015.method_1531().method_4618(class_1059.field_5275);
		GlStateManager.shadeModel(7425);
		this.field_4015.method_16011().method_15405("translucent");
		lv.method_3251(class_1921.field_9179, (double)f, lv5);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		if (lv5.field_6010 + (double)lv5.method_5751() >= 128.0) {
			this.field_4015.method_16011().method_15405("aboveClouds");
			this.method_3206(lv, f, d, e, g);
		}

		this.field_4015.method_16011().method_15405("hand");
		if (this.field_3992) {
			GlStateManager.clear(256, class_310.field_1703);
			this.method_3172(f);
		}
	}

	private void method_3206(class_761 arg, float f, double d, double e, double g) {
		if (this.field_4015.field_1690.method_1632() != 0) {
			this.field_4015.method_16011().method_15405("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				class_1159.method_4929(
					this.method_3196(f, true),
					(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
					0.05F,
					this.field_4025 * 4.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.field_3990.method_3211(0, f);
			arg.method_3259(f, d, e, g);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(
				class_1159.method_4929(
					this.method_3196(f, true),
					(float)this.field_4015.field_1704.method_4489() / (float)this.field_4015.field_1704.method_4506(),
					0.05F,
					this.field_4025 * class_3532.field_15724
				)
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3177() {
		float f = this.field_4015.field_1687.method_8430(1.0F);
		if (!this.field_4015.field_1690.field_1833) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.field_3994.setSeed((long)this.field_4027 * 312987231L);
			class_1297 lv = this.field_4015.method_1560();
			class_1941 lv2 = this.field_4015.field_1687;
			class_2338 lv3 = new class_2338(lv);
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.field_4015.field_1690.field_1882 == 1) {
				k >>= 1;
			} else if (this.field_4015.field_1690.field_1882 == 2) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				class_2338 lv4 = lv2.method_8598(
					class_2902.class_2903.field_13197,
					lv3.method_10069(this.field_3994.nextInt(10) - this.field_3994.nextInt(10), 0, this.field_3994.nextInt(10) - this.field_3994.nextInt(10))
				);
				class_1959 lv5 = lv2.method_8310(lv4);
				class_2338 lv6 = lv4.method_10074();
				if (lv4.method_10264() <= lv3.method_10264() + 10
					&& lv4.method_10264() >= lv3.method_10264() - 10
					&& lv5.method_8694() == class_1959.class_1963.field_9382
					&& lv5.method_8707(lv4) >= 0.15F) {
					double h = this.field_3994.nextDouble();
					double m = this.field_3994.nextDouble();
					class_2680 lv7 = lv2.method_8320(lv6);
					class_3610 lv8 = lv2.method_8316(lv4);
					class_265 lv9 = lv7.method_11628(lv2, lv6);
					double n = lv9.method_1102(class_2350.class_2351.field_11052, h, m);
					double o = (double)lv8.method_15763();
					double p;
					double q;
					if (n >= o) {
						p = n;
						q = lv9.method_1093(class_2350.class_2351.field_11052, h, m);
					} else {
						p = 0.0;
						q = 0.0;
					}

					if (p > -Double.MAX_VALUE) {
						if (!lv8.method_15767(class_3486.field_15518) && lv7.method_11614() != class_2246.field_10092 && lv7.method_11614() != class_2246.field_17350) {
							if (this.field_3994.nextInt(++j) == 0) {
								d = (double)lv6.method_10263() + h;
								e = (double)((float)lv6.method_10264() + 0.1F) + p - 1.0;
								g = (double)lv6.method_10260() + m;
							}

							this.field_4015
								.field_1687
								.method_8406(
									class_2398.field_11242, (double)lv6.method_10263() + h, (double)((float)lv6.method_10264() + 0.1F) + p, (double)lv6.method_10260() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.field_4015
								.field_1687
								.method_8406(
									class_2398.field_11251, (double)lv4.method_10263() + h, (double)((float)lv4.method_10264() + 0.1F) - q, (double)lv4.method_10260() + m, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (j > 0 && this.field_3994.nextInt(3) < this.field_3995++) {
				this.field_3995 = 0;
				if (e > (double)(lv3.method_10264() + 1)
					&& lv2.method_8598(class_2902.class_2903.field_13197, lv3).method_10264() > class_3532.method_15375((float)lv3.method_10264())) {
					this.field_4015.field_1687.method_8486(d, e, g, class_3417.field_15020, class_3419.field_15252, 0.1F, 0.5F, false);
				} else {
					this.field_4015.field_1687.method_8486(d, e, g, class_3417.field_14946, class_3419.field_15252, 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void method_3170(float f) {
		float g = this.field_4015.field_1687.method_8430(f);
		if (!(g <= 0.0F)) {
			this.method_3180();
			class_1297 lv = this.field_4015.method_1560();
			class_1937 lv2 = this.field_4015.field_1687;
			int i = class_3532.method_15357(lv.field_5987);
			int j = class_3532.method_15357(lv.field_6010);
			int k = class_3532.method_15357(lv.field_6035);
			class_289 lv3 = class_289.method_1348();
			class_287 lv4 = lv3.method_1349();
			GlStateManager.disableCull();
			GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.alphaFunc(516, 0.1F);
			double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
			double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
			double h = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
			int l = class_3532.method_15357(e);
			int m = 5;
			if (this.field_4015.field_1690.field_1833) {
				m = 10;
			}

			int n = -1;
			float o = (float)this.field_4027 + f;
			lv4.method_1331(-d, -e, -h);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			class_2338.class_2339 lv5 = new class_2338.class_2339();

			for (int p = k - m; p <= k + m; p++) {
				for (int q = i - m; q <= i + m; q++) {
					int r = (p - k + 16) * 32 + q - i + 16;
					double s = (double)this.field_3991[r] * 0.5;
					double t = (double)this.field_3989[r] * 0.5;
					lv5.method_10103(q, 0, p);
					class_1959 lv6 = lv2.method_8310(lv5);
					if (lv6.method_8694() != class_1959.class_1963.field_9384) {
						int u = lv2.method_8598(class_2902.class_2903.field_13197, lv5).method_10264();
						int v = j - m;
						int w = j + m;
						if (v < u) {
							v = u;
						}

						if (w < u) {
							w = u;
						}

						int x = u;
						if (u < l) {
							x = l;
						}

						if (v != w) {
							this.field_3994.setSeed((long)(q * q * 3121 + q * 45238971 ^ p * p * 418711 + p * 13761));
							lv5.method_10103(q, v, p);
							float y = lv6.method_8707(lv5);
							if (y >= 0.15F) {
								if (n != 0) {
									if (n >= 0) {
										lv3.method_1350();
									}

									n = 0;
									this.field_4015.method_1531().method_4618(field_4011);
									lv4.method_1328(7, class_290.field_1584);
								}

								double z = -((double)(this.field_4027 + q * q * 3121 + q * 45238971 + p * p * 418711 + p * 13761 & 31) + (double)f)
									/ 32.0
									* (3.0 + this.field_3994.nextDouble());
								double aa = (double)((float)q + 0.5F) - lv.field_5987;
								double ab = (double)((float)p + 0.5F) - lv.field_6035;
								float ac = class_3532.method_15368(aa * aa + ab * ab) / (float)m;
								float ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * g;
								lv5.method_10103(q, x, p);
								int ae = lv2.method_8313(lv5, 0);
								int af = ae >> 16 & 65535;
								int ag = ae & 65535;
								lv4.method_1315((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.method_1312(0.0, (double)v * 0.25 + z)
									.method_1336(1.0F, 1.0F, 1.0F, ad)
									.method_1313(af, ag)
									.method_1344();
								lv4.method_1315((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.method_1312(1.0, (double)v * 0.25 + z)
									.method_1336(1.0F, 1.0F, 1.0F, ad)
									.method_1313(af, ag)
									.method_1344();
								lv4.method_1315((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.method_1312(1.0, (double)w * 0.25 + z)
									.method_1336(1.0F, 1.0F, 1.0F, ad)
									.method_1313(af, ag)
									.method_1344();
								lv4.method_1315((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.method_1312(0.0, (double)w * 0.25 + z)
									.method_1336(1.0F, 1.0F, 1.0F, ad)
									.method_1313(af, ag)
									.method_1344();
							} else {
								if (n != 1) {
									if (n >= 0) {
										lv3.method_1350();
									}

									n = 1;
									this.field_4015.method_1531().method_4618(field_4008);
									lv4.method_1328(7, class_290.field_1584);
								}

								double z = (double)(-((float)(this.field_4027 & 511) + f) / 512.0F);
								double aa = this.field_3994.nextDouble() + (double)o * 0.01 * (double)((float)this.field_3994.nextGaussian());
								double ab = this.field_3994.nextDouble() + (double)(o * (float)this.field_3994.nextGaussian()) * 0.001;
								double ah = (double)((float)q + 0.5F) - lv.field_5987;
								double ai = (double)((float)p + 0.5F) - lv.field_6035;
								float aj = class_3532.method_15368(ah * ah + ai * ai) / (float)m;
								float ak = ((1.0F - aj * aj) * 0.3F + 0.5F) * g;
								lv5.method_10103(q, x, p);
								int al = (lv2.method_8313(lv5, 0) * 3 + 15728880) / 4;
								int am = al >> 16 & 65535;
								int an = al & 65535;
								lv4.method_1315((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.method_1312(0.0 + aa, (double)v * 0.25 + z + ab)
									.method_1336(1.0F, 1.0F, 1.0F, ak)
									.method_1313(am, an)
									.method_1344();
								lv4.method_1315((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.method_1312(1.0 + aa, (double)v * 0.25 + z + ab)
									.method_1336(1.0F, 1.0F, 1.0F, ak)
									.method_1313(am, an)
									.method_1344();
								lv4.method_1315((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.method_1312(1.0 + aa, (double)w * 0.25 + z + ab)
									.method_1336(1.0F, 1.0F, 1.0F, ak)
									.method_1313(am, an)
									.method_1344();
								lv4.method_1315((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.method_1312(0.0 + aa, (double)w * 0.25 + z + ab)
									.method_1336(1.0F, 1.0F, 1.0F, ak)
									.method_1313(am, an)
									.method_1344();
							}
						}
					}
				}
			}

			if (n >= 0) {
				lv3.method_1350();
			}

			lv4.method_1331(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			this.method_3187();
		}
	}

	public void method_3201(boolean bl) {
		this.field_3990.method_3212(bl);
	}

	public void method_3203() {
		this.field_4006 = null;
		this.field_4026.method_1771();
	}

	public class_330 method_3194() {
		return this.field_4026;
	}

	public static void method_3179(class_327 arg, String string, float f, float g, float h, int i, float j, float k, boolean bl, boolean bl2) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(f, g, h);
		GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-j, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(bl ? -1 : 1) * k, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		if (!bl2) {
			GlStateManager.disableDepthTest();
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		int l = arg.method_1727(string) / 2;
		GlStateManager.disableTexture();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1576);
		lv2.method_1315((double)(-l - 1), (double)(-1 + i), 0.0).method_1336(0.0F, 0.0F, 0.0F, 0.25F).method_1344();
		lv2.method_1315((double)(-l - 1), (double)(8 + i), 0.0).method_1336(0.0F, 0.0F, 0.0F, 0.25F).method_1344();
		lv2.method_1315((double)(l + 1), (double)(8 + i), 0.0).method_1336(0.0F, 0.0F, 0.0F, 0.25F).method_1344();
		lv2.method_1315((double)(l + 1), (double)(-1 + i), 0.0).method_1336(0.0F, 0.0F, 0.0F, 0.25F).method_1344();
		lv.method_1350();
		GlStateManager.enableTexture();
		if (!bl2) {
			arg.method_1729(string, (float)(-arg.method_1727(string) / 2), (float)i, 553648127);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.depthMask(true);
		arg.method_1729(string, (float)(-arg.method_1727(string) / 2), (float)i, bl2 ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public void method_3189(class_1799 arg) {
		this.field_4006 = arg;
		this.field_4007 = 40;
		this.field_4029 = this.field_3994.nextFloat() * 2.0F - 1.0F;
		this.field_4003 = this.field_3994.nextFloat() * 2.0F - 1.0F;
	}

	private void method_3171(int i, int j, float f) {
		if (this.field_4006 != null && this.field_4007 > 0) {
			int k = 40 - this.field_4007;
			float g = ((float)k + f) / 40.0F;
			float h = g * g;
			float l = g * h;
			float m = 10.25F * l * h - 24.95F * h * h + 25.5F * l - 13.8F * h + 4.0F * g;
			float n = m * (float) Math.PI;
			float o = this.field_4029 * (float)(i / 4);
			float p = this.field_4003 * (float)(j / 4);
			GlStateManager.enableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttributes();
			GlStateManager.enableDepthTest();
			GlStateManager.disableCull();
			class_308.method_1452();
			GlStateManager.translatef(
				(float)(i / 2) + o * class_3532.method_15379(class_3532.method_15374(n * 2.0F)),
				(float)(j / 2) + p * class_3532.method_15379(class_3532.method_15374(n * 2.0F)),
				-50.0F
			);
			float q = 50.0F + 175.0F * class_3532.method_15374(n);
			GlStateManager.scalef(q, -q, q);
			GlStateManager.rotatef(900.0F * class_3532.method_15379(class_3532.method_15374(n)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(6.0F * class_3532.method_15362(g * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(6.0F * class_3532.method_15362(g * 8.0F), 0.0F, 0.0F, 1.0F);
			this.field_4015.method_1480().method_4009(this.field_4006, class_809.class_811.field_4319);
			GlStateManager.popAttributes();
			GlStateManager.popMatrix();
			class_308.method_1450();
			GlStateManager.enableCull();
			GlStateManager.disableDepthTest();
		}
	}

	public class_310 method_3204() {
		return this.field_4015;
	}

	public float method_3195(float f) {
		return class_3532.method_16439(f, this.field_3997, this.field_4016);
	}

	public float method_3193() {
		return this.field_4025;
	}
}
