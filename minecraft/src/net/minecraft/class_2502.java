package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Queue;

public class class_2502 extends class_2248 {
	protected class_2502(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4) {
		if (arg4.method_11614() != arg.method_11614()) {
			this.method_10620(arg2, arg3);
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		this.method_10620(arg2, arg3);
		super.method_9612(arg, arg2, arg3, arg4, arg5);
	}

	protected void method_10620(class_1937 arg, class_2338 arg2) {
		if (this.method_10619(arg, arg2)) {
			arg.method_8652(arg2, class_2246.field_10562.method_9564(), 2);
			arg.method_8535(2001, arg2, class_2248.method_9507(class_2246.field_10382.method_9564()));
		}
	}

	private boolean method_10619(class_1937 arg, class_2338 arg2) {
		Queue<class_3545<class_2338, Integer>> queue = Lists.<class_3545<class_2338, Integer>>newLinkedList();
		queue.add(new class_3545<>(arg2, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			class_3545<class_2338, Integer> lv = (class_3545<class_2338, Integer>)queue.poll();
			class_2338 lv2 = lv.method_15442();
			int j = lv.method_15441();

			for (class_2350 lv3 : class_2350.values()) {
				class_2338 lv4 = lv2.method_10093(lv3);
				class_2680 lv5 = arg.method_8320(lv4);
				class_3610 lv6 = arg.method_8316(lv4);
				class_3614 lv7 = lv5.method_11620();
				if (lv6.method_15767(class_3486.field_15517)) {
					if (lv5.method_11614() instanceof class_2263 && ((class_2263)lv5.method_11614()).method_9700(arg, lv4, lv5) != class_3612.field_15906) {
						i++;
						if (j < 6) {
							queue.add(new class_3545<>(lv4, j + 1));
						}
					} else if (lv5.method_11614() instanceof class_2404) {
						arg.method_8652(lv4, class_2246.field_10124.method_9564(), 3);
						i++;
						if (j < 6) {
							queue.add(new class_3545<>(lv4, j + 1));
						}
					} else if (lv7 == class_3614.field_15947 || lv7 == class_3614.field_15926) {
						class_2586 lv8 = lv5.method_11614().method_9570() ? arg.method_8321(lv4) : null;
						method_9610(lv5, arg, lv4, lv8);
						arg.method_8652(lv4, class_2246.field_10124.method_9564(), 3);
						i++;
						if (j < 6) {
							queue.add(new class_3545<>(lv4, j + 1));
						}
					}
				}
			}

			if (i > 64) {
				break;
			}
		}

		return i > 0;
	}
}
