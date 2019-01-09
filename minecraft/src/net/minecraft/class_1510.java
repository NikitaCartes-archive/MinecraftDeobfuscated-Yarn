package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1510 extends class_1308 implements class_1509, class_1569 {
	private static final Logger field_7021 = LogManager.getLogger();
	public static final class_2940<Integer> field_7013 = class_2945.method_12791(class_1510.class, class_2943.field_13327);
	public final double[][] field_7026 = new double[64][3];
	public int field_7010 = -1;
	public final class_1508[] field_7032;
	public final class_1508 field_7017;
	public final class_1508 field_7011;
	public final class_1508 field_7023;
	public final class_1508 field_7020;
	public final class_1508 field_7009;
	public final class_1508 field_7022;
	public final class_1508 field_7015;
	public final class_1508 field_7014;
	public float field_7019;
	public float field_7030;
	public boolean field_7027;
	public int field_7031;
	public class_1511 field_7024;
	private final class_2881 field_7016;
	private final class_1526 field_7028;
	private int field_7018 = 100;
	private int field_7029;
	private final class_9[] field_7012 = new class_9[24];
	private final int[] field_7025 = new int[24];
	private final class_5 field_7008 = new class_5();

	public class_1510(class_1937 arg) {
		super(class_1299.field_6116, arg);
		this.field_7017 = new class_1508(this, "head", 6.0F, 6.0F);
		this.field_7011 = new class_1508(this, "neck", 6.0F, 6.0F);
		this.field_7023 = new class_1508(this, "body", 8.0F, 8.0F);
		this.field_7020 = new class_1508(this, "tail", 4.0F, 4.0F);
		this.field_7009 = new class_1508(this, "tail", 4.0F, 4.0F);
		this.field_7022 = new class_1508(this, "tail", 4.0F, 4.0F);
		this.field_7015 = new class_1508(this, "wing", 4.0F, 4.0F);
		this.field_7014 = new class_1508(this, "wing", 4.0F, 4.0F);
		this.field_7032 = new class_1508[]{
			this.field_7017, this.field_7011, this.field_7023, this.field_7020, this.field_7009, this.field_7022, this.field_7015, this.field_7014
		};
		this.method_6033(this.method_6063());
		this.method_5835(16.0F, 8.0F);
		this.field_5960 = true;
		this.field_5977 = true;
		this.field_5985 = true;
		if (!arg.field_9236 && arg.field_9247 instanceof class_2880) {
			this.field_7016 = ((class_2880)arg.field_9247).method_12513();
		} else {
			this.field_7016 = null;
		}

		this.field_7028 = new class_1526(this);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(200.0);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.method_5841().method_12784(field_7013, class_1527.field_7075.method_6871());
	}

	public double[] method_6817(int i, float f) {
		if (this.method_6032() <= 0.0F) {
			f = 0.0F;
		}

		f = 1.0F - f;
		int j = this.field_7010 - i & 63;
		int k = this.field_7010 - i - 1 & 63;
		double[] ds = new double[3];
		double d = this.field_7026[j][0];
		double e = class_3532.method_15338(this.field_7026[k][0] - d);
		ds[0] = d + e * (double)f;
		d = this.field_7026[j][1];
		e = this.field_7026[k][1] - d;
		ds[1] = d + e * (double)f;
		ds[2] = class_3532.method_16436((double)f, this.field_7026[j][2], this.field_7026[k][2]);
		return ds;
	}

	@Override
	public void method_6007() {
		if (this.field_6002.field_9236) {
			this.method_6033(this.method_6032());
			if (!this.method_5701()) {
				float f = class_3532.method_15362(this.field_7030 * (float) (Math.PI * 2));
				float g = class_3532.method_15362(this.field_7019 * (float) (Math.PI * 2));
				if (g <= -0.3F && f >= -0.3F) {
					this.field_6002
						.method_8486(
							this.field_5987, this.field_6010, this.field_6035, class_3417.field_14550, this.method_5634(), 5.0F, 0.8F + this.field_5974.nextFloat() * 0.3F, false
						);
				}

				if (!this.field_7028.method_6864().method_6848() && --this.field_7018 < 0) {
					this.field_6002
						.method_8486(
							this.field_5987, this.field_6010, this.field_6035, class_3417.field_14671, this.method_5634(), 2.5F, 0.8F + this.field_5974.nextFloat() * 0.3F, false
						);
					this.field_7018 = 200 + this.field_5974.nextInt(200);
				}
			}
		}

		this.field_7019 = this.field_7030;
		if (this.method_6032() <= 0.0F) {
			float fx = (this.field_5974.nextFloat() - 0.5F) * 8.0F;
			float gx = (this.field_5974.nextFloat() - 0.5F) * 4.0F;
			float h = (this.field_5974.nextFloat() - 0.5F) * 8.0F;
			this.field_6002
				.method_8406(class_2398.field_11236, this.field_5987 + (double)fx, this.field_6010 + 2.0 + (double)gx, this.field_6035 + (double)h, 0.0, 0.0, 0.0);
		} else {
			this.method_6830();
			float fx = 0.2F / (class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006) * 10.0F + 1.0F);
			fx *= (float)Math.pow(2.0, this.field_5984);
			if (this.field_7028.method_6864().method_6848()) {
				this.field_7030 += 0.1F;
			} else if (this.field_7027) {
				this.field_7030 += fx * 0.5F;
			} else {
				this.field_7030 += fx;
			}

			this.field_6031 = class_3532.method_15393(this.field_6031);
			if (this.method_5987()) {
				this.field_7030 = 0.5F;
			} else {
				if (this.field_7010 < 0) {
					for (int i = 0; i < this.field_7026.length; i++) {
						this.field_7026[i][0] = (double)this.field_6031;
						this.field_7026[i][1] = this.field_6010;
					}
				}

				if (++this.field_7010 == this.field_7026.length) {
					this.field_7010 = 0;
				}

				this.field_7026[this.field_7010][0] = (double)this.field_6031;
				this.field_7026[this.field_7010][1] = this.field_6010;
				if (this.field_6002.field_9236) {
					if (this.field_6210 > 0) {
						double d = this.field_5987 + (this.field_6224 - this.field_5987) / (double)this.field_6210;
						double e = this.field_6010 + (this.field_6245 - this.field_6010) / (double)this.field_6210;
						double j = this.field_6035 + (this.field_6263 - this.field_6035) / (double)this.field_6210;
						double k = class_3532.method_15338(this.field_6284 - (double)this.field_6031);
						this.field_6031 = (float)((double)this.field_6031 + k / (double)this.field_6210);
						this.field_5965 = (float)((double)this.field_5965 + (this.field_6221 - (double)this.field_5965) / (double)this.field_6210);
						this.field_6210--;
						this.method_5814(d, e, j);
						this.method_5710(this.field_6031, this.field_5965);
					}

					this.field_7028.method_6864().method_6853();
				} else {
					class_1521 lv = this.field_7028.method_6864();
					lv.method_6855();
					if (this.field_7028.method_6864() != lv) {
						lv = this.field_7028.method_6864();
						lv.method_6855();
					}

					class_243 lv2 = lv.method_6851();
					if (lv2 != null) {
						double e = lv2.field_1352 - this.field_5987;
						double j = lv2.field_1351 - this.field_6010;
						double k = lv2.field_1350 - this.field_6035;
						double l = e * e + j * j + k * k;
						float m = lv.method_6846();
						j = class_3532.method_15350(j / (double)class_3532.method_15368(e * e + k * k), (double)(-m), (double)m);
						this.field_5984 += j * 0.1F;
						this.field_6031 = class_3532.method_15393(this.field_6031);
						double n = class_3532.method_15350(
							class_3532.method_15338(180.0 - class_3532.method_15349(e, k) * 180.0F / (float)Math.PI - (double)this.field_6031), -50.0, 50.0
						);
						class_243 lv3 = new class_243(lv2.field_1352 - this.field_5987, lv2.field_1351 - this.field_6010, lv2.field_1350 - this.field_6035).method_1029();
						class_243 lv4 = new class_243(
								(double)class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0)),
								this.field_5984,
								(double)(-class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0)))
							)
							.method_1029();
						float o = Math.max(((float)lv4.method_1026(lv3) + 0.5F) / 1.5F, 0.0F);
						this.field_6267 *= 0.8F;
						this.field_6267 = (float)((double)this.field_6267 + n * (double)lv.method_6847());
						this.field_6031 = this.field_6031 + this.field_6267 * 0.1F;
						float p = (float)(2.0 / (l + 1.0));
						float q = 0.06F;
						this.method_5724(0.0F, 0.0F, -1.0F, 0.06F * (o * p + (1.0F - p)));
						if (this.field_7027) {
							this.method_5784(class_1313.field_6308, this.field_5967 * 0.8F, this.field_5984 * 0.8F, this.field_6006 * 0.8F);
						} else {
							this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
						}

						class_243 lv5 = new class_243(this.field_5967, this.field_5984, this.field_6006).method_1029();
						float r = ((float)lv5.method_1026(lv4) + 1.0F) / 2.0F;
						r = 0.8F + 0.15F * r;
						this.field_5967 *= (double)r;
						this.field_6006 *= (double)r;
						this.field_5984 *= 0.91F;
					}
				}

				this.field_6283 = this.field_6031;
				this.field_7017.field_5998 = 1.0F;
				this.field_7017.field_6019 = 1.0F;
				this.field_7011.field_5998 = 3.0F;
				this.field_7011.field_6019 = 3.0F;
				this.field_7020.field_5998 = 2.0F;
				this.field_7020.field_6019 = 2.0F;
				this.field_7009.field_5998 = 2.0F;
				this.field_7009.field_6019 = 2.0F;
				this.field_7022.field_5998 = 2.0F;
				this.field_7022.field_6019 = 2.0F;
				this.field_7023.field_6019 = 3.0F;
				this.field_7023.field_5998 = 5.0F;
				this.field_7015.field_6019 = 2.0F;
				this.field_7015.field_5998 = 4.0F;
				this.field_7014.field_6019 = 3.0F;
				this.field_7014.field_5998 = 4.0F;
				class_243[] lvs = new class_243[this.field_7032.length];

				for (int s = 0; s < this.field_7032.length; s++) {
					lvs[s] = new class_243(this.field_7032[s].field_5987, this.field_7032[s].field_6010, this.field_7032[s].field_6035);
				}

				float h = (float)(this.method_6817(5, 1.0F)[1] - this.method_6817(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
				float t = class_3532.method_15362(h);
				float u = class_3532.method_15374(h);
				float v = this.field_6031 * (float) (Math.PI / 180.0);
				float w = class_3532.method_15374(v);
				float x = class_3532.method_15362(v);
				this.field_7023.method_5773();
				this.field_7023.method_5808(this.field_5987 + (double)(w * 0.5F), this.field_6010, this.field_6035 - (double)(x * 0.5F), 0.0F, 0.0F);
				this.field_7015.method_5773();
				this.field_7015.method_5808(this.field_5987 + (double)(x * 4.5F), this.field_6010 + 2.0, this.field_6035 + (double)(w * 4.5F), 0.0F, 0.0F);
				this.field_7014.method_5773();
				this.field_7014.method_5808(this.field_5987 - (double)(x * 4.5F), this.field_6010 + 2.0, this.field_6035 - (double)(w * 4.5F), 0.0F, 0.0F);
				if (!this.field_6002.field_9236 && this.field_6235 == 0) {
					this.method_6825(this.field_6002.method_8335(this, this.field_7015.method_5829().method_1009(4.0, 2.0, 4.0).method_989(0.0, -2.0, 0.0)));
					this.method_6825(this.field_6002.method_8335(this, this.field_7014.method_5829().method_1009(4.0, 2.0, 4.0).method_989(0.0, -2.0, 0.0)));
					this.method_6827(this.field_6002.method_8335(this, this.field_7017.method_5829().method_1014(1.0)));
					this.method_6827(this.field_6002.method_8335(this, this.field_7011.method_5829().method_1014(1.0)));
				}

				double[] ds = this.method_6817(5, 1.0F);
				float y = class_3532.method_15374(this.field_6031 * (float) (Math.PI / 180.0) - this.field_6267 * 0.01F);
				float z = class_3532.method_15362(this.field_6031 * (float) (Math.PI / 180.0) - this.field_6267 * 0.01F);
				this.field_7017.method_5773();
				this.field_7011.method_5773();
				float m = this.method_6820(1.0F);
				this.field_7017
					.method_5808(
						this.field_5987 + (double)(y * 6.5F * t), this.field_6010 + (double)m + (double)(u * 6.5F), this.field_6035 - (double)(z * 6.5F * t), 0.0F, 0.0F
					);
				this.field_7011
					.method_5808(
						this.field_5987 + (double)(y * 5.5F * t), this.field_6010 + (double)m + (double)(u * 5.5F), this.field_6035 - (double)(z * 5.5F * t), 0.0F, 0.0F
					);

				for (int aa = 0; aa < 3; aa++) {
					class_1508 lv6 = null;
					if (aa == 0) {
						lv6 = this.field_7020;
					}

					if (aa == 1) {
						lv6 = this.field_7009;
					}

					if (aa == 2) {
						lv6 = this.field_7022;
					}

					double[] es = this.method_6817(12 + aa * 2, 1.0F);
					float ab = this.field_6031 * (float) (Math.PI / 180.0) + this.method_6832(es[0] - ds[0]) * (float) (Math.PI / 180.0);
					float ac = class_3532.method_15374(ab);
					float ad = class_3532.method_15362(ab);
					float ae = 1.5F;
					float o = (float)(aa + 1) * 2.0F;
					lv6.method_5773();
					lv6.method_5808(
						this.field_5987 - (double)((w * 1.5F + ac * o) * t),
						this.field_6010 + (es[1] - ds[1]) - (double)((o + 1.5F) * u) + 1.5,
						this.field_6035 + (double)((x * 1.5F + ad * o) * t),
						0.0F,
						0.0F
					);
				}

				if (!this.field_6002.field_9236) {
					this.field_7027 = this.method_6821(this.field_7017.method_5829())
						| this.method_6821(this.field_7011.method_5829())
						| this.method_6821(this.field_7023.method_5829());
					if (this.field_7016 != null) {
						this.field_7016.method_12532(this);
					}
				}

				for (int aa = 0; aa < this.field_7032.length; aa++) {
					this.field_7032[aa].field_6014 = lvs[aa].field_1352;
					this.field_7032[aa].field_6036 = lvs[aa].field_1351;
					this.field_7032[aa].field_5969 = lvs[aa].field_1350;
				}
			}
		}
	}

	private float method_6820(float f) {
		double d;
		if (this.field_7028.method_6864().method_6848()) {
			d = -1.0;
		} else {
			double[] ds = this.method_6817(5, 1.0F);
			double[] es = this.method_6817(0, 1.0F);
			d = ds[1] - es[1];
		}

		return (float)d;
	}

	private void method_6830() {
		if (this.field_7024 != null) {
			if (this.field_7024.field_5988) {
				this.field_7024 = null;
			} else if (this.field_6012 % 10 == 0 && this.method_6032() < this.method_6063()) {
				this.method_6033(this.method_6032() + 1.0F);
			}
		}

		if (this.field_5974.nextInt(10) == 0) {
			List<class_1511> list = this.field_6002.method_8403(class_1511.class, this.method_5829().method_1014(32.0));
			class_1511 lv = null;
			double d = Double.MAX_VALUE;

			for (class_1511 lv2 : list) {
				double e = lv2.method_5858(this);
				if (e < d) {
					d = e;
					lv = lv2;
				}
			}

			this.field_7024 = lv;
		}
	}

	private void method_6825(List<class_1297> list) {
		double d = (this.field_7023.method_5829().field_1323 + this.field_7023.method_5829().field_1320) / 2.0;
		double e = (this.field_7023.method_5829().field_1321 + this.field_7023.method_5829().field_1324) / 2.0;

		for (class_1297 lv : list) {
			if (lv instanceof class_1309) {
				double f = lv.field_5987 - d;
				double g = lv.field_6035 - e;
				double h = f * f + g * g;
				lv.method_5762(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.field_7028.method_6864().method_6848() && ((class_1309)lv).method_6117() < lv.field_6012 - 2) {
					lv.method_5643(class_1282.method_5511(this), 5.0F);
					this.method_5723(this, lv);
				}
			}
		}
	}

	private void method_6827(List<class_1297> list) {
		for (int i = 0; i < list.size(); i++) {
			class_1297 lv = (class_1297)list.get(i);
			if (lv instanceof class_1309) {
				lv.method_5643(class_1282.method_5511(this), 10.0F);
				this.method_5723(this, lv);
			}
		}
	}

	private float method_6832(double d) {
		return (float)class_3532.method_15338(d);
	}

	private boolean method_6821(class_238 arg) {
		int i = class_3532.method_15357(arg.field_1323);
		int j = class_3532.method_15357(arg.field_1322);
		int k = class_3532.method_15357(arg.field_1321);
		int l = class_3532.method_15357(arg.field_1320);
		int m = class_3532.method_15357(arg.field_1325);
		int n = class_3532.method_15357(arg.field_1324);
		boolean bl = false;
		boolean bl2 = false;

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				for (int q = k; q <= n; q++) {
					class_2338 lv = new class_2338(o, p, q);
					class_2680 lv2 = this.field_6002.method_8320(lv);
					class_2248 lv3 = lv2.method_11614();
					if (!lv2.method_11588() && lv2.method_11620() != class_3614.field_15943) {
						if (!this.field_6002.method_8450().method_8355("mobGriefing")) {
							bl = true;
						} else if (lv3 == class_2246.field_10499
							|| lv3 == class_2246.field_10540
							|| lv3 == class_2246.field_10471
							|| lv3 == class_2246.field_9987
							|| lv3 == class_2246.field_10027
							|| lv3 == class_2246.field_10398) {
							bl = true;
						} else if (lv3 != class_2246.field_10525
							&& lv3 != class_2246.field_10263
							&& lv3 != class_2246.field_10395
							&& lv3 != class_2246.field_10576
							&& lv3 != class_2246.field_10613) {
							bl2 = this.field_6002.method_8650(lv) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			double d = class_3532.method_16436((double)this.field_5974.nextFloat(), arg.field_1323, arg.field_1320);
			double e = class_3532.method_16436((double)this.field_5974.nextFloat(), arg.field_1322, arg.field_1325);
			double f = class_3532.method_16436((double)this.field_5974.nextFloat(), arg.field_1321, arg.field_1324);
			this.field_6002.method_8406(class_2398.field_11236, d, e, f, 0.0, 0.0, 0.0);
		}

		return bl;
	}

	@Override
	public boolean method_6816(class_1508 arg, class_1282 arg2, float f) {
		f = this.field_7028.method_6864().method_6852(arg, arg2, f);
		if (arg != this.field_7017) {
			f = f / 4.0F + Math.min(f, 1.0F);
		}

		if (f < 0.01F) {
			return false;
		} else {
			if (arg2.method_5529() instanceof class_1657 || arg2.method_5535()) {
				float g = this.method_6032();
				this.method_6819(arg2, f);
				if (this.method_6032() <= 0.0F && !this.field_7028.method_6864().method_6848()) {
					this.method_6033(1.0F);
					this.field_7028.method_6863(class_1527.field_7068);
				}

				if (this.field_7028.method_6864().method_6848()) {
					this.field_7029 = (int)((float)this.field_7029 + (g - this.method_6032()));
					if ((float)this.field_7029 > 0.25F * this.method_6063()) {
						this.field_7029 = 0;
						this.field_7028.method_6863(class_1527.field_7077);
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (arg instanceof class_1285 && ((class_1285)arg).method_5549()) {
			this.method_6816(this.field_7023, arg, f);
		}

		return false;
	}

	protected boolean method_6819(class_1282 arg, float f) {
		return super.method_5643(arg, f);
	}

	@Override
	public void method_5768() {
		this.method_5650();
		if (this.field_7016 != null) {
			this.field_7016.method_12532(this);
			this.field_7016.method_12528(this);
		}
	}

	@Override
	protected void method_6108() {
		if (this.field_7016 != null) {
			this.field_7016.method_12532(this);
		}

		this.field_7031++;
		if (this.field_7031 >= 180 && this.field_7031 <= 200) {
			float f = (this.field_5974.nextFloat() - 0.5F) * 8.0F;
			float g = (this.field_5974.nextFloat() - 0.5F) * 4.0F;
			float h = (this.field_5974.nextFloat() - 0.5F) * 8.0F;
			this.field_6002
				.method_8406(class_2398.field_11221, this.field_5987 + (double)f, this.field_6010 + 2.0 + (double)g, this.field_6035 + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.field_6002.method_8450().method_8355("doMobLoot");
		int i = 500;
		if (this.field_7016 != null && !this.field_7016.method_12536()) {
			i = 12000;
		}

		if (!this.field_6002.field_9236) {
			if (this.field_7031 > 150 && this.field_7031 % 5 == 0 && bl) {
				this.method_6824(class_3532.method_15375((float)i * 0.08F));
			}

			if (this.field_7031 == 1) {
				this.field_6002.method_8474(1028, new class_2338(this), 0);
			}
		}

		this.method_5784(class_1313.field_6308, 0.0, 0.1F, 0.0);
		this.field_6031 += 20.0F;
		this.field_6283 = this.field_6031;
		if (this.field_7031 == 200 && !this.field_6002.field_9236) {
			if (bl) {
				this.method_6824(class_3532.method_15375((float)i * 0.2F));
			}

			if (this.field_7016 != null) {
				this.field_7016.method_12528(this);
			}

			this.method_5650();
		}
	}

	private void method_6824(int i) {
		while (i > 0) {
			int j = class_1303.method_5918(i);
			i -= j;
			this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010, this.field_6035, j));
		}
	}

	public int method_6818() {
		if (this.field_7012[0] == null) {
			for (int i = 0; i < 24; i++) {
				int j = 5;
				int l;
				int m;
				if (i < 12) {
					l = (int)(60.0F * class_3532.method_15362(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
					m = (int)(60.0F * class_3532.method_15374(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
				} else if (i < 20) {
					int k = i - 12;
					l = (int)(40.0F * class_3532.method_15362(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					m = (int)(40.0F * class_3532.method_15374(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					j += 10;
				} else {
					int var7 = i - 20;
					l = (int)(20.0F * class_3532.method_15362(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
					m = (int)(20.0F * class_3532.method_15374(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
				}

				int n = Math.max(
					this.field_6002.method_8615() + 10, this.field_6002.method_8598(class_2902.class_2903.field_13203, new class_2338(l, 0, m)).method_10264() + j
				);
				this.field_7012[i] = new class_9(l, n, m);
			}

			this.field_7025[0] = 6146;
			this.field_7025[1] = 8197;
			this.field_7025[2] = 8202;
			this.field_7025[3] = 16404;
			this.field_7025[4] = 32808;
			this.field_7025[5] = 32848;
			this.field_7025[6] = 65696;
			this.field_7025[7] = 131392;
			this.field_7025[8] = 131712;
			this.field_7025[9] = 263424;
			this.field_7025[10] = 526848;
			this.field_7025[11] = 525313;
			this.field_7025[12] = 1581057;
			this.field_7025[13] = 3166214;
			this.field_7025[14] = 2138120;
			this.field_7025[15] = 6373424;
			this.field_7025[16] = 4358208;
			this.field_7025[17] = 12910976;
			this.field_7025[18] = 9044480;
			this.field_7025[19] = 9706496;
			this.field_7025[20] = 15216640;
			this.field_7025[21] = 13688832;
			this.field_7025[22] = 11763712;
			this.field_7025[23] = 8257536;
		}

		return this.method_6822(this.field_5987, this.field_6010, this.field_6035);
	}

	public int method_6822(double d, double e, double f) {
		float g = 10000.0F;
		int i = 0;
		class_9 lv = new class_9(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
		int j = 0;
		if (this.field_7016 == null || this.field_7016.method_12517() == 0) {
			j = 12;
		}

		for (int k = j; k < 24; k++) {
			if (this.field_7012[k] != null) {
				float h = this.field_7012[k].method_32(lv);
				if (h < g) {
					g = h;
					i = k;
				}
			}
		}

		return i;
	}

	@Nullable
	public class_11 method_6833(int i, int j, @Nullable class_9 arg) {
		for (int k = 0; k < 24; k++) {
			class_9 lv = this.field_7012[k];
			lv.field_42 = false;
			lv.field_47 = 0.0F;
			lv.field_36 = 0.0F;
			lv.field_34 = 0.0F;
			lv.field_35 = null;
			lv.field_37 = -1;
		}

		class_9 lv2 = this.field_7012[i];
		class_9 lv = this.field_7012[j];
		lv2.field_36 = 0.0F;
		lv2.field_34 = lv2.method_31(lv);
		lv2.field_47 = lv2.field_34;
		this.field_7008.method_5();
		this.field_7008.method_2(lv2);
		class_9 lv3 = lv2;
		int l = 0;
		if (this.field_7016 == null || this.field_7016.method_12517() == 0) {
			l = 12;
		}

		while (!this.field_7008.method_8()) {
			class_9 lv4 = this.field_7008.method_6();
			if (lv4.equals(lv)) {
				if (arg != null) {
					arg.field_35 = lv;
					lv = arg;
				}

				return this.method_6826(lv2, lv);
			}

			if (lv4.method_31(lv) < lv3.method_31(lv)) {
				lv3 = lv4;
			}

			lv4.field_42 = true;
			int m = 0;

			for (int n = 0; n < 24; n++) {
				if (this.field_7012[n] == lv4) {
					m = n;
					break;
				}
			}

			for (int nx = l; nx < 24; nx++) {
				if ((this.field_7025[m] & 1 << nx) > 0) {
					class_9 lv5 = this.field_7012[nx];
					if (!lv5.field_42) {
						float f = lv4.field_36 + lv4.method_31(lv5);
						if (!lv5.method_27() || f < lv5.field_36) {
							lv5.field_35 = lv4;
							lv5.field_36 = f;
							lv5.field_34 = lv5.method_31(lv);
							if (lv5.method_27()) {
								this.field_7008.method_3(lv5, lv5.field_36 + lv5.field_34);
							} else {
								lv5.field_47 = lv5.field_36 + lv5.field_34;
								this.field_7008.method_2(lv5);
							}
						}
					}
				}
			}
		}

		if (lv3 == lv2) {
			return null;
		} else {
			field_7021.debug("Failed to find path from {} to {}", i, j);
			if (arg != null) {
				arg.field_35 = lv3;
				lv3 = arg;
			}

			return this.method_6826(lv2, lv3);
		}
	}

	private class_11 method_6826(class_9 arg, class_9 arg2) {
		int i = 1;

		for (class_9 lv = arg2; lv.field_35 != null; lv = lv.field_35) {
			i++;
		}

		class_9[] lvs = new class_9[i];
		class_9 var7 = arg2;
		i--;

		for (lvs[i] = arg2; var7.field_35 != null; lvs[i] = var7) {
			var7 = var7.field_35;
			i--;
		}

		return new class_11(lvs);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("DragonPhase", this.field_7028.method_6864().method_6849().method_6871());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10545("DragonPhase")) {
			this.field_7028.method_6863(class_1527.method_6868(arg.method_10550("DragonPhase")));
		}
	}

	@Override
	protected void method_5982() {
	}

	@Override
	public class_1297[] method_5690() {
		return this.field_7032;
	}

	@Override
	public boolean method_5863() {
		return false;
	}

	@Override
	public class_1937 method_6815() {
		return this.field_6002;
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15024;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15086;
	}

	@Override
	protected float method_6107() {
		return 5.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_6823(int i, double[] ds, double[] es) {
		class_1521 lv = this.field_7028.method_6864();
		class_1527<? extends class_1521> lv2 = lv.method_6849();
		double d;
		if (lv2 == class_1527.field_7067 || lv2 == class_1527.field_7077) {
			class_2338 lv3 = this.field_6002.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600);
			float f = Math.max(class_3532.method_15368(this.method_5677(lv3)) / 4.0F, 1.0F);
			d = (double)((float)i / f);
		} else if (lv.method_6848()) {
			d = (double)i;
		} else if (i == 6) {
			d = 0.0;
		} else {
			d = es[1] - ds[1];
		}

		return (float)d;
	}

	public class_243 method_6834(float f) {
		class_1521 lv = this.field_7028.method_6864();
		class_1527<? extends class_1521> lv2 = lv.method_6849();
		class_243 lv4;
		if (lv2 == class_1527.field_7067 || lv2 == class_1527.field_7077) {
			class_2338 lv3 = this.field_6002.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600);
			float g = Math.max(class_3532.method_15368(this.method_5677(lv3)) / 4.0F, 1.0F);
			float h = 6.0F / g;
			float i = this.field_5965;
			float j = 1.5F;
			this.field_5965 = -h * 1.5F * 5.0F;
			lv4 = this.method_5828(f);
			this.field_5965 = i;
		} else if (lv.method_6848()) {
			float k = this.field_5965;
			float g = 1.5F;
			this.field_5965 = -45.0F;
			lv4 = this.method_5828(f);
			this.field_5965 = k;
		} else {
			lv4 = this.method_5828(f);
		}

		return lv4;
	}

	public void method_6828(class_1511 arg, class_2338 arg2, class_1282 arg3) {
		class_1657 lv;
		if (arg3.method_5529() instanceof class_1657) {
			lv = (class_1657)arg3.method_5529();
		} else {
			lv = this.field_6002.method_8483(arg2, 64.0, 64.0);
		}

		if (arg == this.field_7024) {
			this.method_6816(this.field_7017, class_1282.method_5512(lv), 10.0F);
		}

		this.field_7028.method_6864().method_6850(arg, arg2, arg3, lv);
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_7013.equals(arg) && this.field_6002.field_9236) {
			this.field_7028.method_6863(class_1527.method_6868(this.method_5841().method_12789(field_7013)));
		}

		super.method_5674(arg);
	}

	public class_1526 method_6831() {
		return this.field_7028;
	}

	@Nullable
	public class_2881 method_6829() {
		return this.field_7016;
	}

	@Override
	public boolean method_6092(class_1293 arg) {
		return false;
	}

	@Override
	protected boolean method_5860(class_1297 arg) {
		return false;
	}

	@Override
	public boolean method_5822() {
		return false;
	}
}
