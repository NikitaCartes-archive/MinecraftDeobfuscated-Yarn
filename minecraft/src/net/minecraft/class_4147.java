package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4147 extends class_4148<class_1309> {
	@Override
	protected void method_19101(class_3218 arg, class_1309 arg2) {
		List<class_1657> list = (List<class_1657>)arg.method_18456()
			.stream()
			.filter(class_1301.field_6155)
			.filter(arg2x -> arg2.method_5858(arg2x) < 256.0)
			.sorted(Comparator.comparingDouble(arg2::method_5858))
			.collect(Collectors.toList());
		class_4095<?> lv = arg2.method_18868();
		lv.method_18878(class_4140.field_18443, list);
		lv.method_18879(class_4140.field_18444, list.stream().filter(arg2::method_6057).findFirst());
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18443, class_4140.field_18444);
	}
}
