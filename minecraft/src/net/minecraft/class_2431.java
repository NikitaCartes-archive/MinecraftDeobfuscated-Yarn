package net.minecraft;

import java.util.Random;

public class class_2431 extends class_2248 {
	public class_2431(class_2248.class_2251 arg) {
		super(arg);
	}

	protected int method_10398(Random random) {
		if (this == class_2246.field_10418) {
			return class_3532.method_15395(random, 0, 2);
		} else if (this == class_2246.field_10442) {
			return class_3532.method_15395(random, 3, 7);
		} else if (this == class_2246.field_10013) {
			return class_3532.method_15395(random, 3, 7);
		} else if (this == class_2246.field_10090) {
			return class_3532.method_15395(random, 2, 5);
		} else {
			return this == class_2246.field_10213 ? class_3532.method_15395(random, 2, 5) : 0;
		}
	}

	@Override
	public void method_9565(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1799 arg4) {
		super.method_9565(arg, arg2, arg3, arg4);
		if (class_1890.method_8225(class_1893.field_9099, arg4) == 0) {
			int i = this.method_10398(arg2.field_9229);
			if (i > 0) {
				this.method_9583(arg2, arg3, i);
			}
		}
	}
}
