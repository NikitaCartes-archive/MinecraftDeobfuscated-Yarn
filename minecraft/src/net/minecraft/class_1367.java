package net.minecraft;

public abstract class class_1367 extends class_1352 {
	private final class_1314 field_6516;
	public final double field_6514;
	protected int field_6518;
	protected int field_6517;
	private int field_6511;
	protected class_2338 field_6512 = class_2338.field_10980;
	private boolean field_6513;
	private final int field_6510;
	private final int field_6519;
	public int field_6515;

	public class_1367(class_1314 arg, double d, int i) {
		this(arg, d, i, 1);
	}

	public class_1367(class_1314 arg, double d, int i, int j) {
		this.field_6516 = arg;
		this.field_6514 = d;
		this.field_6510 = i;
		this.field_6515 = 0;
		this.field_6519 = j;
		this.method_6265(5);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6518 > 0) {
			this.field_6518--;
			return false;
		} else {
			this.field_6518 = this.method_6293(this.field_6516);
			return this.method_6292();
		}
	}

	protected int method_6293(class_1314 arg) {
		return 200 + arg.method_6051().nextInt(200);
	}

	@Override
	public boolean method_6266() {
		return this.field_6517 >= -this.field_6511 && this.field_6517 <= 1200 && this.method_6296(this.field_6516.field_6002, this.field_6512);
	}

	@Override
	public void method_6269() {
		this.method_6290();
		this.field_6517 = 0;
		this.field_6511 = this.field_6516.method_6051().nextInt(this.field_6516.method_6051().nextInt(1200) + 1200) + 1200;
	}

	protected void method_6290() {
		this.field_6516
			.method_5942()
			.method_6337(
				(double)((float)this.field_6512.method_10263()) + 0.5,
				(double)(this.field_6512.method_10264() + 1),
				(double)((float)this.field_6512.method_10260()) + 0.5,
				this.field_6514
			);
	}

	public double method_6291() {
		return 1.0;
	}

	@Override
	public void method_6268() {
		if (this.field_6516.method_5677(this.field_6512.method_10084()) > this.method_6291()) {
			this.field_6513 = false;
			this.field_6517++;
			if (this.method_6294()) {
				this.field_6516
					.method_5942()
					.method_6337(
						(double)((float)this.field_6512.method_10263()) + 0.5,
						(double)(this.field_6512.method_10264() + 1),
						(double)((float)this.field_6512.method_10260()) + 0.5,
						this.field_6514
					);
			}
		} else {
			this.field_6513 = true;
			this.field_6517--;
		}
	}

	public boolean method_6294() {
		return this.field_6517 % 40 == 0;
	}

	protected boolean method_6295() {
		return this.field_6513;
	}

	protected boolean method_6292() {
		int i = this.field_6510;
		int j = this.field_6519;
		class_2338 lv = new class_2338(this.field_6516);
		class_2338.class_2339 lv2 = new class_2338.class_2339();

		for (int k = this.field_6515; k <= j; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						lv2.method_10101(lv).method_10100(m, k - 1, n);
						if (this.field_6516.method_6146(lv2) && this.method_6296(this.field_6516.field_6002, lv2)) {
							this.field_6512 = lv2;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean method_6296(class_1941 arg, class_2338 arg2);
}
