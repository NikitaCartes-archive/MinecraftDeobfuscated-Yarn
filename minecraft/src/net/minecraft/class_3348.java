package net.minecraft;

import java.util.Random;

public class class_3348 extends class_3418 {
	private boolean field_14402;
	private boolean field_14401;
	private boolean field_14400;
	private boolean field_14399;
	private static final class_3348.class_3349 field_14403 = new class_3348.class_3349();

	public class_3348(Random random, int i, int j) {
		super(class_3773.field_16953, random, i, 64, j, 12, 10, 15);
	}

	public class_3348(class_3485 arg, class_2487 arg2) {
		super(class_3773.field_16953, arg2);
		this.field_14402 = arg2.method_10577("placedMainChest");
		this.field_14401 = arg2.method_10577("placedHiddenChest");
		this.field_14400 = arg2.method_10577("placedTrap1");
		this.field_14399 = arg2.method_10577("placedTrap2");
	}

	@Override
	protected void method_14943(class_2487 arg) {
		super.method_14943(arg);
		arg.method_10556("placedMainChest", this.field_14402);
		arg.method_10556("placedHiddenChest", this.field_14401);
		arg.method_10556("placedTrap1", this.field_14400);
		arg.method_10556("placedTrap2", this.field_14399);
	}

	@Override
	public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		if (!this.method_14839(arg, arg2, 0)) {
			return false;
		} else {
			this.method_14938(arg, arg2, 0, -4, 0, this.field_15244 - 1, 0, this.field_15242 - 1, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 1, 2, 9, 2, 2, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 1, 12, 9, 2, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 1, 3, 2, 2, 11, false, random, field_14403);
			this.method_14938(arg, arg2, 9, 1, 3, 9, 2, 11, false, random, field_14403);
			this.method_14938(arg, arg2, 1, 3, 1, 10, 6, 1, false, random, field_14403);
			this.method_14938(arg, arg2, 1, 3, 13, 10, 6, 13, false, random, field_14403);
			this.method_14938(arg, arg2, 1, 3, 2, 1, 6, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 10, 3, 2, 10, 6, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 3, 2, 9, 3, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 6, 2, 9, 6, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 3, 7, 3, 8, 7, 11, false, random, field_14403);
			this.method_14938(arg, arg2, 4, 8, 4, 7, 8, 10, false, random, field_14403);
			this.method_14942(arg, arg2, 3, 1, 3, 8, 2, 11);
			this.method_14942(arg, arg2, 4, 3, 6, 7, 3, 9);
			this.method_14942(arg, arg2, 2, 4, 2, 9, 5, 12);
			this.method_14942(arg, arg2, 4, 6, 5, 7, 6, 9);
			this.method_14942(arg, arg2, 5, 7, 6, 6, 7, 8);
			this.method_14942(arg, arg2, 5, 1, 2, 6, 2, 2);
			this.method_14942(arg, arg2, 5, 2, 12, 6, 2, 12);
			this.method_14942(arg, arg2, 5, 5, 1, 6, 5, 1);
			this.method_14942(arg, arg2, 5, 5, 13, 6, 5, 13);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 1, 5, 5, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 10, 5, 5, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 1, 5, 9, arg2);
			this.method_14917(arg, class_2246.field_10124.method_9564(), 10, 5, 9, arg2);

			for (int i = 0; i <= 14; i += 14) {
				this.method_14938(arg, arg2, 2, 4, i, 2, 5, i, false, random, field_14403);
				this.method_14938(arg, arg2, 4, 4, i, 4, 5, i, false, random, field_14403);
				this.method_14938(arg, arg2, 7, 4, i, 7, 5, i, false, random, field_14403);
				this.method_14938(arg, arg2, 9, 4, i, 9, 5, i, false, random, field_14403);
			}

			this.method_14938(arg, arg2, 5, 6, 0, 6, 6, 0, false, random, field_14403);

			for (int i = 0; i <= 11; i += 11) {
				for (int j = 2; j <= 12; j += 2) {
					this.method_14938(arg, arg2, i, 4, j, i, 5, j, false, random, field_14403);
				}

				this.method_14938(arg, arg2, i, 6, 5, i, 6, 5, false, random, field_14403);
				this.method_14938(arg, arg2, i, 6, 9, i, 6, 9, false, random, field_14403);
			}

