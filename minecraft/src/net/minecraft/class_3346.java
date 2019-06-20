package net.minecraft;

import java.util.Random;

public class class_3346 extends class_3418 {
	private final boolean[] field_14397 = new boolean[4];

	public class_3346(Random random, int i, int j) {
		super(class_3773.field_16933, random, i, 64, j, 21, 15, 21);
	}

	public class_3346(class_3485 arg, class_2487 arg2) {
		super(class_3773.field_16933, arg2);
		this.field_14397[0] = arg2.method_10577("hasPlacedChest0");
		this.field_14397[1] = arg2.method_10577("hasPlacedChest1");
		this.field_14397[2] = arg2.method_10577("hasPlacedChest2");
		this.field_14397[3] = arg2.method_10577("hasPlacedChest3");
	}

	@Override
	protected void method_14943(class_2487 arg) {
		super.method_14943(arg);
		arg.method_10556("hasPlacedChest0", this.field_14397[0]);
		arg.method_10556("hasPlacedChest1", this.field_14397[1]);
		arg.method_10556("hasPlacedChest2", this.field_14397[2]);
		arg.method_10556("hasPlacedChest3", this.field_14397[3]);
	}

	@Override
	public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		this.method_14940(
			arg, arg2, 0, -4, 0, this.field_15244 - 1, 0, this.field_15242 - 1, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);

		for (int i = 1; i <= 9; i++) {
			this.method_14940(
				arg, arg2, i, i, i, this.field_15244 - 1 - i, i, this.field_15242 - 1 - i, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
			);
			this.method_14940(
				arg,
				arg2,
				i + 1,
				i,
				i + 1,
				this.field_15244 - 2 - i,
				i,
				this.field_15242 - 2 - i,
				class_2246.field_10124.method_9564(),
				class_2246.field_10124.method_9564(),
				false
			);
		}

		for (int i = 0; i < this.field_15244; i++) {
			for (int j = 0; j < this.field_15242; j++) {
				int k = -5;
				this.method_14936(arg, class_2246.field_9979.method_9564(), i, -5, j, arg2);
			}
		}

