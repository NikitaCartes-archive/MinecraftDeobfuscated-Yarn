package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1688 extends class_1297 {
	private static final class_2940<Integer> field_7663 = class_2945.method_12791(class_1688.class, class_2943.field_13327);
	private static final class_2940<Integer> field_7668 = class_2945.method_12791(class_1688.class, class_2943.field_13327);
	private static final class_2940<Float> field_7667 = class_2945.method_12791(class_1688.class, class_2943.field_13320);
	private static final class_2940<Integer> field_7671 = class_2945.method_12791(class_1688.class, class_2943.field_13327);
	private static final class_2940<Integer> field_7661 = class_2945.method_12791(class_1688.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_7670 = class_2945.method_12791(class_1688.class, class_2943.field_13323);
	private boolean field_7660;
	private static final int[][][] field_7664 = new int[][][]{
		{{0, 0, -1}, {0, 0, 1}},
		{{-1, 0, 0}, {1, 0, 0}},
		{{-1, -1, 0}, {1, 0, 0}},
		{{-1, 0, 0}, {1, -1, 0}},
		{{0, 0, -1}, {0, -1, 1}},
		{{0, -1, -1}, {0, 0, 1}},
		{{0, 0, 1}, {1, 0, 0}},
		{{0, 0, 1}, {-1, 0, 0}},
		{{0, 0, -1}, {-1, 0, 0}},
		{{0, 0, -1}, {1, 0, 0}}
	};
	private int field_7669;
	private double field_7665;
	private double field_7666;
	private double field_7662;
	private double field_7659;
	private double field_7657;
	@Environment(EnvType.CLIENT)
	private double field_7658;
	@Environment(EnvType.CLIENT)
	private double field_7655;
	@Environment(EnvType.CLIENT)
	private double field_7656;

	protected class_1688(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6033 = true;
	}

	protected class_1688(class_1299<?> arg, class_1937 arg2, double d, double e, double f) {
		this(arg, arg2);
		this.method_5814(d, e, f);
		this.method_18799(class_243.field_1353);
		this.field_6014 = d;
		this.field_6036 = e;
		this.field_5969 = f;
	}

	public static class_1688 method_7523(class_1937 arg, double d, double e, double f, class_1688.class_1689 arg2) {
		if (arg2 == class_1688.class_1689.field_7678) {
			return new class_1694(arg, d, e, f);
		} else if (arg2 == class_1688.class_1689.field_7679) {
			return new class_1696(arg, d, e, f);
		} else if (arg2 == class_1688.class_1689.field_7675) {
			return new class_1701(arg, d, e, f);
		} else if (arg2 == class_1688.class_1689.field_7680) {
			return new class_1699(arg, d, e, f);
		} else if (arg2 == class_1688.class_1689.field_7677) {
			return new class_1700(arg, d, e, f);
		} else {
			return (class_1688)(arg2 == class_1688.class_1689.field_7681 ? new class_1697(arg, d, e, f) : new class_1695(arg, d, e, f));
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7663, 0);
		this.field_6011.method_12784(field_7668, 1);
		this.field_6011.method_12784(field_7667, 0.0F);
		this.field_6011.method_12784(field_7671, class_2248.method_9507(class_2246.field_10124.method_9564()));
		this.field_6011.method_12784(field_7661, 6);
		this.field_6011.method_12784(field_7670, false);
	}

	@Nullable
	@Override
	public class_238 method_5708(class_1297 arg) {
		return arg.method_5810() ? arg.method_5829() : null;
	}

	@Override
	public boolean method_5810() {
		return true;
	}

	@Override
	public double method_5621() {
		return 0.0;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.field_6002.field_9236 || this.field_5988) {
			return true;
		} else if (this.method_5679(arg)) {
			return false;
		} else {
			this.method_7524(-this.method_7522());
			this.method_7509(10);
			this.method_5785();
			this.method_7520(this.method_7521() + f * 10.0F);
			boolean bl = arg.method_5529() instanceof class_1657 && ((class_1657)arg.method_5529()).field_7503.field_7477;
			if (bl || this.method_7521() > 40.0F) {
				this.method_5772();
				if (bl && !this.method_16914()) {
					this.method_5650();
				} else {
					this.method_7516(arg);
				}
			}

			return true;
		}
	}

	public void method_7516(class_1282 arg) {
		this.method_5650();
		if (this.field_6002.method_8450().method_8355("doEntityDrops")) {
			class_1799 lv = new class_1799(class_1802.field_8045);
			if (this.method_16914()) {
				lv.method_7977(this.method_5797());
			}

			this.method_5775(lv);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5879() {
		this.method_7524(-this.method_7522());
		this.method_7509(10);
		this.method_7520(this.method_7521() + this.method_7521() * 10.0F);
	}

	@Override
	public boolean method_5863() {
		return !this.field_5988;
	}

	@Override
	public class_2350 method_5755() {
		return this.field_7660 ? this.method_5735().method_10153().method_10170() : this.method_5735().method_10170();
	}

	@Override
	public void method_5773() {
		if (this.method_7507() > 0) {
			this.method_7509(this.method_7507() - 1);
		}

		if (this.method_7521() > 0.0F) {
			this.method_7520(this.method_7521() - 1.0F);
		}

		if (this.field_6010 < -64.0) {
			this.method_5825();
		}

		this.method_18379();
		if (this.field_6002.field_9236) {
			if (this.field_7669 > 0) {
				double d = this.field_5987 + (this.field_7665 - this.field_5987) / (double)this.field_7669;
				double e = this.field_6010 + (this.field_7666 - this.field_6010) / (double)this.field_7669;
				double f = this.field_6035 + (this.field_7662 - this.field_6035) / (double)this.field_7669;
				double g = class_3532.method_15338(this.field_7659 - (double)this.field_6031);
				this.field_6031 = (float)((double)this.field_6031 + g / (double)this.field_7669);
				this.field_5965 = (float)((double)this.field_5965 + (this.field_7657 - (double)this.field_5965) / (double)this.field_7669);
				this.field_7669--;
				this.method_5814(d, e, f);
				this.method_5710(this.field_6031, this.field_5965);
			} else {
				this.method_5814(this.field_5987, this.field_6010, this.field_6035);
				this.method_5710(this.field_6031, this.field_5965);
			}
		} else {
			this.field_6014 = this.field_5987;
			this.field_6036 = this.field_6010;
			this.field_5969 = this.field_6035;
			if (!this.method_5740()) {
				this.method_18799(this.method_18798().method_1031(0.0, -0.04, 0.0));
			}

			int i = class_3532.method_15357(this.field_5987);
			int j = class_3532.method_15357(this.field_6010);
			int k = class_3532.method_15357(this.field_6035);
			if (this.field_6002.method_8320(new class_2338(i, j - 1, k)).method_11602(class_3481.field_15463)) {
				j--;
			}

			class_2338 lv = new class_2338(i, j, k);
			class_2680 lv2 = this.field_6002.method_8320(lv);
			if (lv2.method_11602(class_3481.field_15463)) {
				this.method_7513(lv, lv2);
				if (lv2.method_11614() == class_2246.field_10546) {
					this.method_7506(i, j, k, (Boolean)lv2.method_11654(class_2442.field_11364));
				}
			} else {
				this.method_7512();
			}

			this.method_5852();
			this.field_5965 = 0.0F;
			double h = this.field_6014 - this.field_5987;
			double l = this.field_5969 - this.field_6035;
			if (h * h + l * l > 0.001) {
				this.field_6031 = (float)(class_3532.method_15349(l, h) * 180.0 / Math.PI);
				if (this.field_7660) {
					this.field_6031 += 180.0F;
				}
			}

			double m = (double)class_3532.method_15393(this.field_6031 - this.field_5982);
			if (m < -170.0 || m >= 170.0) {
				this.field_6031 += 180.0F;
				this.field_7660 = !this.field_7660;
			}

			this.method_5710(this.field_6031, this.field_5965);
			if (this.method_7518() == class_1688.class_1689.field_7674 && method_17996(this.method_18798()) > 0.01) {
				List<class_1297> list = this.field_6002.method_8333(this, this.method_5829().method_1009(0.2F, 0.0, 0.2F), class_1301.method_5911(this));
				if (!list.isEmpty()) {
					for (int n = 0; n < list.size(); n++) {
						class_1297 lv3 = (class_1297)list.get(n);
						if (!(lv3 instanceof class_1657) && !(lv3 instanceof class_1439) && !(lv3 instanceof class_1688) && !this.method_5782() && !lv3.method_5765()) {
							lv3.method_5804(this);
						} else {
							lv3.method_5697(this);
						}
					}
				}
			} else {
				for (class_1297 lv4 : this.field_6002.method_8335(this, this.method_5829().method_1009(0.2F, 0.0, 0.2F))) {
					if (!this.method_5626(lv4) && lv4.method_5810() && lv4 instanceof class_1688) {
						lv4.method_5697(this);
					}
				}
			}

			this.method_5713();
		}
	}

	protected double method_7504() {
		return 0.4;
	}

	public void method_7506(int i, int j, int k, boolean bl) {
	}

	protected void method_7512() {
		double d = this.method_7504();
		class_243 lv = this.method_18798();
		this.method_18800(class_3532.method_15350(lv.field_1352, -d, d), lv.field_1351, class_3532.method_15350(lv.field_1350, -d, d));
		if (this.field_5952) {
			this.method_18799(this.method_18798().method_1021(0.5));
		}

		this.method_5784(class_1313.field_6308, this.method_18798());
		if (!this.field_5952) {
			this.method_18799(this.method_18798().method_1021(0.95));
		}
	}

	protected void method_7513(class_2338 arg, class_2680 arg2) {
		this.field_6017 = 0.0F;
		class_243 lv = this.method_7508(this.field_5987, this.field_6010, this.field_6035);
		this.field_6010 = (double)arg.method_10264();
		boolean bl = false;
		boolean bl2 = false;
		class_2241 lv2 = (class_2241)arg2.method_11614();
		if (lv2 == class_2246.field_10425) {
			bl = (Boolean)arg2.method_11654(class_2442.field_11364);
			bl2 = !bl;
		}

		double d = 0.0078125;
		class_243 lv3 = this.method_18798();
		class_2768 lv4 = arg2.method_11654(lv2.method_9474());
		switch (lv4) {
			case field_12667:
				this.method_18799(lv3.method_1031(-0.0078125, 0.0, 0.0));
				this.field_6010++;
				break;
			case field_12666:
				this.method_18799(lv3.method_1031(0.0078125, 0.0, 0.0));
				this.field_6010++;
				break;
			case field_12670:
				this.method_18799(lv3.method_1031(0.0, 0.0, 0.0078125));
				this.field_6010++;
				break;
			case field_12668:
				this.method_18799(lv3.method_1031(0.0, 0.0, -0.0078125));
				this.field_6010++;
		}

		lv3 = this.method_18798();
		int[][] is = field_7664[lv4.method_11896()];
		double e = (double)(is[1][0] - is[0][0]);
		double f = (double)(is[1][2] - is[0][2]);
		double g = Math.sqrt(e * e + f * f);
		double h = lv3.field_1352 * e + lv3.field_1350 * f;
		if (h < 0.0) {
			e = -e;
			f = -f;
		}

		double i = Math.min(2.0, Math.sqrt(method_17996(lv3)));
		lv3 = new class_243(i * e / g, lv3.field_1351, i * f / g);
		this.method_18799(lv3);
		class_1297 lv5 = this.method_5685().isEmpty() ? null : (class_1297)this.method_5685().get(0);
		if (lv5 instanceof class_1657) {
			double j = (double)((class_1657)lv5).field_6250;
			if (j > 0.0) {
				double k = -Math.sin((double)(lv5.field_6031 * (float) (Math.PI / 180.0)));
				double l = Math.cos((double)(lv5.field_6031 * (float) (Math.PI / 180.0)));
				class_243 lv6 = this.method_18798();
				double m = method_17996(lv6);
				if (m < 0.01) {
					this.method_18799(lv6.method_1031(k * 0.1, 0.0, l * 0.1));
					bl2 = false;
				}
			}
		}

		if (bl2) {
			double j = Math.sqrt(method_17996(this.method_18798()));
			if (j < 0.03) {
				this.method_18799(class_243.field_1353);
			} else {
				this.method_18799(this.method_18798().method_18805(0.5, 0.0, 0.5));
			}
		}

		double j = (double)arg.method_10263() + 0.5 + (double)is[0][0] * 0.5;
		double k = (double)arg.method_10260() + 0.5 + (double)is[0][2] * 0.5;
		double l = (double)arg.method_10263() + 0.5 + (double)is[1][0] * 0.5;
		double n = (double)arg.method_10260() + 0.5 + (double)is[1][2] * 0.5;
		e = l - j;
		f = n - k;
		double o;
		if (e == 0.0) {
			this.field_5987 = (double)arg.method_10263() + 0.5;
			o = this.field_6035 - (double)arg.method_10260();
		} else if (f == 0.0) {
			this.field_6035 = (double)arg.method_10260() + 0.5;
			o = this.field_5987 - (double)arg.method_10263();
		} else {
			double p = this.field_5987 - j;
			double q = this.field_6035 - k;
			o = (p * e + q * f) * 2.0;
		}

		this.field_5987 = j + e * o;
		this.field_6035 = k + f * o;
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		double p = this.method_5782() ? 0.75 : 1.0;
		double q = this.method_7504();
		lv3 = this.method_18798();
		this.method_5784(
			class_1313.field_6308, new class_243(class_3532.method_15350(p * lv3.field_1352, -q, q), 0.0, class_3532.method_15350(p * lv3.field_1350, -q, q))
		);
		if (is[0][1] != 0
			&& class_3532.method_15357(this.field_5987) - arg.method_10263() == is[0][0]
			&& class_3532.method_15357(this.field_6035) - arg.method_10260() == is[0][2]) {
			this.method_5814(this.field_5987, this.field_6010 + (double)is[0][1], this.field_6035);
		} else if (is[1][1] != 0
			&& class_3532.method_15357(this.field_5987) - arg.method_10263() == is[1][0]
			&& class_3532.method_15357(this.field_6035) - arg.method_10260() == is[1][2]) {
			this.method_5814(this.field_5987, this.field_6010 + (double)is[1][1], this.field_6035);
		}

		this.method_7525();
		class_243 lv7 = this.method_7508(this.field_5987, this.field_6010, this.field_6035);
		if (lv7 != null && lv != null) {
			double r = (lv.field_1351 - lv7.field_1351) * 0.05;
			class_243 lv8 = this.method_18798();
			double s = Math.sqrt(method_17996(lv8));
			if (s > 0.0) {
				this.method_18799(lv8.method_18805((s + r) / s, 1.0, (s + r) / s));
			}

			this.method_5814(this.field_5987, lv7.field_1351, this.field_6035);
		}

		int t = class_3532.method_15357(this.field_5987);
		int u = class_3532.method_15357(this.field_6035);
		if (t != arg.method_10263() || u != arg.method_10260()) {
			class_243 lv8 = this.method_18798();
			double s = Math.sqrt(method_17996(lv8));
			this.method_18800(s * (double)(t - arg.method_10263()), lv8.field_1351, s * (double)(u - arg.method_10260()));
		}

		if (bl) {
			class_243 lv8 = this.method_18798();
			double s = Math.sqrt(method_17996(lv8));
			if (s > 0.01) {
				double v = 0.06;
				this.method_18799(lv8.method_1031(lv8.field_1352 / s * 0.06, 0.0, lv8.field_1350 / s * 0.06));
			} else {
				class_243 lv9 = this.method_18798();
				double w = lv9.field_1352;
				double x = lv9.field_1350;
				if (lv4 == class_2768.field_12674) {
					if (this.method_18803(arg.method_10067())) {
						w = 0.02;
					} else if (this.method_18803(arg.method_10078())) {
						w = -0.02;
					}
				} else {
					if (lv4 != class_2768.field_12665) {
						return;
					}

					if (this.method_18803(arg.method_10095())) {
						x = 0.02;
					} else if (this.method_18803(arg.method_10072())) {
						x = -0.02;
					}
				}

				this.method_18800(w, lv9.field_1351, x);
			}
		}
	}

	private boolean method_18803(class_2338 arg) {
		return this.field_6002.method_8320(arg).method_11621(this.field_6002, arg);
	}

	protected void method_7525() {
		double d = this.method_5782() ? 0.997 : 0.96;
		this.method_18799(this.method_18798().method_18805(d, 0.0, d));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_243 method_7505(double d, double e, double f, double g) {
		int i = class_3532.method_15357(d);
		int j = class_3532.method_15357(e);
		int k = class_3532.method_15357(f);
		if (this.field_6002.method_8320(new class_2338(i, j - 1, k)).method_11602(class_3481.field_15463)) {
			j--;
		}

		class_2680 lv = this.field_6002.method_8320(new class_2338(i, j, k));
		if (lv.method_11602(class_3481.field_15463)) {
			class_2768 lv2 = lv.method_11654(((class_2241)lv.method_11614()).method_9474());
			e = (double)j;
			if (lv2.method_11897()) {
				e = (double)(j + 1);
			}

			int[][] is = field_7664[lv2.method_11896()];
			double h = (double)(is[1][0] - is[0][0]);
			double l = (double)(is[1][2] - is[0][2]);
			double m = Math.sqrt(h * h + l * l);
			h /= m;
			l /= m;
			d += h * g;
			f += l * g;
			if (is[0][1] != 0 && class_3532.method_15357(d) - i == is[0][0] && class_3532.method_15357(f) - k == is[0][2]) {
				e += (double)is[0][1];
			} else if (is[1][1] != 0 && class_3532.method_15357(d) - i == is[1][0] && class_3532.method_15357(f) - k == is[1][2]) {
				e += (double)is[1][1];
			}

			return this.method_7508(d, e, f);
		} else {
			return null;
		}
	}

	@Nullable
	public class_243 method_7508(double d, double e, double f) {
		int i = class_3532.method_15357(d);
		int j = class_3532.method_15357(e);
		int k = class_3532.method_15357(f);
		if (this.field_6002.method_8320(new class_2338(i, j - 1, k)).method_11602(class_3481.field_15463)) {
			j--;
		}

		class_2680 lv = this.field_6002.method_8320(new class_2338(i, j, k));
		if (lv.method_11602(class_3481.field_15463)) {
			class_2768 lv2 = lv.method_11654(((class_2241)lv.method_11614()).method_9474());
			int[][] is = field_7664[lv2.method_11896()];
			double g = (double)i + 0.5 + (double)is[0][0] * 0.5;
			double h = (double)j + 0.0625 + (double)is[0][1] * 0.5;
			double l = (double)k + 0.5 + (double)is[0][2] * 0.5;
			double m = (double)i + 0.5 + (double)is[1][0] * 0.5;
			double n = (double)j + 0.0625 + (double)is[1][1] * 0.5;
			double o = (double)k + 0.5 + (double)is[1][2] * 0.5;
			double p = m - g;
			double q = (n - h) * 2.0;
			double r = o - l;
			double s;
			if (p == 0.0) {
				s = f - (double)k;
			} else if (r == 0.0) {
				s = d - (double)i;
			} else {
				double t = d - g;
				double u = f - l;
				s = (t * p + u * r) * 2.0;
			}

			d = g + p * s;
			e = h + q * s;
			f = l + r * s;
			if (q < 0.0) {
				e++;
			}

			if (q > 0.0) {
				e += 0.5;
			}

			return new class_243(d, e, f);
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_238 method_5830() {
		class_238 lv = this.method_5829();
		return this.method_7510() ? lv.method_1014((double)Math.abs(this.method_7514()) / 16.0) : lv;
	}

	@Override
	protected void method_5749(class_2487 arg) {
		if (arg.method_10577("CustomDisplayTile")) {
			this.method_7527(class_2512.method_10681(arg.method_10562("DisplayState")));
			this.method_7515(arg.method_10550("DisplayOffset"));
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		if (this.method_7510()) {
			arg.method_10556("CustomDisplayTile", true);
			arg.method_10566("DisplayState", class_2512.method_10686(this.method_7519()));
			arg.method_10569("DisplayOffset", this.method_7514());
		}
	}

	@Override
	public void method_5697(class_1297 arg) {
		if (!this.field_6002.field_9236) {
			if (!arg.field_5960 && !this.field_5960) {
				if (!this.method_5626(arg)) {
					double d = arg.field_5987 - this.field_5987;
					double e = arg.field_6035 - this.field_6035;
					double f = d * d + e * e;
					if (f >= 1.0E-4F) {
						f = (double)class_3532.method_15368(f);
						d /= f;
						e /= f;
						double g = 1.0 / f;
						if (g > 1.0) {
							g = 1.0;
						}

						d *= g;
						e *= g;
						d *= 0.1F;
						e *= 0.1F;
						d *= (double)(1.0F - this.field_5968);
						e *= (double)(1.0F - this.field_5968);
						d *= 0.5;
						e *= 0.5;
						if (arg instanceof class_1688) {
							double h = arg.field_5987 - this.field_5987;
							double i = arg.field_6035 - this.field_6035;
							class_243 lv = new class_243(h, 0.0, i).method_1029();
							class_243 lv2 = new class_243(
									(double)class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)),
									0.0,
									(double)class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0))
								)
								.method_1029();
							double j = Math.abs(lv.method_1026(lv2));
							if (j < 0.8F) {
								return;
							}

							class_243 lv3 = this.method_18798();
							class_243 lv4 = arg.method_18798();
							if (((class_1688)arg).method_7518() == class_1688.class_1689.field_7679 && this.method_7518() != class_1688.class_1689.field_7679) {
								this.method_18799(lv3.method_18805(0.2, 1.0, 0.2));
								this.method_5762(lv4.field_1352 - d, 0.0, lv4.field_1350 - e);
								arg.method_18799(lv4.method_18805(0.95, 1.0, 0.95));
							} else if (((class_1688)arg).method_7518() != class_1688.class_1689.field_7679 && this.method_7518() == class_1688.class_1689.field_7679) {
								arg.method_18799(lv4.method_18805(0.2, 1.0, 0.2));
								arg.method_5762(lv3.field_1352 + d, 0.0, lv3.field_1350 + e);
								this.method_18799(lv3.method_18805(0.95, 1.0, 0.95));
							} else {
								double k = (lv4.field_1352 + lv3.field_1352) / 2.0;
								double l = (lv4.field_1350 + lv3.field_1350) / 2.0;
								this.method_18799(lv3.method_18805(0.2, 1.0, 0.2));
								this.method_5762(k - d, 0.0, l - e);
								arg.method_18799(lv4.method_18805(0.2, 1.0, 0.2));
								arg.method_5762(k + d, 0.0, l + e);
							}
						} else {
							this.method_5762(-d, 0.0, -e);
							arg.method_5762(d / 4.0, 0.0, e / 4.0);
						}
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_7665 = d;
		this.field_7666 = e;
		this.field_7662 = f;
		this.field_7659 = (double)g;
		this.field_7657 = (double)h;
		this.field_7669 = i + 2;
		this.method_18800(this.field_7658, this.field_7655, this.field_7656);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.field_7658 = d;
		this.field_7655 = e;
		this.field_7656 = f;
		this.method_18800(this.field_7658, this.field_7655, this.field_7656);
	}

	public void method_7520(float f) {
		this.field_6011.method_12778(field_7667, f);
	}

	public float method_7521() {
		return this.field_6011.method_12789(field_7667);
	}

	public void method_7509(int i) {
		this.field_6011.method_12778(field_7663, i);
	}

	public int method_7507() {
		return this.field_6011.method_12789(field_7663);
	}

	public void method_7524(int i) {
		this.field_6011.method_12778(field_7668, i);
	}

	public int method_7522() {
		return this.field_6011.method_12789(field_7668);
	}

	public abstract class_1688.class_1689 method_7518();

	public class_2680 method_7519() {
		return !this.method_7510() ? this.method_7517() : class_2248.method_9531(this.method_5841().method_12789(field_7671));
	}

	public class_2680 method_7517() {
		return class_2246.field_10124.method_9564();
	}

	public int method_7514() {
		return !this.method_7510() ? this.method_7526() : this.method_5841().method_12789(field_7661);
	}

	public int method_7526() {
		return 6;
	}

	public void method_7527(class_2680 arg) {
		this.method_5841().method_12778(field_7671, class_2248.method_9507(arg));
		this.method_7511(true);
	}

	public void method_7515(int i) {
		this.method_5841().method_12778(field_7661, i);
		this.method_7511(true);
	}

	public boolean method_7510() {
		return this.method_5841().method_12789(field_7670);
	}

	public void method_7511(boolean bl) {
		this.method_5841().method_12778(field_7670, bl);
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}

	public static enum class_1689 {
		field_7674,
		field_7678,
		field_7679,
		field_7675,
		field_7680,
		field_7677,
		field_7681;
	}
}
