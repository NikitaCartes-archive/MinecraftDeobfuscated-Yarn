package net.minecraft;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1429 extends class_1296 {
	private int field_6745;
	private UUID field_6744;

	protected class_1429(class_1299<? extends class_1429> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5958() {
		if (this.method_5618() != 0) {
			this.field_6745 = 0;
		}

		super.method_5958();
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.method_5618() != 0) {
			this.field_6745 = 0;
		}

		if (this.field_6745 > 0) {
			this.field_6745--;
			if (this.field_6745 % 10 == 0) {
				double d = this.field_5974.nextGaussian() * 0.02;
				double e = this.field_5974.nextGaussian() * 0.02;
				double f = this.field_5974.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
						class_2398.field_11201,
						this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
						this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
						this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
						d,
						e,
						f
					);
			}
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			this.field_6745 = 0;
			return super.method_5643(arg, f);
		}
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return arg2.method_8320(arg.method_10074()).method_11614() == class_2246.field_10219 ? 10.0F : arg2.method_8610(arg) - 0.5F;
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("InLove", this.field_6745);
		if (this.field_6744 != null) {
			arg.method_10560("LoveCause", this.field_6744);
		}
	}

	@Override
	public double method_5678() {
		return 0.14;
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_6745 = arg.method_10550("InLove");
		this.field_6744 = arg.method_10576("LoveCause") ? arg.method_10584("LoveCause") : null;
	}

	public static boolean method_20663(class_1299<? extends class_1429> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		return arg2.method_8320(arg4.method_10074()).method_11614() == class_2246.field_10219 && arg2.method_8624(arg4, 0) > 8;
	}

	@Override
	public int method_5970() {
		return 120;
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Override
	protected int method_6110(class_1657 arg) {
		return 1 + this.field_6002.field_9229.nextInt(3);
	}

	public boolean method_6481(class_1799 arg) {
		return arg.method_7909() == class_1802.field_8861;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (this.method_6481(lv)) {
			if (this.method_5618() == 0 && this.method_6482()) {
				this.method_6475(arg, lv);
				this.method_6480(arg);
				return true;
			}

			if (this.method_6109()) {
				this.method_6475(arg, lv);
				this.method_5620((int)((float)(-this.method_5618() / 20) * 0.1F), true);
				return true;
			}
		}

		return super.method_5992(arg, arg2);
	}

	protected void method_6475(class_1657 arg, class_1799 arg2) {
		if (!arg.field_7503.field_7477) {
			arg2.method_7934(1);
		}
	}

	public boolean method_6482() {
		return this.field_6745 <= 0;
	}

	public void method_6480(@Nullable class_1657 arg) {
		this.field_6745 = 600;
		if (arg != null) {
			this.field_6744 = arg.method_5667();
		}

		this.field_6002.method_8421(this, (byte)18);
	}

	public void method_6476(int i) {
		this.field_6745 = i;
	}

	@Nullable
	public class_3222 method_6478() {
		if (this.field_6744 == null) {
			return null;
		} else {
			class_1657 lv = this.field_6002.method_18470(this.field_6744);
			return lv instanceof class_3222 ? (class_3222)lv : null;
		}
	}

	public boolean method_6479() {
		return this.field_6745 > 0;
	}

	public void method_6477() {
		this.field_6745 = 0;
	}

	public boolean method_6474(class_1429 arg) {
		if (arg == this) {
			return false;
		} else {
			return arg.getClass() != this.getClass() ? false : this.method_6479() && arg.method_6479();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 18) {
			for (int i = 0; i < 7; i++) {
				double d = this.field_5974.nextGaussian() * 0.02;
				double e = this.field_5974.nextGaussian() * 0.02;
				double f = this.field_5974.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
						class_2398.field_11201,
						this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
						this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
						this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
						d,
						e,
						f
					);
			}
		} else {
			super.method_5711(b);
		}
	}
}
