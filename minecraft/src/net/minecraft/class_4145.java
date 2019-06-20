package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;

public class class_4145 extends class_4148<class_1309> {
	@Override
	protected void method_19101(class_3218 arg, class_1309 arg2) {
		class_2874 lv = arg.method_8597().method_12460();
		class_2338 lv2 = new class_2338(arg2);
		List<class_4208> list = Lists.<class_4208>newArrayList();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					class_2338 lv3 = lv2.method_10069(i, j, k);
					if (arg.method_8320(lv3).method_11602(class_3481.field_15494)) {
						list.add(class_4208.method_19443(lv, lv3));
					}
				}
			}
		}

		class_4095<?> lv4 = arg2.method_18868();
		if (!list.isEmpty()) {
			lv4.method_18878(class_4140.field_18450, list);
		} else {
			lv4.method_18875(class_4140.field_18450);
		}
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18450);
	}
}
