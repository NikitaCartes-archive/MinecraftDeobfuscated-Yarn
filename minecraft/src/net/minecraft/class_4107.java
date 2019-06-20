package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4107 extends class_4097<class_1309> {
	public class_4107() {
		super(ImmutableMap.of(class_4140.field_18449, class_4141.field_18456, class_4140.field_18450, class_4141.field_18456));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_11 lv2 = (class_11)lv.method_18904(class_4140.field_18449).get();
		List<class_4208> list = (List<class_4208>)lv.method_18904(class_4140.field_18450).get();
		List<class_2338> list2 = (List<class_2338>)lv2.method_19314()
			.stream()
			.map(argx -> new class_2338(argx.field_40, argx.field_39, argx.field_38))
			.collect(Collectors.toList());
		Set<class_2338> set = this.method_19567(arg, list, list2);
		int i = lv2.method_39() - 1;
		this.method_19568(arg, list2, set, i);
	}

	private Set<class_2338> method_19567(class_3218 arg, List<class_4208> list, List<class_2338> list2) {
		return (Set<class_2338>)list.stream()
			.filter(arg2 -> arg2.method_19442() == arg.method_8597().method_12460())
			.map(class_4208::method_19446)
			.filter(list2::contains)
			.collect(Collectors.toSet());
	}

	private void method_19568(class_3218 arg, List<class_2338> list, Set<class_2338> set, int i) {
		set.forEach(arg2 -> {
			int j = list.indexOf(arg2);
			class_2680 lv = arg.method_8320(arg2);
			class_2248 lv2 = lv.method_11614();
			if (class_3481.field_15494.method_15141(lv2) && lv2 instanceof class_2323) {
				((class_2323)lv2).method_10033(arg, arg2, j >= i);
			}
		});
	}
}
