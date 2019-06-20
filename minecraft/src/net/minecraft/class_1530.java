package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public abstract class class_1530 extends class_1297 {
	protected static final Predicate<class_1297> field_7098 = arg -> arg instanceof class_1530;
	private int field_7097;
	protected class_2338 field_7100;
	protected class_2350 field_7099 = class_2350.field_11035;

	protected class_1530(class_1299<? extends class_1530> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	protected class_1530(class_1299<? extends class_1530> arg, class_1937 arg2, class_2338 arg3) {
		this(arg, arg2);
		this.field_7100 = arg3;
	}

	@Override
	protected void method_5693() {
	}

	protected void method_6892(class_2350 arg) {
		Validate.notNull(arg);
		Validate.isTrue(arg.method_10166().method_10179());
		this.field_7099 = arg;
		this.field_6031 = (float)(this.field_7099.method_10161() * 90);
		this.field_5982 = this.field_6031;
		this.method_6895();
	}

	protected void method_6895() {
		if (this.field_7099 != null) {
			double d = (double)this.field_7100.method_10263() + 0.5;
			double e = (double)this.field_7100.method_10264() + 0.5;
			double f = (double)this.field_7100.method_10260() + 0.5;
			double g = 0.46875;
			double h = this.method_6893(this.method_6897());
			double i = this.method_6893(this.method_6891());
			d -= (double)this.field_7099.method_10148() * 0.46875;
			f -= (double)this.field_7099.method_10165() * 0.46875;
			e += i;
			class_2350 lv = this.field_7099.method_10160();
			d += h * (double)lv.method_10148();
			f += h * (double)lv.method_10165();
			this.field_5987 = d;
			this.field_6010 = e;
			this.field_6035 = f;
			double j = (double)this.method_6897();
			double k = (double)this.method_6891();
			double l = (double)this.method_6897();
			if (this.field_7099.method_10166() == class_2350.class_2351.field_11051) {
				l = 1.0;
			} else {
				j = 1.0;
			}

			j /= 32.0;
			k /= 32.0;
			l /= 32.0;
			this.method_5857(new class_238(d - j, e - k, f - l, d + j, e + k, f + l));
		}
	}

	private double method_6893(int i) {
		return i % 32 == 0 ? 0.5 : 0.0;
	}

	@Override
	public void method_5773() {
		this.field_6014 = this.field_5987;
		this.field_6036 = this.field_6010;
		this.field_5969 = this.field_6035;
		if (this.field_7097++ == 100 && !this.field_6002.field_9236) {
			this.field_7097 = 0;
			if (!this.field_5988 && !this.method_6888()) {
				this.method_5650();
				this.method_6889(null);
			}
		}
	}

	public boolean method_6888() {
		if (!this.field_6002.method_17892(this)) {
			return false;
		} else {
			int i = Math.max(1, this.method_6897() / 16);
			int j = Math.max(1, this.method_6891() / 16);
			class_2338 lv = this.field_7100.method_10093(this.field_7099.method_10153());
			class_2350 lv2 = this.field_7099.method_10160();
			class_2338.class_2339 lv3 = new class_2338.class_2339();

			for (int k = 0; k < i; k++) {
				for (int l = 0; l < j; l++) {
					int m = (i - 1) / -2;
					int n = (j - 1) / -2;
					lv3.method_10101(lv).method_10104(lv2, k + m).method_10104(class_2350.field_11036, l + n);
					class_2680 lv4 = this.field_6002.method_8320(lv3);
					if (!lv4.method_11620().method_15799() && !class_2312.method_9999(lv4)) {
						return false;
					}
				}
			}

			return this.field_6002.method_8333(this, this.method_5829(), field_7098).isEmpty();
		}
	}

	@Override
	public boolean method_5863() {
		return true;
	}

	@Override
	public boolean method_5698(class_1297 arg) {
		return arg instanceof class_1657 ? this.method_5643(class_1282.method_5532((class_1657)arg), 0.0F) : false;
	}

	@Override
	public class_2350 method_5735() {
		return this.field_7099;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			if (!this.field_5988 && !this.field_6002.field_9236) {
				this.method_5650();
				this.method_5785();
				this.method_6889(arg.method_5529());
			}

			return true;
		}
	}

	@Override
	public void method_5784(class_1313 arg, class_243 arg2) {
		if (!this.field_6002.field_9236 && !this.field_5988 && arg2.method_1027() > 0.0) {
			this.method_5650();
			this.method_6889(null);
		}
	}

	@Override
	public void method_5762(double d, double e, double f) {
		if (!this.field_6002.field_9236 && !this.field_5988 && d * d + e * e + f * f > 0.0) {
			this.method_5650();
			this.method_6889(null);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10567("Facing", (byte)this.field_7099.method_10161());
		class_2338 lv = this.method_6896();
		arg.method_10569("TileX", lv.method_10263());
		arg.method_10569("TileY", lv.method_10264());
		arg.method_10569("TileZ", lv.method_10260());
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7100 = new class_2338(arg.method_10550("TileX"), arg.method_10550("TileY"), arg.method_10550("TileZ"));
		this.field_7099 = class_2350.method_10139(arg.method_10571("Facing"));
	}

	public abstract int method_6897();

	public abstract int method_6891();

	public abstract void method_6889(@Nullable class_1297 arg);

	public abstract void method_6894();

	@Override
	public class_1542 method_5699(class_1799 arg, float f) {
		class_1542 lv = new class_1542(
			this.field_6002,
			this.field_5987 + (double)((float)this.field_7099.method_10148() * 0.15F),
			this.field_6010 + (double)f,
			this.field_6035 + (double)((float)this.field_7099.method_10165() * 0.15F),
			arg
		);
		lv.method_6988();
		this.field_6002.method_8649(lv);
		return lv;
	}

	@Override
	protected boolean method_5638() {
		return false;
	}

	@Override
	public void method_5814(double d, double e, double f) {
		this.field_7100 = new class_2338(d, e, f);
		this.method_6895();
		this.field_6007 = true;
	}

	public class_2338 method_6896() {
		return this.field_7100;
	}

	@Override
	public float method_5832(class_2470 arg) {
		if (this.field_7099.method_10166() != class_2350.class_2351.field_11052) {
			switch (arg) {
				case field_11464:
					this.field_7099 = this.field_7099.method_10153();
					break;
				case field_11465:
					this.field_7099 = this.field_7099.method_10160();
					break;
				case field_11463:
					this.field_7099 = this.field_7099.method_10170();
			}
		}

		float f = class_3532.method_15393(this.field_6031);
		switch (arg) {
			case field_11464:
				return f + 180.0F;
			case field_11465:
				return f + 90.0F;
			case field_11463:
				return f + 270.0F;
			default:
				return f;
		}
	}

	@Override
	public float method_5763(class_2415 arg) {
		return this.method_5832(arg.method_10345(this.field_7099));
	}

	@Override
	public void method_5800(class_1538 arg) {
	}

	@Override
	public void method_18382() {
	}
}
