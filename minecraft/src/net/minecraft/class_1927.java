package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1927 {
	private final boolean field_9186;
	private final class_1927.class_4179 field_9184;
	private final Random field_9191 = new Random();
	private final class_1937 field_9187;
	private final double field_9195;
	private final double field_9192;
	private final double field_9189;
	private final class_1297 field_9185;
	private final float field_9190;
	private class_1282 field_9193;
	private final List<class_2338> field_9188 = Lists.<class_2338>newArrayList();
	private final Map<class_1657, class_243> field_9194 = Maps.<class_1657, class_243>newHashMap();

	@Environment(EnvType.CLIENT)
	public class_1927(class_1937 arg, @Nullable class_1297 arg2, double d, double e, double f, float g, List<class_2338> list) {
		this(arg, arg2, d, e, f, g, false, class_1927.class_4179.field_18687, list);
	}

	@Environment(EnvType.CLIENT)
	public class_1927(
		class_1937 arg, @Nullable class_1297 arg2, double d, double e, double f, float g, boolean bl, class_1927.class_4179 arg3, List<class_2338> list
	) {
		this(arg, arg2, d, e, f, g, bl, arg3);
		this.field_9188.addAll(list);
	}

	public class_1927(class_1937 arg, @Nullable class_1297 arg2, double d, double e, double f, float g, boolean bl, class_1927.class_4179 arg3) {
		this.field_9187 = arg;
		this.field_9185 = arg2;
		this.field_9190 = g;
		this.field_9195 = d;
		this.field_9192 = e;
		this.field_9189 = f;
		this.field_9186 = bl;
		this.field_9184 = arg3;
		this.field_9193 = class_1282.method_5531(this);
	}

	public static float method_17752(class_243 arg, class_1297 arg2) {
		class_238 lv = arg2.method_5829();
		double d = 1.0 / ((lv.field_1320 - lv.field_1323) * 2.0 + 1.0);
		double e = 1.0 / ((lv.field_1325 - lv.field_1322) * 2.0 + 1.0);
		double f = 1.0 / ((lv.field_1324 - lv.field_1321) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = class_3532.method_16436((double)k, lv.field_1323, lv.field_1320);
						double o = class_3532.method_16436((double)l, lv.field_1322, lv.field_1325);
						double p = class_3532.method_16436((double)m, lv.field_1321, lv.field_1324);
						class_243 lv2 = new class_243(n + g, o, p + h);
						if (arg2.field_6002.method_17742(new class_3959(lv2, arg, class_3959.class_3960.field_17559, class_3959.class_242.field_1348, arg2)).method_17783()
							== class_239.class_240.field_1333) {
							i++;
						}

						j++;
					}
				}
			}

			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}

	public void method_8348() {
		Set<class_2338> set = Sets.<class_2338>newHashSet();
		int i = 16;

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double e = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double f = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double g = Math.sqrt(d * d + e * e + f * f);
						d /= g;
						e /= g;
						f /= g;
						float h = this.field_9190 * (0.7F + this.field_9187.field_9229.nextFloat() * 0.6F);
						double m = this.field_9195;
						double n = this.field_9192;
						double o = this.field_9189;

						for (float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
							class_2338 lv = new class_2338(m, n, o);
							class_2680 lv2 = this.field_9187.method_8320(lv);
							class_3610 lv3 = this.field_9187.method_8316(lv);
							if (!lv2.method_11588() || !lv3.method_15769()) {
								float q = Math.max(lv2.method_11614().method_9520(), lv3.method_15760());
								if (this.field_9185 != null) {
									q = this.field_9185.method_5774(this, this.field_9187, lv, lv2, lv3, q);
								}

								h -= (q + 0.3F) * 0.3F;
							}

							if (h > 0.0F && (this.field_9185 == null || this.field_9185.method_5853(this, this.field_9187, lv, lv2, h))) {
								set.add(lv);
							}

							m += d * 0.3F;
							n += e * 0.3F;
							o += f * 0.3F;
						}
					}
				}
			}
		}

		this.field_9188.addAll(set);
		float r = this.field_9190 * 2.0F;
		int k = class_3532.method_15357(this.field_9195 - (double)r - 1.0);
		int lx = class_3532.method_15357(this.field_9195 + (double)r + 1.0);
		int s = class_3532.method_15357(this.field_9192 - (double)r - 1.0);
		int t = class_3532.method_15357(this.field_9192 + (double)r + 1.0);
		int u = class_3532.method_15357(this.field_9189 - (double)r - 1.0);
		int v = class_3532.method_15357(this.field_9189 + (double)r + 1.0);
		List<class_1297> list = this.field_9187.method_8335(this.field_9185, new class_238((double)k, (double)s, (double)u, (double)lx, (double)t, (double)v));
		class_243 lv4 = new class_243(this.field_9195, this.field_9192, this.field_9189);

		for (int w = 0; w < list.size(); w++) {
			class_1297 lv5 = (class_1297)list.get(w);
			if (!lv5.method_5659()) {
				double x = (double)(class_3532.method_15368(lv5.method_5707(new class_243(this.field_9195, this.field_9192, this.field_9189))) / r);
				if (x <= 1.0) {
					double y = lv5.field_5987 - this.field_9195;
					double z = lv5.field_6010 + (double)lv5.method_5751() - this.field_9192;
					double aa = lv5.field_6035 - this.field_9189;
					double ab = (double)class_3532.method_15368(y * y + z * z + aa * aa);
					if (ab != 0.0) {
						y /= ab;
						z /= ab;
						aa /= ab;
						double ac = (double)method_17752(lv4, lv5);
						double ad = (1.0 - x) * ac;
						lv5.method_5643(this.method_8349(), (float)((int)((ad * ad + ad) / 2.0 * 7.0 * (double)r + 1.0)));
						double ae = ad;
						if (lv5 instanceof class_1309) {
							ae = class_1900.method_8237((class_1309)lv5, ad);
						}

						lv5.method_18799(lv5.method_18798().method_1031(y * ae, z * ae, aa * ae));
						if (lv5 instanceof class_1657) {
							class_1657 lv6 = (class_1657)lv5;
							if (!lv6.method_7325() && (!lv6.method_7337() || !lv6.field_7503.field_7479)) {
								this.field_9194.put(lv6, new class_243(y * ad, z * ad, aa * ad));
							}
						}
					}
				}
			}
		}
	}

	public void method_8350(boolean bl) {
		this.field_9187
			.method_8465(
				null,
				this.field_9195,
				this.field_9192,
				this.field_9189,
				class_3417.field_15152,
				class_3419.field_15245,
				4.0F,
				(1.0F + (this.field_9187.field_9229.nextFloat() - this.field_9187.field_9229.nextFloat()) * 0.2F) * 0.7F
			);
		boolean bl2 = this.field_9184 != class_1927.class_4179.field_18685;
		if (!(this.field_9190 < 2.0F) && bl2) {
			this.field_9187.method_8406(class_2398.field_11221, this.field_9195, this.field_9192, this.field_9189, 1.0, 0.0, 0.0);
		} else {
			this.field_9187.method_8406(class_2398.field_11236, this.field_9195, this.field_9192, this.field_9189, 1.0, 0.0, 0.0);
		}

		if (bl2) {
			for (class_2338 lv : this.field_9188) {
				class_2680 lv2 = this.field_9187.method_8320(lv);
				class_2248 lv3 = lv2.method_11614();
				if (bl) {
					double d = (double)((float)lv.method_10263() + this.field_9187.field_9229.nextFloat());
					double e = (double)((float)lv.method_10264() + this.field_9187.field_9229.nextFloat());
					double f = (double)((float)lv.method_10260() + this.field_9187.field_9229.nextFloat());
					double g = d - this.field_9195;
					double h = e - this.field_9192;
					double i = f - this.field_9189;
					double j = (double)class_3532.method_15368(g * g + h * h + i * i);
					g /= j;
					h /= j;
					i /= j;
					double k = 0.5 / (j / (double)this.field_9190 + 0.1);
					k *= (double)(this.field_9187.field_9229.nextFloat() * this.field_9187.field_9229.nextFloat() + 0.3F);
					g *= k;
					h *= k;
					i *= k;
					this.field_9187.method_8406(class_2398.field_11203, (d + this.field_9195) / 2.0, (e + this.field_9192) / 2.0, (f + this.field_9189) / 2.0, g, h, i);
					this.field_9187.method_8406(class_2398.field_11251, d, e, f, g, h, i);
				}

				if (!lv2.method_11588()) {
					if (lv3.method_9533(this) && this.field_9187 instanceof class_3218) {
						class_2586 lv4 = lv3.method_9570() ? this.field_9187.method_8321(lv) : null;
						class_47.class_48 lv5 = new class_47.class_48((class_3218)this.field_9187)
							.method_311(this.field_9187.field_9229)
							.method_312(class_181.field_1232, lv)
							.method_312(class_181.field_1229, class_1799.field_8037)
							.method_306(class_181.field_1228, lv4);
						if (this.field_9184 == class_1927.class_4179.field_18687) {
							lv5.method_312(class_181.field_1225, this.field_9190);
						}

						class_2248.method_9566(lv2, lv5);
					}

					this.field_9187.method_8652(lv, class_2246.field_10124.method_9564(), 3);
					lv3.method_9586(this.field_9187, lv, this);
				}
			}
		}

		if (this.field_9186) {
			for (class_2338 lv : this.field_9188) {
				if (this.field_9187.method_8320(lv).method_11588()
					&& this.field_9187.method_8320(lv.method_10074()).method_11598(this.field_9187, lv.method_10074())
					&& this.field_9191.nextInt(3) == 0) {
					this.field_9187.method_8501(lv, class_2246.field_10036.method_9564());
				}
			}
		}
	}

	public class_1282 method_8349() {
		return this.field_9193;
	}

	public void method_8345(class_1282 arg) {
		this.field_9193 = arg;
	}

	public Map<class_1657, class_243> method_8351() {
		return this.field_9194;
	}

	@Nullable
	public class_1309 method_8347() {
		if (this.field_9185 == null) {
			return null;
		} else if (this.field_9185 instanceof class_1541) {
			return ((class_1541)this.field_9185).method_6970();
		} else {
			return this.field_9185 instanceof class_1309 ? (class_1309)this.field_9185 : null;
		}
	}

	public void method_8352() {
		this.field_9188.clear();
	}

	public List<class_2338> method_8346() {
		return this.field_9188;
	}

	public static enum class_4179 {
		field_18685,
		field_18686,
		field_18687;
	}
}
