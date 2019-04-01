package net.minecraft;

public class class_3719 extends class_2621 implements class_3000 {
	private class_2371<class_1799> field_16410 = class_2371.method_10213(27, class_1799.field_8037);
	private int field_17583;
	private int field_17585;

	private class_3719(class_2591<?> arg) {
		super(arg);
	}

	public class_3719() {
		this(class_2591.field_16411);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_11286(arg)) {
			class_1262.method_5426(arg, this.field_16410);
		}

		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_16410 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (!this.method_11283(arg)) {
			class_1262.method_5429(arg, this.field_16410);
		}
	}

	@Override
	public int method_5439() {
		return 27;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_16410) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		return this.field_16410.get(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		return class_1262.method_5430(this.field_16410, i, j);
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_16410, i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.field_16410.set(i, arg);
		if (arg.method_7947() > this.method_5444()) {
			arg.method_7939(this.method_5444());
		}
	}

	@Override
	public void method_5448() {
		this.field_16410.clear();
	}

	@Override
	protected class_2371<class_1799> method_11282() {
		return this.field_16410;
	}

	@Override
	protected void method_11281(class_2371<class_1799> arg) {
		this.field_16410 = arg;
	}

	@Override
	protected class_2561 method_17823() {
		return new class_2588("container.barrel");
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return class_1707.method_19245(i, arg, this);
	}

	@Override
	public void method_5435(class_1657 arg) {
		if (!arg.method_7325()) {
			if (this.field_17583 < 0) {
				this.field_17583 = 0;
			}

			this.field_17583++;
		}
	}

	@Override
	public void method_5432(class_1657 arg) {
		if (!arg.method_7325()) {
			this.field_17583--;
		}
	}

	@Override
	public void method_16896() {
		if (!this.field_11863.field_9236) {
			int i = this.field_11867.method_10263();
			int j = this.field_11867.method_10264();
			int k = this.field_11867.method_10260();
			this.field_17585++;
			this.field_17583 = class_2595.method_17765(this.field_11863, this, this.field_17585, i, j, k, this.field_17583);
			class_2680 lv = this.method_11010();
			boolean bl = (Boolean)lv.method_11654(class_3708.field_18006);
			boolean bl2 = this.field_17583 > 0;
			if (bl2 != bl) {
				this.method_17764(lv, bl2 ? class_3417.field_17604 : class_3417.field_17603);
				if (!bl2) {
					if (this.method_5442()) {
						this.field_11863.method_8437(null, (double)i, (double)j, (double)k, 2.0F, class_1927.class_4179.field_18685);
						this.field_11863.method_8652(this.method_11016(), class_2246.field_10124.method_9564(), 3);
					}
				} else {
					this.method_18318(lv, true);
				}
			}
		}
	}

	private void method_18318(class_2680 arg, boolean bl) {
		this.field_11863.method_8652(this.method_11016(), arg.method_11657(class_3708.field_18006, Boolean.valueOf(bl)), 3);
	}

	private void method_17764(class_2680 arg, class_3414 arg2) {
		class_2382 lv = ((class_2350)arg.method_11654(class_3708.field_16320)).method_10163();
		double d = (double)this.field_11867.method_10263() + 0.5 + (double)lv.method_10263() / 2.0;
		double e = (double)this.field_11867.method_10264() + 0.5 + (double)lv.method_10264() / 2.0;
		double f = (double)this.field_11867.method_10260() + 0.5 + (double)lv.method_10260() / 2.0;
		this.field_11863.method_8465(null, d, e, f, arg2, class_3419.field_15245, 0.5F, this.field_11863.field_9229.nextFloat() * 0.1F + 0.9F);
	}
}
