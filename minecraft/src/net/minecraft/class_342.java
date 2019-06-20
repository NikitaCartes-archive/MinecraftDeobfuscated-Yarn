package net.minecraft;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_342 extends class_339 implements class_4068, class_364 {
	private final class_327 field_2105;
	private String field_2092 = "";
	private int field_2108 = 32;
	private int field_2107;
	private boolean field_2095 = true;
	private boolean field_2096 = true;
	private boolean field_2094 = true;
	private boolean field_17037;
	private int field_2103;
	private int field_2102;
	private int field_2101;
	private int field_2100 = 14737632;
	private int field_2098 = 7368816;
	private String field_2106;
	private Consumer<String> field_2088;
	private Predicate<String> field_2104 = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> field_2099 = (stringx, integer) -> stringx;

	public class_342(class_327 arg, int i, int j, int k, int l, String string) {
		this(arg, i, j, k, l, null, string);
	}

	public class_342(class_327 arg, int i, int j, int k, int l, @Nullable class_342 arg2, String string) {
		super(i, j, k, l, string);
		this.field_2105 = arg;
		if (arg2 != null) {
			this.method_1852(arg2.method_1882());
		}
	}

	public void method_1863(Consumer<String> consumer) {
		this.field_2088 = consumer;
	}

	public void method_1854(BiFunction<String, Integer, String> biFunction) {
		this.field_2099 = biFunction;
	}

	public void method_1865() {
		this.field_2107++;
	}

	@Override
	protected String getNarrationMessage() {
		String string = this.getMessage();
		return string.isEmpty() ? "" : class_1074.method_4662("gui.narrate.editBox", string, this.field_2092);
	}

	public void method_1852(String string) {
		if (this.field_2104.test(string)) {
			if (string.length() > this.field_2108) {
				this.field_2092 = string.substring(0, this.field_2108);
			} else {
				this.field_2092 = string;
			}

			this.method_1872();
			this.method_1884(this.field_2102);
			this.method_1874(string);
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
			this.method_1874(this.field_2092);
		}
	}

	private void method_1874(String string) {
		if (this.field_2088 != null) {
			this.field_2088.accept(string);
		}

		this.nextNarration = class_156.method_658() + 500L;
	}

	private void method_16873(int i) {
		if (class_437.hasControlDown()) {
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

					this.method_1874(this.field_2092);
				}
			}
		}
	}

	public int method_1853(int i) {
		return this.method_1869(i, this.method_1881());
	}

	private int method_1869(int i, int j) {
		return this.method_1864(i, j, true);
	}

	private int method_1864(int i, int j, boolean bl) {
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

		this.method_1874(this.field_2092);
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
	public boolean keyPressed(int i, int j, int k) {
		if (!this.method_20315()) {
			return false;
		} else {
			this.field_17037 = class_437.hasShiftDown();
			if (class_437.isSelectAll(i)) {
				this.method_1872();
				this.method_1884(0);
				return true;
			} else if (class_437.isCopy(i)) {
				class_310.method_1551().field_1774.method_1455(this.method_1866());
				return true;
			} else if (class_437.isPaste(i)) {
				if (this.field_2094) {
					this.method_1867(class_310.method_1551().field_1774.method_1460());
				}

				return true;
			} else if (class_437.isCut(i)) {
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
						return false;
					case 261:
						if (this.field_2094) {
							this.method_16873(1);
						}

						return true;
					case 262:
						if (class_437.hasControlDown()) {
							this.method_1883(this.method_1853(1));
						} else {
							this.method_1855(1);
						}

						return true;
					case 263:
						if (class_437.hasControlDown()) {
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
		}
	}

	public boolean method_20315() {
		return this.method_1885() && this.isFocused() && this.method_20316();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (!this.method_20315()) {
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
	public boolean mouseClicked(double d, double e, int i) {
		if (!this.method_1885()) {
			return false;
		} else {
			boolean bl = d >= (double)this.field_23658
				&& d < (double)(this.field_23658 + this.width)
				&& e >= (double)this.field_23659
				&& e < (double)(this.field_23659 + this.height);
			if (this.field_2096) {
				this.method_1876(bl);
			}

			if (this.isFocused() && bl && i == 0) {
				int j = class_3532.method_15357(d) - this.field_23658;
				if (this.field_2095) {
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

	public void method_1876(boolean bl) {
		super.setFocused(bl);
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.method_1885()) {
			if (this.method_1851()) {
				fill(this.field_23658 - 1, this.field_23659 - 1, this.field_23658 + this.width + 1, this.field_23659 + this.height + 1, -6250336);
				fill(this.field_23658, this.field_23659, this.field_23658 + this.width, this.field_23659 + this.height, -16777216);
			}

			int k = this.field_2094 ? this.field_2100 : this.field_2098;
			int l = this.field_2102 - this.field_2103;
			int m = this.field_2101 - this.field_2103;
			String string = this.field_2105.method_1714(this.field_2092.substring(this.field_2103), this.method_1859());
			boolean bl = l >= 0 && l <= string.length();
			boolean bl2 = this.isFocused() && this.field_2107 / 6 % 2 == 0 && bl;
			int n = this.field_2095 ? this.field_23658 + 4 : this.field_23658;
			int o = this.field_2095 ? this.field_23659 + (this.height - 8) / 2 : this.field_23659;
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
				q = l > 0 ? n + this.width : n;
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
					class_332.fill(q, o - 1, q + 1, o + 1 + 9, -3092272);
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

		if (k > this.field_23658 + this.width) {
			k = this.field_23658 + this.width;
		}

		if (i > this.field_23658 + this.width) {
			i = this.field_23658 + this.width;
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
			this.method_1874(this.field_2092);
		}
	}

	private int method_1861() {
		return this.field_2108;
	}

	public int method_1881() {
		return this.field_2102;
	}

	private boolean method_1851() {
		return this.field_2095;
	}

	public void method_1858(boolean bl) {
		this.field_2095 = bl;
	}

	public void method_1868(int i) {
		this.field_2100 = i;
	}

	public void method_1860(int i) {
		this.field_2098 = i;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return this.visible && this.field_2094 ? super.changeFocus(bl) : false;
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return this.visible
			&& d >= (double)this.field_23658
			&& d < (double)(this.field_23658 + this.width)
			&& e >= (double)this.field_23659
			&& e < (double)(this.field_23659 + this.height);
	}

	@Override
	protected void onFocusedChanged(boolean bl) {
		if (bl) {
			this.field_2107 = 0;
		}
	}

	private boolean method_20316() {
		return this.field_2094;
	}

	public void method_1888(boolean bl) {
		this.field_2094 = bl;
	}

	public int method_1859() {
		return this.method_1851() ? this.width - 8 : this.width;
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
		return this.visible;
	}

	public void method_1862(boolean bl) {
		this.visible = bl;
	}

	public void method_1887(@Nullable String string) {
		this.field_2106 = string;
	}

	public int method_1889(int i) {
		return i > this.field_2092.length() ? this.field_23658 : this.field_23658 + this.field_2105.method_1727(this.field_2092.substring(0, i));
	}

	public void method_16872(int i) {
		this.field_23658 = i;
	}
}
