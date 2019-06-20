package net.minecraft;

public class class_1716 extends class_1703 {
	private final class_1263 field_7806;

	public class_1716(int i, class_1661 arg) {
		this(i, arg, new class_1277(9));
	}

	public class_1716(int i, class_1661 arg, class_1263 arg2) {
		super(class_3917.field_17328, i);
		method_17359(arg2, 9);
		this.field_7806 = arg2;
		arg2.method_5435(arg.field_7546);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				this.method_7621(new class_1735(arg2, k + j * 3, 62 + k * 18, 17 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7806.method_5443(arg);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i < 9) {
				if (!this.method_7616(lv3, 9, 45, true)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 0, 9, false)) {
				return class_1799.field_8037;
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			} else {
				lv2.method_7668();
			}

			if (lv3.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			lv2.method_7667(arg, lv3);
		}

		return lv;
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7806.method_5432(arg);
	}
}
