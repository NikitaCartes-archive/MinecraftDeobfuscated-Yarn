package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4146 extends class_4148<class_1309> {
	private static final class_4051 field_18965 = new class_4051().method_18418(16.0).method_18421().method_18423().method_18422();

	@Override
	public void method_19101(class_3218 arg, class_1309 arg2) {
		this.field_18463 = arg.method_8510();
		List<class_1309> list = arg.method_8390(class_1309.class, arg2.method_5829().method_1009(16.0, 16.0, 16.0), arg2x -> arg2x != arg2 && arg2x.method_5805());
		list.sort(Comparator.comparingDouble(arg2::method_5858));
		class_4095<?> lv = arg2.method_18868();
		lv.method_18878(class_4140.field_18441, list);
		lv.method_18878(
			class_4140.field_18442,
			(List<class_1309>)list.stream().filter(arg2x -> field_18965.method_18419(arg2, arg2x)).filter(arg2::method_6057).collect(Collectors.toList())
		);
	}

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18441, class_4140.field_18442);
	}
}
