package net.minecraft;

import javax.annotation.Nullable;

public class class_1731 implements class_1263, class_1732 {
	private final class_2371<class_1799> field_7866 = class_2371.method_10213(1, class_1799.field_8037);
	private class_1860<?> field_7865;

	@Override
	public int method_5439() {
		return 1;
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_7866) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		return this.field_7866.get(0);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		return class_1262.method_5428(this.field_7866, 0);
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_7866, 0);
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.field_7866.set(0, arg);
	}

	@Override
	public void method_5431() {
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return true;
	}

	@Override
	public void method_5448() {
		this.field_7866.clear();
	}

	@Override
	public void method_7662(@Nullable class_1860<?> arg) {
		this.field_7865 = arg;
	}

	@Nullable
	@Override
	public class_1860<?> method_7663() {
		return this.field_7865;
	}
}
