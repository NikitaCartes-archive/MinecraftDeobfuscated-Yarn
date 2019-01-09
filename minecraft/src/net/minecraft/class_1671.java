package net.minecraft;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public class class_1671 extends class_1297 implements class_3856 {
	private static final class_2940<class_1799> field_7614 = class_2945.method_12791(class_1671.class, class_2943.field_13322);
	private static final class_2940<Optional<UUID>> field_7611 = class_2945.method_12791(class_1671.class, class_2943.field_13313);
	private static final class_2940<Boolean> field_7615 = class_2945.method_12791(class_1671.class, class_2943.field_13323);
	private static final Predicate<class_1297> field_16996 = class_1301.field_6155.and(class_1301.field_6154.and(class_1297::method_5863));
	private int field_7613;
	private int field_7612;
	private class_1309 field_7616;

	public class_1671(class_1937 arg) {
		super(class_1299.field_6133, arg);
		this.method_5835(0.25F, 0.25F);
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7614, class_1799.field_8037);
		this.field_6011.method_12784(field_7611, Optional.empty());
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
		this.method_5835(0.25F, 0.25F);
		this.method_5814(d, e, f);
		int i = 1;
		if (!arg2.method_7960() && arg2.method_7985()) {
			this.field_6011.method_12778(field_7614, arg2.method_7972());
			i += arg2.method_7911("Fireworks").method_10571("Flight");
		}

		this.field_5967 = this.field_5974.nextGaussian() * 0.001;
		this.field_6006 = this.field_5974.nextGaussian() * 0.001;
		this.field_5984 = 0.05;
		this.field_7612 = 10 * i + this.field_5974.nextInt(6) + this.field_5974.nextInt(7);
	}

	public class_1671(class_1937 arg, class_1799 arg2, class_1309 arg3) {
		this(arg, arg3.field_5987, arg3.field_6010, arg3.field_6035, arg2);
		this.field_6011.method_12778(field_7611, Optional.of(arg3.method_5667()));
		this.field_7616 = arg3;
	}

	public class_1671(class_1937 arg, class_1799 arg2, double d, double e, double f, boolean bl) {
		this(arg, d, e, f, arg2);
		this.field_6011.method_12778(field_7615, bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5750(double d, double e, double f) {
		this.field_5967 = d;
		this.field_5984 = e;
		this.field_6006 = f;
		if (this.field_6004 == 0.0F && this.field_5982 == 0.0F) {
			float g = class_3532.method_15368(d * d + f * f);
			this.field_6031 = (float)(class_3532.method_15349(d, f) * 180.0F / (float)Math.PI);
			this.field_5965 = (float)(class_3532.method_15349(e, (double)g) * 180.0F / (float)Math.PI);
			this.field_5982 = this.field_6031;
			this.field_6004 = this.field_5965;
		}
	}

	public void method_7474(class_1297 arg, float f, float g) {
		float h = -class_3532.method_15374(f * (float) (Math.PI / 180.0)) * class_3532.method_15362(arg.field_5965 * (float) (Math.PI / 180.0));
		float i = -class_3532.method_15374(arg.field_5965 * (float) (Math.PI / 180.0));
		float j = class_3532.method_15362(f * (float) (Math.PI / 180.0)) * class_3532.method_15362(arg.field_5965 * (float) (Math.PI / 180.0));
		float k = class_3532.method_15355(h * h + i * i + j * j);
		h /= k;
		i /= k;
		j /= k;
		h = (float)((double)h + this.field_5974.nextGaussian() * 0.0075F * (double)g);
		i = (float)((double)i + this.field_5974.nextGaussian() * 0.0075F * (double)g);
		j = (float)((double)j + this.field_5974.nextGaussian() * 0.0075F * (double)g);
		this.field_5967 = (double)h;
		this.field_5984 = (double)i;
		this.field_6006 = (double)j;
		float l = class_3532.method_15355(h * h + j * j);
		this.field_6031 = (float)(class_3532.method_15349((double)h, (double)j) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349((double)i, (double)l) * 180.0F / (float)Math.PI);
		this.field_5982 = this.field_6031;
		this.field_6004 = this.field_5965;
		this.field_7613 = 0;
		this.field_5967 = this.field_5967 + arg.field_5967;
		this.field_5984 = this.field_5984 + arg.field_5984;
		this.field_6006 = this.field_6006 + arg.field_6006;
	}

	@Override
	public void method_5773() {
		this.field_6038 = this.field_5987;
		this.field_5971 = this.field_6010;
		this.field_5989 = this.field_6035;
		super.method_5773();
		if (this.method_7476()) {
			if (this.field_7616 == null) {
				UUID uUID = (UUID)this.field_6011.method_12789(field_7611).orElse(null);
				class_1297 lv = this.field_6002.method_14190(uUID);
				if (lv instanceof class_1309) {
					this.field_7616 = (class_1309)lv;
				}
			}

			if (this.field_7616 != null) {
				if (this.field_7616.method_6128()) {
					class_243 lv2 = this.field_7616.method_5720();
					double d = 1.5;
					double e = 0.1;
					this.field_7616.field_5967 = this.field_7616.field_5967 + lv2.field_1352 * 0.1 + (lv2.field_1352 * 1.5 - this.field_7616.field_5967) * 0.5;
					this.field_7616.field_5984 = this.field_7616.field_5984 + lv2.field_1351 * 0.1 + (lv2.field_1351 * 1.5 - this.field_7616.field_5984) * 0.5;
					this.field_7616.field_6006 = this.field_7616.field_6006 + lv2.field_1350 * 0.1 + (lv2.field_1350 * 1.5 - this.field_7616.field_6006) * 0.5;
				}

				this.method_5814(this.field_7616.field_5987, this.field_7616.field_6010, this.field_7616.field_6035);
				this.field_5967 = this.field_7616.field_5967;
				this.field_5984 = this.field_7616.field_5984;
				this.field_6006 = this.field_7616.field_6006;
			}
		} else {
			if (!this.method_7477()) {
				this.field_5967 *= 1.15;
				this.field_6006 *= 1.15;
				this.field_5984 += 0.04;
			}

			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
		}

		class_243 lv2 = new class_243(this.field_5987, this.field_6010, this.field_6035);
		class_243 lv3 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		class_239 lv4 = this.field_6002.method_8531(lv2, lv3, class_242.field_1348, true, false);
		lv2 = new class_243(this.field_5987, this.field_6010, this.field_6035);
		lv3 = new class_243(this.field_5987 + this.field_5967, this.field_6010 + this.field_5984, this.field_6035 + this.field_6006);
		if (lv4 != null) {
			lv3 = new class_243(lv4.field_1329.field_1352, lv4.field_1329.field_1351, lv4.field_1329.field_1350);
		}

		class_1297 lv5 = this.method_16829(lv2, lv3);
		if (lv5 != null) {
			lv4 = new class_239(lv5);
		}

		if (!this.field_5960) {
			this.method_16828(lv4);
			this.field_6007 = true;
		}

		float f = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		this.field_6031 = (float)(class_3532.method_15349(this.field_5967, this.field_6006) * 180.0F / (float)Math.PI);
		this.field_5965 = (float)(class_3532.method_15349(this.field_5984, (double)f) * 180.0F / (float)Math.PI);

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
					-this.field_5984 * 0.5,
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

	protected void method_16828(@Nullable class_239 arg) {
		if (arg != null && arg.field_1326 != null && !this.field_6002.field_9236) {
			this.method_16830();
		} else if (this.field_6015) {
			class_2338 lv;
			if (arg != null) {
				lv = arg.method_1015();
			} else {
				lv = new class_2338(
					(double)class_3532.method_15357(this.field_5987),
					(double)((float)class_3532.method_15357(this.field_6010) + 0.2F),
					(double)class_3532.method_15357(this.field_6035)
				);
			}

			class_2680 lv2 = this.field_6002.method_8320(lv);
			if (!lv2.method_11588()) {
				lv2.method_11613(this.field_6002, lv, this);
			}

			this.method_16830();
		}
	}

	@Nullable
	protected class_1297 method_16829(class_243 arg, class_243 arg2) {
		class_1297 lv = null;
		List<class_1297> list = this.field_6002
			.method_8333(this, this.method_5829().method_1012(this.field_5967, this.field_5984, this.field_6006).method_1014(1.0), field_16996);
		double d = 0.0;

		for (class_1297 lv2 : list) {
			class_238 lv3 = lv2.method_5829().method_1014(0.3F);
			class_239 lv4 = lv3.method_1004(arg, arg2);
			if (lv4 != null) {
				double e = arg.method_1025(lv4.field_1329);
				if (e < d || d == 0.0) {
					lv = lv2;
					d = e;
				}
			}
		}

		return lv;
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

			for (class_1309 lv5 : this.field_6002.method_8403(class_1309.class, this.method_5829().method_1014(5.0))) {
				if (lv5 != this.field_7616 && !(this.method_5858(lv5) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						class_239 lv6 = this.field_6002
							.method_8531(
								lv4, new class_243(lv5.field_5987, lv5.field_6010 + (double)lv5.field_6019 * 0.5 * (double)i, lv5.field_6035), class_242.field_1348, true, false
							);
						if (lv6 == null || lv6.field_1330 == class_239.class_240.field_1333) {
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
		return this.field_6011.method_12789(field_7611).orElse(null) != null;
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
			this.field_6002.method_8547(this.field_5987, this.field_6010, this.field_6035, this.field_5967, this.field_5984, this.field_6006, lv2);
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
}
