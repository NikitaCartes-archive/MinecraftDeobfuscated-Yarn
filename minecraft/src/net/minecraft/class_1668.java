package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1668 extends class_1297 {
	public class_1309 field_7604;
	private int field_7603;
	private int field_7602;
	public double field_7601;
	public double field_7600;
	public double field_7599;

	protected class_1668(class_1299<? extends class_1668> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1668(class_1299<? extends class_1668> arg, double d, double e, double f, double g, double h, double i, class_1937 arg2) {
		this(arg, arg2);
		this.method_5808(d, e, f, this.field_6031, this.field_5965);
		this.method_5814(d, e, f);
		double j = (double)class_3532.method_15368(g * g + h * h + i * i);
		this.field_7601 = g / j * 0.1;
		this.field_7600 = h / j * 0.1;
		this.field_7599 = i / j * 0.1;
	}

	public class_1668(class_1299<? extends class_1668> arg, class_1309 arg2, double d, double e, double f, class_1937 arg3) {
		this(arg, arg3);
		this.field_7604 = arg2;
		this.method_5808(arg2.field_5987, arg2.field_6010, arg2.field_6035, arg2.field_6031, arg2.field_5965);
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.method_18799(class_243.field_1353);
		d += this.field_5974.nextGaussian() * 0.4;
		e += this.field_5974.nextGaussian() * 0.4;
		f += this.field_5974.nextGaussian() * 0.4;
		double g = (double)class_3532.method_15368(d * d + e * e + f * f);
		this.field_7601 = d / g * 0.1;
		this.field_7600 = e / g * 0.1;
		this.field_7599 = f / g * 0.1;
	}

	@Override
	protected void method_5693() {
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

	@Override
	public void method_5773() {
		if (this.field_6002.field_9236 || (this.field_7604 == null || !this.field_7604.field_5988) && this.field_6002.method_8591(new class_2338(this))) {
			super.method_5773();
			if (this.method_7468()) {
				this.method_5639(1);
			}

			this.field_7602++;
			class_239 lv = class_1675.method_18076(this, true, this.field_7602 >= 25, this.field_7604, class_3959.class_3960.field_17558);
			if (lv.method_17783() != class_239.class_240.field_1333) {
				this.method_7469(lv);
			}

			class_243 lv2 = this.method_18798();
			this.field_5987 = this.field_5987 + lv2.field_1352;
			this.field_6010 = this.field_6010 + lv2.field_1351;
			this.field_6035 = this.field_6035 + lv2.field_1350;
			class_1675.method_7484(this, 0.2F);
			float f = this.method_7466();
			if (this.method_5799()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
					this.field_6002
						.method_8406(
							class_2398.field_11247,
							this.field_5987 - lv2.field_1352 * 0.25,
							this.field_6010 - lv2.field_1351 * 0.25,
							this.field_6035 - lv2.field_1350 * 0.25,
							lv2.field_1352,
							lv2.field_1351,
							lv2.field_1350
						);
				}

				f = 0.8F;
			}

			this.method_18799(lv2.method_1031(this.field_7601, this.field_7600, this.field_7599).method_1021((double)f));
			this.field_6002.method_8406(this.method_7467(), this.field_5987, this.field_6010 + 0.5, this.field_6035, 0.0, 0.0, 0.0);
			this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		} else {
			this.method_5650();
		}
	}

	protected boolean method_7468() {
		return true;
	}

	protected class_2394 method_7467() {
		return class_2398.field_11251;
	}

	protected float method_7466() {
		return 0.95F;
	}

	protected abstract void method_7469(class_239 arg);

	@Override
	public void method_5652(class_2487 arg) {
		class_243 lv = this.method_18798();
		arg.method_10566("direction", this.method_5846(new double[]{lv.field_1352, lv.field_1351, lv.field_1350}));
		arg.method_10566("power", this.method_5846(new double[]{this.field_7601, this.field_7600, this.field_7599}));
		arg.method_10569("life", this.field_7603);
	}

	@Override
	public void method_5749(class_2487 arg) {
		if (arg.method_10573("power", 9)) {
			class_2499 lv = arg.method_10554("power", 6);
			if (lv.size() == 3) {
				this.field_7601 = lv.method_10611(0);
				this.field_7600 = lv.method_10611(1);
				this.field_7599 = lv.method_10611(2);
			}
		}

		this.field_7603 = arg.method_10550("life");
		if (arg.method_10573("direction", 9) && arg.method_10554("direction", 6).size() == 3) {
			class_2499 lv = arg.method_10554("direction", 6);
			this.method_18800(lv.method_10611(0), lv.method_10611(1), lv.method_10611(2));
		} else {
			this.method_5650();
		}
	}

	@Override
	public boolean method_5863() {
		return true;
	}

	@Override
	public float method_5871() {
		return 1.0F;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			this.method_5785();
			if (arg.method_5529() != null) {
				class_243 lv = arg.method_5529().method_5720();
				this.method_18799(lv);
				this.field_7601 = lv.field_1352 * 0.1;
				this.field_7600 = lv.field_1351 * 0.1;
				this.field_7599 = lv.field_1350 * 0.1;
				if (arg.method_5529() instanceof class_1309) {
					this.field_7604 = (class_1309)arg.method_5529();
				}

				return true;
			} else {
				return false;
			}
		}
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
	public class_2596<?> method_18002() {
		int i = this.field_7604 == null ? 0 : this.field_7604.method_5628();
		return new class_2604(
			this.method_5628(),
			this.method_5667(),
			this.field_5987,
			this.field_6010,
			this.field_6035,
			this.field_5965,
			this.field_6031,
			this.method_5864(),
			i,
			new class_243(this.field_7601, this.field_7600, this.field_7599)
		);
	}
}