			this.method_14938(arg, arg2, 2, 7, 2, 2, 9, 2, false, random, field_14403);
			this.method_14938(arg, arg2, 9, 7, 2, 9, 9, 2, false, random, field_14403);
			this.method_14938(arg, arg2, 2, 7, 12, 2, 9, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 9, 7, 12, 9, 9, 12, false, random, field_14403);
			this.method_14938(arg, arg2, 4, 9, 4, 4, 9, 4, false, random, field_14403);
			this.method_14938(arg, arg2, 7, 9, 4, 7, 9, 4, false, random, field_14403);
			this.method_14938(arg, arg2, 4, 9, 10, 4, 9, 10, false, random, field_14403);
			this.method_14938(arg, arg2, 7, 9, 10, 7, 9, 10, false, random, field_14403);
			this.method_14938(arg, arg2, 5, 9, 7, 6, 9, 7, false, random, field_14403);
			class_2680 lv = class_2246.field_10596.method_9564().method_11657(class_2510.field_11571, class_2350.field_11034);
			class_2680 lv2 = class_2246.field_10596.method_9564().method_11657(class_2510.field_11571, class_2350.field_11039);
			class_2680 lv3 = class_2246.field_10596.method_9564().method_11657(class_2510.field_11571, class_2350.field_11035);
			class_2680 lv4 = class_2246.field_10596.method_9564().method_11657(class_2510.field_11571, class_2350.field_11043);
			this.method_14917(arg, lv4, 5, 9, 6, arg2);
			this.method_14917(arg, lv4, 6, 9, 6, arg2);
			this.method_14917(arg, lv3, 5, 9, 8, arg2);
			this.method_14917(arg, lv3, 6, 9, 8, arg2);
			this.method_14917(arg, lv4, 4, 0, 0, arg2);
			this.method_14917(arg, lv4, 5, 0, 0, arg2);
			this.method_14917(arg, lv4, 6, 0, 0, arg2);
			this.method_14917(arg, lv4, 7, 0, 0, arg2);
			this.method_14917(arg, lv4, 4, 1, 8, arg2);
			this.method_14917(arg, lv4, 4, 2, 9, arg2);
			this.method_14917(arg, lv4, 4, 3, 10, arg2);
			this.method_14917(arg, lv4, 7, 1, 8, arg2);
			this.method_14917(arg, lv4, 7, 2, 9, arg2);
			this.method_14917(arg, lv4, 7, 3, 10, arg2);
			this.method_14938(arg, arg2, 4, 1, 9, 4, 1, 9, false, random, field_14403);
			this.method_14938(arg, arg2, 7, 1, 9, 7, 1, 9, false, random, field_14403);
			this.method_14938(arg, arg2, 4, 1, 10, 7, 2, 10, false, random, field_14403);
			this.method_14938(arg, arg2, 5, 4, 5, 6, 4, 5, false, random, field_14403);
			this.method_14917(arg, lv, 4, 4, 5, arg2);
			this.method_14917(arg, lv2, 7, 4, 5, arg2);

