package net.minecraft;

public class class_1715 implements class_1263, class_1737 {
	private final class_2371<class_1799> field_7805;
	private final int field_7804;
	private final int field_7803;
	private final class_1703 field_7802;

	public class_1715(class_1703 arg, int i, int j) {
		this.field_7805 = class_2371.method_10213(i * j, class_1799.field_8037);
		this.field_7802 = arg;
		this.field_7804 = i;
		this.field_7803 = j;
	}

	@Override
	public int method_5439() {
		return this.field_7805.size();
	}

	@Override
	public boolean method_5442() {
		for (class_1799 lv : this.field_7805) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public class_1799 method_5438(int i) {
		return i >= this.method_5439() ? class_1799.field_8037 : this.field_7805.get(i);
	}

	@Override
	public class_1799 method_5441(int i) {
		return class_1262.method_5428(this.field_7805, i);
	}

	@Override
	public class_1799 method_5434(int i, int j) {
		class_1799 lv = class_1262.method_5430(this.field_7805, i, j);
		if (!lv.method_7960()) {
			this.field_7802.method_7609(this);
		}

		return lv;
	}

	@Override
	public void method_5447(int i, class_1799 arg) {
		this.field_7805.set(i, arg);
		this.field_7802.method_7609(this);
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
		this.field_7805.clear();
	}

	public int method_17397() {
		return this.field_7803;
	}

	public int method_17398() {
		return this.field_7804;
	}

	@Override
	public void method_7683(class_1662 arg) {
		for (class_1799 lv : this.field_7805) {
			arg.method_7404(lv);
		}
	}
}
