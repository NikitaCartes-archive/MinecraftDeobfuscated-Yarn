package net.minecraft;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public class class_1671 extends class_1297 implements class_3856, class_1676 {
	private static final class_2940<class_1799> field_7614 = class_2945.method_12791(class_1671.class, class_2943.field_13322);
	private static final class_2940<OptionalInt> field_7611 = class_2945.method_12791(class_1671.class, class_2943.field_17910);
	private static final class_2940<Boolean> field_7615 = class_2945.method_12791(class_1671.class, class_2943.field_13323);
	private int field_7613;
	private int field_7612;
	private class_1309 field_7616;

	public class_1671(class_1299<? extends class_1671> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7614, class_1799.field_8037);
		this.field_6011.method_12784(field_7611, OptionalInt.empty());
		this.field_6011.method_12784(field_7615, false);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		return d < 4096.0 && !this.method_7476();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5727(double d, double e, double f) {
		return super.method_5727(d, e, f) && !this.method_7476();
	}

	public class_1671(class_1937 arg, double d, double e, double f, class_1799 arg2) {
		super(class_1299.field_6133, arg);
		this.field_7613 = 0;
		this.method_5814(d, e, f);
		int i = 1;
		if (!arg2.method_7960() && arg2.method_7985()) {
			this.field_6011.method_12778(field_7614, arg2.method_7972());
			i += arg2.method_7911("Fireworks").method_10571("Flight");
		}

		this.method_18800(this.field_5974.nextGaussian() * 0.001, 0.05, this.field_5974.nextGaussian() * 0.001);
		this.field_7612 = 10 * i + this.field_5974.nextInt(6) + this.field_5974.nextInt(7);
	}

	public class_1671(class_1937 arg, class_1799 arg2, class_1309 arg3) {
		this(arg, arg3.field_5987, arg3.field_6010, arg3.field_6035, arg2);
		this.field_6011.method_12778(field_7611, OptionalInt.of(arg3.method_5628()));
		this.field_7616 = arg3;
	}

	public class_1671(class_1937 arg, class_1799 arg2, double d, double e, double f, boolean bl) {
		this(arg, d, e, f, arg2);
		this.field_6011.method_12778(field_7615, bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.method_18800(d, e, f);
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
		}
	}

	@Override
	public void method_5773() {
		this.field_6038 = this.field_5987;
		this.field_5971 = this.field_6010;
		this.field_5989 = this.field_6035;
		super.method_5773();
		if (this.method_7476()) {
			if (this.field_7616 == null) {
				this.field_6011.method_12789(field_7611).ifPresent(i -> {
					class_1297 lvx = this.field_6002.method_8469(i);
					if (lvx instanceof class_1309) {
						this.field_7616 = (class_1309)lvx;
					}
				});
			}

			if (this.field_7616 != null) {
				if (this.field_7616.method_6128()) {
					class_243 lv = this.field_7616.method_5720();
					double d = 1.5;
					double e = 0.1;
					class_243 lv2 = this.field_7616.method_18798();
					this.field_7616
						.method_18799(
							lv2.method_1031(
								lv.field_1352 * 0.1 + (lv.field_1352 * 1.5 - lv2.field_1352) * 0.5,
								lv.field_1351 * 0.1 + (lv.field_1351 * 1.5 - lv2.field_1351) * 0.5,
								lv.field_1350 * 0.1 + (lv.field_1350 * 1.5 - lv2.field_1350) * 0.5
							)
						);
				}

				this.method_5814(this.field_7616.field_5987, this.field_7616.field_6010, this.field_7616.field_6035);
				this.method_18799(this.field_7616.method_18798());
			}
		} else {
			if (!this.method_7477()) {
				this.method_18799(this.method_18798().method_18805(1.15, 1.0, 1.15).method_1031(0.0, 0.04, 0.0));
			}

			this.method_5784(class_1313.field_6308, this.method_18798());
		}

		class_243 lv = this.method_18798();
		class_239 lv3 = class_1675.method_18074(
			this,
			this.method_5829().method_18804(lv).method_1014(1.0),
			arg -> !arg.method_7325() && arg.method_5805() && arg.method_5863(),
			class_3959.class_3960.field_17558,
			true
		);
		if (!this.field_5960) {
			this.method_16828(lv3);
			this.field_6007 = true;
		}

		float f = class_3532.method_15368(method_17996(lv));
		this.field_6031 = (float)(class_3532.method_15349(lv.field_1352, lv.field_1350) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(lv.field_1351, (double)f) * 180.0F / (float)Math.PI);

		while (this.field_5965 - this.field_6004 < -180.0F) {
			this.field_6004 -= 360.0F;
		}

		while (this.field_5965 - this.field_6004 >= 180.0F) {
			this.field_6004 += 360.0F;
		}

		while (this.field_6031 - this.field_5982 < -180.0F) {
			this.field_5982 -= 360.0F;
		}

		while (this.field_6031 - this.field_5982 >= 180.0F) {
			this.field_5982 += 360.0F;
		}

		this.field_5965 = class_3532.method_16439(0.2F, this.field_6004, this.field_5965);
		this.field_6031 = class_3532.method_16439(0.2F, this.field_5982, this.field_6031);
		if (this.field_7613 == 0 && !this.method_5701()) {
			this.field_6002.method_8465(null, this.field_5987, this.field_6010, this.field_6035, class_3417.field_14702, class_3419.field_15256, 3.0F, 1.0F);
		}

		this.field_7613++;
		if (this.field_6002.field_9236 && this.field_7613 % 2 < 2) {
			this.field_6002
				.method_8406(
					class_2398.field_11248,
					this.field_5987,
					this.field_6010 - 0.3,
					this.field_6035,
					this.field_5974.nextGaussian() * 0.05,
					-this.method_18798().field_1351 * 0.5,
					this.field_5974.nextGaussian() * 0.05
				);
		}

		if (!this.field_6002.field_9236 && this.field_7613 > this.field_7612) {
			this.method_16830();
		}
	}

	private void method_16830() {
		this.field_6002.method_8421(this, (byte)17);
		this.method_7475();
		this.method_5650();
	}

	protected void method_16828(class_239 arg) {
		if (arg.method_17783() == class_239.class_240.field_1331 && !this.field_6002.field_9236) {
			this.method_16830();
		} else if (this.field_6015) {
			class_2338 lv;
			if (arg.method_17783() == class_239.class_240.field_1332) {
				lv = new class_2338(((class_3965)arg).method_17777());
			} else {
				lv = new class_2338(this);
			}

			this.field_6002.method_8320(lv).method_11613(this.field_6002, lv, this);
			this.method_16830();
		}
	}

	private void method_7475() {
		float f = 0.0F;
		class_1799 lv = this.field_6011.method_12789(field_7614);
		class_2487 lv2 = lv.method_7960() ? null : lv.method_7941("Fireworks");
		class_2499 lv3 = lv2 != null ? lv2.method_10554("Explosions", 10) : null;
		if (lv3 != null && !lv3.isEmpty()) {
			f = 5.0F + (float)(lv3.size() * 2);
		}

		if (f > 0.0F) {
			if (this.field_7616 != null) {
				this.field_7616.method_5643(class_1282.field_5860, 5.0F + (float)(lv3.size() * 2));
			}

			double d = 5.0;
			class_243 lv4 = new class_243(this.field_5987, this.field_6010, this.field_6035);

			for (class_1309 lv5 : this.field_6002.method_18467(class_1309.class, this.method_5829().method_1014(5.0))) {
				if (lv5 != this.field_7616 && !(this.method_5858(lv5) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						class_243 lv6 = new class_243(lv5.field_5987, lv5.field_6010 + (double)lv5.method_17682() * 0.5 * (double)i, lv5.field_6035);
						class_239 lv7 = this.field_6002.method_17742(new class_3959(lv4, lv6, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, this));
						if (lv7.method_17783() == class_239.class_240.field_1333) {
							bl = true;
							break;
						}
					}

					if (bl) {
						float g = f * (float)Math.sqrt((5.0 - (double)this.method_5739(lv5)) / 5.0);
						lv5.method_5643(class_1282.field_5860, g);
					}
				}
			}
		}
	}

	private boolean method_7476() {
		return this.field_6011.method_12789(field_7611).isPresent();
	}

	public boolean method_7477() {
		return this.field_6011.method_12789(field_7615);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 17 && this.field_6002.field_9236) {
			class_1799 lv = this.field_6011.method_12789(field_7614);
			class_2487 lv2 = lv.method_7960() ? null : lv.method_7941("Fireworks");
			class_243 lv3 = this.method_18798();
			this.field_6002.method_8547(this.field_5987, this.field_6010, this.field_6035, lv3.field_1352, lv3.field_1351, lv3.field_1350, lv2);
		}

		super.method_5711(b);
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10569("Life", this.field_7613);
		arg.method_10569("LifeTime", this.field_7612);
		class_1799 lv = this.field_6011.method_12789(field_7614);
		if (!lv.method_7960()) {
			arg.method_10566("FireworksItem", lv.method_7953(new class_2487()));
		}

		arg.method_10556("ShotAtAngle", this.field_6011.method_12789(field_7615));
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7613 = arg.method_10550("Life");
		this.field_7612 = arg.method_10550("LifeTime");
		class_1799 lv = class_1799.method_7915(arg.method_10562("FireworksItem"));
		if (!lv.method_7960()) {
			this.field_6011.method_12778(field_7614, lv);
		}

		if (arg.method_10545("ShotAtAngle")) {
			this.field_6011.method_12778(field_7615, arg.method_10577("ShotAtAngle"));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_7495() {
		class_1799 lv = this.field_6011.method_12789(field_7614);
		return lv.method_7960() ? new class_1799(class_1802.field_8639) : lv;
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}

	@Override
	public void method_7485(double d, double e, double f, float g, float h) {
		float i = class_3532.method_15368(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		e += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		f += this.field_5974.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.method_18800(d, e, f);
	}
}
