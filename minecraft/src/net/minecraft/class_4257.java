package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class class_4257 extends class_4148<class_1309> {
	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_19006);
	}

	@Override
	protected void method_19101(class_3218 arg, class_1309 arg2) {
		arg2.method_18868().method_18878(class_4140.field_19006, this.method_20000(arg2));
	}

	private List<class_1309> method_20000(class_1309 arg) {
		return (List<class_1309>)this.method_20002(arg).stream().filter(this::method_20001).collect(Collectors.toList());
	}

	private boolean method_20001(class_1309 arg) {
		return arg.method_5864() == class_1299.field_6077 && arg.method_6109();
	}

	private List<class_1309> method_20002(class_1309 arg) {
		return (List<class_1309>)arg.method_18868().method_18904(class_4140.field_18442).orElse(Lists.newArrayList());
	}
}
