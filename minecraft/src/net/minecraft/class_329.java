package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_329 extends class_332 {
	private static final class_2960 field_2020 = new class_2960("textures/misc/vignette.png");
	private static final class_2960 field_2028 = new class_2960("textures/gui/widgets.png");
	private static final class_2960 field_19192 = new class_2960("textures/gui/hotbar.png");
	private static final class_2960 field_2019 = new class_2960("textures/misc/pumpkinblur.png");
	private final Random field_2034 = new Random();
	private final class_310 field_2035;
	private final class_918 field_2024;
	private final class_338 field_2021;
	private int field_2042;
	private String field_2018 = "";
	private int field_2041;
	private boolean field_2038;
	public float field_2013 = 1.0F;
	private int field_2040;
	private class_1799 field_2031 = class_1799.field_8037;
	private final class_340 field_2026;
	private final class_359 field_2027;
	private final class_365 field_2025;
	private final class_355 field_2015;
	private final class_337 field_2030;
	private int field_2023;
	private String field_2016 = "";
	private String field_2039 = "";
	private int field_2037;
	private int field_2017;
	private int field_2036;
	private int field_2011;
	private int field_2029;
	private final Map<class_2556, List<class_334>> field_2022 = Maps.<class_2556, List<class_334>>newHashMap();

	public class_329(class_310 arg) {
		this.field_2035 = arg;
		this.field_2024 = arg.method_1480();
		this.field_2026 = new class_340(arg);
		this.field_2025 = new class_365(arg);
		this.field_2021 = new class_338(arg);
		this.field_2015 = new class_355(arg, this);
		this.field_2030 = new class_337(arg);
		this.field_2027 = new class_359(arg);

		for (class_2556 lv : class_2556.values()) {
			this.field_2022.put(lv, Lists.newArrayList());
		}

		class_334 lv2 = class_333.field_2054;
		((List)this.field_2022.get(class_2556.field_11737)).add(new class_335(arg));
		((List)this.field_2022.get(class_2556.field_11737)).add(lv2);
		((List)this.field_2022.get(class_2556.field_11735)).add(new class_335(arg));
		((List)this.field_2022.get(class_2556.field_11735)).add(lv2);
		((List)this.field_2022.get(class_2556.field_11733)).add(new class_336(arg));
		this.method_1742();
	}

	public void method_1742() {
		this.field_2037 = 10;
		this.field_2017 = 70;
		this.field_2036 = 20;
	}

	public void method_1753(float f) {
		this.field_2011 = this.field_2035.field_1704.method_4486();
		this.field_2029 = this.field_2035.field_1704.method_4502();
		class_327 lv = this.method_1756();
		GlStateManager.enableBlend();
		if (class_310.method_1517()) {
			this.method_1735(this.field_2035.method_1560());
		} else {
			GlStateManager.enableDepthTest();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
		}

		class_1799 lv2 = this.field_2035.field_1724.field_7514.method_7372(3);
		if (this.field_2035.field_1690.field_1850 == 0 && lv2.method_7909() == class_2246.field_10147.method_8389()) {
			this.method_1761();
		}

		if (!this.field_2035.field_1724.method_6059(class_1294.field_5916)) {
			float g = class_3532.method_16439(f, this.field_2035.field_1724.field_3911, this.field_2035.field_1724.field_3929);
			if (g > 0.0F) {
				this.method_1746(g);
			}
		}

		if (this.field_2035.field_1761.method_2920() == class_1934.field_9219) {
			this.field_2025.method_1978(f);
		} else if (!this.field_2035.field_1690.field_1842) {
			this.method_1759(f);
		}

		if (!this.field_2035.field_1690.field_1842) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2035.method_1531().method_4618(GUI_ICONS_LOCATION);
			GlStateManager.enableBlend();
			GlStateManager.enableAlphaTest();
			this.method_1736();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			this.field_2035.method_16011().method_15396("bossHealth");
			this.field_2030.method_1796();
			this.field_2035.method_16011().method_15407();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2035.method_1531().method_4618(GUI_ICONS_LOCATION);
			if (this.field_2035.field_1761.method_2908()) {
				this.method_20259();
			}

			GlStateManager.disableBlend();
			int i = this.field_2011 / 2 - 91;
			if (this.field_2035.field_1724.method_3131()) {
				this.method_1752(i);
			} else if (this.field_2035.field_1761.method_2913()) {
			}

			if (this.field_2035.field_1690.field_1905 && this.field_2035.field_1761.method_2920() != class_1934.field_9219) {
				this.method_1749();
			} else if (this.field_2035.field_1724.method_7325()) {
				this.field_2025.method_1979();
			}
		}

		if (this.field_2035.field_1724.method_7297() > 0) {
			this.field_2035.method_16011().method_15396("sleep");
			GlStateManager.disableDepthTest();
			GlStateManager.disableAlphaTest();
			float g = (float)this.field_2035.field_1724.method_7297();
			float h = g / 100.0F;
			if (h > 1.0F) {
				h = 1.0F - (g - 100.0F) / 10.0F;
			}

			int j = (int)(220.0F * h) << 24 | 1052704;
			fill(0, 0, this.field_2011, this.field_2029, j);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableDepthTest();
			this.field_2035.method_16011().method_15407();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.field_2035.method_1530()) {
			this.method_1766();
		}

		this.method_1765();
		if (this.field_2035.field_1690.field_1866) {
			this.field_2026.method_1846();
		}

		if (!this.field_2035.field_1690.field_1842) {
			if (this.field_2041 > 0) {
				this.field_2035.method_16011().method_15396("overlayMessage");
				float g = (float)this.field_2041 - f;
				int k = (int)(g * 255.0F / 20.0F);
				if (k > 255) {
					k = 255;
				}

				if (k > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)(this.field_2011 / 2), (float)(this.field_2029 - 68), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(
						GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
					);
					int j = 16777215;
					if (this.field_2038) {
						j = class_3532.method_15369(g / 50.0F, 0.7F, 0.6F) & 16777215;
					}

					int l = k << 24 & 0xFF000000;
					this.method_19346(lv, -4, lv.method_1727(this.field_2018));
					lv.method_1729(this.field_2018, (float)(-lv.method_1727(this.field_2018) / 2), -4.0F, j | l);
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.field_2035.method_16011().method_15407();
			}

			if (this.field_2023 > 0) {
				this.field_2035.method_16011().method_15396("titleAndSubtitle");
				float gx = (float)this.field_2023 - f;
				int kx = 255;
				if (this.field_2023 > this.field_2036 + this.field_2017) {
					float m = (float)(this.field_2037 + this.field_2017 + this.field_2036) - gx;
					kx = (int)(m * 255.0F / (float)this.field_2037);
				}

				if (this.field_2023 <= this.field_2036) {
					kx = (int)(gx * 255.0F / (float)this.field_2036);
				}

				kx = class_3532.method_15340(kx, 0, 255);
				if (kx > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)(this.field_2011 / 2), (float)(this.field_2029 / 2), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.blendFuncSeparate(
						GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
					);
					GlStateManager.pushMatrix();
					GlStateManager.scalef(4.0F, 4.0F, 4.0F);
					int j = kx << 24 & 0xFF000000;
					int l = lv.method_1727(this.field_2016);
					this.method_19346(lv, -10, l);
					lv.method_1720(this.field_2016, (float)(-l / 2), -10.0F, 16777215 | j);
					GlStateManager.popMatrix();
					if (!this.field_2039.isEmpty()) {
						GlStateManager.pushMatrix();
						GlStateManager.scalef(2.0F, 2.0F, 2.0F);
						int n = lv.method_1727(this.field_2039);
						this.method_19346(lv, 5, n);
						lv.method_1720(this.field_2039, (float)(-n / 2), 5.0F, 16777215 | j);
						GlStateManager.popMatrix();
					}

					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.field_2035.method_16011().method_15407();
			}

			this.field_2027.method_1957();
			class_269 lv3 = this.field_2035.field_1687.method_8428();
			class_266 lv4 = null;
			class_268 lv5 = lv3.method_1164(this.field_2035.field_1724.method_5820());
			if (lv5 != null) {
				int l = lv5.method_1202().method_536();
				if (l >= 0) {
					lv4 = lv3.method_1189(3 + l);
				}
			}

			class_266 lv6 = lv4 != null ? lv4 : lv3.method_1189(1);
			if (lv6 != null) {
				this.method_1757(lv6);
			}

			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, (float)(this.field_2029 - 48), 0.0F);
			this.field_2035.method_16011().method_15396("chat");
			this.field_2021.method_1805(this.field_2042);
			this.field_2035.method_16011().method_15407();
			GlStateManager.popMatrix();
			lv6 = lv3.method_1189(0);
			if (!this.field_2035.field_1690.field_1907.method_1434()
				|| this.field_2035.method_1542() && this.field_2035.field_1724.field_3944.method_2880().size() <= 1 && lv6 == null) {
				this.field_2015.method_1921(false);
			} else {
				this.field_2015.method_1921(true);
				this.field_2015.method_1919(this.field_2011, lv3, lv6);
			}
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlphaTest();
	}

	private void method_19346(class_327 arg, int i, int j) {
		int k = this.field_2035.field_1690.method_19345(0.0F);
		if (k != 0) {
			int l = -j / 2;
			fill(l - 2, i - 2, l + j + 2, i + 9 + 2, k);
		}
	}

	private void method_1736() {
		class_315 lv = this.field_2035.field_1690;
		if (lv.field_1850 == 0) {
			if (this.field_2035.field_1761.method_2920() != class_1934.field_9219 || this.method_17534(this.field_2035.field_1765)) {
				if (lv.field_1866 && !lv.field_1842 && !this.field_2035.field_1724.method_7302() && !lv.field_1910) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef((float)(this.field_2011 / 2), (float)(this.field_2029 / 2), this.blitOffset);
					class_4184 lv2 = this.field_2035.field_1773.method_19418();
					GlStateManager.rotatef(lv2.method_19329(), -1.0F, 0.0F, 0.0F);
					GlStateManager.rotatef(lv2.method_19330(), 0.0F, 1.0F, 0.0F);
					GlStateManager.scalef(-1.0F, -1.0F, -1.0F);
					GLX.renderCrosshair(10);
					GlStateManager.popMatrix();
				} else {
					GlStateManager.blendFuncSeparate(
						GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
						GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
						GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO
					);
					int i = 15;
					this.blit((float)this.field_2011 / 2.0F - 7.5F, (float)this.field_2029 / 2.0F - 7.5F, 0, 0, 15, 15);
					if (this.field_2035.field_1690.field_1895 == class_4061.field_18152) {
						float f = this.field_2035.field_1724.method_7261(0.0F);
						boolean bl = false;
						if (this.field_2035.field_1692 != null && this.field_2035.field_1692 instanceof class_1309 && f >= 1.0F) {
							bl = this.field_2035.field_1724.method_7279() > 5.0F;
							bl &= this.field_2035.field_1692.method_5805();
						}

						int j = this.field_2029 / 2 - 7 + 16;
						int k = this.field_2011 / 2 - 8;
						if (bl) {
							this.blit(k, j, 68, 94, 16, 16);
						} else if (f < 1.0F) {
							int l = (int)(f * 17.0F);
							this.blit(k, j, 36, 94, 16, 4);
							this.blit(k, j, 52, 94, l, 4);
						}
					}
				}
			}
		}
	}

	private boolean method_17534(class_239 arg) {
		if (arg == null) {
			return false;
		} else if (arg.method_17783() == class_239.class_240.field_1331) {
			return ((class_3966)arg).method_17782() instanceof class_3908;
		} else if (arg.method_17783() == class_239.class_240.field_1332) {
			class_2338 lv = ((class_3965)arg).method_17777();
			class_1937 lv2 = this.field_2035.field_1687;
			return lv2.method_8320(lv).method_17526(lv2, lv) != null;
		} else {
			return false;
		}
	}

	protected void method_1765() {
		Collection<class_1293> collection = this.field_2035.field_1724.method_6026();
		if (!collection.isEmpty()) {
			GlStateManager.enableBlend();
			int i = 0;
			int j = 0;
			class_4074 lv = this.field_2035.method_18505();
			List<Runnable> list = Lists.<Runnable>newArrayListWithExpectedSize(collection.size());
			this.field_2035.method_1531().method_4618(class_465.field_2801);

			for (class_1293 lv2 : Ordering.natural().reverse().sortedCopy(collection)) {
				class_1291 lv3 = lv2.method_5579();
				if (lv2.method_5592()) {
					int k = this.field_2011;
					int l = 1;
					if (this.field_2035.method_1530()) {
						l += 15;
					}

					if (lv3.method_5573()) {
						i++;
						k -= 25 * i;
					} else {
						j++;
						k -= 25 * j;
						l += 26;
					}

					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float f = 1.0F;
					if (lv2.method_5591()) {
						this.blit(k, l, 165, 166, 24, 24);
					} else {
						this.blit(k, l, 141, 166, 24, 24);
						if (lv2.method_5584() <= 200) {
							int m = 10 - lv2.method_5584() / 20;
							f = class_3532.method_15363((float)lv2.method_5584() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
								+ class_3532.method_15362((float)lv2.method_5584() * (float) Math.PI / 5.0F) * class_3532.method_15363((float)m / 10.0F * 0.25F, 0.0F, 0.25F);
						}
					}

					class_1058 lv4 = lv.method_18663(lv3);
					int n = k;
					int o = l;
					float g = f;
					list.add((Runnable)() -> {
						GlStateManager.color4f(1.0F, 1.0F, 1.0F, g);
						this.blit(n + 3, o + 3, lv4, 18, 18);
					});
				}
			}

			this.field_2035.method_1531().method_4618(class_1059.field_18229);
			list.forEach(Runnable::run);
		}
	}

	protected void method_1759(float f) {
		class_1657 lv = this.method_1737();
		if (lv != null) {
			int i = this.field_2011 / 2;
			int j = i - 128;
			int k = this.field_2029 - 64;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2035.method_1531().method_4618(field_19192);
			float g = this.blitOffset;
			this.blitOffset = -666.0F;
			this.blit(j, k, 0, 64, 256, 64);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, -200.0F);
			int l = (int)(class_156.method_658() / 2000L % 4L);
			float[] fs = new float[]{-20.0F, 0.0F, 20.0F, 0.0F};
			if (this.field_2035.method_1561().field_4686 != null) {
				class_490.method_2486(j + 128, k + 135, 64, fs[l], 0.0F, lv);
			}

			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2035.method_1531().method_4618(field_19192);
			this.blitOffset = -90.0F;
			this.blit(j, k, 0, 0, 256, 64);
			this.field_2035.method_1531().method_4618(field_2028);
			int m = lv.field_7514.field_7545 % 3;
			int n = lv.field_7514.field_7545 / 3;
			this.blit(j + 194 + m * 20 - 1, k + 2 + n * 20 - 1, 0, 22, 24, 22);
			this.blitOffset = g;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			class_308.method_1453();

			for (int o = 0; o < 9; o++) {
				int p = j + 194 + o % 3 * 20 + 2;
				int q = k + 2 + o / 3 * 20 + 2;
				this.method_1762(p, q, f, lv, lv.field_7514.field_7547.get(o));
			}

			if (this.field_2035.field_1690.field_1895 == class_4061.field_18153) {
				float h = this.field_2035.field_1724.method_7261(0.0F);
				if (h < 1.0F) {
					int p = k + 2 + n * 20;
					int q = j + 194 + m * 22;
					this.field_2035.method_1531().method_4618(class_332.GUI_ICONS_LOCATION);
					int r = (int)(h * 19.0F);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					this.blit(q, p, 0, 94, 18, 18);
					this.blit(q, p + 18 - r, 18, 112 - r, 18, r);
				}
			}

			class_308.method_1450();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

	public void method_1752(int i) {
		this.field_2035.method_16011().method_15396("jumpBar");
		this.field_2035.method_1531().method_4618(class_332.GUI_ICONS_LOCATION);
		float f = this.field_2035.field_1724.method_3151();
		int j = 182;
		int k = (int)(f * 183.0F);
		int l = 20;
		this.blit(i, 20, 0, 84, 182, 5);
		if (k > 0) {
			this.blit(i, 20, 0, 89, k, 5);
		}

		this.field_2035.method_16011().method_15407();
	}

	public void method_1749() {
		this.field_2035.method_16011().method_15396("selectedItemName");
		if (this.field_2040 > 0 && !this.field_2031.method_7960()) {
			class_2561 lv = new class_2585("").method_10852(this.field_2031.method_7964()).method_10854(this.field_2031.method_7932().field_8908);
			if (this.field_2031.method_7938()) {
				lv.method_10854(class_124.field_1056);
			}

			String string = lv.method_10863();
			int i = (this.field_2011 - this.method_1756().method_1727(string)) / 2;
			int j = this.field_2029 - 84;
			int k = (int)((float)this.field_2040 * 256.0F / 10.0F);
			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				fill(i - 2, j - 2, i + this.method_1756().method_1727(string) + 2, j + 9 + 2, this.field_2035.field_1690.method_19344(0));
				this.method_1756().method_1720(string, (float)i, (float)j, 16777215 + (k << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}

		this.field_2035.method_16011().method_15407();
	}

	public void method_1766() {
		this.field_2035.method_16011().method_15396("demo");
		String string;
		if (this.field_2035.field_1687.method_8510() >= 120500L) {
			string = class_1074.method_4662("demo.demoExpired");
		} else {
			string = class_1074.method_4662("demo.remainingTime", class_3544.method_15439((int)(120500L - this.field_2035.field_1687.method_8510())));
		}

		int i = this.method_1756().method_1727(string);
		this.method_1756().method_1720(string, (float)(this.field_2011 - i - 10), 5.0F, 16777215);
		this.field_2035.method_16011().method_15407();
	}

	private void method_1757(class_266 arg) {
		class_269 lv = arg.method_1117();
		Collection<class_267> collection = lv.method_1184(arg);
		List<class_267> list = (List<class_267>)collection.stream()
			.filter(argx -> argx.method_1129() != null && !argx.method_1129().startsWith("#"))
			.collect(Collectors.toList());
		if (list.size() > 15) {
			collection = Lists.<class_267>newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		String string = arg.method_1114().method_10863();
		int i = this.method_1756().method_1727(string);
		int j = i;

		for (class_267 lv2 : collection) {
			class_268 lv3 = lv.method_1164(lv2.method_1129());
			String string2 = class_268.method_1142(lv3, new class_2585(lv2.method_1129())).method_10863() + ": " + class_124.field_1061 + lv2.method_1126();
			j = Math.max(j, this.method_1756().method_1727(string2));
		}

		int k = collection.size() * 9;
		int l = this.field_2029 / 2 + k / 3;
		int m = 3;
		int n = this.field_2011 - j - 3;
		int o = 0;
		int p = this.field_2035.field_1690.method_19345(0.3F);
		int q = this.field_2035.field_1690.method_19345(0.4F);

		for (class_267 lv4 : collection) {
			o++;
			class_268 lv5 = lv.method_1164(lv4.method_1129());
			String string3 = class_268.method_1142(lv5, new class_2585(lv4.method_1129())).method_10863();
			String string4 = class_124.field_1061 + "" + lv4.method_1126();
			int s = l - o * 9;
			int t = this.field_2011 - 3 + 2;
			fill(n - 2, s, t, s + 9, p);
			this.method_1756().method_1729(string3, (float)n, (float)s, 553648127);
			this.method_1756().method_1729(string4, (float)(t - this.method_1756().method_1727(string4)), (float)s, 553648127);
			if (o == collection.size()) {
				fill(n - 2, s - 9 - 1, t, s - 1, q);
				fill(n - 2, s - 1, t, s, p);
				this.method_1756().method_1729(string, (float)(n + j / 2 - i / 2), (float)(s - 9), 553648127);
			}
		}
	}

	private class_1657 method_1737() {
		return !(this.field_2035.method_1560() instanceof class_1657) ? null : (class_1657)this.field_2035.method_1560();
	}

	private void method_20259() {
		class_1657 lv = this.method_1737();
		if (lv != null) {
			int i = class_3532.method_15386(lv.method_6032());
			this.field_2034.setSeed((long)(this.field_2042 * 312871));
			class_1702 lv2 = lv.method_7344();
			int j = lv2.method_7586();
			int k = this.field_2011 / 2;
			int l = k - 91;
			int m = k + 91;
			int n = this.field_2029 - 74;
			int o = class_3532.method_15386(lv.method_6067());
			this.field_2035.method_16011().method_15396("armor");
			int p = lv.method_6096();
			String string = String.format("%02d%%", p * 100 / 20);
			int q = this.method_1756().method_1727(string);
			int r = k - 128;
			int s = this.field_2029 - 64;
			this.method_1756().method_1729(string, (float)(r + 25 + (23 - q)), (float)(s + 41), -1);
			this.field_2035.method_16011().method_15405("health");
			float f = lv.method_6063();
			String string2 = String.format("%02.0f%%", (float)(i + o) / f * 100.0F);
			int t = this.method_1756().method_1727(string2);
			this.method_1756().method_1729(string2, (float)(r + 25 + (23 - t)), (float)(s + 8), o > 0 ? -256 : -65536);
			this.field_2035.method_1531().method_4618(field_19192);
			int u = n - 10;
			this.field_2035.method_16011().method_15405("food");
			int v = 32 * j / 20;
			this.blit(r + 161, s + 14, 0, 128, 32, v);
			u -= 10;
			this.field_2035.method_1531().method_4618(class_332.GUI_ICONS_LOCATION);
			this.field_2035.method_16011().method_15405("air");
			int w = lv.method_5669();
			int x = lv.method_5748();
			if (lv.method_5777(class_3486.field_15517) || w < x) {
				int y = 0;
				u += 0;
				int z = class_3532.method_15384((double)(w - 2) * 10.0 / (double)x);
				int aa = class_3532.method_15384((double)w * 10.0 / (double)x) - z;

				for (int ab = 0; ab < z + aa; ab++) {
					if (ab < z) {
						this.blit(m - ab * 8 - 9, u, 16, 18, 9, 9);
					} else {
						this.blit(m - ab * 8 - 9, u, 25, 18, 9, 9);
					}
				}
			}

			this.field_2035.method_16011().method_15407();
		}
	}

	private void method_1761() {
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlphaTest();
		this.field_2035.method_1531().method_4618(field_2019);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(0.0, (double)this.field_2029, -90.0).method_1312(0.0, 1.0).method_1344();
		lv2.method_1315((double)this.field_2011, (double)this.field_2029, -90.0).method_1312(1.0, 1.0).method_1344();
		lv2.method_1315((double)this.field_2011, 0.0, -90.0).method_1312(1.0, 0.0).method_1344();
		lv2.method_1315(0.0, 0.0, -90.0).method_1312(0.0, 0.0).method_1344();
		lv.method_1350();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_1731(class_1297 arg) {
		if (arg != null) {
			float f = class_3532.method_15363(1.0F - arg.method_5718(), 0.0F, 1.0F);
			this.field_2013 = (float)((double)this.field_2013 + (double)(f - this.field_2013) * 0.01);
		}
	}

	private void method_1735(class_1297 arg) {
		class_2784 lv = this.field_2035.field_1687.method_8621();
		float f = (float)lv.method_11979(arg);
		double d = Math.min(lv.method_11974() * (double)lv.method_11956() * 1000.0, Math.abs(lv.method_11954() - lv.method_11965()));
		double e = Math.max((double)lv.method_11972(), d);
		if ((double)f < e) {
			f = 1.0F - (float)((double)f / e);
		} else {
			f = 0.0F;
		}

		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		if (f > 0.0F) {
			GlStateManager.color4f(0.0F, f, f, 1.0F);
		} else {
			GlStateManager.color4f(this.field_2013, this.field_2013, this.field_2013, 1.0F);
		}

		this.field_2035.method_1531().method_4618(field_2020);
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		lv3.method_1328(7, class_290.field_1585);
		lv3.method_1315(0.0, (double)this.field_2029, -90.0).method_1312(0.0, 1.0).method_1344();
		lv3.method_1315((double)this.field_2011, (double)this.field_2029, -90.0).method_1312(1.0, 1.0).method_1344();
		lv3.method_1315((double)this.field_2011, 0.0, -90.0).method_1312(1.0, 0.0).method_1344();
		lv3.method_1315(0.0, 0.0, -90.0).method_1312(0.0, 0.0).method_1344();
		lv2.method_1350();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
	}

	private void method_1746(float f) {
		if (f < 1.0F) {
			f *= f;
			f *= f;
			f = f * 0.8F + 0.2F;
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.field_2035.method_1531().method_4618(class_1059.field_5275);
		class_1058 lv = this.field_2035.method_1541().method_3351().method_3339(class_2246.field_10316.method_9564());
		float g = lv.method_4594();
		float h = lv.method_4593();
		float i = lv.method_4577();
		float j = lv.method_4575();
		class_289 lv2 = class_289.method_1348();
		class_287 lv3 = lv2.method_1349();
		lv3.method_1328(7, class_290.field_1585);
		lv3.method_1315(0.0, (double)this.field_2029, -90.0).method_1312((double)g, (double)j).method_1344();
		lv3.method_1315((double)this.field_2011, (double)this.field_2029, -90.0).method_1312((double)i, (double)j).method_1344();
		lv3.method_1315((double)this.field_2011, 0.0, -90.0).method_1312((double)i, (double)h).method_1344();
		lv3.method_1315(0.0, 0.0, -90.0).method_1312((double)g, (double)h).method_1344();
		lv2.method_1350();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_1762(int i, int j, float f, class_1657 arg, class_1799 arg2) {
		if (!arg2.method_7960()) {
			float g = (float)arg2.method_7965() - f;
			if (g > 0.0F) {
				GlStateManager.pushMatrix();
				float h = 1.0F + g / 5.0F;
				GlStateManager.translatef((float)(i + 8), (float)(j + 12), 0.0F);
				GlStateManager.scalef(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translatef((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
			}

			this.field_2024.method_4026(arg, arg2, i, j);
			if (g > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.field_2024.method_4025(this.field_2035.field_1772, arg2, i, j);
		}
	}

	public void method_1748() {
		if (this.field_2041 > 0) {
			this.field_2041--;
		}

		if (this.field_2023 > 0) {
			this.field_2023--;
			if (this.field_2023 <= 0) {
				this.field_2016 = "";
				this.field_2039 = "";
			}
		}

		this.field_2042++;
		class_1297 lv = this.field_2035.method_1560();
		if (lv != null) {
			this.method_1731(lv);
		}

		if (this.field_2035.field_1724 != null) {
			class_1799 lv2 = this.field_2035.field_1724.field_7514.method_7391();
			if (lv2.method_7960()) {
				this.field_2040 = 0;
			} else if (this.field_2031.method_7960() || lv2.method_7909() != this.field_2031.method_7909() || !lv2.method_7964().equals(this.field_2031.method_7964())) {
				this.field_2040 = 40;
			} else if (this.field_2040 > 0) {
				this.field_2040--;
			}

			this.field_2031 = lv2;
		}
	}

	public void method_1732(String string) {
		this.method_1764(class_1074.method_4662("record.nowPlaying", string), true);
	}

	public void method_1764(String string, boolean bl) {
		this.field_2018 = string;
		this.field_2041 = 60;
		this.field_2038 = bl;
	}

	public void method_1763(String string, String string2, int i, int j, int k) {
		if (string == null && string2 == null && i < 0 && j < 0 && k < 0) {
			this.field_2016 = "";
			this.field_2039 = "";
			this.field_2023 = 0;
		} else if (string != null) {
			this.field_2016 = string;
			this.field_2023 = this.field_2037 + this.field_2017 + this.field_2036;
		} else if (string2 != null) {
			this.field_2039 = string2;
		} else {
			if (i >= 0) {
				this.field_2037 = i;
			}

			if (j >= 0) {
				this.field_2017 = j;
			}

			if (k >= 0) {
				this.field_2036 = k;
			}

			if (this.field_2023 > 0) {
				this.field_2023 = this.field_2037 + this.field_2017 + this.field_2036;
			}
		}
	}

	public void method_1758(class_2561 arg, boolean bl) {
		this.method_1764(arg.getString(), bl);
	}

	public void method_1755(class_2556 arg, class_2561 arg2) {
		for (class_334 lv : (List)this.field_2022.get(arg)) {
			lv.method_1794(arg, arg2);
		}
	}

	public class_338 method_1743() {
		return this.field_2021;
	}

	public int method_1738() {
		return this.field_2042;
	}

	public class_327 method_1756() {
		return this.field_2035.field_1772;
	}

	public class_365 method_1739() {
		return this.field_2025;
	}

	public class_355 method_1750() {
		return this.field_2015;
	}

	public void method_1747() {
		this.field_2015.method_1920();
		this.field_2030.method_1801();
		this.field_2035.method_1566().method_2000();
	}

	public class_337 method_1740() {
		return this.field_2030;
	}

	public void method_1745() {
		this.field_2026.method_1842();
	}
}
