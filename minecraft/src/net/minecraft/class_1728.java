package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1728 extends class_1703 {
	private final class_1915 field_7863;
	private final class_1725 field_7861;

	public class_1728(int i, class_1661 arg) {
		this(i, arg, new class_1645(arg.field_7546));
	}

	public class_1728(int i, class_1661 arg, class_1915 arg2) {
		super(i);
		this.field_7863 = arg2;
		this.field_7861 = new class_1725(arg2);
		this.method_7621(new class_1735(this.field_7861, 0, 36, 53));
		this.method_7621(new class_1735(this.field_7861, 1, 62, 53));
		this.method_7621(new class_1727(arg.field_7546, arg2, this.field_7861, 2, 120, 53));

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
	public class_3917<class_1728> method_17358() {
		return class_3917.field_17340;
	}

	@Override
	public void method_7609(class_1263 arg) {
		this.field_7861.method_7645();
		super.method_7609(arg);
	}

	public void method_7650(int i) {
		this.field_7861.method_7643(i);
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7863.method_8257() == arg;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 2) {
				if (!this.method_7616(lv3, 3, 39, true)) {
					return class_1799.field_8037;
				}

				lv2.method_7670(lv3, lv);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 30) {
					if (!this.method_7616(lv3, 30, 39, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 30 && i < 39 && !this.method_7616(lv3, 3, 30, false)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 3, 39, false)) {
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
		this.field_7863.method_8259(null);
		if (!this.field_7863.method_8260().field_9236) {
			class_1799 lv = this.field_7861.method_5441(0);
			if (!lv.method_7960()) {
				arg.method_7328(lv, false);
			}

			lv = this.field_7861.method_5441(1);
			if (!lv.method_7960()) {
				arg.method_7328(lv, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_17437(class_1916 arg) {
		this.field_7863.method_8261(arg);
	}

	@Environment(EnvType.CLIENT)
	public class_1916 method_17438() {
		return this.field_7863.method_8264();
	}
}
