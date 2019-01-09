package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1707<T extends class_1707<T>> extends class_1703 {
	private final class_1263 field_7784;
	private final int field_7783;

	public class_1707(int i, class_1661 arg, int j) {
		this(i, arg, new class_1277(9 * j), j);
	}

	public class_1707(int i, class_1661 arg, class_1263 arg2, int j) {
		super(i);
		method_17359(arg2, j * 9);
		this.field_7784 = arg2;
		this.field_7783 = j;
		arg2.method_5435(arg.field_7546);
		int k = (this.field_7783 - 4) * 18;

		for (int l = 0; l < this.field_7783; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg2, m + l * 9, 8 + m * 18, 18 + l * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new class_1735(arg, l, 8 + l * 18, 161 + k));
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_7784.method_5443(arg);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i < this.field_7783 * 9) {
				if (!this.method_7616(lv3, this.field_7783 * 9, this.field_7761.size(), true)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 0, this.field_7783 * 9, false)) {
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
		this.field_7784.method_5432(arg);
	}

	public class_1263 method_7629() {
		return this.field_7784;
	}

	@Environment(EnvType.CLIENT)
	public int method_17388() {
		return this.field_7783;
	}

	public static class class_3911 extends class_1707<class_1707.class_3911> {
		public class_3911(int i, class_1661 arg) {
			super(i, arg, 6);
		}

		public class_3911(int i, class_1661 arg, class_1263 arg2) {
			super(i, arg, arg2, 6);
		}

		@Override
		public class_3917<class_1707.class_3911> method_17358() {
			return class_3917.field_17327;
		}
	}

	public static class class_3912 extends class_1707<class_1707.class_3912> {
		public class_3912(int i, class_1661 arg) {
			super(i, arg, 3);
		}

		public class_3912(int i, class_1661 arg, class_1263 arg2) {
			super(i, arg, arg2, 3);
		}

		@Override
		public class_3917<class_1707.class_3912> method_17358() {
			return class_3917.field_17326;
		}
	}
}
