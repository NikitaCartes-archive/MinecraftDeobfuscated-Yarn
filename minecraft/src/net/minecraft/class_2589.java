package net.minecraft;

import java.util.Arrays;
import javax.annotation.Nullable;

public class class_2589 extends class_2624 implements class_1278, class_3000 {
	private static final int[] field_11886 = new int[]{3};
	private static final int[] field_11879 = new int[]{0, 1, 2, 3};
	private static final int[] field_11880 = new int[]{0, 1, 2, 4};
	private class_2371<class_1799> field_11882 = class_2371.method_10213(5, class_1799.field_8037);
	private int field_11878;
	private boolean[] field_11883;
	private class_1792 field_11881;
	private int field_11885;
	protected final class_3913 field_17381 = new class_3913() {
		@Override
		public int method_17390(int i) {
			switch (i) {
				case 0:
					return class_2589.this.field_11878;
				case 1:
					return class_2589.this.field_11885;
				default:
					return 0;
			}
		}

		@Override
		public void method_17391(int i, int j) {
			switch (i) {
				case 0:
					class_2589.this.field_11878 = j;
					break;
				case 1:
					class_2589.this.field_11885 = j;
			}
		}

		@Override
		public int method_17389() {
			return 2;
		}
	};

	public class_2589() {
		super(class_2591.field_11894);
	}

	@Override
	protected class_2561 method_17823() {
		return new class_2588("container.brewing");
	}

	@Override
	public int method_5439() {
		return this.field_11882.size();
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_11882) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void method_16896() {
		class_1799 lv = this.field_11882.get(4);
		if (this.field_11885 <= 0 && lv.method_7909() == class_1802.field_8183) {
			this.field_11885 = 20;
			lv.method_7934(1);
			this.method_5431();
		}

		boolean bl = this.method_11027();
		boolean bl2 = this.field_11878 > 0;
		class_1799 lv2 = this.field_11882.get(3);
		if (bl2) {
			this.field_11878--;
			boolean bl3 = this.field_11878 == 0;
			if (bl3 && bl) {
				this.method_11029();
				this.method_5431();
			} else if (!bl) {
				this.field_11878 = 0;
				this.method_5431();
			} else if (this.field_11881 != lv2.method_7909()) {
				this.field_11878 = 0;
				this.method_5431();
			}
		} else if (bl && this.field_11885 > 0) {
			this.field_11885--;
			this.field_11878 = 400;
			this.field_11881 = lv2.method_7909();
			this.method_5431();
		}

		if (!this.field_11863.field_9236) {
			boolean[] bls = this.method_11028();
			if (!Arrays.equals(bls, this.field_11883)) {
				this.field_11883 = bls;
				class_2680 lv3 = this.field_11863.method_8320(this.method_11016());
				if (!(lv3.method_11614() instanceof class_2260)) {
					return;
				}

				for (int i = 0; i < class_2260.field_10700.length; i++) {
					lv3 = lv3.method_11657(class_2260.field_10700[i], Boolean.valueOf(bls[i]));
				}

				this.field_11863.method_8652(this.field_11867, lv3, 2);
			}
		}
	}

	public boolean[] method_11028() {
		boolean[] bls = new boolean[3];

		for (int i = 0; i < 3; i++) {
			if (!this.field_11882.get(i).method_7960()) {
				bls[i] = true;
			}
		}

		return bls;
	}

	private boolean method_11027() {
		class_1799 lv = this.field_11882.get(3);
		if (lv.method_7960()) {
			return false;
		} else if (!class_1845.method_8077(lv)) {
			return false;
		} else {
			for (int i = 0; i < 3; i++) {
				class_1799 lv2 = this.field_11882.get(i);
				if (!lv2.method_7960() && class_1845.method_8072(lv2, lv)) {
					return true;
				}
			}

			return false;
		}
	}

	private void method_11029() {
		class_1799 lv = this.field_11882.get(3);

		for (int i = 0; i < 3; i++) {
			this.field_11882.set(i, class_1845.method_8078(lv, this.field_11882.get(i)));
		}

		lv.method_7934(1);
		class_2338 lv2 = this.method_11016();
		if (lv.method_7909().method_7857()) {
			class_1799 lv3 = new class_1799(lv.method_7909().method_7858());
			if (lv.method_7960()) {
				lv = lv3;
			} else if (!this.field_11863.field_9236) {
				class_1264.method_5449(this.field_11863, (double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260(), lv3);
			}
		}

		this.field_11882.set(3, lv);
		this.field_11863.method_20290(1035, lv2, 0);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11882 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		class_1262.method_5429(arg, this.field_11882);
		this.field_11878 = arg.method_10568("BrewTime");
		this.field_11885 = arg.method_10571("Fuel");
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10575("BrewTime", (short)this.field_11878);
		class_1262.method_5426(arg, this.field_11882);
		arg.method_10567("Fuel", (byte)this.field_11885);
		return arg;
	}

	@Override
	public class_1799 method_5438(int i) {
		return i >= 0 && i < this.field_11882.size() ? this.field_11882.get(i) : class_1799.field_8037;
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		return class_1262.method_5430(this.field_11882, i, j);
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_11882, i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		if (i >= 0 && i < this.field_11882.size()) {
			this.field_11882.set(i, arg);
		}
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_11863.method_8321(this.field_11867) != this
			? false
			: !(
				arg.method_5649((double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5)
					> 64.0
			);
	}

	@Override
	public boolean method_5437(int i, class_1799 arg) {
		if (i == 3) {
			return class_1845.method_8077(arg);
		} else {
			class_1792 lv = arg.method_7909();
			return i == 4
				? lv == class_1802.field_8183
				: (lv == class_1802.field_8574 || lv == class_1802.field_8436 || lv == class_1802.field_8150 || lv == class_1802.field_8469)
					&& this.method_5438(i).method_7960();
		}
	}

	@Override
	public int[] method_5494(class_2350 arg) {
		if (arg == class_2350.field_11036) {
			return field_11886;
		} else {
			return arg == class_2350.field_11033 ? field_11879 : field_11880;
		}
	}

	@Override
	public boolean method_5492(int i, class_1799 arg, @Nullable class_2350 arg2) {
		return this.method_5437(i, arg);
	}

	@Override
	public boolean method_5493(int i, class_1799 arg, class_2350 arg2) {
		return i == 3 ? arg.method_7909() == class_1802.field_8469 : true;
	}

	@Override
	public void method_5448() {
		this.field_11882.clear();
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_1708(i, arg, this, this.field_17381);
	}
}
