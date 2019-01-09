package net.minecraft;

public class class_1366 extends class_1352 {
	protected final class_1314 field_6503;
	protected int field_6505;
	private final double field_6500;
	private final boolean field_6502;
	private class_11 field_6509;
	private int field_6501;
	private double field_6508;
	private double field_6507;
	private double field_6506;
	protected final int field_6504 = 20;

	public class_1366(class_1314 arg, double d, boolean bl) {
		this.field_6503 = arg;
		this.field_6500 = d;
		this.field_6502 = bl;
		this.method_6265(3);
	}

	@Override
	public boolean method_6264() {
		class_1309 lv = this.field_6503.method_5968();
		if (lv == null) {
			return false;
		} else if (!lv.method_5805()) {
			return false;
		} else {
			this.field_6509 = this.field_6503.method_5942().method_6349(lv);
			return this.field_6509 != null ? true : this.method_6289(lv) >= this.field_6503.method_5649(lv.field_5987, lv.method_5829().field_1322, lv.field_6035);
		}
	}

	@Override
	public boolean method_6266() {
		class_1309 lv = this.field_6503.method_5968();
		if (lv == null) {
			return false;
		} else if (!lv.method_5805()) {
			return false;
		} else if (!this.field_6502) {
			return !this.field_6503.method_5942().method_6357();
		} else {
			return !this.field_6503.method_6146(new class_2338(lv))
				? false
				: !(lv instanceof class_1657) || !((class_1657)lv).method_7325() && !((class_1657)lv).method_7337();
		}
	}

	@Override
	public void method_6269() {
		this.field_6503.method_5942().method_6334(this.field_6509, this.field_6500);
		this.field_6501 = 0;
	}

	@Override
	public void method_6270() {
		class_1309 lv = this.field_6503.method_5968();
		if (lv instanceof class_1657 && (((class_1657)lv).method_7325() || ((class_1657)lv).method_7337())) {
			this.field_6503.method_5980(null);
		}

		this.field_6503.method_5942().method_6340();
	}

	@Override
	public void method_6268() {
		class_1309 lv = this.field_6503.method_5968();
		this.field_6503.method_5988().method_6226(lv, 30.0F, 30.0F);
		double d = this.field_6503.method_5649(lv.field_5987, lv.method_5829().field_1322, lv.field_6035);
		this.field_6501--;
		if ((this.field_6502 || this.field_6503.method_5985().method_6369(lv))
			&& this.field_6501 <= 0
			&& (
				this.field_6508 == 0.0 && this.field_6507 == 0.0 && this.field_6506 == 0.0
					|| lv.method_5649(this.field_6508, this.field_6507, this.field_6506) >= 1.0
					|| this.field_6503.method_6051().nextFloat() < 0.05F
			)) {
			this.field_6508 = lv.field_5987;
			this.field_6507 = lv.method_5829().field_1322;
			this.field_6506 = lv.field_6035;
			this.field_6501 = 4 + this.field_6503.method_6051().nextInt(7);
			if (d > 1024.0) {
				this.field_6501 += 10;
			} else if (d > 256.0) {
				this.field_6501 += 5;
			}

			if (!this.field_6503.method_5942().method_6335(lv, this.field_6500)) {
				this.field_6501 += 15;
			}
		}

		this.field_6505 = Math.max(this.field_6505 - 1, 0);
		this.method_6288(lv, d);
	}

	protected void method_6288(class_1309 arg, double d) {
		double e = this.method_6289(arg);
		if (d <= e && this.field_6505 <= 0) {
			this.field_6505 = 20;
			this.field_6503.method_6104(class_1268.field_5808);
			this.field_6503.method_6121(arg);
		}
	}

	protected double method_6289(class_1309 arg) {
		return (double)(this.field_6503.field_5998 * 2.0F * this.field_6503.field_5998 * 2.0F + arg.field_5998);
	}
}
