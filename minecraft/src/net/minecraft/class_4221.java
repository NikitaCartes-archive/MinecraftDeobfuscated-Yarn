package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;

public class class_4221 extends class_4148<class_1646> {
	public class_4221() {
		super(40);
	}

	protected void method_19617(class_3218 arg, class_1646 arg2) {
		class_2874 lv = arg.method_8597().method_12460();
		class_2338 lv2 = new class_2338(arg2);
		List<class_4208> list = Lists.<class_4208>newArrayList();
		int i = 4;

		for (int j = -4; j <= 4; j++) {
			for (int k = -2; k <= 2; k++) {
				for (int l = -4; l <= 4; l++) {
					class_2338 lv3 = lv2.method_10069(j, k, l);
					if (arg2.method_7231().method_16924().method_19630().contains(arg.method_8320(lv3).method_11614())) {
						list.add(class_4208.method_19443(lv, lv3));
					}
				}
			}
		}

		class_4095<?> lv4 = arg2.method_18868();
		if (!list.isEmpty()) {
			lv4.method_18878(class_4140.field_18873, list);
		} else {
			lv4.method_18875(class_4140.field_18873);
		}
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18873);
	}
}
