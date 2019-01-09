package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1408 {
	protected final class_1308 field_6684;
	protected final class_1937 field_6677;
	@Nullable
	protected class_11 field_6681;
	protected double field_6668;
	private final class_1324 field_6671;
	protected int field_6675;
	protected int field_6674;
	protected class_243 field_6672 = class_243.field_1353;
	protected class_243 field_6680 = class_243.field_1353;
	protected long field_6670;
	protected long field_6669;
	protected double field_6682;
	protected float field_6683 = 0.5F;
	protected boolean field_6679;
	protected long field_6685;
	protected class_8 field_6678;
	private class_2338 field_6676;
	private class_13 field_6673;

	public class_1408(class_1308 arg, class_1937 arg2) {
		this.field_6684 = arg;
		this.field_6677 = arg2;
		this.field_6671 = arg.method_5996(class_1612.field_7365);
		this.field_6673 = this.method_6336();
	}

	public class_2338 method_6355() {
		return this.field_6676;
	}

	protected abstract class_13 method_6336();

	public void method_6344(double d) {
		this.field_6668 = d;
	}

	public float method_6338() {
		return (float)this.field_6671.method_6194();
	}

	public boolean method_6343() {
		return this.field_6679;
	}

	public void method_6356() {
		if (this.field_6677.method_8510() - this.field_6685 > 20L) {
			if (this.field_6676 != null) {
				this.field_6681 = null;
				this.field_6681 = this.method_6348(this.field_6676);
				this.field_6685 = this.field_6677.method_8510();
				this.field_6679 = false;
			}
		} else {
			this.field_6679 = true;
		}
	}

	@Nullable
	public final class_11 method_6352(double d, double e, double f) {
		return this.method_6348(new class_2338(d, e, f));
	}

	@Nullable
	public class_11 method_6348(class_2338 arg) {
		if (!this.method_6358()) {
			return null;
		} else if (this.field_6681 != null && !this.field_6681.method_46() && arg.equals(this.field_6676)) {
			return this.field_6681;
		} else {
			this.field_6676 = arg;
			float f = this.method_6338();
			this.field_6677.method_16107().method_15396("pathfind");
			class_2338 lv = new class_2338(this.field_6684);
			int i = (int)(f + 8.0F);
			class_1922 lv2 = new class_1950(this.field_6677, lv.method_10069(-i, -i, -i), lv.method_10069(i, i, i), 0);
			class_11 lv3 = this.field_6673.method_53(lv2, this.field_6684, this.field_6676, f);
			this.field_6677.method_16107().method_15407();
			return lv3;
		}
	}

	@Nullable
	public class_11 method_6349(class_1297 arg) {
		if (!this.method_6358()) {
			return null;
		} else {
			class_2338 lv = new class_2338(arg);
			if (this.field_6681 != null && !this.field_6681.method_46() && lv.equals(this.field_6676)) {
				return this.field_6681;
			} else {
				this.field_6676 = lv;
				float f = this.method_6338();
				this.field_6677.method_16107().method_15396("pathfind");
				class_2338 lv2 = new class_2338(this.field_6684).method_10084();
				int i = (int)(f + 16.0F);
				class_1922 lv3 = new class_1950(this.field_6677, lv2.method_10069(-i, -i, -i), lv2.method_10069(i, i, i), 0);
				class_11 lv4 = this.field_6673.method_56(lv3, this.field_6684, arg, f);
				this.field_6677.method_16107().method_15407();
				return lv4;
			}
		}
	}

	public boolean method_6337(double d, double e, double f, double g) {
		return this.method_6334(this.method_6352(d, e, f), g);
	}

	public boolean method_6335(class_1297 arg, double d) {
		class_11 lv = this.method_6349(arg);
		return lv != null && this.method_6334(lv, d);
	}

	public boolean method_6334(@Nullable class_11 arg, double d) {
		if (arg == null) {
			this.field_6681 = null;
			return false;
		} else {
			if (!arg.method_41(this.field_6681)) {
				this.field_6681 = arg;
			}

			this.method_6359();
			if (this.field_6681.method_38() <= 0) {
				return false;
			} else {
				this.field_6668 = d;
				class_243 lv = this.method_6347();
				this.field_6674 = this.field_6675;
				this.field_6672 = lv;
				return true;
			}
		}
	}

	@Nullable
	public class_11 method_6345() {
		return this.field_6681;
	}

	public void method_6360() {
		this.field_6675++;
		if (this.field_6679) {
			this.method_6356();
		}

		if (!this.method_6357()) {
			if (this.method_6358()) {
				this.method_6339();
			} else if (this.field_6681 != null && this.field_6681.method_39() < this.field_6681.method_38()) {
				class_243 lv = this.method_6347();
				class_243 lv2 = this.field_6681.method_47(this.field_6684, this.field_6681.method_39());
				if (lv.field_1351 > lv2.field_1351
					&& !this.field_6684.field_5952
					&& class_3532.method_15357(lv.field_1352) == class_3532.method_15357(lv2.field_1352)
					&& class_3532.method_15357(lv.field_1350) == class_3532.method_15357(lv2.field_1350)) {
					this.field_6681.method_42(this.field_6681.method_39() + 1);
				}
			}

			this.method_6353();
			if (!this.method_6357()) {
				class_243 lv = this.field_6681.method_49(this.field_6684);
				class_2338 lv3 = new class_2338(lv);
				this.field_6684
					.method_5962()
					.method_6239(
						lv.field_1352,
						this.field_6677.method_8320(lv3.method_10074()).method_11588() ? lv.field_1351 : class_14.method_60(this.field_6677, lv3),
						lv.field_1350,
						this.field_6668
					);
			}
		}
	}

	protected void method_6353() {
	}

	protected void method_6339() {
		class_243 lv = this.method_6347();
		int i = this.field_6681.method_38();

		for (int j = this.field_6681.method_39(); j < this.field_6681.method_38(); j++) {
			if ((double)this.field_6681.method_40(j).field_39 != Math.floor(lv.field_1351)) {
				i = j;
				break;
			}
		}

		this.field_6683 = this.field_6684.field_5998 > 0.75F ? this.field_6684.field_5998 / 2.0F : 0.75F - this.field_6684.field_5998 / 2.0F;
		class_243 lv2 = this.field_6681.method_35();
		if (class_3532.method_15379((float)(this.field_6684.field_5987 - (lv2.field_1352 + 0.5))) < this.field_6683
			&& class_3532.method_15379((float)(this.field_6684.field_6035 - (lv2.field_1350 + 0.5))) < this.field_6683
			&& Math.abs(this.field_6684.field_6010 - lv2.field_1351) < 1.0) {
			this.field_6681.method_42(this.field_6681.method_39() + 1);
		}

		int k = class_3532.method_15386(this.field_6684.field_5998);
		int l = class_3532.method_15386(this.field_6684.field_6019);
		int m = k;

		for (int n = i - 1; n >= this.field_6681.method_39(); n--) {
			if (this.method_6341(lv, this.field_6681.method_47(this.field_6684, n), k, l, m)) {
				this.field_6681.method_42(n);
				break;
			}
		}

		this.method_6346(lv);
	}

	protected void method_6346(class_243 arg) {
		if (this.field_6675 - this.field_6674 > 100) {
			if (arg.method_1025(this.field_6672) < 2.25) {
				this.method_6340();
			}

			this.field_6674 = this.field_6675;
			this.field_6672 = arg;
		}

		if (this.field_6681 != null && !this.field_6681.method_46()) {
			class_243 lv = this.field_6681.method_35();
			if (lv.equals(this.field_6680)) {
				this.field_6670 = this.field_6670 + (class_156.method_658() - this.field_6669);
			} else {
				this.field_6680 = lv;
				double d = arg.method_1022(this.field_6680);
				this.field_6682 = this.field_6684.method_6029() > 0.0F ? d / (double)this.field_6684.method_6029() * 1000.0 : 0.0;
			}

			if (this.field_6682 > 0.0 && (double)this.field_6670 > this.field_6682 * 3.0) {
				this.field_6680 = class_243.field_1353;
				this.field_6670 = 0L;
				this.field_6682 = 0.0;
				this.method_6340();
			}

			this.field_6669 = class_156.method_658();
		}
	}

	public boolean method_6357() {
		return this.field_6681 == null || this.field_6681.method_46();
	}

	public void method_6340() {
		this.field_6681 = null;
	}

	protected abstract class_243 method_6347();

	protected abstract boolean method_6358();

	protected boolean method_6351() {
		return this.field_6684.method_5816() || this.field_6684.method_5771();
	}

	protected void method_6359() {
		if (this.field_6681 != null) {
			for (int i = 0; i < this.field_6681.method_38(); i++) {
				class_9 lv = this.field_6681.method_40(i);
				class_9 lv2 = i + 1 < this.field_6681.method_38() ? this.field_6681.method_40(i + 1) : null;
				class_2680 lv3 = this.field_6677.method_8320(new class_2338(lv.field_40, lv.field_39, lv.field_38));
				class_2248 lv4 = lv3.method_11614();
				if (lv4 == class_2246.field_10593) {
					this.field_6681.method_33(i, lv.method_26(lv.field_40, lv.field_39 + 1, lv.field_38));
					if (lv2 != null && lv.field_39 >= lv2.field_39) {
						this.field_6681.method_33(i + 1, lv2.method_26(lv2.field_40, lv.field_39 + 1, lv2.field_38));
					}
				}
			}
		}
	}

	protected abstract boolean method_6341(class_243 arg, class_243 arg2, int i, int j, int k);

	public boolean method_6333(class_2338 arg) {
		class_2338 lv = arg.method_10074();
		return this.field_6677.method_8320(lv).method_11598(this.field_6677, lv);
	}

	public class_8 method_6342() {
		return this.field_6678;
	}

	public void method_6354(boolean bl) {
		this.field_6678.method_14(bl);
	}

	public boolean method_6350() {
		return this.field_6678.method_22();
	}
}
