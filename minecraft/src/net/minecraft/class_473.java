package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_473 extends class_437 {
	private final class_1657 field_2826;
	private final class_1799 field_2835;
	private boolean field_2837;
	private boolean field_2828;
	private int field_2844;
	private int field_2840;
	private final List<String> field_17116 = Lists.<String>newArrayList();
	private String field_2847 = "";
	private int field_2833;
	private int field_2829;
	private long field_2830;
	private int field_2827 = -1;
	private class_474 field_2843;
	private class_474 field_2839;
	private class_339 field_2848;
	private class_339 field_2831;
	private class_339 field_2841;
	private class_339 field_2849;
	private final class_1268 field_2832;

	public class_473(class_1657 arg, class_1799 arg2, class_1268 arg3) {
		this.field_2826 = arg;
		this.field_2835 = arg2;
		this.field_2832 = arg3;
		class_2487 lv = arg2.method_7969();
		if (lv != null) {
			class_2499 lv2 = lv.method_10554("pages", 8).method_10612();

			for (int i = 0; i < lv2.size(); i++) {
				this.field_17116.add(lv2.method_10608(i));
			}
		}

		if (this.field_17116.isEmpty()) {
			this.field_17116.add("");
		}
	}

	private int method_17046() {
		return this.field_17116.size();
	}

	@Override
	public void method_2225() {
		super.method_2225();
		this.field_2844++;
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_2831 = this.method_2219(new class_339(3, this.field_2561 / 2 - 100, 196, 98, 20, class_1074.method_4662("book.signButton")) {
			@Override
			public void method_1826(double d, double e) {
				class_473.this.field_2828 = true;
				class_473.this.method_2413();
			}
		});
		this.field_2848 = this.method_2219(new class_339(0, this.field_2561 / 2 + 2, 196, 98, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_473.this.field_2563.method_1507(null);
				class_473.this.method_2407(false);
			}
		});
		this.field_2841 = this.method_2219(new class_339(5, this.field_2561 / 2 - 100, 196, 98, 20, class_1074.method_4662("book.finalizeButton")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_473.this.field_2828) {
					class_473.this.method_2407(true);
					class_473.this.field_2563.method_1507(null);
				}
			}
		});
		this.field_2849 = this.method_2219(new class_339(4, this.field_2561 / 2 + 2, 196, 98, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_473.this.field_2828) {
					class_473.this.field_2828 = false;
				}

				class_473.this.method_2413();
			}
		});
		int i = (this.field_2561 - 192) / 2;
		int j = 2;
		this.field_2843 = this.method_2219(new class_474(1, i + 116, 159, true) {
			@Override
			public void method_1826(double d, double e) {
				class_473.this.method_2444();
			}

			@Override
			public void method_1832(class_1144 arg) {
				class_473.this.method_17549();
			}
		});
		this.field_2839 = this.method_2219(new class_474(2, i + 43, 159, false) {
			@Override
			public void method_1826(double d, double e) {
				class_473.this.method_2437();
			}

			@Override
			public void method_1832(class_1144 arg) {
				class_473.this.method_17549();
			}
		});
		this.method_2413();
	}

	protected void method_17549() {
		class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3417.field_17481, 1.0F));
	}

	private String method_16345(String string) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (c != 167 && c != 127) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private void method_2437() {
		if (this.field_2840 > 0) {
			this.field_2840--;
			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		}

		this.method_2413();
	}

	private void method_2444() {
		if (this.field_2840 < this.method_17046() - 1) {
			this.field_2840++;
			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		} else {
			this.method_2436();
			if (this.field_2840 < this.method_17046() - 1) {
				this.field_2840++;
			}

			this.field_2833 = 0;
			this.field_2829 = this.field_2833;
		}

		this.method_2413();
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	private void method_2413() {
		this.field_2839.field_2076 = !this.field_2828 && this.field_2840 > 0;
		this.field_2848.field_2076 = !this.field_2828;
		this.field_2831.field_2076 = !this.field_2828;
		this.field_2849.field_2076 = this.field_2828;
		this.field_2841.field_2076 = this.field_2828;
		this.field_2841.field_2078 = !this.field_2847.trim().isEmpty();
	}

	private void method_17047() {
		ListIterator<String> listIterator = this.field_17116.listIterator(this.field_17116.size());

		while (listIterator.hasPrevious() && ((String)listIterator.previous()).isEmpty()) {
			listIterator.remove();
		}
	}

	private void method_2407(boolean bl) {
		if (this.field_2837) {
			this.method_17047();
			class_2499 lv = new class_2499();
			this.field_17116.stream().map(class_2519::new).forEach(lv::method_10606);
			if (!this.field_17116.isEmpty()) {
				this.field_2835.method_7959("pages", lv);
			}

			if (bl) {
				this.field_2835.method_7959("author", new class_2519(this.field_2826.method_7334().getName()));
				this.field_2835.method_7959("title", new class_2519(this.field_2847.trim()));
			}

			this.field_2563.method_1562().method_2883(new class_2820(this.field_2835, bl, this.field_2832));
		}
	}

	private void method_2436() {
		if (this.method_17046() < 100) {
			this.field_17116.add("");
			this.field_2837 = true;
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (super.method_16805(i, j, k)) {
			return true;
		} else {
			return this.field_2828 ? this.method_2446(i, j, k) : this.method_2411(i, j, k);
		}
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (super.method_16806(c, i)) {
			return true;
		} else if (this.field_2828) {
			if (this.field_2847.length() < 16 && class_155.method_643(c)) {
				this.field_2847 = this.field_2847 + Character.toString(c);
				this.method_2413();
				this.field_2837 = true;
				return true;
			} else {
				return false;
			}
		} else if (class_155.method_643(c)) {
			this.method_2431(Character.toString(c));
			return true;
		} else {
			return false;
		}
	}

	private boolean method_2411(int i, int j, int k) {
		String string = this.method_2427();
		if (class_437.method_2226(i)) {
			this.field_2829 = 0;
			this.field_2833 = string.length();
			return true;
		} else if (class_437.method_2227(i)) {
			this.field_2563.field_1774.method_1455(this.method_2442());
			return true;
		} else if (class_437.method_2235(i)) {
			this.method_2431(this.method_16345(class_124.method_539(this.field_2563.field_1774.method_1460().replaceAll("\\r", ""))));
			this.field_2829 = this.field_2833;
			return true;
		} else if (class_437.method_2212(i)) {
			this.field_2563.field_1774.method_1455(this.method_2442());
			this.method_2410();
			return true;
		} else {
			switch (i) {
				case 257:
				case 335:
					this.method_2431("\n");
					return true;
				case 259:
					this.method_2428(string);
					return true;
				case 261:
					this.method_2434(string);
					return true;
				case 262:
					this.method_2408(string);
					return true;
				case 263:
					this.method_2440(string);
					return true;
				case 264:
					this.method_2435(string);
					return true;
				case 265:
					this.method_2430(string);
					return true;
				case 266:
					this.field_2839.method_1826(0.0, 0.0);
					return true;
				case 267:
					this.field_2843.method_1826(0.0, 0.0);
					return true;
				case 268:
					this.method_2421(string);
					return true;
				case 269:
					this.method_2414(string);
					return true;
				default:
					return false;
			}
		}
	}

	private void method_2428(String string) {
		if (!string.isEmpty()) {
			if (this.field_2829 != this.field_2833) {
				this.method_2410();
			} else if (this.field_2833 > 0) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_2833 - 1)).toString();
				this.method_2439(string2);
				this.field_2833 = Math.max(0, this.field_2833 - 1);
				this.field_2829 = this.field_2833;
			}
		}
	}

	private void method_2434(String string) {
		if (!string.isEmpty()) {
			if (this.field_2829 != this.field_2833) {
				this.method_2410();
			} else if (this.field_2833 < string.length()) {
				String string2 = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_2833)).toString();
				this.method_2439(string2);
			}
		}
	}

	private void method_2440(String string) {
		int i = this.field_2554.method_1726() ? 1 : -1;
		if (class_437.method_2238()) {
			this.field_2833 = this.field_2554.method_16196(string, i, this.field_2833, true);
		} else {
			this.field_2833 = Math.max(0, this.field_2833 + i);
		}

		if (!class_437.method_2223()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2408(String string) {
		int i = this.field_2554.method_1726() ? -1 : 1;
		if (class_437.method_2238()) {
			this.field_2833 = this.field_2554.method_16196(string, i, this.field_2833, true);
		} else {
			this.field_2833 = Math.min(string.length(), this.field_2833 + i);
		}

		if (!class_437.method_2223()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2430(String string) {
		if (!string.isEmpty()) {
			class_473.class_475 lv = this.method_2416(string, this.field_2833);
			if (lv.field_2853 == 0) {
				this.field_2833 = 0;
				if (!class_437.method_2223()) {
					this.field_2829 = this.field_2833;
				}
			} else {
				int i = this.method_2404(string, new class_473.class_475(lv.field_2854 + this.method_2412(string, this.field_2833) / 3, lv.field_2853 - 9));
				if (i >= 0) {
					this.field_2833 = i;
					if (!class_437.method_2223()) {
						this.field_2829 = this.field_2833;
					}
				}
			}
		}
	}

	private void method_2435(String string) {
		if (!string.isEmpty()) {
			class_473.class_475 lv = this.method_2416(string, this.field_2833);
			int i = this.field_2554.method_1713(string + "" + class_124.field_1074 + "_", 114);
			if (lv.field_2853 + 9 == i) {
				this.field_2833 = string.length();
				if (!class_437.method_2223()) {
					this.field_2829 = this.field_2833;
				}
			} else {
				int j = this.method_2404(string, new class_473.class_475(lv.field_2854 + this.method_2412(string, this.field_2833) / 3, lv.field_2853 + 9));
				if (j >= 0) {
					this.field_2833 = j;
					if (!class_437.method_2223()) {
						this.field_2829 = this.field_2833;
					}
				}
			}
		}
	}

	private void method_2421(String string) {
		this.field_2833 = this.method_2404(string, new class_473.class_475(0, this.method_2416(string, this.field_2833).field_2853));
		if (!class_437.method_2223()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2414(String string) {
		this.field_2833 = this.method_2404(string, new class_473.class_475(113, this.method_2416(string, this.field_2833).field_2853));
		if (!class_437.method_2223()) {
			this.field_2829 = this.field_2833;
		}
	}

	private void method_2410() {
		if (this.field_2829 != this.field_2833) {
			String string = this.method_2427();
			int i = Math.min(this.field_2833, this.field_2829);
			int j = Math.max(this.field_2833, this.field_2829);
			String string2 = string.substring(0, i) + string.substring(j);
			this.field_2833 = i;
			this.field_2829 = this.field_2833;
			this.method_2439(string2);
		}
	}

	private int method_2412(String string, int i) {
		return (int)this.field_2554.method_1725(string.charAt(class_3532.method_15340(i, 0, string.length() - 1)));
	}

	private boolean method_2446(int i, int j, int k) {
		switch (i) {
			case 257:
			case 335:
				if (!this.field_2847.isEmpty()) {
					this.method_2407(true);
					this.field_2563.method_1507(null);
				}

				return true;
			case 259:
				if (!this.field_2847.isEmpty()) {
					this.field_2847 = this.field_2847.substring(0, this.field_2847.length() - 1);
					this.method_2413();
				}

				return true;
			default:
				return false;
		}
	}

	private String method_2427() {
		return this.field_2840 >= 0 && this.field_2840 < this.field_17116.size() ? (String)this.field_17116.get(this.field_2840) : "";
	}

	private void method_2439(String string) {
		if (this.field_2840 >= 0 && this.field_2840 < this.field_17116.size()) {
			this.field_17116.set(this.field_2840, string);
			this.field_2837 = true;
		}
	}

	private void method_2431(String string) {
		if (this.field_2829 != this.field_2833) {
			this.method_2410();
		}

		String string2 = this.method_2427();
		this.field_2833 = class_3532.method_15340(this.field_2833, 0, string2.length());
		String string3 = new StringBuilder(string2).insert(this.field_2833, string).toString();
		int i = this.field_2554.method_1713(string3 + "" + class_124.field_1074 + "_", 114);
		if (i <= 128 && string3.length() < 1024) {
			this.method_2439(string3);
			this.field_2829 = this.field_2833 = Math.min(this.method_2427().length(), this.field_2833 + string.length());
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(class_3872.field_17117);
		int k = (this.field_2561 - 192) / 2;
		int l = 2;
		this.method_1788(k, 2, 0, 0, 192, 192);
		if (this.field_2828) {
			String string = this.field_2847;
			if (this.field_2844 / 6 % 2 == 0) {
				string = string + "" + class_124.field_1074 + "_";
			} else {
				string = string + "" + class_124.field_1080 + "_";
			}

			String string2 = class_1074.method_4662("book.editTitle");
			int m = this.method_2424(string2);
			this.field_2554.method_1729(string2, (float)(k + 36 + (114 - m) / 2), 34.0F, 0);
			int n = this.method_2424(string);
			this.field_2554.method_1729(string, (float)(k + 36 + (114 - n) / 2), 50.0F, 0);
			String string3 = class_1074.method_4662("book.byAuthor", this.field_2826.method_5477().getString());
			int o = this.method_2424(string3);
			this.field_2554.method_1729(class_124.field_1063 + string3, (float)(k + 36 + (114 - o) / 2), 60.0F, 0);
			String string4 = class_1074.method_4662("book.finalizeWarning");
			this.field_2554.method_1712(string4, k + 36, 82, 114, 0);
		} else {
			String string = class_1074.method_4662("book.pageIndicator", this.field_2840 + 1, this.method_17046());
			String string2 = this.method_2427();
			int m = this.method_2424(string);
			this.field_2554.method_1729(string, (float)(k - m + 192 - 44), 18.0F, 0);
			this.field_2554.method_1712(string2, k + 36, 32, 114, 0);
			this.method_2441(string2);
			if (this.field_2844 / 6 % 2 == 0) {
				class_473.class_475 lv = this.method_2416(string2, this.field_2833);
				if (this.field_2554.method_1726()) {
					this.method_2429(lv);
					lv.field_2854 = lv.field_2854 - 4;
				}

				this.method_2415(lv);
				if (this.field_2833 < string2.length()) {
					class_332.method_1785(lv.field_2854, lv.field_2853 - 1, lv.field_2854 + 1, lv.field_2853 + 9, -16777216);
				} else {
					this.field_2554.method_1729("_", (float)lv.field_2854, (float)lv.field_2853, 0);
				}
			}
		}

		super.method_2214(i, j, f);
	}

	private int method_2424(String string) {
		return this.field_2554.method_1727(this.field_2554.method_1726() ? this.field_2554.method_1721(string) : string);
	}

	private int method_2417(String string, int i) {
		return this.field_2554.method_1716(string, i);
	}

	private String method_2442() {
		String string = this.method_2427();
		int i = Math.min(this.field_2833, this.field_2829);
		int j = Math.max(this.field_2833, this.field_2829);
		return string.substring(i, j);
	}

	private void method_2441(String string) {
		if (this.field_2829 != this.field_2833) {
			int i = Math.min(this.field_2833, this.field_2829);
			int j = Math.max(this.field_2833, this.field_2829);
			String string2 = string.substring(i, j);
			int k = this.field_2554.method_16196(string, 1, j, true);
			String string3 = string.substring(i, k);
			class_473.class_475 lv = this.method_2416(string, i);
			class_473.class_475 lv2 = new class_473.class_475(lv.field_2854, lv.field_2853 + 9);

			while (!string2.isEmpty()) {
				int l = this.method_2417(string3, 114 - lv.field_2854);
				if (string2.length() <= l) {
					lv2.field_2854 = lv.field_2854 + this.method_2424(string2);
					this.method_2409(lv, lv2);
					break;
				}

				l = Math.min(l, string2.length() - 1);
				String string4 = string2.substring(0, l);
				char c = string2.charAt(l);
				boolean bl = c == ' ' || c == '\n';
				string2 = class_124.method_538(string4) + string2.substring(l + (bl ? 1 : 0));
				string3 = class_124.method_538(string4) + string3.substring(l + (bl ? 1 : 0));
				lv2.field_2854 = lv.field_2854 + this.method_2424(string4 + " ");
				this.method_2409(lv, lv2);
				lv.field_2854 = 0;
				lv.field_2853 = lv.field_2853 + 9;
				lv2.field_2853 = lv2.field_2853 + 9;
			}
		}
	}

	private void method_2409(class_473.class_475 arg, class_473.class_475 arg2) {
		class_473.class_475 lv = new class_473.class_475(arg.field_2854, arg.field_2853);
		class_473.class_475 lv2 = new class_473.class_475(arg2.field_2854, arg2.field_2853);
		if (this.field_2554.method_1726()) {
			this.method_2429(lv);
			this.method_2429(lv2);
			int i = lv2.field_2854;
			lv2.field_2854 = lv.field_2854;
			lv.field_2854 = i;
		}

		this.method_2415(lv);
		this.method_2415(lv2);
		class_289 lv3 = class_289.method_1348();
		class_287 lv4 = lv3.method_1349();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.class_1030.field_5110);
		lv4.method_1328(7, class_290.field_1592);
		lv4.method_1315((double)lv.field_2854, (double)lv2.field_2853, 0.0).method_1344();
		lv4.method_1315((double)lv2.field_2854, (double)lv2.field_2853, 0.0).method_1344();
		lv4.method_1315((double)lv2.field_2854, (double)lv.field_2853, 0.0).method_1344();
		lv4.method_1315((double)lv.field_2854, (double)lv.field_2853, 0.0).method_1344();
		lv3.method_1350();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	private class_473.class_475 method_2416(String string, int i) {
		class_473.class_475 lv = new class_473.class_475();
		int j = 0;
		int k = 0;

		for (String string2 = string; !string2.isEmpty(); k = j) {
			int l = this.method_2417(string2, 114);
			if (string2.length() <= l) {
				String string3 = string2.substring(0, Math.min(Math.max(i - k, 0), string2.length()));
				lv.field_2854 = lv.field_2854 + this.method_2424(string3);
				break;
			}

			String string3 = string2.substring(0, l);
			char c = string2.charAt(l);
			boolean bl = c == ' ' || c == '\n';
			string2 = class_124.method_538(string3) + string2.substring(l + (bl ? 1 : 0));
			j += string3.length() + (bl ? 1 : 0);
			if (j - 1 >= i) {
				String string4 = string3.substring(0, Math.min(Math.max(i - k, 0), string3.length()));
				lv.field_2854 = lv.field_2854 + this.method_2424(string4);
				break;
			}

			lv.field_2853 = lv.field_2853 + 9;
		}

		return lv;
	}

	private void method_2429(class_473.class_475 arg) {
		if (this.field_2554.method_1726()) {
			arg.field_2854 = 114 - arg.field_2854;
		}
	}

	private void method_2443(class_473.class_475 arg) {
		arg.field_2854 = arg.field_2854 - (this.field_2561 - 192) / 2 - 36;
		arg.field_2853 = arg.field_2853 - 32;
	}

	private void method_2415(class_473.class_475 arg) {
		arg.field_2854 = arg.field_2854 + (this.field_2561 - 192) / 2 + 36;
		arg.field_2853 = arg.field_2853 + 32;
	}

	private int method_2425(String string, int i) {
		if (i < 0) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;
			String string2 = string + " ";

			for (int j = 0; j < string2.length(); j++) {
				char c = string2.charAt(j);
				float g = this.field_2554.method_1725(c);
				if (c == 167 && j < string2.length() - 1) {
					c = string2.charAt(++j);
					if (c == 'l' || c == 'L') {
						bl = true;
					} else if (c == 'r' || c == 'R') {
						bl = false;
					}

					g = 0.0F;
				}

				float h = f;
				f += g;
				if (bl && g > 0.0F) {
					f++;
				}

				if ((float)i >= h && (float)i < f) {
					return j;
				}
			}

			return (float)i >= f ? string2.length() - 1 : -1;
		}
	}

	private int method_2404(String string, class_473.class_475 arg) {
		int i = 16 * 9;
		if (arg.field_2853 > i) {
			return -1;
		} else {
			int j = Integer.MIN_VALUE;
			int k = 9;
			int l = 0;

			for (String string2 = string; !string2.isEmpty() && j < i; k += 9) {
				int m = this.method_2417(string2, 114);
				if (m < string2.length()) {
					String string3 = string2.substring(0, m);
					if (arg.field_2853 >= j && arg.field_2853 < k) {
						int n = this.method_2425(string3, arg.field_2854);
						return n < 0 ? -1 : l + n;
					}

					char c = string2.charAt(m);
					boolean bl = c == ' ' || c == '\n';
					string2 = class_124.method_538(string3) + string2.substring(m + (bl ? 1 : 0));
					l += string3.length() + (bl ? 1 : 0);
				} else if (arg.field_2853 >= j && arg.field_2853 < k) {
					int o = this.method_2425(string2, arg.field_2854);
					return o < 0 ? -1 : l + o;
				}

				j = k;
			}

			return string.length();
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (i == 0) {
			long l = class_156.method_658();
			String string = this.method_2427();
			if (!string.isEmpty()) {
				class_473.class_475 lv = new class_473.class_475((int)d, (int)e);
				this.method_2443(lv);
				this.method_2429(lv);
				int j = this.method_2404(string, lv);
				if (j >= 0) {
					if (j != this.field_2827 || l - this.field_2830 >= 250L) {
						this.field_2833 = j;
						if (!class_437.method_2223()) {
							this.field_2829 = this.field_2833;
						}
					} else if (this.field_2829 == this.field_2833) {
						this.field_2829 = this.field_2554.method_16196(string, -1, j, false);
						this.field_2833 = this.field_2554.method_16196(string, 1, j, false);
					} else {
						this.field_2829 = 0;
						this.field_2833 = this.method_2427().length();
					}
				}

				this.field_2827 = j;
			}

			this.field_2830 = l;
		}

		return super.method_16807(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		if (i == 0 && this.field_2840 >= 0 && this.field_2840 < this.field_17116.size()) {
			String string = (String)this.field_17116.get(this.field_2840);
			class_473.class_475 lv = new class_473.class_475((int)d, (int)e);
			this.method_2443(lv);
			this.method_2429(lv);
			int j = this.method_2404(string, lv);
			if (j >= 0) {
				this.field_2833 = j;
			}
		}

		return super.method_16801(d, e, i, f, g);
	}

	@Environment(EnvType.CLIENT)
	class class_475 {
		private int field_2854;
		private int field_2853;

		class_475() {
		}

		class_475(int i, int j) {
			this.field_2854 = i;
			this.field_2853 = j;
		}
	}
}
