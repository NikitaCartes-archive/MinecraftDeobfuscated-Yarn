package net.minecraft;

import java.util.Random;

public class class_3447 extends class_3418 {
	private boolean field_15322;
	private boolean field_16445;

	public class_3447(Random random, int i, int j) {
		super(class_3773.field_16918, random, i, 64, j, 7, 7, 9);
	}

	public class_3447(class_3485 arg, class_2487 arg2) {
		super(class_3773.field_16918, arg2);
		this.field_15322 = arg2.method_10577("Witch");
		this.field_16445 = arg2.method_10577("Cat");
	}

	@Override
	protected void method_14943(class_2487 arg) {
		super.method_14943(arg);
		arg.method_10556("Witch", this.field_15322);
		arg.method_10556("Cat", this.field_16445);
	}

	@Override
	public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		if (!this.method_14839(arg, arg2, 0)) {
			return false;
		} else {
			this.method_14940(arg, arg2, 1, 1, 1, 5, 1, 7, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 1, 4, 2, 5, 4, 7, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 2, 1, 0, 4, 1, 0, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 2, 2, 2, 3, 3, 2, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 1, 2, 3, 1, 3, 6, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 5, 2, 3, 5, 3, 6, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 2, 2, 7, 4, 3, 7, class_2246.field_9975.method_9564(), class_2246.field_9975.method_9564(), false);
			this.method_14940(arg, arg2, 1, 0, 2, 1, 3, 2, class_2246.field_10431.method_9564(), class_2246.field_10431.method_9564(), false);
			this.method_14940(arg, arg2, 5, 0, 2, 5, 3, 2, class_2246.field_10431.method_9564(), class_2246.field_10431.method_9564(), false);
			this.method_14940(arg, arg2, 1, 0, 7, 1, 3, 7, class_2246.field_10431.method_9564(), class_2246.field_10431.method_9564(), false);
			this.method_14940(arg, arg2, 5, 0, 7, 5, 3, 7, class_2246.field_10431.method_9564(), class_2246.field_10431.method_9564(), false);
			this.method_14917(arg, class_2246.field_10620.method_9564(), 2, 3, 2, arg2);
			this.method_14917(arg, class_2246.field_10620.method_9564(), 3, 3, 7, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 1, 3, 4, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 5, 3, 4, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 5, 3, 5, arg2);
			this.method_14917(arg, class_2246.field_10138.method_9564(), 1, 3, 5, arg2);
			this.method_14917(arg, class_2246.field_9980.method_9564(), 3, 2, 6, arg2);
			this.method_14917(arg, class_2246.field_10593.method_9564(), 4, 2, 6, arg2);
			this.method_14917(arg, class_2246.field_10620.method_9564(), 1, 2, 1, arg2);
			this.method_14917(arg, class_2246.field_10620.method_9564(), 5, 2, 1, arg2);
			class_2680 lv = class_2246.field_10569.method_9564().method_11657(class_2510.field_11571, class_2350.field_11043);
			class_2680 lv2 = class_2246.field_10569.method_9564().method_11657(class_2510.field_11571, class_2350.field_11034);
			class_2680 lv3 = class_2246.field_10569.method_9564().method_11657(class_2510.field_11571, class_2350.field_11039);
			class_2680 lv4 = class_2246.field_10569.method_9564().method_11657(class_2510.field_11571, class_2350.field_11035);
			this.method_14940(arg, arg2, 0, 4, 1, 6, 4, 1, lv, lv, false);
			this.method_14940(arg, arg2, 0, 4, 2, 0, 4, 7, lv2, lv2, false);
			this.method_14940(arg, arg2, 6, 4, 2, 6, 4, 7, lv3, lv3, false);
			this.method_14940(arg, arg2, 0, 4, 8, 6, 4, 8, lv4, lv4, false);
			this.method_14917(arg, lv.method_11657(class_2510.field_11565, class_2778.field_12709), 0, 4, 1, arg2);
			this.method_14917(arg, lv.method_11657(class_2510.field_11565, class_2778.field_12708), 6, 4, 1, arg2);
			this.method_14917(arg, lv4.method_11657(class_2510.field_11565, class_2778.field_12708), 0, 4, 8, arg2);
			this.method_14917(arg, lv4.method_11657(class_2510.field_11565, class_2778.field_12709), 6, 4, 8, arg2);

			for (int i = 2; i <= 7; i += 5) {
				for (int j = 1; j <= 5; j += 4) {
					this.method_14936(arg, class_2246.field_10431.method_9564(), j, -1, i, arg2);
				}
			}

			if (!this.field_15322) {
				int i = this.method_14928(2, 5);
				int j = this.method_14924(2);
				int k = this.method_14941(2, 5);
				if (arg2.method_14662(new class_2338(i, j, k))) {
					this.field_15322 = true;
					class_1640 lv5 = new class_1640(arg.method_8410());
					lv5.method_5971();
					lv5.method_5808((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
					lv5.method_5943(arg, arg.method_8404(new class_2338(i, j, k)), class_3730.field_16474, null, null);
					arg.method_8649(lv5);
				}
			}

			this.method_16181(arg, arg2);
			return true;
		}
	}

	private void method_16181(class_1936 arg, class_3341 arg2) {
		if (!this.field_16445) {
			int i = this.method_14928(2, 5);
			int j = this.method_14924(2);
			int k = this.method_14941(2, 5);
			if (arg2.method_14662(new class_2338(i, j, k))) {
				this.field_16445 = true;
				class_1451 lv = new class_1451(arg.method_8410());
				lv.method_5971();
				lv.method_5808((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
				lv.method_5943(arg, arg.method_8404(new class_2338(i, j, k)), class_3730.field_16474, null, null);
				arg.method_8649(lv);
			}
		}
	}
}
