package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_358 extends class_362 {
	protected final class_310 field_2164;
	protected int field_2168;
	protected int field_2167;
	protected int field_2166;
	protected int field_2165;
	protected int field_2181;
	protected int field_2180;
	protected final int field_2179;
	protected boolean field_2173 = true;
	protected int field_2178 = -2;
	protected double field_2175;
	protected int field_2176;
	protected long field_2177 = Long.MIN_VALUE;
	protected boolean field_2172 = true;
	protected boolean field_2171 = true;
	protected boolean field_2170;
	protected int field_2174;
	private boolean field_2169;

	public class_358(class_310 arg, int i, int j, int k, int l, int m) {
		this.field_2164 = arg;
		this.field_2168 = i;
		this.field_2167 = j;
		this.field_2166 = k;
		this.field_2165 = l;
		this.field_2179 = m;
		this.field_2180 = 0;
		this.field_2181 = i;
	}

	public void method_1953(int i, int j, int k, int l) {
		this.field_2168 = i;
		this.field_2167 = j;
		this.field_2166 = k;
		this.field_2165 = l;
		this.field_2180 = 0;
		this.field_2181 = i;
	}

	public void method_1943(boolean bl) {
		this.field_2171 = bl;
	}

	protected void method_1927(boolean bl, int i) {
		this.field_2170 = bl;
		this.field_2174 = i;
		if (!bl) {
			this.field_2174 = 0;
		}
	}

	public boolean method_1939() {
		return this.field_2172;
	}

	protected abstract int method_1947();

	public void method_1946(int i) {
	}

	@Override
	protected List<? extends class_364> method_1968() {
		return Collections.emptyList();
	}

	protected boolean method_1937(int i, int j, double d, double e) {
		return true;
	}

	protected abstract boolean method_1955(int i);

	protected int method_1928() {
		return this.method_1947() * this.field_2179 + this.field_2174;
	}

	protected abstract void method_1936();

	protected void method_1952(int i, int j, int k, float f) {
	}

	protected abstract void method_1935(int i, int j, int k, int l, int m, int n, float f);

	protected void method_1940(int i, int j, class_289 arg) {
	}

	protected void method_1929(int i, int j) {
	}

	protected void method_1942(int i, int j) {
	}

	public int method_1956(double d, double e) {
		int i = this.field_2180 + this.field_2168 / 2 - this.method_1932() / 2;
		int j = this.field_2180 + this.field_2168 / 2 + this.method_1932() / 2;
		int k = class_3532.method_15357(e - (double)this.field_2166) - this.field_2174 + (int)this.field_2175 - 4;
		int l = k / this.field_2179;
		return d < (double)this.method_1948() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.method_1947() ? l : -1;
	}

	protected void method_1934() {
		this.field_2175 = class_3532.method_15350(this.field_2175, 0.0, (double)this.method_1931());
	}

	public int method_1931() {
		return Math.max(0, this.method_1928() - (this.field_2165 - this.field_2166 - 4));
	}

	public int method_1944() {
		return (int)this.field_2175;
	}

	public boolean method_1938(double d, double e) {
		return e >= (double)this.field_2166 && e <= (double)this.field_2165 && d >= (double)this.field_2180 && d <= (double)this.field_2181;
	}

	public void method_1951(int i) {
		this.field_2175 += (double)i;
		this.method_1934();
		this.field_2178 = -2;
	}

	public void method_1930(int i, int j, float f) {
		if (this.field_2172) {
			this.method_1936();
			int k = this.method_1948();
			int l = k + 6;
			this.method_1934();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			this.field_2164.method_1531().method_4618(class_332.field_2051);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)this.field_2165, 0.0)
				.method_1312((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2165, 0.0)
				.method_1312((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2166, 0.0)
				.method_1312((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2180, (double)this.field_2166, 0.0)
				.method_1312((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv.method_1350();
			int m = this.field_2180 + this.field_2168 / 2 - this.method_1932() / 2 + 2;
			int n = this.field_2166 + 4 - (int)this.field_2175;
			if (this.field_2170) {
				this.method_1940(m, n, lv);
			}

			this.method_1950(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.method_1954(0, this.field_2166, 255, 255);
			this.method_1954(this.field_2165, this.field_2167, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5146, GlStateManager.class_1027.field_5078
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = 4;
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)(this.field_2166 + 4), 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2181, (double)(this.field_2166 + 4), 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2166, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2180, (double)this.field_2166, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)this.field_2165, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2165, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2181, (double)(this.field_2165 - 4), 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2180, (double)(this.field_2165 - 4), 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
			lv.method_1350();
			int p = this.method_1931();
			if (p > 0) {
				int q = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.method_1928());
				q = class_3532.method_15340(q, 32, this.field_2165 - this.field_2166 - 8);
				int r = (int)this.field_2175 * (this.field_2165 - this.field_2166 - q) / p + this.field_2166;
				if (r < this.field_2166) {
					r = this.field_2166;
				}

				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)this.field_2165, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2165, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2166, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)k, (double)this.field_2166, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv.method_1350();
				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)(r + q), 0.0).method_1312(0.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)l, (double)(r + q), 0.0).method_1312(1.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)l, (double)r, 0.0).method_1312(1.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv.method_1350();
				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)(r + q - 1), 0.0).method_1312(0.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)(l - 1), (double)(r + q - 1), 0.0).method_1312(1.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)(l - 1), (double)r, 0.0).method_1312(1.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
				lv.method_1350();
			}

			this.method_1942(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	protected void method_1933(double d, double e, int i) {
		this.field_2169 = i == 0 && d >= (double)this.method_1948() && d < (double)(this.method_1948() + 6);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		this.method_1933(d, e, i);
		if (this.method_1939() && this.method_1938(d, e)) {
			int j = this.method_1956(d, e);
			if (j == -1 && i == 0) {
				this.method_1929(
					(int)(d - (double)(this.field_2180 + this.field_2168 / 2 - this.method_1932() / 2)), (int)(e - (double)this.field_2166) + (int)this.field_2175 - 4
				);
				return true;
			} else if (j != -1 && this.method_1937(j, i, d, e)) {
				if (this.method_1968().size() > j) {
					this.method_1967((class_364)this.method_1968().get(j));
				}

				this.method_1966(true);
				this.method_1946(j);
				return true;
			} else {
				return this.field_2169;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		if (this.getFocused() != null) {
			this.getFocused().method_16804(d, e, i);
		}

		this.method_1968().forEach(arg -> arg.method_16804(d, e, i));
		return false;
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		if (super.method_16801(d, e, i, f, g)) {
			return true;
		} else if (this.method_1939() && i == 0 && this.field_2169) {
			if (e < (double)this.field_2166) {
				this.field_2175 = 0.0;
			} else if (e > (double)this.field_2165) {
				this.field_2175 = (double)this.method_1931();
			} else {
				double h = (double)this.method_1931();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.method_1928());
				j = class_3532.method_15340(j, 32, this.field_2165 - this.field_2166 - 8);
				double k = h / (double)(this.field_2165 - this.field_2166 - j);
				if (k < 1.0) {
					k = 1.0;
				}

				this.field_2175 += g * k;
				this.method_1934();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16802(double d) {
		if (!this.method_1939()) {
			return false;
		} else {
			this.field_2175 = this.field_2175 - d * (double)this.field_2179 / 2.0;
			return true;
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		return !this.method_1939() ? false : super.method_16805(i, j, k);
	}

	@Override
	public boolean method_16806(char c, int i) {
		return !this.method_1939() ? false : super.method_16806(c, i);
	}

	public int method_1932() {
		return 220;
	}

	protected void method_1950(int i, int j, int k, int l, float f) {
		int m = this.method_1947();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.field_2179 + this.field_2174;
			int p = this.field_2179 - 4;
			if (o > this.field_2165 || o + p < this.field_2166) {
				this.method_1952(n, i, o, f);
			}

			if (this.field_2171 && this.method_1955(n)) {
				int q = this.field_2180 + this.field_2168 / 2 - this.method_1932() / 2;
				int r = this.field_2180 + this.field_2168 / 2 + this.method_1932() / 2;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture();
				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)q, (double)(o + p + 2), 0.0).method_1312(0.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)r, (double)(o + p + 2), 0.0).method_1312(1.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)r, (double)(o - 2), 0.0).method_1312(1.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)q, (double)(o - 2), 0.0).method_1312(0.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)(q + 1), (double)(o + p + 1), 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)(r - 1), (double)(o + p + 1), 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)(r - 1), (double)(o - 1), 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)(q + 1), (double)(o - 1), 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv.method_1350();
				GlStateManager.enableTexture();
			}

			this.method_1935(n, i, o, p, k, l, f);
		}
	}

	protected int method_1948() {
		return this.field_2168 / 2 + 124;
	}

	protected void method_1954(int i, int j, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.field_2164.method_1531().method_4618(class_332.field_2051);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_2180, (double)j, 0.0).method_1312(0.0, (double)((float)j / 32.0F)).method_1323(64, 64, 64, l).method_1344();
		lv2.method_1315((double)(this.field_2180 + this.field_2168), (double)j, 0.0)
			.method_1312((double)((float)this.field_2168 / 32.0F), (double)((float)j / 32.0F))
			.method_1323(64, 64, 64, l)
			.method_1344();
		lv2.method_1315((double)(this.field_2180 + this.field_2168), (double)i, 0.0)
			.method_1312((double)((float)this.field_2168 / 32.0F), (double)((float)i / 32.0F))
			.method_1323(64, 64, 64, k)
			.method_1344();
		lv2.method_1315((double)this.field_2180, (double)i, 0.0).method_1312(0.0, (double)((float)i / 32.0F)).method_1323(64, 64, 64, k).method_1344();
		lv.method_1350();
	}

	public void method_1945(int i) {
		this.field_2180 = i;
		this.field_2181 = i + this.field_2168;
	}

	public int method_1941() {
		return this.field_2179;
	}
}
