package net.minecraft;

public class class_3719 extends class_2621 {
	private class_2371<class_1799> field_16410 = class_2371.method_10213(27, class_1799.field_8037);

	private class_3719(class_2591<?> arg) {
		super(arg);
	}

	public class_3719() {
		this(class_2591.field_16411);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		class_1262.method_5426(arg, this.field_16410);
		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_16410 = class_2371.method_10213(this.method_5439(), class_1799.field_8037);
		class_1262.method_5429(arg, this.field_16410);
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
	protected class_2561 method_5477() {
		return new class_2588("container.barrel");
	}

	@Override
	protected class_1703 method_5465(int i, class_1661 arg) {
		return new class_1707.class_3912(i, arg, this);
	}
}
