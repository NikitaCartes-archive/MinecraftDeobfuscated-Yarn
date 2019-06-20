package net.minecraft;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_355 extends class_332 {
	private static final Ordering<class_640> field_2156 = Ordering.from(new class_355.class_356());
	private final class_310 field_2155;
	private final class_329 field_2157;
	private class_2561 field_2154;
	private class_2561 field_2153;
	private long field_2152;
	private boolean field_2158;

	public class_355(class_310 arg, class_329 arg2) {
		this.field_2155 = arg;
		this.field_2157 = arg2;
	}

	public class_2561 method_1918(class_640 arg) {
		return arg.method_2971() != null ? arg.method_2971() : class_268.method_1142(arg.method_2955(), new class_2585(arg.method_2966().getName()));
	}

	public void method_1921(boolean bl) {
		if (bl && !this.field_2158) {
			this.field_2152 = class_156.method_658();
		}

		this.field_2158 = bl;
	}

	public void method_1919(int i, class_269 arg, @Nullable class_266 arg2) {
		class_634 lv = this.field_2155.field_1724.field_3944;
		List<class_640> list = field_2156.sortedCopy(lv.method_2880());
		int j = 0;
		int k = 0;

		for (class_640 lv2 : list) {
			int l = this.field_2155.field_1772.method_1727(this.method_1918(lv2).method_10863());
			j = Math.max(j, l);
			if (arg2 != null && arg2.method_1118() != class_274.class_275.field_1471) {
				l = this.field_2155.field_1772.method_1727(" " + arg.method_1180(lv2.method_2966().getName(), arg2).method_1126());
				k = Math.max(k, l);
			}
		}

		list = list.subList(0, Math.min(list.size(), 80));
		int m = list.size();
		int n = m;

		int l;
		for (l = 1; n > 20; n = (m + l - 1) / l) {
			l++;
		}

		boolean bl = this.field_2155.method_1542() || this.field_2155.method_1562().method_2872().method_10771();
		int o;
		if (arg2 != null) {
			if (arg2.method_1118() == class_274.class_275.field_1471) {
				o = 90;
			} else {
				o = k;
			}
		} else {
			o = 0;
		}

		int p = Math.min(l * ((bl ? 9 : 0) + j + o + 13), i - 50) / l;
		int q = i / 2 - (p * l + (l - 1) * 5) / 2;
		int r = 10;
		int s = p * l + (l - 1) * 5;
		List<String> list2 = null;
		if (this.field_2153 != null) {
			list2 = this.field_2155.field_1772.method_1728(this.field_2153.method_10863(), i - 50);

			for (String string : list2) {
				s = Math.max(s, this.field_2155.field_1772.method_1727(string));
			}
		}

		List<String> list3 = null;
		if (this.field_2154 != null) {
			list3 = this.field_2155.field_1772.method_1728(this.field_2154.method_10863(), i - 50);

			for (String string2 : list3) {
				s = Math.max(s, this.field_2155.field_1772.method_1727(string2));
			}
		}

		if (list2 != null) {
			fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * 9, Integer.MIN_VALUE);

			for (String string2 : list2) {
				int t = this.field_2155.field_1772.method_1727(string2);
				this.field_2155.field_1772.method_1720(string2, (float)(i / 2 - t / 2), (float)r, -1);
				r += 9;
			}

			r++;
		}

		fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
		int u = this.field_2155.field_1690.method_19344(553648127);

		for (int v = 0; v < m; v++) {
			int t = v / n;
			int w = v % n;
			int x = q + t * p + t * 5;
			int y = r + w * 9;
			fill(x, y, x + p, y + 8, u);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
			);
			if (v < list.size()) {
				class_640 lv3 = (class_640)list.get(v);
				GameProfile gameProfile = lv3.method_2966();
				if (bl) {
					class_1657 lv4 = this.field_2155.field_1687.method_18470(gameProfile.getId());
					boolean bl2 = lv4 != null
						&& lv4.method_7348(class_1664.field_7559)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.field_2155.method_1531().method_4618(lv3.method_2968());
					int z = 8 + (bl2 ? 8 : 0);
					int aa = 8 * (bl2 ? -1 : 1);
					class_332.blit(x, y, 8, 8, 8.0F, (float)z, 8, aa, 64, 64);
					if (lv4 != null && lv4.method_7348(class_1664.field_7563)) {
						int ab = 8 + (bl2 ? 8 : 0);
						int ac = 8 * (bl2 ? -1 : 1);
						class_332.blit(x, y, 8, 8, 40.0F, (float)ab, 8, ac, 64, 64);
					}

					x += 9;
				}

				String string3 = this.method_1918(lv3).method_10863();
				if (lv3.method_2958() == class_1934.field_9219) {
					this.field_2155.field_1772.method_1720(class_124.field_1056 + string3, (float)x, (float)y, -1862270977);
				} else {
					this.field_2155.field_1772.method_1720(string3, (float)x, (float)y, -1);
				}

				if (arg2 != null && lv3.method_2958() != class_1934.field_9219) {
					int ad = x + j + 1;
					int z = ad + o;
					if (z - ad > 5) {
						this.method_1922(arg2, y, gameProfile.getName(), ad, z, lv3);
					}
				}

				this.method_1923(p, x - (bl ? 9 : 0), y, lv3);
			}
		}

		if (list3 != null) {
			r += n * 9 + 1;
			fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list3.size() * 9, Integer.MIN_VALUE);

			for (String string4 : list3) {
				int w = this.field_2155.field_1772.method_1727(string4);
				this.field_2155.field_1772.method_1720(string4, (float)(i / 2 - w / 2), (float)r, -1);
				r += 9;
			}
		}
	}

	protected void method_1923(int i, int j, int k, class_640 arg) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2155.method_1531().method_4618(GUI_ICONS_LOCATION);
		int l = 0;
		int m;
		if (arg.method_2959() < 0) {
			m = 5;
		} else if (arg.method_2959() < 150) {
			m = 0;
		} else if (arg.method_2959() < 300) {
			m = 1;
		} else if (arg.method_2959() < 600) {
			m = 2;
		} else if (arg.method_2959() < 1000) {
			m = 3;
		} else {
			m = 4;
		}

		this.blitOffset += 100;
		this.blit(j + i - 11, k, 0, 176 + m * 8, 10, 8);
		this.blitOffset -= 100;
	}

	private void method_1922(class_266 arg, int i, String string, int j, int k, class_640 arg2) {
		int l = arg.method_1117().method_1180(string, arg).method_1126();
		if (arg.method_1118() == class_274.class_275.field_1471) {
			this.field_2155.method_1531().method_4618(GUI_ICONS_LOCATION);
			long m = class_156.method_658();
			if (this.field_2152 == arg2.method_2976()) {
				if (l < arg2.method_2973()) {
					arg2.method_2978(m);
					arg2.method_2975((long)(this.field_2157.method_1738() + 20));
				} else if (l > arg2.method_2973()) {
					arg2.method_2978(m);
					arg2.method_2975((long)(this.field_2157.method_1738() + 10));
				}
			}

			if (m - arg2.method_2974() > 1000L || this.field_2152 != arg2.method_2976()) {
				arg2.method_2972(l);
				arg2.method_2965(l);
				arg2.method_2978(m);
			}

			arg2.method_2964(this.field_2152);
			arg2.method_2972(l);
			int n = class_3532.method_15386((float)Math.max(l, arg2.method_2960()) / 2.0F);
			int o = Math.max(class_3532.method_15386((float)(l / 2)), Math.max(class_3532.method_15386((float)(arg2.method_2960() / 2)), 10));
			boolean bl = arg2.method_2961() > (long)this.field_2157.method_1738() && (arg2.method_2961() - (long)this.field_2157.method_1738()) / 3L % 2L == 1L;
			if (n > 0) {
				int p = class_3532.method_15375(Math.min((float)(k - j - 4) / (float)o, 9.0F));
				if (p > 3) {
					for (int q = n; q < o; q++) {
						this.blit(j + q * p, i, bl ? 25 : 16, 0, 9, 9);
					}

					for (int q = 0; q < n; q++) {
						this.blit(j + q * p, i, bl ? 25 : 16, 0, 9, 9);
						if (bl) {
							if (q * 2 + 1 < arg2.method_2960()) {
								this.blit(j + q * p, i, 70, 0, 9, 9);
							}

							if (q * 2 + 1 == arg2.method_2960()) {
								this.blit(j + q * p, i, 79, 0, 9, 9);
							}
						}

						if (q * 2 + 1 < l) {
							this.blit(j + q * p, i, q >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (q * 2 + 1 == l) {
							this.blit(j + q * p, i, q >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float f = class_3532.method_15363((float)l / 20.0F, 0.0F, 1.0F);
					int r = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
					String string2 = "" + (float)l / 2.0F;
					if (k - this.field_2155.field_1772.method_1727(string2 + "hp") >= j) {
						string2 = string2 + "hp";
					}

					this.field_2155.field_1772.method_1720(string2, (float)((k + j) / 2 - this.field_2155.field_1772.method_1727(string2) / 2), (float)i, r);
				}
			}
		} else {
			String string3 = class_124.field_1054 + "" + l;
			this.field_2155.field_1772.method_1720(string3, (float)(k - this.field_2155.field_1772.method_1727(string3)), (float)i, 16777215);
		}
	}

	public void method_1924(@Nullable class_2561 arg) {
		this.field_2154 = arg;
	}

	public void method_1925(@Nullable class_2561 arg) {
		this.field_2153 = arg;
	}

	public void method_1920() {
		this.field_2153 = null;
		this.field_2154 = null;
	}

	@Environment(EnvType.CLIENT)
	static class class_356 implements Comparator<class_640> {
		private class_356() {
		}

		public int method_1926(class_640 arg, class_640 arg2) {
			class_268 lv = arg.method_2955();
			class_268 lv2 = arg2.method_2955();
			return ComparisonChain.start()
				.compareTrueFirst(arg.method_2958() != class_1934.field_9219, arg2.method_2958() != class_1934.field_9219)
				.compare(lv != null ? lv.method_1197() : "", lv2 != null ? lv2.method_1197() : "")
				.compare(arg.method_2966().getName(), arg2.method_2966().getName(), String::compareToIgnoreCase)
				.result();
		}
	}
}
