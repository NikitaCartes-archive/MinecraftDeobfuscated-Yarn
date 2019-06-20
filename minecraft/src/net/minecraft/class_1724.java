package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1724 extends class_1703 {
	private final class_1263 field_7836;
	private final class_1496 field_7837;

	public class_1724(int i, class_1661 arg, class_1263 arg2, class_1496 arg3) {
		super(null, i);
		this.field_7836 = arg2;
		this.field_7837 = arg3;
		int j = 3;
		arg2.method_5435(arg.field_7546);
		int k = -18;
		this.method_7621(new class_1735(arg2, 0, 8, 18) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() == class_1802.field_8175 && !this.method_7681() && arg3.method_6765();
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean method_7682() {
				return arg3.method_6765();
			}
		});
		this.method_7621(new class_1735(arg2, 1, 8, 36) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg3.method_6773(arg);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean method_7682() {
				return arg3.method_6735();
			}

			@Override
			public int method_7675() {
				return 1;
			}
		});
		if (arg3 instanceof class_1492 && ((class_1492)arg3).method_6703()) {
			for (int l = 0; l < 3; l++) {
				for (int m = 0; m < ((class_1492)arg3).method_6702(); m++) {
					this.method_7621(new class_1735(arg2, 2 + m + l * ((class_1492)arg3).method_6702(), 80 + m * 18, 18 + l * 18));
				}
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg, m + l * 9 + 9, 8 + m * 18, 102 + l * 18 + -18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new class_1735(arg, l, 8 + l * 18, 142));
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7836.method_5443(arg) && this.field_7837.method_5805() && this.field_7837.method_5739(arg) < 8.0F;
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i < this.field_7836.method_5439()) {
				if (!this.method_7616(lv3, this.field_7836.method_5439(), this.field_7761.size(), true)) {
					return class_1799.field_8037;
				}
			} else if (this.method_7611(1).method_7680(lv3) && !this.method_7611(1).method_7681()) {
				if (!this.method_7616(lv3, 1, 2, false)) {
					return class_1799.field_8037;
				}
			} else if (this.method_7611(0).method_7680(lv3)) {
				if (!this.method_7616(lv3, 0, 1, false)) {
					return class_1799.field_8037;
				}
			} else if (this.field_7836.method_5439() <= 2 || !this.method_7616(lv3, 2, this.field_7836.method_5439(), false)) {
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
		this.field_7836.method_5432(arg);
	}
}