		class_2680 lv = class_2246.field_10142.method_9564().method_11657(class_2510.field_11571, class_2350.field_11043);
		class_2680 lv2 = class_2246.field_10142.method_9564().method_11657(class_2510.field_11571, class_2350.field_11035);
		class_2680 lv3 = class_2246.field_10142.method_9564().method_11657(class_2510.field_11571, class_2350.field_11034);
		class_2680 lv4 = class_2246.field_10142.method_9564().method_11657(class_2510.field_11571, class_2350.field_11039);
		this.method_14940(arg, arg2, 0, 0, 0, 4, 9, 4, class_2246.field_9979.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 1, 10, 1, 3, 10, 3, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14917(arg, lv, 2, 10, 0, arg2);
		this.method_14917(arg, lv2, 2, 10, 4, arg2);
		this.method_14917(arg, lv3, 0, 10, 2, arg2);
		this.method_14917(arg, lv4, 4, 10, 2, arg2);
		this.method_14940(
			arg, arg2, this.field_15244 - 5, 0, 0, this.field_15244 - 1, 9, 4, class_2246.field_9979.method_9564(), class_2246.field_10124.method_9564(), false
		);
		this.method_14940(
			arg, arg2, this.field_15244 - 4, 10, 1, this.field_15244 - 2, 10, 3, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14917(arg, lv, this.field_15244 - 3, 10, 0, arg2);
		this.method_14917(arg, lv2, this.field_15244 - 3, 10, 4, arg2);
		this.method_14917(arg, lv3, this.field_15244 - 5, 10, 2, arg2);
		this.method_14917(arg, lv4, this.field_15244 - 1, 10, 2, arg2);
		this.method_14940(arg, arg2, 8, 0, 0, 12, 4, 4, class_2246.field_9979.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 9, 1, 0, 11, 3, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 9, 1, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 9, 2, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 9, 3, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 10, 3, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 11, 3, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 11, 2, 1, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 11, 1, 1, arg2);
		this.method_14940(arg, arg2, 4, 1, 1, 8, 3, 3, class_2246.field_9979.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 4, 1, 2, 8, 2, 2, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 12, 1, 1, 16, 3, 3, class_2246.field_9979.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 12, 1, 2, 16, 2, 2, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(
			arg, arg2, 5, 4, 5, this.field_15244 - 6, 4, this.field_15242 - 6, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14940(arg, arg2, 9, 4, 9, 11, 4, 11, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(arg, arg2, 8, 1, 8, 8, 3, 8, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 12, 1, 8, 12, 3, 8, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 8, 1, 12, 8, 3, 12, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 12, 1, 12, 12, 3, 12, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 1, 1, 5, 4, 4, 11, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 5, 1, 5, this.field_15244 - 2, 4, 11, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14940(arg, arg2, 6, 7, 9, 6, 7, 11, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 7, 7, 9, this.field_15244 - 7, 7, 11, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14940(arg, arg2, 5, 5, 9, 5, 7, 11, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 6, 5, 9, this.field_15244 - 6, 7, 11, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false
		);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 5, 5, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 5, 6, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 6, 6, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), this.field_15244 - 6, 5, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), this.field_15244 - 6, 6, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), this.field_15244 - 7, 6, 10, arg2);
		this.method_14940(arg, arg2, 2, 4, 4, 2, 6, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 3, 4, 4, this.field_15244 - 3, 6, 4, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false
		);
		this.method_14917(arg, lv, 2, 4, 5, arg2);
		this.method_14917(arg, lv, 2, 3, 4, arg2);
		this.method_14917(arg, lv, this.field_15244 - 3, 4, 5, arg2);
		this.method_14917(arg, lv, this.field_15244 - 3, 3, 4, arg2);
		this.method_14940(arg, arg2, 1, 1, 3, 2, 2, 3, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 3, 1, 3, this.field_15244 - 2, 2, 3, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14917(arg, class_2246.field_9979.method_9564(), 1, 1, 2, arg2);
		this.method_14917(arg, class_2246.field_9979.method_9564(), this.field_15244 - 2, 1, 2, arg2);
		this.method_14917(arg, class_2246.field_10007.method_9564(), 1, 2, 2, arg2);
		this.method_14917(arg, class_2246.field_10007.method_9564(), this.field_15244 - 2, 2, 2, arg2);
		this.method_14917(arg, lv4, 2, 1, 2, arg2);
		this.method_14917(arg, lv3, this.field_15244 - 3, 1, 2, arg2);
		this.method_14940(arg, arg2, 4, 3, 5, 4, 3, 17, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 5, 3, 5, this.field_15244 - 5, 3, 17, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false
		);
		this.method_14940(arg, arg2, 3, 1, 5, 4, 2, 16, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14940(
			arg, arg2, this.field_15244 - 6, 1, 5, this.field_15244 - 5, 2, 16, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false
		);

		for (int l = 5; l <= 17; l += 2) {
			this.method_14917(arg, class_2246.field_10361.method_9564(), 4, 1, l, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), 4, 2, l, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), this.field_15244 - 5, 1, l, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), this.field_15244 - 5, 2, l, arg2);
		}

		this.method_14917(arg, class_2246.field_10184.method_9564(), 10, 0, 7, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 10, 0, 8, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 9, 0, 9, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 11, 0, 9, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 8, 0, 10, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 12, 0, 10, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 7, 0, 10, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 13, 0, 10, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 9, 0, 11, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 11, 0, 11, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 10, 0, 12, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 10, 0, 13, arg2);
		this.method_14917(arg, class_2246.field_10409.method_9564(), 10, 0, 10, arg2);

		for (int l = 0; l <= this.field_15244 - 1; l += this.field_15244 - 1) {
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 2, 1, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 2, 2, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 2, 3, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 3, 1, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 3, 2, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 3, 3, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 4, 1, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), l, 4, 2, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 4, 3, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 5, 1, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 5, 2, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 5, 3, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 6, 1, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), l, 6, 2, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 6, 3, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 7, 1, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 7, 2, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 7, 3, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 8, 1, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 8, 2, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 8, 3, arg2);
		}

		for (int l = 2; l <= this.field_15244 - 3; l += this.field_15244 - 3 - 2) {
			this.method_14917(arg, class_2246.field_10361.method_9564(), l - 1, 2, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 2, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l + 1, 2, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l - 1, 3, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 3, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l + 1, 3, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l - 1, 4, 0, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), l, 4, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l + 1, 4, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l - 1, 5, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 5, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l + 1, 5, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l - 1, 6, 0, arg2);
			this.method_14917(arg, class_2246.field_10292.method_9564(), l, 6, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l + 1, 6, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l - 1, 7, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l, 7, 0, arg2);
			this.method_14917(arg, class_2246.field_10184.method_9564(), l + 1, 7, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l - 1, 8, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l, 8, 0, arg2);
			this.method_14917(arg, class_2246.field_10361.method_9564(), l + 1, 8, 0, arg2);
		}

		this.method_14940(arg, arg2, 8, 4, 0, 12, 6, 0, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 8, 6, 0, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 12, 6, 0, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 9, 5, 0, arg2);
		this.method_14917(arg, class_2246.field_10292.method_9564(), 10, 5, 0, arg2);
		this.method_14917(arg, class_2246.field_10184.method_9564(), 11, 5, 0, arg2);
		this.method_14940(arg, arg2, 8, -14, 8, 12, -11, 12, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 8, -10, 8, 12, -10, 12, class_2246.field_10292.method_9564(), class_2246.field_10292.method_9564(), false);
		this.method_14940(arg, arg2, 8, -9, 8, 12, -9, 12, class_2246.field_10361.method_9564(), class_2246.field_10361.method_9564(), false);
		this.method_14940(arg, arg2, 8, -8, 8, 12, -1, 12, class_2246.field_9979.method_9564(), class_2246.field_9979.method_9564(), false);
		this.method_14940(arg, arg2, 9, -11, 9, 11, -1, 11, class_2246.field_10124.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14917(arg, class_2246.field_10158.method_9564(), 10, -11, 10, arg2);
		this.method_14940(arg, arg2, 9, -13, 9, 11, -13, 11, class_2246.field_10375.method_9564(), class_2246.field_10124.method_9564(), false);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 8, -11, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 8, -10, 10, arg2);
		this.method_14917(arg, class_2246.field_10292.method_9564(), 7, -10, 10, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 7, -11, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 12, -11, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 12, -10, 10, arg2);
		this.method_14917(arg, class_2246.field_10292.method_9564(), 13, -10, 10, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 13, -11, 10, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 10, -11, 8, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 10, -10, 8, arg2);
		this.method_14917(arg, class_2246.field_10292.method_9564(), 10, -10, 7, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 10, -11, 7, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 10, -11, 12, arg2);
		this.method_14917(arg, class_2246.field_10124.method_9564(), 10, -10, 12, arg2);
		this.method_14917(arg, class_2246.field_10292.method_9564(), 10, -10, 13, arg2);
		this.method_14917(arg, class_2246.field_10361.method_9564(), 10, -11, 13, arg2);

		for (class_2350 lv5 : class_2350.class_2353.field_11062) {
			if (!this.field_14397[lv5.method_10161()]) {
				int m = lv5.method_10148() * 2;
				int n = lv5.method_10165() * 2;
				this.field_14397[lv5.method_10161()] = this.method_14915(arg, arg2, random, 10 + m, -11, 10 + n, class_39.field_885);
			}
		}

		return true;
	}
}
