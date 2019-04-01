package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1707 extends class_1703 {
	private final class_1263 field_17495;
	private final int field_17496;

	private class_1707(class_3917<?> arg, int i, class_1661 arg2, int j) {
		this(arg, i, arg2, new class_1277(9 * j), j);
	}

	public static class_1707 method_19244(int i, class_1661 arg) {
		return new class_1707(class_3917.field_18664, i, arg, 1);
	}

	public static class_1707 method_19246(int i, class_1661 arg) {
		return new class_1707(class_3917.field_18665, i, arg, 2);
	}

	public static class_1707 method_19248(int i, class_1661 arg) {
		return new class_1707(class_3917.field_17326, i, arg, 3);
	}

	public static class_1707 method_19249(int i, class_1661 arg) {
		return new class_1707(class_3917.field_18666, i, arg, 4);
	}

	public static class_1707 method_19250(int i, class_1661 arg) {
		return new class_1707(class_3917.field_18667, i, arg, 5);
	}

	public static class_1707 method_19251(int i, class_1661 arg) {
		return new class_1707(class_3917.field_17327, i, arg, 6);
	}

	public static class_1707 method_19245(int i, class_1661 arg, class_1263 arg2) {
		return new class_1707(class_3917.field_17326, i, arg, arg2, 3);
	}

	public static class_1707 method_19247(int i, class_1661 arg, class_1263 arg2) {
		return new class_1707(class_3917.field_17327, i, arg, arg2, 6);
	}

	public class_1707(class_3917<?> arg, int i, class_1661 arg2, class_1263 arg3, int j) {
		super(arg, i);
		method_17359(arg3, j * 9);
		this.field_17495 = arg3;
		this.field_17496 = j;
		arg3.method_5435(arg2.field_7546);
		int k = (this.field_17496 - 4) * 18;

		for (int l = 0; l < this.field_17496; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg3, m + l * 9, 8 + m * 18, 18 + l * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new class_1735(arg2, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new class_1735(arg2, l, 8 + l * 18, 161 + k));
		}
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_17495.method_5443(arg);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i < this.field_17496 * 9) {
				if (!this.method_7616(lv3, this.field_17496 * 9, this.field_7761.size(), true)) {
					return class_1799.field_8037;
				}
			} else if (!this.method_7616(lv3, 0, this.field_17496 * 9, false)) {
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
		this.field_17495.method_5432(arg);
	}

	public class_1263 method_7629() {
		return this.field_17495;
	}

	@Environment(EnvType.CLIENT)
	public int method_17388() {
		return this.field_17496;
	}
}
