package net.minecraft;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_327 implements AutoCloseable {
	public final int field_2000 = 9;
	public final Random field_2001 = new Random();
	private final class_1060 field_1998;
	private final class_377 field_1997;
	private boolean field_1999;

	public class_327(class_1060 arg, class_377 arg2) {
		this.field_1998 = arg;
		this.field_1997 = arg2;
	}

	public void method_1715(List<class_390> list) {
		this.field_1997.method_2004(list);
	}

	public void close() {
		this.field_1997.close();
	}

	public int method_1720(String string, float f, float g, int i) {
		GlStateManager.enableAlphaTest();
		return this.method_1723(string, f, g, i, true);
	}

	public int method_1729(String string, float f, float g, int i) {
		GlStateManager.enableAlphaTest();
		return this.method_1723(string, f, g, i, false);
	}

	public String method_1721(String string) {
		try {
			Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException var3) {
			return string;
		}
	}

	private int method_1723(String string, float f, float g, int i, boolean bl) {
		if (string == null) {
			return 0;
		} else {
			if (this.field_1999) {
				string = this.method_1721(string);
			}

			if ((i & -67108864) == 0) {
				i |= -16777216;
			}

			if (bl) {
				this.method_1724(string, f, g, i, true);
			}

			f = this.method_1724(string, f, g, i, false);
			return (int)f + (bl ? 1 : 0);
		}
	}

	private float method_1724(String string, float f, float g, int i, boolean bl) {
		float h = bl ? 0.25F : 1.0F;
		float j = (float)(i >> 16 & 0xFF) / 255.0F * h;
		float k = (float)(i >> 8 & 0xFF) / 255.0F * h;
		float l = (float)(i & 0xFF) / 255.0F * h;
		float m = j;
		float n = k;
		float o = l;
		float p = (float)(i >> 24 & 0xFF) / 255.0F;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		class_2960 lv3 = null;
		lv2.method_1328(7, class_290.field_1575);
		boolean bl2 = false;
		boolean bl3 = false;
		boolean bl4 = false;
		boolean bl5 = false;
		boolean bl6 = false;
		List<class_327.class_328> list = Lists.<class_327.class_328>newArrayList();

		for (int q = 0; q < string.length(); q++) {
			char c = string.charAt(q);
			if (c == 167 && q + 1 < string.length()) {
				class_124 lv4 = class_124.method_544(string.charAt(q + 1));
				if (lv4 != null) {
					if (lv4.method_545()) {
						bl2 = false;
						bl3 = false;
						bl6 = false;
						bl5 = false;
						bl4 = false;
						m = j;
						n = k;
						o = l;
					}

					if (lv4.method_532() != null) {
						int r = lv4.method_532();
						m = (float)(r >> 16 & 0xFF) / 255.0F * h;
						n = (float)(r >> 8 & 0xFF) / 255.0F * h;
						o = (float)(r & 0xFF) / 255.0F * h;
					} else if (lv4 == class_124.field_1051) {
						bl2 = true;
					} else if (lv4 == class_124.field_1067) {
						bl3 = true;
					} else if (lv4 == class_124.field_1055) {
						bl6 = true;
					} else if (lv4 == class_124.field_1073) {
						bl5 = true;
					} else if (lv4 == class_124.field_1056) {
						bl4 = true;
					}
				}

				q++;
			} else {
				class_379 lv5 = this.field_1997.method_2011(c);
				class_382 lv6 = bl2 && c != ' ' ? this.field_1997.method_2013(lv5) : this.field_1997.method_2014(c);
				class_2960 lv7 = lv6.method_2026();
				if (lv7 != null) {
					if (lv3 != lv7) {
						lv.method_1350();
						this.field_1998.method_4618(lv7);
						lv2.method_1328(7, class_290.field_1575);
						lv3 = lv7;
					}

					float s = bl3 ? lv5.method_16799() : 0.0F;
					float t = bl ? lv5.method_16800() : 0.0F;
					this.method_1710(lv6, bl3, bl4, s, f + t, g + t, lv2, m, n, o, p);
				}

				float s = lv5.method_16798(bl3);
				float t = bl ? 1.0F : 0.0F;
				if (bl6) {
					list.add(new class_327.class_328(f + t - 1.0F, g + t + 4.5F, f + t + s, g + t + 4.5F - 1.0F, m, n, o, p));
				}

				if (bl5) {
					list.add(new class_327.class_328(f + t - 1.0F, g + t + 9.0F, f + t + s, g + t + 9.0F - 1.0F, m, n, o, p));
				}

				f += s;
			}
		}

		lv.method_1350();
		if (!list.isEmpty()) {
			GlStateManager.disableTexture();
			lv2.method_1328(7, class_290.field_1576);

			for (class_327.class_328 lv8 : list) {
				lv8.method_1730(lv2);
			}

			lv.method_1350();
			GlStateManager.enableTexture();
		}

		return f;
	}

	private void method_1710(class_382 arg, boolean bl, boolean bl2, float f, float g, float h, class_287 arg2, float i, float j, float k, float l) {
		arg.method_2025(this.field_1998, bl2, g, h, arg2, i, j, k, l);
		if (bl) {
			arg.method_2025(this.field_1998, bl2, g + f, h, arg2, i, j, k, l);
		}
	}

	public int method_1727(String string) {
		if (string == null) {
			return 0;
		} else {
			float f = 0.0F;
			boolean bl = false;

			for (int i = 0; i < string.length(); i++) {
				char c = string.charAt(i);
				if (c == 167 && i < string.length() - 1) {
					class_124 lv = class_124.method_544(string.charAt(++i));
					if (lv == class_124.field_1067) {
						bl = true;
					} else if (lv != null && lv.method_545()) {
						bl = false;
					}
				} else {
					f += this.field_1997.method_2011(c).method_16798(bl);
				}
			}

			return class_3532.method_15386(f);
		}
	}

	public float method_1725(char c) {
		return c == 167 ? 0.0F : this.field_1997.method_2011(c).method_16798(false);
	}

	public String method_1714(String string, int i) {
		return this.method_1711(string, i, false);
	}

	public String method_1711(String string, int i, boolean bl) {
		StringBuilder stringBuilder = new StringBuilder();
		float f = 0.0F;
		int j = bl ? string.length() - 1 : 0;
		int k = bl ? -1 : 1;
		boolean bl2 = false;
		boolean bl3 = false;

		for (int l = j; l >= 0 && l < string.length() && f < (float)i; l += k) {
			char c = string.charAt(l);
			if (bl2) {
				bl2 = false;
				class_124 lv = class_124.method_544(c);
				if (lv == class_124.field_1067) {
					bl3 = true;
				} else if (lv != null && lv.method_545()) {
					bl3 = false;
				}
			} else if (c == 167) {
				bl2 = true;
			} else {
				f += this.method_1725(c);
				if (bl3) {
					f++;
				}
			}

			if (f > (float)i) {
				break;
			}

			if (bl) {
				stringBuilder.insert(0, c);
			} else {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	private String method_1718(String string) {
		while (string != null && string.endsWith("\n")) {
			string = string.substring(0, string.length() - 1);
		}

		return string;
	}

	public void method_1712(String string, int i, int j, int k, int l) {
		string = this.method_1718(string);
		this.method_1717(string, i, j, k, l);
	}

	private void method_1717(String string, int i, int j, int k, int l) {
		for (String string2 : this.method_1728(string, k)) {
			float f = (float)i;
			if (this.field_1999) {
				int m = this.method_1727(this.method_1721(string2));
				f += (float)(k - m);
			}

			this.method_1723(string2, f, (float)j, l, false);
			j += 9;
		}
	}

	public int method_1713(String string, int i) {
		return 9 * this.method_1728(string, i).size();
	}

	public void method_1719(boolean bl) {
		this.field_1999 = bl;
	}

	public List<String> method_1728(String string, int i) {
		return Arrays.asList(this.method_1722(string, i).split("\n"));
	}

	public String method_1722(String string, int i) {
		String string2 = "";

		while (!string.isEmpty()) {
			int j = this.method_1716(string, i);
			if (string.length() <= j) {
				return string2 + string;
			}

			String string3 = string.substring(0, j);
			char c = string.charAt(j);
			boolean bl = c == ' ' || c == '\n';
			string = class_124.method_538(string3) + string.substring(j + (bl ? 1 : 0));
			string2 = string2 + string3 + "\n";
		}

		return string2;
	}

	public int method_1716(String string, int i) {
		int j = Math.max(1, i);
		int k = string.length();
		float f = 0.0F;
		int l = 0;
		int m = -1;
		boolean bl = false;

		for (boolean bl2 = true; l < k; l++) {
			char c = string.charAt(l);
			switch (c) {
				case '\n':
					l--;
					break;
				case ' ':
					m = l;
				default:
					if (f != 0.0F) {
						bl2 = false;
					}

					f += this.method_1725(c);
					if (bl) {
						f++;
					}
					break;
				case '§':
					if (l < k - 1) {
						class_124 lv = class_124.method_544(string.charAt(++l));
						if (lv == class_124.field_1067) {
							bl = true;
						} else if (lv != null && lv.method_545()) {
							bl = false;
						}
					}
			}

			if (c == '\n') {
				m = ++l;
				break;
			}

			if (f > (float)j) {
				if (bl2) {
					l++;
				}
				break;
			}
		}

		return l != k && m != -1 && m < l ? m : l;
	}

	public int method_16196(String string, int i, int j, boolean bl) {
		int k = j;
		boolean bl2 = i < 0;
		int l = Math.abs(i);

		for (int m = 0; m < l; m++) {
			if (bl2) {
				while (bl && k > 0 && (string.charAt(k - 1) == ' ' || string.charAt(k - 1) == '\n')) {
					k--;
				}

				while (k > 0 && string.charAt(k - 1) != ' ' && string.charAt(k - 1) != '\n') {
					k--;
				}
			} else {
				int n = string.length();
				int o = string.indexOf(32, k);
				int p = string.indexOf(10, k);
				if (o == -1 && p == -1) {
					k = -1;
				} else if (o != -1 && p != -1) {
					k = Math.min(o, p);
				} else if (o != -1) {
					k = o;
				} else {
					k = p;
				}

				if (k == -1) {
					k = n;
				} else {
					while (bl && k < n && (string.charAt(k) == ' ' || string.charAt(k) == '\n')) {
						k++;
					}
				}
			}
		}

		return k;
	}

	public boolean method_1726() {
		return this.field_1999;
	}

	@Environment(EnvType.CLIENT)
	static class class_328 {
		protected final float field_2008;
		protected final float field_2007;
		protected final float field_2006;
		protected final float field_2005;
		protected final float field_2004;
		protected final float field_2003;
		protected final float field_2010;
		protected final float field_2009;

		private class_328(float f, float g, float h, float i, float j, float k, float l, float m) {
			this.field_2008 = f;
			this.field_2007 = g;
			this.field_2006 = h;
			this.field_2005 = i;
			this.field_2004 = j;
			this.field_2003 = k;
			this.field_2010 = l;
			this.field_2009 = m;
		}

		public void method_1730(class_287 arg) {
			arg.method_1315((double)this.field_2008, (double)this.field_2007, 0.0)
				.method_1336(this.field_2004, this.field_2003, this.field_2010, this.field_2009)
				.method_1344();
			arg.method_1315((double)this.field_2006, (double)this.field_2007, 0.0)
				.method_1336(this.field_2004, this.field_2003, this.field_2010, this.field_2009)
				.method_1344();
			arg.method_1315((double)this.field_2006, (double)this.field_2005, 0.0)
				.method_1336(this.field_2004, this.field_2003, this.field_2010, this.field_2009)
				.method_1344();
			arg.method_1315((double)this.field_2008, (double)this.field_2005, 0.0)
				.method_1336(this.field_2004, this.field_2003, this.field_2010, this.field_2009)
				.method_1344();
		}
	}
}