			for (int k = 0; k < 4; k++) {
				this.method_14917(arg, lv3, 5, 0 - k, 6 + k, arg2);
				this.method_14917(arg, lv3, 6, 0 - k, 6 + k, arg2);
				this.method_14942(arg, arg2, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
			}

			this.method_14942(arg, arg2, 1, -3, 12, 10, -1, 13);
			this.method_14942(arg, arg2, 1, -3, 1, 3, -1, 13);
			this.method_14942(arg, arg2, 1, -3, 1, 9, -1, 5);

			for (int k = 1; k <= 13; k += 2) {
				this.method_14938(arg, arg2, 1, -3, k, 1, -2, k, false, random, field_14403);
			}

			for (int k = 2; k <= 12; k += 2) {
				this.method_14938(arg, arg2, 1, -1, k, 3, -1, k, false, random, field_14403);
			}

			this.method_14938(arg, arg2, 2, -2, 1, 5, -2, 1, false, random, field_14403);
			this.method_14938(arg, arg2, 7, -2, 1, 9, -2, 1, false, random, field_14403);
			this.method_14938(arg, arg2, 6, -3, 1, 6, -3, 1, false, random, field_14403);
			this.method_14938(arg, arg2, 6, -1, 1, 6, -1, 1, false, random, field_14403);
			this.method_14917(
				arg,
				class_2246.field_10348
					.method_9564()
					.method_11657(class_2537.field_11666, class_2350.field_11034)
					.method_11657(class_2537.field_11669, Boolean.valueOf(true)),
				1,
				-3,
				8,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10348
					.method_9564()
					.method_11657(class_2537.field_11666, class_2350.field_11039)
					.method_11657(class_2537.field_11669, Boolean.valueOf(true)),
				4,
				-3,
				8,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10589
					.method_9564()
					.method_11657(class_2538.field_11673, Boolean.valueOf(true))
					.method_11657(class_2538.field_11674, Boolean.valueOf(true))
					.method_11657(class_2538.field_11683, Boolean.valueOf(true)),
				2,
				-3,
				8,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10589
					.method_9564()
					.method_11657(class_2538.field_11673, Boolean.valueOf(true))
					.method_11657(class_2538.field_11674, Boolean.valueOf(true))
					.method_11657(class_2538.field_11683, Boolean.valueOf(true)),
				3,
				-3,
				8,
				arg2
			);
			class_2680 lv5 = class_2246.field_10091
				.method_9564()
				.method_11657(class_2457.field_11440, class_2773.field_12689)
				.method_11657(class_2457.field_11437, class_2773.field_12689);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11437, class_2773.field_12689), 5, -3, 7, arg2);
			this.method_14917(arg, lv5, 5, -3, 6, arg2);
			this.method_14917(arg, lv5, 5, -3, 5, arg2);
			this.method_14917(arg, lv5, 5, -3, 4, arg2);
			this.method_14917(arg, lv5, 5, -3, 3, arg2);
			this.method_14917(arg, lv5, 5, -3, 2, arg2);
			this.method_14917(
				arg,
				class_2246.field_10091
					.method_9564()
					.method_11657(class_2457.field_11440, class_2773.field_12689)
					.method_11657(class_2457.field_11439, class_2773.field_12689),
				5,
				-3,
				1,
				arg2
			);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11436, class_2773.field_12689), 4, -3, 1, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 3, -3, 1, arg2);
			if (!this.field_14400) {
				this.field_14400 = this.method_14930(arg, arg2, random, 3, -2, 1, class_2350.field_11043, class_39.field_751);
			}

			this.method_14917(arg, class_2246.field_10597.method_9564().method_11657(class_2541.field_11699, Boolean.valueOf(true)), 3, -2, 2, arg2);
			this.method_14917(
				arg,
				class_2246.field_10348
					.method_9564()
					.method_11657(class_2537.field_11666, class_2350.field_11043)
					.method_11657(class_2537.field_11669, Boolean.valueOf(true)),
				7,
				-3,
				1,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10348
					.method_9564()
					.method_11657(class_2537.field_11666, class_2350.field_11035)
					.method_11657(class_2537.field_11669, Boolean.valueOf(true)),
				7,
				-3,
				5,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10589
					.method_9564()
					.method_11657(class_2538.field_11675, Boolean.valueOf(true))
					.method_11657(class_2538.field_11678, Boolean.valueOf(true))
					.method_11657(class_2538.field_11683, Boolean.valueOf(true)),
				7,
				-3,
				2,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10589
					.method_9564()
					.method_11657(class_2538.field_11675, Boolean.valueOf(true))
					.method_11657(class_2538.field_11678, Boolean.valueOf(true))
					.method_11657(class_2538.field_11683, Boolean.valueOf(true)),
				7,
				-3,
				3,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10589
					.method_9564()
					.method_11657(class_2538.field_11675, Boolean.valueOf(true))
					.method_11657(class_2538.field_11678, Boolean.valueOf(true))
					.method_11657(class_2538.field_11683, Boolean.valueOf(true)),
				7,
				-3,
				4,
				arg2
			);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11436, class_2773.field_12689), 8, -3, 6, arg2);
			this.method_14917(
				arg,
				class_2246.field_10091
					.method_9564()
					.method_11657(class_2457.field_11439, class_2773.field_12689)
					.method_11657(class_2457.field_11437, class_2773.field_12689),
				9,
				-3,
				6,
				arg2
			);
			this.method_14917(
				arg,
				class_2246.field_10091
					.method_9564()
					.method_11657(class_2457.field_11440, class_2773.field_12689)
					.method_11657(class_2457.field_11437, class_2773.field_12686),
				9,
				-3,
				5,
				arg2
			);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 9, -3, 4, arg2);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11440, class_2773.field_12689), 9, -2, 4, arg2);
			if (!this.field_14399) {
				this.field_14399 = this.method_14930(arg, arg2, random, 9, -2, 3, class_2350.field_11039, class_39.field_751);
			}

			this.method_14917(arg, class_2246.field_10597.method_9564().method_11657(class_2541.field_11702, Boolean.valueOf(true)), 8, -1, 3, arg2);
			this.method_14917(arg, class_2246.field_10597.method_9564().method_11657(class_2541.field_11702, Boolean.valueOf(true)), 8, -2, 3, arg2);
			if (!this.field_14402) {
				this.field_14402 = this.method_14915(arg, arg2, random, 8, -3, 3, class_39.field_803);
			}

			this.method_14917(arg, class_2246.field_9989.method_9564(), 9, -3, 2, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 8, -3, 1, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 4, -3, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 5, -2, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 5, -1, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 6, -3, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 7, -2, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 7, -1, 5, arg2);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 8, -3, 5, arg2);
			this.method_14938(arg, arg2, 9, -1, 1, 9, -1, 5, false, random, field_14403);
			this.method_14942(arg, arg2, 8, -3, 8, 10, -1, 10);
			this.method_14917(arg, class_2246.field_10552.method_9564(), 8, -2, 11, arg2);
			this.method_14917(arg, class_2246.field_10552.method_9564(), 9, -2, 11, arg2);
			this.method_14917(arg, class_2246.field_10552.method_9564(), 10, -2, 11, arg2);
			class_2680 lv6 = class_2246.field_10363
				.method_9564()
				.method_11657(class_2401.field_11177, class_2350.field_11043)
				.method_11657(class_2401.field_11007, class_2738.field_12471);
			this.method_14917(arg, lv6, 8, -2, 12, arg2);
			this.method_14917(arg, lv6, 9, -2, 12, arg2);
			this.method_14917(arg, lv6, 10, -2, 12, arg2);
			this.method_14938(arg, arg2, 8, -3, 8, 8, -3, 10, false, random, field_14403);
			this.method_14938(arg, arg2, 10, -3, 8, 10, -3, 10, false, random, field_14403);
			this.method_14917(arg, class_2246.field_9989.method_9564(), 10, -2, 9, arg2);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11440, class_2773.field_12689), 8, -2, 9, arg2);
			this.method_14917(arg, class_2246.field_10091.method_9564().method_11657(class_2457.field_11437, class_2773.field_12689), 8, -2, 10, arg2);
			this.method_14917(arg, class_2246.field_10091.method_9564(), 10, -1, 9, arg2);
			this.method_14917(arg, class_2246.field_10615.method_9564().method_11657(class_2665.field_10927, class_2350.field_11036), 9, -2, 8, arg2);
			this.method_14917(arg, class_2246.field_10615.method_9564().method_11657(class_2665.field_10927, class_2350.field_11039), 10, -2, 8, arg2);
			this.method_14917(arg, class_2246.field_10615.method_9564().method_11657(class_2665.field_10927, class_2350.field_11039), 10, -1, 8, arg2);
			this.method_14917(arg, class_2246.field_10450.method_9564().method_11657(class_2462.field_11177, class_2350.field_11043), 10, -2, 10, arg2);
			if (!this.field_14401) {
				this.field_14401 = this.method_14915(arg, arg2, random, 9, -3, 10, class_39.field_803);
			}

			return true;
		}
	}

	static class class_3349 extends class_3443.class_3444 {
		private class_3349() {
		}

		@Override
		public void method_14948(Random random, int i, int j, int k, boolean bl) {
			if (random.nextFloat() < 0.4F) {
				this.field_15317 = class_2246.field_10445.method_9564();
			} else {
				this.field_15317 = class_2246.field_9989.method_9564();
			}
		}
	}
}
