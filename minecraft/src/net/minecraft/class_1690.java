package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1690 extends class_1297 {
	private static final class_2940<Integer> field_7688 = class_2945.method_12791(class_1690.class, class_2943.field_13327);
	private static final class_2940<Integer> field_7707 = class_2945.method_12791(class_1690.class, class_2943.field_13327);
	private static final class_2940<Float> field_7705 = class_2945.method_12791(class_1690.class, class_2943.field_13320);
	private static final class_2940<Integer> field_7698 = class_2945.method_12791(class_1690.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_7687 = class_2945.method_12791(class_1690.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_7713 = class_2945.method_12791(class_1690.class, class_2943.field_13323);
	private static final class_2940<Integer> field_7691 = class_2945.method_12791(class_1690.class, class_2943.field_13327);
	private final float[] field_7704 = new float[2];
	private float field_7692;
	private float field_7706;
	private float field_7690;
	private int field_7708;
	private double field_7686;
	private double field_7700;
	private double field_7685;
	private double field_7699;
	private double field_7684;
	private boolean field_7710;
	private boolean field_7695;
	private boolean field_7709;
	private boolean field_7693;
	private double field_7697;
	private float field_7714;
	private class_1690.class_1691 field_7702;
	private class_1690.class_1691 field_7701;
	private double field_7696;
	private boolean field_7689;
	private boolean field_7703;
	private float field_7712;
	private float field_7694;
	private float field_7711;

	public class_1690(class_1299<? extends class_1690> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6033 = true;
	}

	public class_1690(class_1937 arg, double d, double e, double f) {
		this(class_1299.field_6121, arg);
		this.method_5814(d, e, f);
		this.method_18799(class_243.field_1353);
		this.field_6014 = d;
		this.field_6036 = e;
		this.field_5969 = f;
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7688, 0);
		this.field_6011.method_12784(field_7707, 1);
		this.field_6011.method_12784(field_7705, 0.0F);
		this.field_6011.method_12784(field_7698, class_1690.class_1692.field_7727.ordinal());
		this.field_6011.method_12784(field_7687, false);
		this.field_6011.method_12784(field_7713, false);
		this.field_6011.method_12784(field_7691, 0);
	}

	@Nullable
	@Override
	public class_238 method_5708(class_1297 arg) {
		return arg.method_5810() ? arg.method_5829() : null;
	}

	@Nullable
	@Override
	public class_238 method_5827() {
		return this.method_5829();
	}

	@Override
	public boolean method_5810() {
		return true;
	}

	@Override
	public double method_5621() {
		return -0.1;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else if (this.field_6002.field_9236 || this.field_5988) {
			return true;
		} else if (arg instanceof class_1284 && arg.method_5529() != null && this.method_5626(arg.method_5529())) {
			return false;
		} else {
			this.method_7540(-this.method_7543());
			this.method_7553(10);
			this.method_7542(this.method_7554() + f * 10.0F);
			this.method_5785();
			boolean bl = arg.method_5529() instanceof class_1657 && ((class_1657)arg.method_5529()).field_7503.field_7477;
			if (bl || this.method_7554() > 40.0F) {
				if (!bl && this.field_6002.method_8450().method_8355(class_1928.field_19393)) {
					this.method_5706(this.method_7557());
				}

				this.method_5650();
			}

			return true;
		}
	}

	@Override
	public void method_5700(boolean bl) {
		if (!this.field_6002.field_9236) {
			this.field_7689 = true;
			this.field_7703 = bl;
			if (this.method_7539() == 0) {
				this.method_7531(60);
			}
		}

		this.field_6002
			.method_8406(
				class_2398.field_11202,
				this.field_5987 + (double)this.field_5974.nextFloat(),
				this.field_6010 + 0.7,
				this.field_6035 + (double)this.field_5974.nextFloat(),
				0.0,
				0.0,
				0.0
			);
		if (this.field_5974.nextInt(20) == 0) {
			this.field_6002
				.method_8486(
					this.field_5987, this.field_6010, this.field_6035, this.method_5625(), this.method_5634(), 1.0F, 0.8F + 0.4F * this.field_5974.nextFloat(), false
				);
		}
	}

	@Override
	public void method_5697(class_1297 arg) {
		if (arg instanceof class_1690) {
			if (arg.method_5829().field_1322 < this.method_5829().field_1325) {
				super.method_5697(arg);
			}
		} else if (arg.method_5829().field_1322 <= this.method_5829().field_1322) {
			super.method_5697(arg);
		}
	}

	public class_1792 method_7557() {
		switch (this.method_7536()) {
			case field_7727:
			default:
				return class_1802.field_8533;
			case field_7728:
				return class_1802.field_8486;
			case field_7729:
				return class_1802.field_8442;
			case field_7730:
				return class_1802.field_8730;
			case field_7725:
				return class_1802.field_8094;
			case field_7723:
				return class_1802.field_8138;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5879() {
		this.method_7540(-this.method_7543());
		this.method_7553(10);
		this.method_7542(this.method_7554() * 11.0F);
	}

	@Override
	public boolean method_5863() {
		return !this.field_5988;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_7686 = d;
		this.field_7700 = e;
		this.field_7685 = f;
		this.field_7699 = (double)g;
		this.field_7684 = (double)h;
		this.field_7708 = 10;
	}

	@Override
	public class_2350 method_5755() {
		return this.method_5735().method_10170();
	}

	@Override
	public void method_5773() {
		this.field_7701 = this.field_7702;
		this.field_7702 = this.method_7552();
		if (this.field_7702 != class_1690.class_1691.field_7717 && this.field_7702 != class_1690.class_1691.field_7716) {
			this.field_7706 = 0.0F;
		} else {
			this.field_7706++;
		}

		if (!this.field_6002.field_9236 && this.field_7706 >= 60.0F) {
			this.method_5772();
		}

		if (this.method_7533() > 0) {
			this.method_7553(this.method_7533() - 1);
		}

		if (this.method_7554() > 0.0F) {
			this.method_7542(this.method_7554() - 1.0F);
		}

		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		super.method_5773();
		this.method_7555();
		if (this.method_5787()) {
			if (this.method_5685().isEmpty() || !(this.method_5685().get(0) instanceof class_1657)) {
				this.method_7538(false, false);
			}

			this.method_7534();
			if (this.field_6002.field_9236) {
				this.method_7549();
				this.field_6002.method_8522(new class_2836(this.method_7556(0), this.method_7556(1)));
			}

			this.method_5784(class_1313.field_6308, this.method_18798());
		} else {
			this.method_18799(class_243.field_1353);
		}

		this.method_7550();

		for (int i = 0; i <= 1; i++) {
			if (this.method_7556(i)) {
				if (!this.method_5701()
					&& (double)(this.field_7704[i] % (float) (Math.PI * 2)) <= (float) (Math.PI / 4)
					&& ((double)this.field_7704[i] + (float) (Math.PI / 8)) % (float) (Math.PI * 2) >= (float) (Math.PI / 4)) {
					class_3414 lv = this.method_7537();
					if (lv != null) {
						class_243 lv2 = this.method_5828(1.0F);
						double d = i == 1 ? -lv2.field_1350 : lv2.field_1350;
						double e = i == 1 ? lv2.field_1352 : -lv2.field_1352;
						this.field_6002
							.method_8465(null, this.field_5987 + d, this.field_6010, this.field_6035 + e, lv, this.method_5634(), 1.0F, 0.8F + 0.4F * this.field_5974.nextFloat());
					}
				}

				this.field_7704[i] = (float)((double)this.field_7704[i] + (float) (Math.PI / 8));
			} else {
				this.field_7704[i] = 0.0F;
			}
		}

		this.method_5852();
		List<class_1297> list = this.field_6002.method_8333(this, this.method_5829().method_1009(0.2F, -0.01F, 0.2F), class_1301.method_5911(this));
		if (!list.isEmpty()) {
			boolean bl = !this.field_6002.field_9236 && !(this.method_5642() instanceof class_1657);

			for (int j = 0; j < list.size(); j++) {
				class_1297 lv3 = (class_1297)list.get(j);
				if (!lv3.method_5626(this)) {
					if (bl
						&& this.method_5685().size() < 2
						&& !lv3.method_5765()
						&& lv3.method_17681() < this.method_17681()
						&& lv3 instanceof class_1309
						&& !(lv3 instanceof class_1480)
						&& !(lv3 instanceof class_1657)) {
						lv3.method_5804(this);
					} else {
						this.method_5697(lv3);
					}
				}
			}
		}
	}

	private void method_7550() {
		if (this.field_6002.field_9236) {
			int i = this.method_7539();
			if (i > 0) {
				this.field_7712 += 0.05F;
			} else {
				this.field_7712 -= 0.1F;
			}

			this.field_7712 = class_3532.method_15363(this.field_7712, 0.0F, 1.0F);
			this.field_7711 = this.field_7694;
			this.field_7694 = 10.0F * (float)Math.sin((double)(0.5F * (float)this.field_6002.method_8510())) * this.field_7712;
		} else {
			if (!this.field_7689) {
				this.method_7531(0);
			}

			int i = this.method_7539();
			if (i > 0) {
				this.method_7531(--i);
				int j = 60 - i - 1;
				if (j > 0 && i == 0) {
					this.method_7531(0);
					class_243 lv = this.method_18798();
					if (this.field_7703) {
						this.method_18799(lv.method_1031(0.0, -0.7, 0.0));
						this.method_5772();
					} else {
						this.method_18800(lv.field_1352, this.method_5703(class_1657.class) ? 2.7 : 0.6, lv.field_1350);
					}
				}

				this.field_7689 = false;
			}
		}
	}

	@Nullable
	protected class_3414 method_7537() {
		switch (this.method_7552()) {
			case field_7718:
			case field_7717:
			case field_7716:
				return class_3417.field_15171;
			case field_7719:
				return class_3417.field_14886;
			case field_7720:
			default:
				return null;
		}
	}

	private void method_7555() {
		if (this.field_7708 > 0 && !this.method_5787()) {
			double d = this.field_5987 + (this.field_7686 - this.field_5987) / (double)this.field_7708;
			double e = this.field_6010 + (this.field_7700 - this.field_6010) / (double)this.field_7708;
			double f = this.field_6035 + (this.field_7685 - this.field_6035) / (double)this.field_7708;
			double g = class_3532.method_15338(this.field_7699 - (double)this.field_6031);
			this.field_6031 = (float)((double)this.field_6031 + g / (double)this.field_7708);
			this.field_5965 = (float)((double)this.field_5965 + (this.field_7684 - (double)this.field_5965) / (double)this.field_7708);
			this.field_7708--;
			this.method_5814(d, e, f);
			this.method_5710(this.field_6031, this.field_5965);
		}
	}

	public void method_7538(boolean bl, boolean bl2) {
		this.field_6011.method_12778(field_7687, bl);
		this.field_6011.method_12778(field_7713, bl2);
	}

	@Environment(EnvType.CLIENT)
	public float method_7551(int i, float f) {
		return this.method_7556(i) ? (float)class_3532.method_15390((double)this.field_7704[i] - (float) (Math.PI / 8), (double)this.field_7704[i], (double)f) : 0.0F;
	}

	private class_1690.class_1691 method_7552() {
		class_1690.class_1691 lv = this.method_7532();
		if (lv != null) {
			this.field_7697 = this.method_5829().field_1325;
			return lv;
		} else if (this.method_7545()) {
			return class_1690.class_1691.field_7718;
		} else {
			float f = this.method_7548();
			if (f > 0.0F) {
				this.field_7714 = f;
				return class_1690.class_1691.field_7719;
			} else {
				return class_1690.class_1691.field_7720;
			}
		}
	}

	public float method_7544() {
		class_238 lv = this.method_5829();
		int i = class_3532.method_15357(lv.field_1323);
		int j = class_3532.method_15384(lv.field_1320);
		int k = class_3532.method_15357(lv.field_1325);
		int l = class_3532.method_15384(lv.field_1325 - this.field_7696);
		int m = class_3532.method_15357(lv.field_1321);
		int n = class_3532.method_15384(lv.field_1324);

		try (class_2338.class_2340 lv2 = class_2338.class_2340.method_10109()) {
			label136:
			for (int o = k; o < l; o++) {
				float f = 0.0F;
				int p = i;

				while (true) {
					if (p < j) {
						for (int q = m; q < n; q++) {
							lv2.method_10113(p, o, q);
							class_3610 lv3 = this.field_6002.method_8316(lv2);
							if (lv3.method_15767(class_3486.field_15517)) {
								f = Math.max(f, lv3.method_15763(this.field_6002, lv2));
							}

							if (f >= 1.0F) {
								continue label136;
							}
						}

						p++;
					} else {
						if (f < 1.0F) {
							return (float)lv2.method_10264() + f;
						}
						break;
					}
				}
			}

			return (float)(l + 1);
		}
	}

	public float method_7548() {
		class_238 lv = this.method_5829();
		class_238 lv2 = new class_238(lv.field_1323, lv.field_1322 - 0.001, lv.field_1321, lv.field_1320, lv.field_1322, lv.field_1324);
		int i = class_3532.method_15357(lv2.field_1323) - 1;
		int j = class_3532.method_15384(lv2.field_1320) + 1;
		int k = class_3532.method_15357(lv2.field_1322) - 1;
		int l = class_3532.method_15384(lv2.field_1325) + 1;
		int m = class_3532.method_15357(lv2.field_1321) - 1;
		int n = class_3532.method_15384(lv2.field_1324) + 1;
		class_265 lv3 = class_259.method_1078(lv2);
		float f = 0.0F;
		int o = 0;

		try (class_2338.class_2340 lv4 = class_2338.class_2340.method_10109()) {
			for (int p = i; p < j; p++) {
				for (int q = m; q < n; q++) {
					int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
					if (r != 2) {
						for (int s = k; s < l; s++) {
							if (r <= 0 || s != k && s != l - 1) {
								lv4.method_10113(p, s, q);
								class_2680 lv5 = this.field_6002.method_8320(lv4);
								if (!(lv5.method_11614() instanceof class_2553)
									&& class_259.method_1074(lv5.method_11628(this.field_6002, lv4).method_1096((double)p, (double)s, (double)q), lv3, class_247.field_16896)) {
									f += lv5.method_11614().method_9499();
									o++;
								}
							}
						}
					}
				}
			}
		}

		return f / (float)o;
	}

	private boolean method_7545() {
		class_238 lv = this.method_5829();
		int i = class_3532.method_15357(lv.field_1323);
		int j = class_3532.method_15384(lv.field_1320);
		int k = class_3532.method_15357(lv.field_1322);
		int l = class_3532.method_15384(lv.field_1322 + 0.001);
		int m = class_3532.method_15357(lv.field_1321);
		int n = class_3532.method_15384(lv.field_1324);
		boolean bl = false;
		this.field_7697 = Double.MIN_VALUE;

		try (class_2338.class_2340 lv2 = class_2338.class_2340.method_10109()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						lv2.method_10113(o, p, q);
						class_3610 lv3 = this.field_6002.method_8316(lv2);
						if (lv3.method_15767(class_3486.field_15517)) {
							float f = (float)p + lv3.method_15763(this.field_6002, lv2);
							this.field_7697 = Math.max((double)f, this.field_7697);
							bl |= lv.field_1322 < (double)f;
						}
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private class_1690.class_1691 method_7532() {
		class_238 lv = this.method_5829();
		double d = lv.field_1325 + 0.001;
		int i = class_3532.method_15357(lv.field_1323);
		int j = class_3532.method_15384(lv.field_1320);
		int k = class_3532.method_15357(lv.field_1325);
		int l = class_3532.method_15384(d);
		int m = class_3532.method_15357(lv.field_1321);
		int n = class_3532.method_15384(lv.field_1324);
		boolean bl = false;

		try (class_2338.class_2340 lv2 = class_2338.class_2340.method_10109()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						lv2.method_10113(o, p, q);
						class_3610 lv3 = this.field_6002.method_8316(lv2);
						if (lv3.method_15767(class_3486.field_15517) && d < (double)((float)lv2.method_10264() + lv3.method_15763(this.field_6002, lv2))) {
							if (!lv3.method_15771()) {
								return class_1690.class_1691.field_7716;
							}

							bl = true;
						}
					}
				}
			}
		}

		return bl ? class_1690.class_1691.field_7717 : null;
	}

	private void method_7534() {
		double d = -0.04F;
		double e = this.method_5740() ? 0.0 : -0.04F;
		double f = 0.0;
		this.field_7692 = 0.05F;
		if (this.field_7701 == class_1690.class_1691.field_7720
			&& this.field_7702 != class_1690.class_1691.field_7720
			&& this.field_7702 != class_1690.class_1691.field_7719) {
			this.field_7697 = this.method_5829().field_1322 + (double)this.method_17682();
			this.method_5814(this.field_5987, (double)(this.method_7544() - this.method_17682()) + 0.101, this.field_6035);
			this.method_18799(this.method_18798().method_18805(1.0, 0.0, 1.0));
			this.field_7696 = 0.0;
			this.field_7702 = class_1690.class_1691.field_7718;
		} else {
			if (this.field_7702 == class_1690.class_1691.field_7718) {
				f = (this.field_7697 - this.method_5829().field_1322) / (double)this.method_17682();
				this.field_7692 = 0.9F;
			} else if (this.field_7702 == class_1690.class_1691.field_7716) {
				e = -7.0E-4;
				this.field_7692 = 0.9F;
			} else if (this.field_7702 == class_1690.class_1691.field_7717) {
				f = 0.01F;
				this.field_7692 = 0.45F;
			} else if (this.field_7702 == class_1690.class_1691.field_7720) {
				this.field_7692 = 0.9F;
			} else if (this.field_7702 == class_1690.class_1691.field_7719) {
				this.field_7692 = this.field_7714;
				if (this.method_5642() instanceof class_1657) {
					this.field_7714 /= 2.0F;
				}
			}

			class_243 lv = this.method_18798();
			this.method_18800(lv.field_1352 * (double)this.field_7692, lv.field_1351 + e, lv.field_1350 * (double)this.field_7692);
			this.field_7690 = this.field_7690 * this.field_7692;
			if (f > 0.0) {
				class_243 lv2 = this.method_18798();
				this.method_18800(lv2.field_1352, (lv2.field_1351 + f * 0.06153846016296973) * 0.75, lv2.field_1350);
			}
		}
	}

	private void method_7549() {
		if (this.method_5782()) {
			float f = 0.0F;
			if (this.field_7710) {
				this.field_7690--;
			}

			if (this.field_7695) {
				this.field_7690++;
			}

			if (this.field_7695 != this.field_7710 && !this.field_7709 && !this.field_7693) {
				f += 0.005F;
			}

			this.field_6031 = this.field_6031 + this.field_7690;
			if (this.field_7709) {
				f += 0.04F;
			}

			if (this.field_7693) {
				f -= 0.005F;
			}

			this.method_18799(
				this.method_18798()
					.method_1031(
						(double)(class_3532.method_15374(-this.field_6031 * (float) (Math.PI / 180.0)) * f),
						0.0,
						(double)(class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)) * f)
					)
			);
			this.method_7538(this.field_7695 && !this.field_7710 || this.field_7709, this.field_7710 && !this.field_7695 || this.field_7709);
		}
	}

	@Override
	public void method_5865(class_1297 arg) {
		if (this.method_5626(arg)) {
			float f = 0.0F;
			float g = (float)((this.field_5988 ? 0.01F : this.method_5621()) + arg.method_5678());
			if (this.method_5685().size() > 1) {
				int i = this.method_5685().indexOf(arg);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (arg instanceof class_1429) {
					f = (float)((double)f + 0.2);
				}
			}

			class_243 lv = new class_243((double)f, 0.0, 0.0).method_1024(-this.field_6031 * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			arg.method_5814(this.field_5987 + lv.field_1352, this.field_6010 + (double)g, this.field_6035 + lv.field_1350);
			arg.field_6031 = arg.field_6031 + this.field_7690;
			arg.method_5847(arg.method_5791() + this.field_7690);
			this.method_7546(arg);
			if (arg instanceof class_1429 && this.method_5685().size() > 1) {
				int j = arg.method_5628() % 2 == 0 ? 90 : 270;
				arg.method_5636(((class_1429)arg).field_6283 + (float)j);
				arg.method_5847(arg.method_5791() + (float)j);
			}
		}
	}

	protected void method_7546(class_1297 arg) {
		arg.method_5636(this.field_6031);
		float f = class_3532.method_15393(arg.field_6031 - this.field_6031);
		float g = class_3532.method_15363(f, -105.0F, 105.0F);
		arg.field_5982 += g - f;
		arg.field_6031 += g - f;
		arg.method_5847(arg.field_6031);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5644(class_1297 arg) {
		this.method_7546(arg);
	}

	@Override
	protected void method_5652(class_2487 arg) {
		arg.method_10582("Type", this.method_7536().method_7559());
	}

	@Override
	protected void method_5749(class_2487 arg) {
		if (arg.method_10573("Type", 8)) {
			this.method_7541(class_1690.class_1692.method_7561(arg.method_10558("Type")));
		}
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		if (arg.method_5715()) {
			return false;
		} else {
			if (!this.field_6002.field_9236 && this.field_7706 < 60.0F) {
				arg.method_5804(this);
			}

			return true;
		}
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
		this.field_7696 = this.method_18798().field_1351;
		if (!this.method_5765()) {
			if (bl) {
				if (this.field_6017 > 3.0F) {
					if (this.field_7702 != class_1690.class_1691.field_7719) {
						this.field_6017 = 0.0F;
						return;
					}

					this.method_5747(this.field_6017, 1.0F);
					if (!this.field_6002.field_9236 && !this.field_5988) {
						this.method_5650();
						if (this.field_6002.method_8450().method_8355(class_1928.field_19393)) {
							for (int i = 0; i < 3; i++) {
								this.method_5706(this.method_7536().method_7560());
							}

							for (int i = 0; i < 2; i++) {
								this.method_5706(class_1802.field_8600);
							}
						}
					}
				}

				this.field_6017 = 0.0F;
			} else if (!this.field_6002.method_8316(new class_2338(this).method_10074()).method_15767(class_3486.field_15517) && d < 0.0) {
				this.field_6017 = (float)((double)this.field_6017 - d);
			}
		}
	}

	public boolean method_7556(int i) {
		return this.field_6011.method_12789(i == 0 ? field_7687 : field_7713) && this.method_5642() != null;
	}

	public void method_7542(float f) {
		this.field_6011.method_12778(field_7705, f);
	}

	public float method_7554() {
		return this.field_6011.method_12789(field_7705);
	}

	public void method_7553(int i) {
		this.field_6011.method_12778(field_7688, i);
	}

	public int method_7533() {
		return this.field_6011.method_12789(field_7688);
	}

	private void method_7531(int i) {
		this.field_6011.method_12778(field_7691, i);
	}

	private int method_7539() {
		return this.field_6011.method_12789(field_7691);
	}

	@Environment(EnvType.CLIENT)
	public float method_7547(float f) {
		return class_3532.method_16439(f, this.field_7711, this.field_7694);
	}

	public void method_7540(int i) {
		this.field_6011.method_12778(field_7707, i);
	}

	public int method_7543() {
		return this.field_6011.method_12789(field_7707);
	}

	public void method_7541(class_1690.class_1692 arg) {
		this.field_6011.method_12778(field_7698, arg.ordinal());
	}

	public class_1690.class_1692 method_7536() {
		return class_1690.class_1692.method_7558(this.field_6011.method_12789(field_7698));
	}

	@Override
	protected boolean method_5818(class_1297 arg) {
		return this.method_5685().size() < 2 && !this.method_5777(class_3486.field_15517);
	}

	@Nullable
	@Override
	public class_1297 method_5642() {
		List<class_1297> list = this.method_5685();
		return list.isEmpty() ? null : (class_1297)list.get(0);
	}

	@Environment(EnvType.CLIENT)
	public void method_7535(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.field_7710 = bl;
		this.field_7695 = bl2;
		this.field_7709 = bl3;
		this.field_7693 = bl4;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}

	public static enum class_1691 {
		field_7718,
		field_7717,
		field_7716,
		field_7719,
		field_7720;
	}

	public static enum class_1692 {
		field_7727(class_2246.field_10161, "oak"),
		field_7728(class_2246.field_9975, "spruce"),
		field_7729(class_2246.field_10148, "birch"),
		field_7730(class_2246.field_10334, "jungle"),
		field_7725(class_2246.field_10218, "acacia"),
		field_7723(class_2246.field_10075, "dark_oak");

		private final String field_7726;
		private final class_2248 field_7731;

		private class_1692(class_2248 arg, String string2) {
			this.field_7726 = string2;
			this.field_7731 = arg;
		}

		public String method_7559() {
			return this.field_7726;
		}

		public class_2248 method_7560() {
			return this.field_7731;
		}

		public String toString() {
			return this.field_7726;
		}

		public static class_1690.class_1692 method_7558(int i) {
			class_1690.class_1692[] lvs = values();
			if (i < 0 || i >= lvs.length) {
				i = 0;
			}

			return lvs[i];
		}

		public static class_1690.class_1692 method_7561(String string) {
			class_1690.class_1692[] lvs = values();

			for (int i = 0; i < lvs.length; i++) {
				if (lvs[i].method_7559().equals(string)) {
					return lvs[i];
				}
			}

			return lvs[0];
		}
	}
}
