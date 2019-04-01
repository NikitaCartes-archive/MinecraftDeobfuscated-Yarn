package net.minecraft;

public class class_1258 implements class_1263 {
	private final class_1263 field_5769;
	private final class_1263 field_5771;

	public class_1258(class_1263 arg, class_1263 arg2) {
		if (arg == null) {
			arg = arg2;
		}

		if (arg2 == null) {
			arg2 = arg;
		}

		this.field_5769 = arg;
		this.field_5771 = arg2;
	}

	@Override
	public int method_5439() {
		return this.field_5769.method_5439() + this.field_5771.method_5439();
	}

	@Override
	public boolean method_5442() {
		return this.field_5769.method_5442() && this.field_5771.method_5442();
	}

	public boolean method_5405(class_1263 arg) {
		return this.field_5769 == arg || this.field_5771 == arg;
	}

	@Override
	public class_1799 method_5438(int i) {
		return i >= this.field_5769.method_5439() ? this.field_5771.method_5438(i - this.field_5769.method_5439()) : this.field_5769.method_5438(i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		return i >= this.field_5769.method_5439() ? this.field_5771.method_5434(i - this.field_5769.method_5439(), j) : this.field_5769.method_5434(i, j);
	}

	@Override
	public class_1799 method_5441(int i) {
		return i >= this.field_5769.method_5439() ? this.field_5771.method_5441(i - this.field_5769.method_5439()) : this.field_5769.method_5441(i);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		if (i >= this.field_5769.method_5439()) {
			this.field_5771.method_5447(i - this.field_5769.method_5439(), arg);
		} else {
			this.field_5769.method_5447(i, arg);
		}
	}

	@Override
	public int method_5444() {
		return this.field_5769.method_5444();
	}

	@Override
	public void method_5431() {
		this.field_5769.method_5431();
		this.field_5771.method_5431();
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_5769.method_5443(arg) && this.field_5771.method_5443(arg);
	}

	@Override
	public void method_5435(class_1657 arg) {
		this.field_5769.method_5435(arg);
		this.field_5771.method_5435(arg);
	}

	@Override
	public void method_5432(class_1657 arg) {
		this.field_5769.method_5432(arg);
		this.field_5771.method_5432(arg);
	}

	@Override
	public boolean method_5437(int i, class_1799 arg) {
		return i >= this.field_5769.method_5439() ? this.field_5771.method_5437(i - this.field_5769.method_5439(), arg) : this.field_5769.method_5437(i, arg);
	}

	@Override
	public void method_5448() {
		this.field_5769.method_5448();
		this.field_5771.method_5448();
	}
}
