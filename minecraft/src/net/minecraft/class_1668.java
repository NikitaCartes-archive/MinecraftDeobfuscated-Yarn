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

	protected class_1668(class_1299<?> arg, class_1937 arg2, float f, float g) {
		super(arg, arg2);
		this.method_5835(f, g);
	}

	public class_1668(class_1299<?> arg, double d, double e, double f, double g, double h, double i, class_1937 arg2, float j, float k) {
		this(arg, arg2, j, k);
		this.method_5808(d, e, f, this.field_6031, this.field_5965);
		this.method_5814(d, e, f);
		double l = (double)class_3532.method_15368(g * g + h * h + i * i);
		this.field_7601 = g / l * 0.1;
		this.field_7600 = h / l * 0.1;
		this.field_7599 = i / l * 0.1;
	}

	public class_1668(class_1299<?> arg, class_1309 arg2, double d, double e, double f, class_1937 arg3, float g, float h) {
		this(arg, arg3, g, h);
		this.field_7604 = arg2;
		this.method_5808(arg2.field_5987, arg2.field_6010, arg2.field_6035, arg2.field_6031, arg2.field_5965);
		this.method_5814(this.field_5987, this.field_6010, this.field_6035);
		this.field_5967 = 0.0;
		this.field_5984 = 0.0;
		this.field_6006 = 0.0;
		d += this.field_5974.nextGaussian() * 0.4;
		e += this.field_5974.nextGaussian() * 0.4;
		f += this.field_5974.nextGaussian() * 0.4;
		double i = (double)class_3532.method_15368(d * d + e * e + f * f);
		this.field_7601 = d / i * 0.1;
		this.field_7600 = e / i * 0.1;
		this.field_7599 = f / i * 0.1;
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
			class_239 lv = class_1675.method_7482(this, true, this.field_7602 >= 25, this.field_7604);
			if (lv != null) {
				this.method_7469(lv);
			}

			this.field_5987 = this.field_5987 + this.field_5967;
			this.field_6010 = this.field_6010 + this.field_5984;
			this.field_6035 = this.field_6035 + this.field_6006;
			class_1675.method_7484(this, 0.2F);
			float f = this.method_7466();
			if (this.method_5799()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
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

				f = 0.8F;
			}

			this.field_5967 = this.field_5967 + this.field_7601;
			this.field_5984 = this.field_5984 + this.field_7600;
			this.field_6006 = this.field_6006 + this.field_7599;
			this.field_5967 *= (double)f;
			this.field_5984 *= (double)f;
			this.field_6006 *= (double)f;
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
		arg.method_10566("direction", this.method_5846(new double[]{this.field_5967, this.field_5984, this.field_6006}));
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
			this.field_5967 = lv.method_10611(0);
			this.field_5984 = lv.method_10611(1);
			this.field_6006 = lv.method_10611(2);
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
				if (lv != null) {
					this.field_5967 = lv.field_1352;
					this.field_5984 = lv.field_1351;
					this.field_6006 = lv.field_1350;
					this.field_7601 = this.field_5967 * 0.1;
					this.field_7600 = this.field_5984 * 0.1;
					this.field_7599 = this.field_6006 * 0.1;
				}

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
}
