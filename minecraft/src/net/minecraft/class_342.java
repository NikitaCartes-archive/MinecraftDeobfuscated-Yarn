package net.minecraft;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_342 extends class_332 implements class_364 {
	private final int field_2089;
	private final class_327 field_2105;
	private int field_2093;
	private int field_2090;
	private final int field_2087;
	private final int field_2109;
	private String field_2092 = "";
	private int field_2108 = 32;
	private int field_2107;
	private boolean field_2097 = true;
	private boolean field_2096 = true;
	private boolean field_2095;
	private boolean field_2094 = true;
	private boolean field_17037;
	private int field_2103;
	private int field_2102;
	private int field_2101;
	private int field_2100 = 14737632;
	private int field_2098 = 7368816;
	private boolean field_2091 = true;
	private String field_2106;
	private BiConsumer<Integer, String> field_2088;
	private Predicate<String> field_2104 = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> field_2099 = (string, integer) -> string;

	public class_342(int i, class_327 arg, int j, int k, int l, int m) {
		this(i, arg, j, k, l, m, null);
	}

	public class_342(int i, class_327 arg, int j, int k, int l, int m, @Nullable class_342 arg2) {
		this.field_2089 = i;
		this.field_2105 = arg;
		this.field_2093 = j;
		this.field_2090 = k;
		this.field_2087 = l;
		this.field_2109 = m;
		if (arg2 != null) {
			this.method_1852(arg2.method_1882());
		}
	}

	public void method_1863(BiConsumer<Integer, String> biConsumer) {
		this.field_2088 = biConsumer;
	}

	public void method_1854(BiFunction<String, Integer, String> biFunction) {
		this.field_2099 = biFunction;
	}

	public void method_1865() {
		this.field_2107++;
	}

	public void method_1852(String string) {
		if (this.field_2104.test(string)) {
			if (string.length() > this.field_2108) {
				this.field_2092 = string.substring(0, this.field_2108);
			} else {
				this.field_2092 = string;
			}

			this.method_1874(this.field_2089, string);
			this.method_1872();
		}
	}

	public String method_1882() {
		return this.field_2092;
	}

	public String method_1866() {
		int i = this.field_2102 < this.field_2101 ? this.field_2102 : this.field_2101;
		int j = this.field_2102 < this.field_2101 ? this.field_2101 : this.field_2102;
		return this.field_2092.substring(i, j);
	}

	public void method_1890(Predicate<String> predicate) {
		this.field_2104 = predicate;
	}

	public void method_1867(String string) {
		String string2 = "";
		String string3 = class_155.method_644(string);
		int i = this.field_2102 < this.field_2101 ? this.field_2102 : this.field_2101;
		int j = this.field_2102 < this.field_2101 ? this.field_2101 : this.field_2102;
		int k = this.field_2108 - this.field_2092.length() - (i - j);
		if (!this.field_2092.isEmpty()) {
			string2 = string2 + this.field_2092.substring(0, i);
		}

		int l;
		if (k < string3.length()) {
			string2 = string2 + string3.substring(0, k);
			l = k;
		} else {
			string2 = string2 + string3;
			l = string3.length();
		}

		if (!this.field_2092.isEmpty() && j < this.field_2092.length()) {
			string2 = string2 + this.field_2092.substring(j);
		}

		if (this.field_2104.test(string2)) {
			this.field_2092 = string2;
			this.method_1875(i + l);
			this.method_1884(this.field_2102);
			this.method_1874(this.field_2089, this.field_2092);
		}
	}

	public void method_1874(int i, String string) {
		if (this.field_2088 != null) {
			this.field_2088.accept(i, string);
		}
	}

	private void method_16873(int i) {
		if (class_437.method_2238()) {
			this.method_1877(i);
		} else {
			this.method_1878(i);
		}
	}

	public void method_1877(int i) {
		if (!this.field_2092.isEmpty()) {
			if (this.field_2101 != this.field_2102) {
				this.method_1867("");
			} else {
				this.method_1878(this.method_1853(i) - this.field_2102);
			}
		}
	}

	public void method_1878(int i) {
		if (!this.field_2092.isEmpty()) {
			if (this.field_2101 != this.field_2102) {
				this.method_1867("");
			} else {
				boolean bl = i < 0;
				int j = bl ? this.field_2102 + i : this.field_2102;
				int k = bl ? this.field_2102 : this.field_2102 + i;
				String string = "";
				if (j >= 0) {
					string = this.field_2092.substring(0, j);
				}

				if (k < this.field_2092.length()) {
					string = string + this.field_2092.substring(k);
				}

				if (this.field_2104.test(string)) {
					this.field_2092 = string;
					if (bl) {
						this.method_1855(i);
					}

					this.method_1874(this.field_2089, this.field_2092);
				}
			}
		}
	}

	public int method_1853(int i) {
		return this.method_1869(i, this.method_1881());
	}

	public int method_1869(int i, int j) {
		return this.method_1864(i, j, true);
	}

	public int method_1864(int i, int j, boolean bl) {
		int k = j;
		boolean bl2 = i < 0;
		int l = Math.abs(i);

		for (int m = 0; m < l; m++) {
			if (!bl2) {
				int n = this.field_2092.length();
				k = this.field_2092.indexOf(32, k);
				if (k == -1) {
					k = n;
				} else {
					while (bl && k < n && this.field_2092.charAt(k) == ' ') {
						k++;
					}
				}
			} else {
				while (bl && k > 0 && this.field_2092.charAt(k - 1) == ' ') {
					k--;
				}

				while (k > 0 && this.field_2092.charAt(k - 1) != ' ') {
					k--;
				}
			}
		}

		return k;
	}

	public void method_1855(int i) {
		this.method_1883(this.field_2102 + i);
	}

	public void method_1883(int i) {
		this.method_1875(i);
		if (!this.field_17037) {
			this.method_1884(this.field_2102);
		}

		this.method_1874(this.field_2089, this.field_2092);
	}

	public void method_1875(int i) {
		this.field_2102 = class_3532.method_15340(i, 0, this.field_2092.length());
	}

	public void method_1870() {
		this.method_1883(0);
	}

	public void method_1872() {
		this.method_1883(this.field_2092.length());
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (this.method_1885() && this.method_1871()) {
			this.field_17037 = class_437.method_2223();
			if (class_437.method_2226(i)) {
				this.method_1872();
				this.method_1884(0);
				return true;
			} else if (class_437.method_2227(i)) {
				class_310.method_1551().field_1774.method_1455(this.method_1866());
				return true;
			} else if (class_437.method_2235(i)) {
				if (this.field_2094) {
					this.method_1867(class_310.method_1551().field_1774.method_1460());
				}

				return true;
			} else if (class_437.method_2212(i)) {
				class_310.method_1551().field_1774.method_1455(this.method_1866());
				if (this.field_2094) {
					this.method_1867("");
				}

				return true;
			} else {
				switch (i) {
					case 259:
						if (this.field_2094) {
							this.method_16873(-1);
						}

						return true;
					case 260:
					case 264:
					case 265:
					case 266:
					case 267:
					default:
						return i != 256;
					case 261:
						if (this.field_2094) {
							this.method_16873(1);
						}

						return true;
					case 262:
						if (class_437.method_2238()) {
							this.method_1883(this.method_1853(1));
						} else {
							this.method_1855(1);
						}

						return true;
					case 263:
						if (class_437.method_2238()) {
							this.method_1883(this.method_1853(-1));
						} else {
							this.method_1855(-1);
						}

						return true;
					case 268:
						this.method_1870();
						return true;
					case 269:
						this.method_1872();
						return true;
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (!this.method_1885() || !this.method_1871()) {
			return false;
		} else if (class_155.method_643(c)) {
			if (this.field_2094) {
				this.method_1867(Character.toString(c));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (!this.method_1885()) {
			return false;
		} else {
			boolean bl = d >= (double)this.field_2093
				&& d < (double)(this.field_2093 + this.field_2087)
				&& e >= (double)this.field_2090
				&& e < (double)(this.field_2090 + this.field_2109);
			if (this.field_2096) {
				this.method_1876(bl);
			}

			if (this.field_2095 && bl && i == 0) {
				int j = class_3532.method_15357(d) - this.field_2093;
				if (this.field_2097) {
					j -= 4;
				}

				String string = this.field_2105.method_1714(this.field_2092.substring(this.field_2103), this.method_1859());
				this.method_1883(this.field_2105.method_1714(string, j).length() + this.field_2103);
				return true;
			} else {
				return false;
			}
		}
	}

	public void method_1857(int i, int j, float f) {
		if (this.method_1885()) {
			if (this.method_1851()) {
				method_1785(this.field_2093 - 1, this.field_2090 - 1, this.field_2093 + this.field_2087 + 1, this.field_2090 + this.field_2109 + 1, -6250336);
				method_1785(this.field_2093, this.field_2090, this.field_2093 + this.field_2087, this.field_2090 + this.field_2109, -16777216);
			}

			int k = this.field_2094 ? this.field_2100 : this.field_2098;
			int l = this.field_2102 - this.field_2103;
			int m = this.field_2101 - this.field_2103;
			String string = this.field_2105.method_1714(this.field_2092.substring(this.field_2103), this.method_1859());
			boolean bl = l >= 0 && l <= string.length();
			boolean bl2 = this.field_2095 && this.field_2107 / 6 % 2 == 0 && bl;
			int n = this.field_2097 ? this.field_2093 + 4 : this.field_2093;
			int o = this.field_2097 ? this.field_2090 + (this.field_2109 - 8) / 2 : this.field_2090;
			int p = n;
			if (m > string.length()) {
				m = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, l) : string;
				p = this.field_2105.method_1720((String)this.field_2099.apply(string2, this.field_2103), (float)n, (float)o, k);
			}

			boolean bl3 = this.field_2102 < this.field_2092.length() || this.field_2092.length() >= this.method_1861();
			int q = p;
			if (!bl) {
				q = l > 0 ? n + this.field_2087 : n;
			} else if (bl3) {
				q = p - 1;
				p--;
			}

			if (!string.isEmpty() && bl && l < string.length()) {
				this.field_2105.method_1720((String)this.field_2099.apply(string.substring(l), this.field_2102), (float)p, (float)o, k);
			}

			if (!bl3 && this.field_2106 != null) {
				this.field_2105.method_1720(this.field_2106, (float)(q - 1), (float)o, -8355712);
			}

			if (bl2) {
				if (bl3) {
					class_332.method_1785(q, o - 1, q + 1, o + 1 + 9, -3092272);
				} else {
					this.field_2105.method_1720("_", (float)q, (float)o, k);
				}
			}

			if (m != l) {
				int r = n + this.field_2105.method_1727(string.substring(0, m));
				this.method_1886(q, o - 1, r - 1, o + 1 + 9);
			}
		}
	}

	private void method_1886(int i, int j, int k, int l) {
		if (i < k) {
			int m = i;
			i = k;
			k = m;
		}

		if (j < l) {
			int m = j;
			j = l;
			l = m;
		}

		if (k > this.field_2093 + this.field_2087) {
			k = this.field_2093 + this.field_2087;
		}

		if (i > this.field_2093 + this.field_2087) {
			i = this.field_2093 + this.field_2087;
		}

		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.class_1030.field_5110);
		lv2.method_1328(7, class_290.field_1592);
		lv2.method_1315((double)i, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)l, 0.0).method_1344();
		lv2.method_1315((double)k, (double)j, 0.0).method_1344();
		lv2.method_1315((double)i, (double)j, 0.0).method_1344();
		lv.method_1350();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	public void method_1880(int i) {
		this.field_2108 = i;
		if (this.field_2092.length() > i) {
			this.field_2092 = this.field_2092.substring(0, i);
			this.method_1874(this.field_2089, this.field_2092);
		}
	}

	public int method_1861() {
		return this.field_2108;
	}

	public int method_1881() {
		return this.field_2102;
	}

	public boolean method_1851() {
		return this.field_2097;
	}

	public void method_1858(boolean bl) {
		this.field_2097 = bl;
	}

	public void method_1868(int i) {
		this.field_2100 = i;
	}

	public void method_1860(int i) {
		this.field_2098 = i;
	}

	@Override
	public void method_1974(boolean bl) {
		this.method_1876(bl);
	}

	@Override
	public boolean method_16015() {
		return true;
	}

	public void method_1876(boolean bl) {
		if (bl && !this.field_2095) {
			this.field_2107 = 0;
		}

		this.field_2095 = bl;
	}

	public boolean method_1871() {
		return this.field_2095;
	}

	public void method_1888(boolean bl) {
		this.field_2094 = bl;
	}

	public int method_1859() {
		return this.method_1851() ? this.field_2087 - 8 : this.field_2087;
	}

	public void method_1884(int i) {
		int j = this.field_2092.length();
		this.field_2101 = class_3532.method_15340(i, 0, j);
		if (this.field_2105 != null) {
			if (this.field_2103 > j) {
				this.field_2103 = j;
			}

			int k = this.method_1859();
			String string = this.field_2105.method_1714(this.field_2092.substring(this.field_2103), k);
			int l = string.length() + this.field_2103;
			if (this.field_2101 == this.field_2103) {
				this.field_2103 = this.field_2103 - this.field_2105.method_1711(this.field_2092, k, true).length();
			}

			if (this.field_2101 > l) {
				this.field_2103 = this.field_2103 + (this.field_2101 - l);
			} else if (this.field_2101 <= this.field_2103) {
				this.field_2103 = this.field_2103 - (this.field_2103 - this.field_2101);
			}

			this.field_2103 = class_3532.method_15340(this.field_2103, 0, j);
		}
	}

	public void method_1856(boolean bl) {
		this.field_2096 = bl;
	}

	public boolean method_1885() {
		return this.field_2091;
	}

	public void method_1862(boolean bl) {
		this.field_2091 = bl;
	}

	public void method_1887(@Nullable String string) {
		this.field_2106 = string;
	}

	public int method_1889(int i) {
		return i > this.field_2092.length() ? this.field_2093 : this.field_2093 + this.field_2105.method_1727(this.field_2092.substring(0, i));
	}

	public void method_16872(int i) {
		this.field_2093 = i;
	}
}
