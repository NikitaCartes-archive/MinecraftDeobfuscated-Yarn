package net.minecraft;

import java.util.Random;

public class class_2601 extends class_2621 {
	private static final Random field_11944 = new Random();
	private class_2371<class_1799> field_11945 = class_2371.method_10213(9, class_1799.field_8037);

	protected class_2601(class_2591<?> arg) {
		super(arg);
	}

	public class_2601() {
		this(class_2591.field_11887);
	}

	@Override
	public int method_5439() {
		return 9;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_11945) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	public int method_11076() {
		this.method_11289(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.field_11945.size(); k++) {
			if (!this.field_11945.get(k).method_7960() && field_11944.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	public int method_11075(class_1799 arg) {
		for (int i = 0; i < this.field_11945.size(); i++) {
			if (this.field_11945.get(i).method_7960()) {
				this.method_5447(i, arg);
				return i;
			}
		}

		return -1;
	}

	@Override
	protected class_2561 method_5477() {
		return new class_2588("container.dispenser");
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_11945 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		if (!this.method_11283(arg)) {
			class_1262.method_5429(arg, this.field_11945);
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_11286(arg)) {
			class_1262.method_5426(arg, this.field_11945);
		}

		return arg;
	}

	@Override
	protected class_2371<class_1799> method_11282() {
		return this.field_11945;
	}

	@Override
	protected void method_11281(class_2371<class_1799> arg) {
		this.field_11945 = arg;
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_1716(i, arg, this);
	}
}
