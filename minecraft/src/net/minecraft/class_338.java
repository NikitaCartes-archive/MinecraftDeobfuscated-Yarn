package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_338 extends class_332 {
	private static final Logger field_2065 = LogManager.getLogger();
	private final class_310 field_2062;
	private final List<String> field_2063 = Lists.<String>newArrayList();
	private final List<class_303> field_2061 = Lists.<class_303>newArrayList();
	private final List<class_303> field_2064 = Lists.<class_303>newArrayList();
	private int field_2066;
	private boolean field_2067;

	public class_338(class_310 arg) {
		this.field_2062 = arg;
	}

	public void method_1805(int i) {
		if (this.field_2062.field_1690.field_1877 != class_1659.field_7536) {
			int j = this.method_1813();
			int k = this.field_2064.size();
			if (k > 0) {
				boolean bl = false;
				if (this.method_1819()) {
					bl = true;
				}

				double d = this.method_1814();
				int l = class_3532.method_15384((double)this.method_1811() / d);
				GlStateManager.pushMatrix();
				GlStateManager.translatef(2.0F, 8.0F, 0.0F);
				GlStateManager.scaled(d, d, 1.0);
				double e = this.field_2062.field_1690.field_1820 * 0.9F + 0.1F;
				double f = this.field_2062.field_1690.field_18726;
				int m = 0;

				for (int n = 0; n + this.field_2066 < this.field_2064.size() && n < j; n++) {
					class_303 lv = (class_303)this.field_2064.get(n + this.field_2066);
					if (lv != null) {
						int o = i - lv.method_1414();
						if (o < 200 || bl) {
							double g = bl ? 1.0 : method_19348(o);
							int p = (int)(255.0 * g * e);
							int q = (int)(255.0 * g * f);
							m++;
							if (p > 3) {
								int r = 0;
								int s = -n * 9;
								fill(-2, s - 9, 0 + l + 4, s, q << 24);
								String string = lv.method_1412().method_10863();
								GlStateManager.enableBlend();
								this.field_2062.field_1772.method_1720(string, 0.0F, (float)(s - 8), 16777215 + (p << 24));
								GlStateManager.disableAlphaTest();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if (bl) {
					int nx = 9;
					GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
					int t = k * nx + k;
					int o = m * nx + m;
					int u = this.field_2066 * o / k;
					int v = o * o / t;
					if (t != o) {
						int p = u > 0 ? 170 : 96;
						int q = this.field_2067 ? 13382451 : 3355562;
						fill(0, -u, 2, -u - v, q + (p << 24));
						fill(2, -u, 1, -u - v, 13421772 + (p << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	private static double method_19348(int i) {
		double d = (double)i / 200.0;
		d = 1.0 - d;
		d *= 10.0;
		d = class_3532.method_15350(d, 0.0, 1.0);
		return d * d;
	}

	public void method_1808(boolean bl) {
		this.field_2064.clear();
		this.field_2061.clear();
		if (bl) {
			this.field_2063.clear();
		}
	}

	public void method_1812(class_2561 arg) {
		this.method_1804(arg, 0);
	}

	public void method_1804(class_2561 arg, int i) {
		this.method_1815(arg, i, this.field_2062.field_1705.method_1738(), false);
		field_2065.info("[CHAT] {}", arg.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}

	private void method_1815(class_2561 arg, int i, int j, boolean bl) {
		if (i != 0) {
			this.method_1807(i);
		}

		int k = class_3532.method_15357((double)this.method_1811() / this.method_1814());
		List<class_2561> list = class_341.method_1850(arg, k, this.field_2062.field_1772, false, false);
		boolean bl2 = this.method_1819();

		for (class_2561 lv : list) {
			if (bl2 && this.field_2066 > 0) {
				this.field_2067 = true;
				this.method_1802(1.0);
			}

			this.field_2064.add(0, new class_303(j, lv, i));
		}

		while (this.field_2064.size() > 100) {
			this.field_2064.remove(this.field_2064.size() - 1);
		}

		if (!bl) {
			this.field_2061.add(0, new class_303(j, arg, i));

			while (this.field_2061.size() > 100) {
				this.field_2061.remove(this.field_2061.size() - 1);
			}
		}
	}

	public void method_1817() {
		this.field_2064.clear();
		this.method_1820();

		for (int i = this.field_2061.size() - 1; i >= 0; i--) {
			class_303 lv = (class_303)this.field_2061.get(i);
			this.method_1815(lv.method_1412(), lv.method_1413(), lv.method_1414(), true);
		}
	}

	public List<String> method_1809() {
		return this.field_2063;
	}

	public void method_1803(String string) {
		if (this.field_2063.isEmpty() || !((String)this.field_2063.get(this.field_2063.size() - 1)).equals(string)) {
			this.field_2063.add(string);
		}
	}

	public void method_1820() {
		this.field_2066 = 0;
		this.field_2067 = false;
	}

	public void method_1802(double d) {
		this.field_2066 = (int)((double)this.field_2066 + d);
		int i = this.field_2064.size();
		if (this.field_2066 > i - this.method_1813()) {
			this.field_2066 = i - this.method_1813();
		}

		if (this.field_2066 <= 0) {
			this.field_2066 = 0;
			this.field_2067 = false;
		}
	}

	@Nullable
	public class_2561 method_1816(double d, double e) {
		if (!this.method_1819()) {
			return null;
		} else {
			double f = this.method_1814();
			double g = d - 2.0;
			double h = (double)this.field_2062.field_1704.method_4502() - e - 40.0;
			g = (double)class_3532.method_15357(g / f);
			h = (double)class_3532.method_15357(h / f);
			if (!(g < 0.0) && !(h < 0.0)) {
				int i = Math.min(this.method_1813(), this.field_2064.size());
				if (g <= (double)class_3532.method_15357((double)this.method_1811() / this.method_1814()) && h < (double)(9 * i + i)) {
					int j = (int)(h / 9.0 + (double)this.field_2066);
					if (j >= 0 && j < this.field_2064.size()) {
						class_303 lv = (class_303)this.field_2064.get(j);
						int k = 0;

						for (class_2561 lv2 : lv.method_1412()) {
							if (lv2 instanceof class_2585) {
								k += this.field_2062.field_1772.method_1727(class_341.method_1849(((class_2585)lv2).method_10993(), false));
								if ((double)k > g) {
									return lv2;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public boolean method_1819() {
		return this.field_2062.field_1755 instanceof class_408;
	}

	public void method_1807(int i) {
		Iterator<class_303> iterator = this.field_2064.iterator();

		while (iterator.hasNext()) {
			class_303 lv = (class_303)iterator.next();
			if (lv.method_1413() == i) {
				iterator.remove();
			}
		}

		iterator = this.field_2061.iterator();

		while (iterator.hasNext()) {
			class_303 lv = (class_303)iterator.next();
			if (lv.method_1413() == i) {
				iterator.remove();
				break;
			}
		}
	}

	public int method_1811() {
		return method_1806(this.field_2062.field_1690.field_1915);
	}

	public int method_1810() {
		return method_1818(this.method_1819() ? this.field_2062.field_1690.field_1838 : this.field_2062.field_1690.field_1825);
	}

	public double method_1814() {
		return this.field_2062.field_1690.field_1908;
	}

	public static int method_1806(double d) {
		int i = 320;
		int j = 40;
		return class_3532.method_15357(d * 280.0 + 40.0);
	}

	public static int method_1818(double d) {
		int i = 180;
		int j = 20;
		return class_3532.method_15357(d * 160.0 + 20.0);
	}

	public int method_1813() {
		return this.method_1810() / 9;
	}
}
