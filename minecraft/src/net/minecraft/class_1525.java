package net.minecraft;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1525 extends class_1512 {
	private static final Logger field_7061 = LogManager.getLogger();
	private int field_7060;
	private class_11 field_7059;
	private class_243 field_7057;
	private class_1309 field_7062;
	private boolean field_7058;

	public class_1525(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6855() {
		if (this.field_7062 == null) {
			field_7061.warn("Skipping player strafe phase because no player was found");
			this.field_7036.method_6831().method_6863(class_1527.field_7069);
		} else {
			if (this.field_7059 != null && this.field_7059.method_46()) {
				double d = this.field_7062.field_5987;
				double e = this.field_7062.field_6035;
				double f = d - this.field_7036.field_5987;
				double g = e - this.field_7036.field_6035;
				double h = (double)class_3532.method_15368(f * f + g * g);
				double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
				this.field_7057 = new class_243(d, this.field_7062.field_6010 + i, e);
			}

			double d = this.field_7057 == null ? 0.0 : this.field_7057.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035);
			if (d < 100.0 || d > 22500.0) {
				this.method_6860();
			}

			double e = 64.0;
			if (this.field_7062.method_5858(this.field_7036) < 4096.0) {
				if (this.field_7036.method_6057(this.field_7062)) {
					this.field_7060++;
					class_243 lv = new class_243(this.field_7062.field_5987 - this.field_7036.field_5987, 0.0, this.field_7062.field_6035 - this.field_7036.field_6035)
						.method_1029();
					class_243 lv2 = new class_243(
							(double)class_3532.method_15374(this.field_7036.field_6031 * (float) (Math.PI / 180.0)),
							0.0,
							(double)(-class_3532.method_15362(this.field_7036.field_6031 * (float) (Math.PI / 180.0)))
						)
						.method_1029();
					float j = (float)lv2.method_1026(lv);
					float k = (float)(Math.acos((double)j) * 180.0F / (float)Math.PI);
					k += 0.5F;
					if (this.field_7060 >= 5 && k >= 0.0F && k < 10.0F) {
						double h = 1.0;
						class_243 lv3 = this.field_7036.method_5828(1.0F);
						double l = this.field_7036.field_7017.field_5987 - lv3.field_1352 * 1.0;
						double m = this.field_7036.field_7017.field_6010 + (double)(this.field_7036.field_7017.field_6019 / 2.0F) + 0.5;
						double n = this.field_7036.field_7017.field_6035 - lv3.field_1350 * 1.0;
						double o = this.field_7062.field_5987 - l;
						double p = this.field_7062.field_6010 + (double)(this.field_7062.field_6019 / 2.0F) - (m + (double)(this.field_7036.field_7017.field_6019 / 2.0F));
						double q = this.field_7062.field_6035 - n;
						this.field_7036.field_6002.method_8444(null, 1017, new class_2338(this.field_7036), 0);
						class_1670 lv4 = new class_1670(this.field_7036.field_6002, this.field_7036, o, p, q);
						lv4.method_5808(l, m, n, 0.0F, 0.0F);
						this.field_7036.field_6002.method_8649(lv4);
						this.field_7060 = 0;
						if (this.field_7059 != null) {
							while (!this.field_7059.method_46()) {
								this.field_7059.method_44();
							}
						}

						this.field_7036.method_6831().method_6863(class_1527.field_7069);
					}
				} else if (this.field_7060 > 0) {
					this.field_7060--;
				}
			} else if (this.field_7060 > 0) {
				this.field_7060--;
			}
		}
	}

	private void method_6860() {
		if (this.field_7059 == null || this.field_7059.method_46()) {
			int i = this.field_7036.method_6818();
			int j = i;
			if (this.field_7036.method_6051().nextInt(8) == 0) {
				this.field_7058 = !this.field_7058;
				j = i + 6;
			}

			if (this.field_7058) {
				j++;
			} else {
				j--;
			}

			if (this.field_7036.method_6829() != null && this.field_7036.method_6829().method_12517() > 0) {
				j %= 12;
				if (j < 0) {
					j += 12;
				}
			} else {
				j -= 12;
				j &= 7;
				j += 12;
			}

			this.field_7059 = this.field_7036.method_6833(i, j, null);
			if (this.field_7059 != null) {
				this.field_7059.method_44();
			}
		}

		this.method_6861();
	}

	private void method_6861() {
		if (this.field_7059 != null && !this.field_7059.method_46()) {
			class_243 lv = this.field_7059.method_35();
			this.field_7059.method_44();
			double d = lv.field_1352;
			double e = lv.field_1350;

			double f;
			do {
				f = lv.field_1351 + (double)(this.field_7036.method_6051().nextFloat() * 20.0F);
			} while (f < lv.field_1351);

			this.field_7057 = new class_243(d, f, e);
		}
	}

	@Override
	public void method_6856() {
		this.field_7060 = 0;
		this.field_7057 = null;
		this.field_7059 = null;
		this.field_7062 = null;
	}

	public void method_6862(class_1309 arg) {
		this.field_7062 = arg;
		int i = this.field_7036.method_6818();
		int j = this.field_7036.method_6822(this.field_7062.field_5987, this.field_7062.field_6010, this.field_7062.field_6035);
		int k = class_3532.method_15357(this.field_7062.field_5987);
		int l = class_3532.method_15357(this.field_7062.field_6035);
		double d = (double)k - this.field_7036.field_5987;
		double e = (double)l - this.field_7036.field_6035;
		double f = (double)class_3532.method_15368(d * d + e * e);
		double g = Math.min(0.4F + f / 80.0 - 1.0, 10.0);
		int m = class_3532.method_15357(this.field_7062.field_6010 + g);
		class_9 lv = new class_9(k, m, l);
		this.field_7059 = this.field_7036.method_6833(i, j, lv);
		if (this.field_7059 != null) {
			this.field_7059.method_44();
			this.method_6861();
		}
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7057;
	}

	@Override
	public class_1527<class_1525> method_6849() {
		return class_1527.field_7076;
	}
}
