package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_677 {
	@Environment(EnvType.CLIENT)
	public static class class_3997 implements class_707<class_2400> {
		private final class_4002 field_17810;

		public class_3997(class_4002 arg) {
			this.field_17810 = arg;
		}

		public class_703 method_18121(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_677.class_678 lv = new class_677.class_678(arg2, d, e, f);
			lv.method_18140(this.field_17810);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_678 extends class_4003 {
		private class_678(class_1937 arg, double d, double e, double f) {
			super(arg, d, e, f);
			this.field_3847 = 4;
		}

		@Override
		public class_3999 method_18122() {
			return class_3999.field_17829;
		}

		@Override
		public void method_3074(class_287 arg, class_4184 arg2, float f, float g, float h, float i, float j, float k) {
			this.method_3083(0.6F - ((float)this.field_3866 + f - 1.0F) * 0.25F * 0.5F);
			super.method_3074(arg, arg2, f, g, h, i, j, k);
		}

		@Override
		public float method_18132(float f) {
			return 7.1F * class_3532.method_15374(((float)this.field_3866 + f - 1.0F) * 0.25F * (float) Math.PI);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_679 implements class_707<class_2400> {
		private final class_4002 field_17811;

		public class_679(class_4002 arg) {
			this.field_17811 = arg;
		}

		public class_703 method_3025(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_677.class_680 lv = new class_677.class_680(arg2, d, e, f, g, h, i, class_310.method_1551().field_1713, this.field_17811);
			lv.method_3083(0.99F);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_680 extends class_708 {
		private boolean field_3804;
		private boolean field_3803;
		private final class_702 field_3798;
		private float field_3801;
		private float field_3800;
		private float field_3799;
		private boolean field_3802;

		private class_680(class_1937 arg, double d, double e, double f, double g, double h, double i, class_702 arg2, class_4002 arg3) {
			super(arg, d, e, f, arg3, -0.004F);
			this.field_3852 = g;
			this.field_3869 = h;
			this.field_3850 = i;
			this.field_3798 = arg2;
			this.field_17867 *= 0.75F;
			this.field_3847 = 48 + this.field_3840.nextInt(12);
			this.method_18142(arg3);
		}

		public void method_3027(boolean bl) {
			this.field_3804 = bl;
		}

		public void method_3026(boolean bl) {
			this.field_3803 = bl;
		}

		@Override
		public void method_3074(class_287 arg, class_4184 arg2, float f, float g, float h, float i, float j, float k) {
			if (!this.field_3803 || this.field_3866 < this.field_3847 / 3 || (this.field_3866 + this.field_3847) / 3 % 2 == 0) {
				super.method_3074(arg, arg2, f, g, h, i, j, k);
			}
		}

		@Override
		public void method_3070() {
			super.method_3070();
			if (this.field_3804 && this.field_3866 < this.field_3847 / 2 && (this.field_3866 + this.field_3847) % 2 == 0) {
				class_677.class_680 lv = new class_677.class_680(
					this.field_3851, this.field_3874, this.field_3854, this.field_3871, 0.0, 0.0, 0.0, this.field_3798, this.field_17866
				);
				lv.method_3083(0.99F);
				lv.method_3084(this.field_3861, this.field_3842, this.field_3859);
				lv.field_3866 = lv.field_3847 / 2;
				if (this.field_3802) {
					lv.field_3802 = true;
					lv.field_3801 = this.field_3801;
					lv.field_3800 = this.field_3800;
					lv.field_3799 = this.field_3799;
				}

				lv.field_3803 = this.field_3803;
				this.field_3798.method_3058(lv);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_681 extends class_3998 {
		private int field_3808;
		private final class_702 field_3805;
		private class_2499 field_3806;
		private boolean field_3807;

		public class_681(class_1937 arg, double d, double e, double f, double g, double h, double i, class_702 arg2, @Nullable class_2487 arg3) {
			super(arg, d, e, f);
			this.field_3852 = g;
			this.field_3869 = h;
			this.field_3850 = i;
			this.field_3805 = arg2;
			this.field_3847 = 8;
			if (arg3 != null) {
				this.field_3806 = arg3.method_10554("Explosions", 10);
				if (this.field_3806.isEmpty()) {
					this.field_3806 = null;
				} else {
					this.field_3847 = this.field_3806.size() * 2 - 1;

					for (int j = 0; j < this.field_3806.size(); j++) {
						class_2487 lv = this.field_3806.method_10602(j);
						if (lv.method_10577("Flicker")) {
							this.field_3807 = true;
							this.field_3847 += 15;
							break;
						}
					}
				}
			}
		}

		@Override
		public void method_3070() {
			if (this.field_3808 == 0 && this.field_3806 != null) {
				boolean bl = this.method_3029();
				boolean bl2 = false;
				if (this.field_3806.size() >= 3) {
					bl2 = true;
				} else {
					for (int i = 0; i < this.field_3806.size(); i++) {
						class_2487 lv = this.field_3806.method_10602(i);
						if (class_1781.class_1782.method_7813(lv.method_10571("Type")) == class_1781.class_1782.field_7977) {
							bl2 = true;
							break;
						}
					}
				}

				class_3414 lv2;
				if (bl2) {
					lv2 = bl ? class_3417.field_14612 : class_3417.field_15188;
				} else {
					lv2 = bl ? class_3417.field_15090 : class_3417.field_14917;
				}

				this.field_3851
					.method_8486(this.field_3874, this.field_3854, this.field_3871, lv2, class_3419.field_15256, 20.0F, 0.95F + this.field_3840.nextFloat() * 0.1F, true);
			}

			if (this.field_3808 % 2 == 0 && this.field_3806 != null && this.field_3808 / 2 < this.field_3806.size()) {
				int j = this.field_3808 / 2;
				class_2487 lv3 = this.field_3806.method_10602(j);
				class_1781.class_1782 lv4 = class_1781.class_1782.method_7813(lv3.method_10571("Type"));
				boolean bl3 = lv3.method_10577("Trail");
				boolean bl4 = lv3.method_10577("Flicker");
				int[] is = lv3.method_10561("Colors");
				int[] js = lv3.method_10561("FadeColors");
				if (is.length == 0) {
					is = new int[]{class_1767.field_7963.method_7790()};
				}

				switch (lv4) {
					case field_7976:
					default:
						this.method_3031(0.25, 2, is, js, bl3, bl4);
						break;
					case field_7977:
						this.method_3031(0.5, 4, is, js, bl3, bl4);
						break;
					case field_7973:
						this.method_3028(
							0.5,
							new double[][]{
								{0.0, 1.0},
								{0.3455, 0.309},
								{0.9511, 0.309},
								{0.3795918367346939, -0.12653061224489795},
								{0.6122448979591837, -0.8040816326530612},
								{0.0, -0.35918367346938773}
							},
							is,
							js,
							bl3,
							bl4,
							false
						);
						break;
					case field_7974:
						this.method_3028(
							0.5,
							new double[][]{
								{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}
							},
							is,
							js,
							bl3,
							bl4,
							true
						);
						break;
					case field_7970:
						this.method_3032(is, js, bl3, bl4);
				}

				int k = is[0];
				float f = (float)((k & 0xFF0000) >> 16) / 255.0F;
				float g = (float)((k & 0xFF00) >> 8) / 255.0F;
				float h = (float)((k & 0xFF) >> 0) / 255.0F;
				class_703 lv5 = this.field_3805.method_3056(class_2398.field_17909, this.field_3874, this.field_3854, this.field_3871, 0.0, 0.0, 0.0);
				lv5.method_3084(f, g, h);
			}

			this.field_3808++;
			if (this.field_3808 > this.field_3847) {
				if (this.field_3807) {
					boolean blx = this.method_3029();
					class_3414 lv6 = blx ? class_3417.field_14882 : class_3417.field_14800;
					this.field_3851
						.method_8486(this.field_3874, this.field_3854, this.field_3871, lv6, class_3419.field_15256, 20.0F, 0.9F + this.field_3840.nextFloat() * 0.15F, true);
				}

				this.method_3085();
			}
		}

		private boolean method_3029() {
			class_310 lv = class_310.method_1551();
			return lv.field_1773.method_19418().method_19326().method_1028(this.field_3874, this.field_3854, this.field_3871) >= 256.0;
		}

		private void method_3030(double d, double e, double f, double g, double h, double i, int[] is, int[] js, boolean bl, boolean bl2) {
			class_677.class_680 lv = (class_677.class_680)this.field_3805.method_3056(class_2398.field_11248, d, e, f, g, h, i);
			lv.method_3027(bl);
			lv.method_3026(bl2);
			lv.method_3083(0.99F);
			int j = this.field_3840.nextInt(is.length);
			lv.method_3093(is[j]);
			if (js.length > 0) {
				lv.method_3092(js[this.field_3840.nextInt(js.length)]);
			}
		}

		private void method_3031(double d, int i, int[] is, int[] js, boolean bl, boolean bl2) {
			double e = this.field_3874;
			double f = this.field_3854;
			double g = this.field_3871;

			for (int j = -i; j <= i; j++) {
				for (int k = -i; k <= i; k++) {
					for (int l = -i; l <= i; l++) {
						double h = (double)k + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 0.5;
						double m = (double)j + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 0.5;
						double n = (double)l + (this.field_3840.nextDouble() - this.field_3840.nextDouble()) * 0.5;
						double o = (double)class_3532.method_15368(h * h + m * m + n * n) / d + this.field_3840.nextGaussian() * 0.05;
						this.method_3030(e, f, g, h / o, m / o, n / o, is, js, bl, bl2);
						if (j != -i && j != i && k != -i && k != i) {
							l += i * 2 - 1;
						}
					}
				}
			}
		}

		private void method_3028(double d, double[][] ds, int[] is, int[] js, boolean bl, boolean bl2, boolean bl3) {
			double e = ds[0][0];
			double f = ds[0][1];
			this.method_3030(this.field_3874, this.field_3854, this.field_3871, e * d, f * d, 0.0, is, js, bl, bl2);
			float g = this.field_3840.nextFloat() * (float) Math.PI;
			double h = bl3 ? 0.034 : 0.34;

			for (int i = 0; i < 3; i++) {
				double j = (double)g + (double)((float)i * (float) Math.PI) * h;
				double k = e;
				double l = f;

				for (int m = 1; m < ds.length; m++) {
					double n = ds[m][0];
					double o = ds[m][1];

					for (double p = 0.25; p <= 1.0; p += 0.25) {
						double q = class_3532.method_16436(p, k, n) * d;
						double r = class_3532.method_16436(p, l, o) * d;
						double s = q * Math.sin(j);
						q *= Math.cos(j);

						for (double t = -1.0; t <= 1.0; t += 2.0) {
							this.method_3030(this.field_3874, this.field_3854, this.field_3871, q * t, r, s * t, is, js, bl, bl2);
						}
					}

					k = n;
					l = o;
				}
			}
		}

		private void method_3032(int[] is, int[] js, boolean bl, boolean bl2) {
			double d = this.field_3840.nextGaussian() * 0.05;
			double e = this.field_3840.nextGaussian() * 0.05;

			for (int i = 0; i < 70; i++) {
				double f = this.field_3852 * 0.5 + this.field_3840.nextGaussian() * 0.15 + d;
				double g = this.field_3850 * 0.5 + this.field_3840.nextGaussian() * 0.15 + e;
				double h = this.field_3869 * 0.5 + this.field_3840.nextDouble() * 0.5;
				this.method_3030(this.field_3874, this.field_3854, this.field_3871, f, h, g, is, js, bl, bl2);
			}
		}
	}
}
