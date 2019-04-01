package net.minecraft;

import java.util.Random;

public class class_3033 extends class_3031<class_3111> {
	public static final class_2338 field_13600 = class_2338.field_10980;
	private final boolean field_13599;

	public class_3033(boolean bl) {
		super(class_3111::method_13565);
		this.field_13599 = bl;
	}

	public boolean method_13163(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (class_2338 lv : class_2338.method_10097(
			new class_2338(arg3.method_10263() - 4, arg3.method_10264() - 1, arg3.method_10260() - 4),
			new class_2338(arg3.method_10263() + 4, arg3.method_10264() + 32, arg3.method_10260() + 4)
		)) {
			boolean bl = lv.method_19771(arg3, 2.5);
			if (bl || lv.method_19771(arg3, 3.5)) {
				if (lv.method_10264() < arg3.method_10264()) {
					if (bl) {
						this.method_13153(arg, lv, class_2246.field_9987.method_9564());
					} else if (lv.method_10264() < arg3.method_10264()) {
						this.method_13153(arg, lv, class_2246.field_10471.method_9564());
					}
				} else if (lv.method_10264() > arg3.method_10264()) {
					this.method_13153(arg, lv, class_2246.field_10124.method_9564());
				} else if (!bl) {
					this.method_13153(arg, lv, class_2246.field_9987.method_9564());
				} else if (this.field_13599) {
					this.method_13153(arg, new class_2338(lv), class_2246.field_10027.method_9564());
				} else {
					this.method_13153(arg, new class_2338(lv), class_2246.field_10124.method_9564());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.method_13153(arg, arg3.method_10086(i), class_2246.field_9987.method_9564());
		}

		class_2338 lv2 = arg3.method_10086(2);

		for (class_2350 lv3 : class_2350.class_2353.field_11062) {
			this.method_13153(arg, lv2.method_10093(lv3), class_2246.field_10099.method_9564().method_11657(class_2555.field_11731, lv3));
		}

		return true;
	}
}
