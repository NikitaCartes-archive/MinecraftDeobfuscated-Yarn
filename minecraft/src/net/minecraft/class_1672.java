package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public class class_1672 extends class_1297 implements class_3856 {
	private static final class_2940<class_1799> field_17080 = class_2945.method_12791(class_1672.class, class_2943.field_13322);
	private double field_7619;
	private double field_7618;
	private double field_7617;
	private int field_7620;
	private boolean field_7621;

	public class_1672(class_1937 arg) {
		super(class_1299.field_6061, arg);
		this.method_5835(0.25F, 0.25F);
	}

	public class_1672(class_1937 arg, double d, double e, double f) {
		this(arg);
		this.field_7620 = 0;
		this.method_5814(d, e, f);
	}

	public void method_16933(class_1799 arg) {
		if (arg.method_7909() != class_1802.field_8449 || arg.method_7985()) {
			this.method_5841().method_12778(field_17080, class_156.method_654(arg.method_7972(), argx -> argx.method_7939(1)));
		}
	}

	private class_1799 method_16935() {
		return this.method_5841().method_12789(field_17080);
	}

	@Override
	public class_1799 method_7495() {
		class_1799 lv = this.method_16935();
		return lv.method_7960() ? new class_1799(class_1802.field_8449) : lv;
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_17080, class_1799.field_8037);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	public void method_7478(class_2338 arg) {
		double d = (double)arg.method_10263();
		int i = arg.method_10264();
		double e = (double)arg.method_10260();
		double f = d - this.field_5987;
		double g = e - this.field_6035;
		float h = class_3532.method_15368(f * f + g * g);
		if (h > 12.0F) {
			this.field_7619 = this.field_5987 + f / (double)h * 12.0;
			this.field_7617 = this.field_6035 + g / (double)h * 12.0;
			this.field_7618 = this.field_6010 + 8.0;
		} else {
			this.field_7619 = d;
			this.field_7618 = (double)i;
			this.field_7617 = e;
		}

		this.field_7620 = 0;
		this.field_7621 = this.field_5974.nextInt(5) > 0;
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

	@Override
	public void method_5773() {
		this.field_6038 = this.field_5987;
		this.field_5971 = this.field_6010;
		this.field_5989 = this.field_6035;
		super.method_5773();
		this.field_5987 = this.field_5987 + this.field_5967;
		this.field_6010 = this.field_6010 + this.field_5984;
		this.field_6035 = this.field_6035 + this.field_6006;
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
		if (!this.field_6002.field_9236) {
			double d = this.field_7619 - this.field_5987;
			double e = this.field_7617 - this.field_6035;
			float g = (float)Math.sqrt(d * d + e * e);
			float h = (float)class_3532.method_15349(e, d);
			double i = class_3532.method_16436(0.0025, (double)f, (double)g);
			if (g < 1.0F) {
				i *= 0.8;
				this.field_5984 *= 0.8;
			}

			this.field_5967 = Math.cos((double)h) * i;
			this.field_6006 = Math.sin((double)h) * i;
			if (this.field_6010 < this.field_7618) {
				this.field_5984 = this.field_5984 + (1.0 - this.field_5984) * 0.015F;
			} else {
				this.field_5984 = this.field_5984 + (-1.0 - this.field_5984) * 0.015F;
			}
		}

		float j = 0.25F;
		if (this.method_5799()) {
			for (int k = 0; k < 4; k++) {
				this.field_6002
					.method_8406(
						class_2398.field_11247,
						this.field_5987 - this.field_5967 * 0.25,
						this.field_6010 - this.field_5984 * 0.25,
						this.field_6035 - this.field_6006 * 0.25,
						this.field_5967,
						this.field_5984,
						this.field_6006
					);
			}
		} else {
			this.field_6002
				.method_8406(
					class_2398.field_11214,
					this.field_5987 - this.field_5967 * 0.25 + this.field_5974.nextDouble() * 0.6 - 0.3,
					this.field_6010 - this.field_5984 * 0.25 - 0.5,
					this.field_6035 - this.field_6006 * 0.25 + this.field_5974.nextDouble() * 0.6 - 0.3,
					this.field_5967,
					this.field_5984,
					this.field_6006
				);
		}

		if (!this.field_6002.field_9236) {
			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
			this.field_7620++;
			if (this.field_7620 > 80 && !this.field_6002.field_9236) {
				this.method_5783(class_3417.field_15210, 1.0F, 1.0F);
				this.method_5650();
				if (this.field_7621) {
					this.field_6002.method_8649(new class_1542(this.field_6002, this.field_5987, this.field_6010, this.field_6035, this.method_7495()));
				} else {
					this.field_6002.method_8535(2003, new class_2338(this), 0);
				}
			}
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		class_1799 lv = this.method_16935();
		if (!lv.method_7960()) {
			arg.method_10566("Item", lv.method_7953(new class_2487()));
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		class_1799 lv = class_1799.method_7915(arg.method_10562("Item"));
		this.method_16933(lv);
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_5635() {
		return 15728880;
	}

	@Override
	public boolean method_5732() {
		return false;
	}
}
