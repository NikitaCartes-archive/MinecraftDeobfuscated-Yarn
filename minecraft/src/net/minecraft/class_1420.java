package net.minecraft;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;

public class class_1420 extends class_1421 {
	private static final class_2940<Byte> field_6728 = class_2945.method_12791(class_1420.class, class_2943.field_13319);
	private class_2338 field_6729;

	public class_1420(class_1937 arg) {
		super(class_1299.field_6108, arg);
		this.method_5835(0.5F, 0.9F);
		this.method_6449(true);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6728, (byte)0);
	}

	@Override
	protected float method_6107() {
		return 0.1F;
	}

	@Override
	protected float method_6017() {
		return super.method_6017() * 0.95F;
	}

	@Nullable
	@Override
	public class_3414 method_5994() {
		return this.method_6450() && this.field_5974.nextInt(4) != 0 ? null : class_3417.field_15009;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14746;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14911;
	}

	@Override
	public boolean method_5810() {
		return false;
	}

	@Override
	protected void method_6087(class_1297 arg) {
	}

	@Override
	protected void method_6070() {
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(6.0);
	}

	public boolean method_6450() {
		return (this.field_6011.method_12789(field_6728) & 1) != 0;
	}

	public void method_6449(boolean bl) {
		byte b = this.field_6011.method_12789(field_6728);
		if (bl) {
			this.field_6011.method_12778(field_6728, (byte)(b | 1));
		} else {
			this.field_6011.method_12778(field_6728, (byte)(b & -2));
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.method_6450()) {
			this.field_5967 = 0.0;
			this.field_5984 = 0.0;
			this.field_6006 = 0.0;
			this.field_6010 = (double)class_3532.method_15357(this.field_6010) + 1.0 - (double)this.field_6019;
		} else {
			this.field_5984 *= 0.6F;
		}
	}

	@Override
	protected void method_5958() {
		super.method_5958();
		class_2338 lv = new class_2338(this);
		class_2338 lv2 = lv.method_10084();
		if (this.method_6450()) {
			if (this.field_6002.method_8320(lv2).method_11621(this.field_6002, lv)) {
				if (this.field_5974.nextInt(200) == 0) {
					this.field_6241 = (float)this.field_5974.nextInt(360);
				}

				if (this.field_6002.method_8613(this, 4.0) != null) {
					this.method_6449(false);
					this.field_6002.method_8444(null, 1025, lv, 0);
				}
			} else {
				this.method_6449(false);
				this.field_6002.method_8444(null, 1025, lv, 0);
			}
		} else {
			if (this.field_6729 != null && (!this.field_6002.method_8623(this.field_6729) || this.field_6729.method_10264() < 1)) {
				this.field_6729 = null;
			}

			if (this.field_6729 == null
				|| this.field_5974.nextInt(30) == 0
				|| this.field_6729.method_10261((double)((int)this.field_5987), (double)((int)this.field_6010), (double)((int)this.field_6035)) < 4.0) {
				this.field_6729 = new class_2338(
					(int)this.field_5987 + this.field_5974.nextInt(7) - this.field_5974.nextInt(7),
					(int)this.field_6010 + this.field_5974.nextInt(6) - 2,
					(int)this.field_6035 + this.field_5974.nextInt(7) - this.field_5974.nextInt(7)
				);
			}

			double d = (double)this.field_6729.method_10263() + 0.5 - this.field_5987;
			double e = (double)this.field_6729.method_10264() + 0.1 - this.field_6010;
			double f = (double)this.field_6729.method_10260() + 0.5 - this.field_6035;
			this.field_5967 = this.field_5967 + (Math.signum(d) * 0.5 - this.field_5967) * 0.1F;
			this.field_5984 = this.field_5984 + (Math.signum(e) * 0.7F - this.field_5984) * 0.1F;
			this.field_6006 = this.field_6006 + (Math.signum(f) * 0.5 - this.field_6006) * 0.1F;
			float g = (float)(class_3532.method_15349(this.field_6006, this.field_5967) * 180.0F / (float)Math.PI) - 90.0F;
			float h = class_3532.method_15393(g - this.field_6031);
			this.field_6250 = 0.5F;
			this.field_6031 += h;
			if (this.field_5974.nextInt(100) == 0 && this.field_6002.method_8320(lv2).method_11621(this.field_6002, lv2)) {
				this.method_6449(true);
			}
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
	}

	@Override
	public boolean method_5696() {
		return true;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			if (!this.field_6002.field_9236 && this.method_6450()) {
				this.method_6449(false);
			}

			return super.method_5643(arg, f);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_6011.method_12778(field_6728, arg.method_10571("BatFlags"));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10567("BatFlags", this.field_6011.method_12789(field_6728));
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		class_2338 lv = new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035);
		if (lv.method_10264() >= arg.method_8615()) {
			return false;
		} else {
			int i = arg.method_8602(lv);
			int j = 4;
			if (this.method_6451()) {
				j = 7;
			} else if (this.field_5974.nextBoolean()) {
				return false;
			}

			return i > this.field_5974.nextInt(j) ? false : super.method_5979(arg, arg2);
		}
	}

	private boolean method_6451() {
		LocalDate localDate = LocalDate.now();
		int i = localDate.get(ChronoField.DAY_OF_MONTH);
		int j = localDate.get(ChronoField.MONTH_OF_YEAR);
		return j == 10 && i >= 20 || j == 11 && i <= 3;
	}

	@Override
	public float method_5751() {
		return this.field_6019 / 2.0F;
	}
}
