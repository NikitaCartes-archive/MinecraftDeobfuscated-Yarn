package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1720<T extends class_1720<T>> extends class_1729<T> {
	private final class_1263 field_7824;
	private final class_3913 field_17286;
	protected final class_1937 field_7822;

	protected class_1720(int i, class_1661 arg) {
		this(i, arg, new class_1277(3), new class_3919(4));
	}

	protected class_1720(int i, class_1661 arg, class_1263 arg2, class_3913 arg3) {
		super(i);
		method_17359(arg2, 3);
		method_17361(arg3, 4);
		this.field_7824 = arg2;
		this.field_17286 = arg3;
		this.field_7822 = arg.field_7546.field_6002;
		this.method_7621(new class_1735(arg2, 0, 56, 17));
		this.method_7621(new class_1717(this, arg2, 1, 56, 53));
		this.method_7621(new class_1719(arg.field_7546, arg2, 2, 116, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}

		this.method_17360(arg3);
	}

	@Override
	public void method_7654(class_1662 arg) {
		if (this.field_7824 instanceof class_1737) {
			((class_1737)this.field_7824).method_7683(arg);
		}
	}

	@Override
	public void method_7657() {
		this.field_7824.method_5448();
	}

	@Override
	public boolean method_7652(class_1860 arg) {
		return arg.method_8115(this.field_7824, this.field_7822);
	}

	@Override
	public int method_7655() {
		return 2;
	}

	@Override
	public int method_7653() {
		return 1;
	}

	@Override
	public int method_7656() {
		return 1;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_7658() {
		return 3;
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7824.method_5443(arg);
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
			} else if (i != 1 && i != 0) {
				if (this.method_7640(lv3)) {
					if (!this.method_7616(lv3, 0, 1, false)) {
						return class_1799.field_8037;
					}
				} else if (this.method_16945(lv3)) {
					if (!this.method_7616(lv3, 1, 2, false)) {
						return class_1799.field_8037;
					}
				} else if (i >= 3 && i < 30) {
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

	protected abstract boolean method_7640(class_1799 arg);

	protected abstract boolean method_16945(class_1799 arg);

	@Environment(EnvType.CLIENT)
	public int method_17363() {
		int i = this.field_17286.method_17390(2);
		int j = this.field_17286.method_17390(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	@Environment(EnvType.CLIENT)
	public int method_17364() {
		int i = this.field_17286.method_17390(1);
		if (i == 0) {
			i = 200;
		}

		return this.field_17286.method_17390(0) * 13 / i;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17365() {
		return this.field_17286.method_17390(0) > 0;
	}
}
