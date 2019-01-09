package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_456 extends class_332 {
	private static final class_2960 field_2709 = new class_2960("textures/gui/advancements/widgets.png");
	private static final Pattern field_2708 = Pattern.compile("(.+) \\S+");
	private final class_454 field_2703;
	private final class_161 field_2702;
	private final class_185 field_2712;
	private final String field_2713;
	private final int field_2715;
	private final List<String> field_2705;
	private final class_310 field_2704;
	private class_456 field_2706;
	private final List<class_456> field_2707 = Lists.<class_456>newArrayList();
	private class_167 field_2714;
	private final int field_2711;
	private final int field_2710;

	public class_456(class_454 arg, class_310 arg2, class_161 arg3, class_185 arg4) {
		this.field_2703 = arg;
		this.field_2702 = arg3;
		this.field_2712 = arg4;
		this.field_2704 = arg2;
		this.field_2713 = arg2.field_1772.method_1714(arg4.method_811().method_10863(), 163);
		this.field_2711 = class_3532.method_15375(arg4.method_818() * 28.0F);
		this.field_2710 = class_3532.method_15375(arg4.method_819() * 27.0F);
		int i = arg3.method_683();
		int j = String.valueOf(i).length();
		int k = i > 1 ? arg2.field_1772.method_1727("  ") + arg2.field_1772.method_1727("0") * j * 2 + arg2.field_1772.method_1727("/") : 0;
		int l = 29 + arg2.field_1772.method_1727(this.field_2713) + k;
		String string = arg4.method_817().method_10863();
		this.field_2705 = this.method_2330(string, l);

		for (String string2 : this.field_2705) {
			l = Math.max(l, arg2.field_1772.method_1727(string2));
		}

		this.field_2715 = l + 3 + 5;
	}

	private List<String> method_2330(String string, int i) {
		if (string.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<String> list = this.field_2704.field_1772.method_1728(string, i);
			if (list.size() < 2) {
				return list;
			} else {
				String string2 = (String)list.get(0);
				String string3 = (String)list.get(1);
				int j = this.field_2704.field_1772.method_1727(string2 + ' ' + string3.split(" ")[0]);
				if (j - i <= 10) {
					return this.field_2704.field_1772.method_1728(string, j);
				} else {
					Matcher matcher = field_2708.matcher(string2);
					if (matcher.matches()) {
						int k = this.field_2704.field_1772.method_1727(matcher.group(1));
						if (i - k <= 10) {
							return this.field_2704.field_1772.method_1728(string, k);
						}
					}

					return list;
				}
			}
		}
	}

	@Nullable
	private class_456 method_2328(class_161 arg) {
		do {
			arg = arg.method_687();
		} while (arg != null && arg.method_686() == null);

		return arg != null && arg.method_686() != null ? this.field_2703.method_2308(arg) : null;
	}

	public void method_2323(int i, int j, boolean bl) {
		if (this.field_2706 != null) {
			int k = i + this.field_2706.field_2711 + 13;
			int l = i + this.field_2706.field_2711 + 26 + 4;
			int m = j + this.field_2706.field_2710 + 13;
			int n = i + this.field_2711 + 13;
			int o = j + this.field_2710 + 13;
			int p = bl ? -16777216 : -1;
			if (bl) {
				this.method_1783(l, k, m - 1, p);
				this.method_1783(l + 1, k, m, p);
				this.method_1783(l, k, m + 1, p);
				this.method_1783(n, l - 1, o - 1, p);
				this.method_1783(n, l - 1, o, p);
				this.method_1783(n, l - 1, o + 1, p);
				this.method_1787(l - 1, o, m, p);
				this.method_1787(l + 1, o, m, p);
			} else {
				this.method_1783(l, k, m, p);
				this.method_1783(n, l, o, p);
				this.method_1787(l, o, m, p);
			}
		}

		for (class_456 lv : this.field_2707) {
			lv.method_2323(i, j, bl);
		}
	}

	public void method_2325(int i, int j) {
		if (!this.field_2712.method_824() || this.field_2714 != null && this.field_2714.method_740()) {
			float f = this.field_2714 == null ? 0.0F : this.field_2714.method_735();
			class_455 lv;
			if (f >= 1.0F) {
				lv = class_455.field_2701;
			} else {
				lv = class_455.field_2699;
			}

			this.field_2704.method_1531().method_4618(field_2709);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			this.method_1788(i + this.field_2711 + 3, j + this.field_2710, this.field_2712.method_815().method_832(), 128 + lv.method_2320() * 26, 26, 26);
			class_308.method_1453();
			this.field_2704.method_1480().method_4026(null, this.field_2712.method_821(), i + this.field_2711 + 8, j + this.field_2710 + 5);
		}

		for (class_456 lv2 : this.field_2707) {
			lv2.method_2325(i, j);
		}
	}

	public void method_2333(class_167 arg) {
		this.field_2714 = arg;
	}

	public void method_2322(class_456 arg) {
		this.field_2707.add(arg);
	}

	public void method_2331(int i, int j, float f, int k, int l) {
		boolean bl = k + i + this.field_2711 + this.field_2715 + 26 >= this.field_2703.method_2312().field_2561;
		String string = this.field_2714 == null ? null : this.field_2714.method_728();
		int m = string == null ? 0 : this.field_2704.field_1772.method_1727(string);
		boolean bl2 = 113 - j - this.field_2710 - 26 <= 6 + this.field_2705.size() * 9;
		float g = this.field_2714 == null ? 0.0F : this.field_2714.method_735();
		int n = class_3532.method_15375(g * (float)this.field_2715);
		class_455 lv;
		class_455 lv2;
		class_455 lv3;
		if (g >= 1.0F) {
			n = this.field_2715 / 2;
			lv = class_455.field_2701;
			lv2 = class_455.field_2701;
			lv3 = class_455.field_2701;
		} else if (n < 2) {
			n = this.field_2715 / 2;
			lv = class_455.field_2699;
			lv2 = class_455.field_2699;
			lv3 = class_455.field_2699;
		} else if (n > this.field_2715 - 2) {
			n = this.field_2715 / 2;
			lv = class_455.field_2701;
			lv2 = class_455.field_2701;
			lv3 = class_455.field_2699;
		} else {
			lv = class_455.field_2701;
			lv2 = class_455.field_2699;
			lv3 = class_455.field_2699;
		}

		int o = this.field_2715 - n;
		this.field_2704.method_1531().method_4618(field_2709);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		int p = j + this.field_2710;
		int q;
		if (bl) {
			q = i + this.field_2711 - this.field_2715 + 26 + 6;
		} else {
			q = i + this.field_2711;
		}

		int r = 32 + this.field_2705.size() * 9;
		if (!this.field_2705.isEmpty()) {
			if (bl2) {
				this.method_2324(q, p + 26 - r, this.field_2715, r, 10, 200, 26, 0, 52);
			} else {
				this.method_2324(q, p, this.field_2715, r, 10, 200, 26, 0, 52);
			}
		}

		this.method_1788(q, p, 0, lv.method_2320() * 26, n, 26);
		this.method_1788(q + n, p, 200 - o, lv2.method_2320() * 26, o, 26);
		this.method_1788(i + this.field_2711 + 3, j + this.field_2710, this.field_2712.method_815().method_832(), 128 + lv3.method_2320() * 26, 26, 26);
		if (bl) {
			this.field_2704.field_1772.method_1720(this.field_2713, (float)(q + 5), (float)(j + this.field_2710 + 9), -1);
			if (string != null) {
				this.field_2704.field_1772.method_1720(string, (float)(i + this.field_2711 - m), (float)(j + this.field_2710 + 9), -1);
			}
		} else {
			this.field_2704.field_1772.method_1720(this.field_2713, (float)(i + this.field_2711 + 32), (float)(j + this.field_2710 + 9), -1);
			if (string != null) {
				this.field_2704.field_1772.method_1720(string, (float)(i + this.field_2711 + this.field_2715 - m - 5), (float)(j + this.field_2710 + 9), -1);
			}
		}

		if (bl2) {
			for (int s = 0; s < this.field_2705.size(); s++) {
				this.field_2704.field_1772.method_1729((String)this.field_2705.get(s), (float)(q + 5), (float)(p + 26 - r + 7 + s * 9), -5592406);
			}
		} else {
			for (int s = 0; s < this.field_2705.size(); s++) {
				this.field_2704.field_1772.method_1729((String)this.field_2705.get(s), (float)(q + 5), (float)(j + this.field_2710 + 9 + 17 + s * 9), -5592406);
			}
		}

		class_308.method_1453();
		this.field_2704.method_1480().method_4026(null, this.field_2712.method_821(), i + this.field_2711 + 8, j + this.field_2710 + 5);
	}

	protected void method_2324(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		this.method_1788(i, j, p, q, m, m);
		this.method_2321(i + m, j, k - m - m, m, p + m, q, n - m - m, o);
		this.method_1788(i + k - m, j, p + n - m, q, m, m);
		this.method_1788(i, j + l - m, p, q + o - m, m, m);
		this.method_2321(i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
		this.method_1788(i + k - m, j + l - m, p + n - m, q + o - m, m, m);
		this.method_2321(i, j + m, m, l - m - m, p, q + m, n, o - m - m);
		this.method_2321(i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
		this.method_2321(i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
	}

	protected void method_2321(int i, int j, int k, int l, int m, int n, int o, int p) {
		int q = 0;

		while (q < k) {
			int r = i + q;
			int s = Math.min(o, k - q);
			int t = 0;

			while (t < l) {
				int u = j + t;
				int v = Math.min(p, l - t);
				this.method_1788(r, u, m, n, s, v);
				t += p;
			}

			q += o;
		}
	}

	public boolean method_2329(int i, int j, int k, int l) {
		if (!this.field_2712.method_824() || this.field_2714 != null && this.field_2714.method_740()) {
			int m = i + this.field_2711;
			int n = m + 26;
			int o = j + this.field_2710;
			int p = o + 26;
			return k >= m && k <= n && l >= o && l <= p;
		} else {
			return false;
		}
	}

	public void method_2332() {
		if (this.field_2706 == null && this.field_2702.method_687() != null) {
			this.field_2706 = this.method_2328(this.field_2702);
			if (this.field_2706 != null) {
				this.field_2706.method_2322(this);
			}
		}
	}

	public int method_2326() {
		return this.field_2710;
	}

	public int method_2327() {
		return this.field_2711;
	}
}
