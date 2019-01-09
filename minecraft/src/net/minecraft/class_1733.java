package net.minecraft;

public class class_1733 extends class_1703 {
	private final class_1263 field_7867;

	public class_1733(int i, class_1661 arg) {
		this(i, arg, new class_1277(27));
	}

	public class_1733(int i, class_1661 arg, class_1263 arg2) {
		super(i);
		method_17359(arg2, 27);
		this.field_7867 = arg2;
		arg2.method_5435(arg.field_7546);
		int j = 3;
		int k = 9;

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1736(arg2, m + l * 9, 8 + m * 18, 18 + l * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg, m + l * 9 + 9, 8 + m * 18, 84 + l * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new class_1735(arg, l, 8 + l * 18, 142));
		}
	}

	@Override
	public class_3917<class_1733> method_17358() {
		return class_3917.field_17341;
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7867.method_5443(arg);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i < this.field_7867.method_5439()) {
				if (!this.method_7616(lv3, this.field_7867.method_5439(), this.field_7761.size(), true)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 0, this.field_7867.method_5439(), false)) {
				return class_1799.field_8037;
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			} else {
				lv2.method_7668();
			}
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7867.method_5432(arg);
	}
}
