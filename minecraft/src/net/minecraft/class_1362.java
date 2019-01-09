package net.minecraft;

import java.util.List;

public class class_1362 extends class_1352 {
	public final class_1501 field_6488;
	private double field_6487;
	private int field_6489;

	public class_1362(class_1501 arg, double d) {
		this.field_6488 = arg;
		this.field_6487 = d;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6488.method_5934() && !this.field_6488.method_6805()) {
			List<class_1501> list = this.field_6488.field_6002.method_8403(this.field_6488.getClass(), this.field_6488.method_5829().method_1009(9.0, 4.0, 9.0));
			class_1501 lv = null;
			double d = Double.MAX_VALUE;

			for (class_1501 lv2 : list) {
				if (lv2.method_6805() && !lv2.method_6793()) {
					double e = this.field_6488.method_5858(lv2);
					if (!(e > d)) {
						d = e;
						lv = lv2;
					}
				}
			}

			if (lv == null) {
				for (class_1501 lv2x : list) {
					if (lv2x.method_5934() && !lv2x.method_6793()) {
						double e = this.field_6488.method_5858(lv2x);
						if (!(e > d)) {
							d = e;
							lv = lv2x;
						}
					}
				}
			}

			if (lv == null) {
				return false;
			} else if (d < 4.0) {
				return false;
			} else if (!lv.method_5934() && !this.method_6285(lv, 1)) {
				return false;
			} else {
				this.field_6488.method_6791(lv);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean method_6266() {
		if (this.field_6488.method_6805() && this.field_6488.method_6806().method_5805() && this.method_6285(this.field_6488, 0)) {
			double d = this.field_6488.method_5858(this.field_6488.method_6806());
			if (d > 676.0) {
				if (this.field_6487 <= 3.0) {
					this.field_6487 *= 1.2;
					this.field_6489 = 40;
					return true;
				}

				if (this.field_6489 == 0) {
					return false;
				}
			}

			if (this.field_6489 > 0) {
				this.field_6489--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_6270() {
		this.field_6488.method_6797();
		this.field_6487 = 2.1;
	}

	@Override
	public void method_6268() {
		if (this.field_6488.method_6805()) {
			class_1501 lv = this.field_6488.method_6806();
			double d = (double)this.field_6488.method_5739(lv);
			float f = 2.0F;
			class_243 lv2 = new class_243(
					lv.field_5987 - this.field_6488.field_5987, lv.field_6010 - this.field_6488.field_6010, lv.field_6035 - this.field_6488.field_6035
				)
				.method_1029()
				.method_1021(Math.max(d - 2.0, 0.0));
			this.field_6488
				.method_5942()
				.method_6337(
					this.field_6488.field_5987 + lv2.field_1352, this.field_6488.field_6010 + lv2.field_1351, this.field_6488.field_6035 + lv2.field_1350, this.field_6487
				);
		}
	}

	private boolean method_6285(class_1501 arg, int i) {
		if (i > 8) {
			return false;
		} else if (arg.method_6805()) {
			return arg.method_6806().method_5934() ? true : this.method_6285(arg.method_6806(), ++i);
		} else {
			return false;
		}
	}
}
