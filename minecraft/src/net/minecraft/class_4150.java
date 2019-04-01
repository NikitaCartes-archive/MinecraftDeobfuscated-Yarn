package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class class_4150 extends class_4148<class_1309> {
	private static final ImmutableMap<class_1299<?>, Float> field_18473 = ImmutableMap.<class_1299<?>, Float>builder()
		.put(class_1299.field_6051, 8.0F)
		.put(class_1299.field_6090, 12.0F)
		.put(class_1299.field_6117, 8.0F)
		.put(class_1299.field_6059, 8.0F)
		.put(class_1299.field_6105, 15.0F)
		.put(class_1299.field_6065, 12.0F)
		.build();

	@Override
	public Set<class_4140<?>> method_19099() {
		return ImmutableSet.of(class_4140.field_18453);
	}

	@Override
	public void method_19101(class_3218 arg, class_1309 arg2) {
		arg2.method_18868().method_18879(class_4140.field_18453, this.method_19618(arg2));
	}

	private Optional<class_1309> method_19618(class_1309 arg) {
		return this.method_19620(arg)
			.flatMap(
				list -> list.stream().filter(this::method_19104).filter(arg2 -> this.method_19105(arg, arg2)).min((arg2, arg3) -> this.method_19619(arg, arg2, arg3))
			);
	}

	private Optional<List<class_1309>> method_19620(class_1309 arg) {
		return arg.method_18868().method_18904(class_4140.field_18442);
	}

	private int method_19619(class_1309 arg, class_1309 arg2, class_1309 arg3) {
		return (int)arg2.method_5858(arg) - (int)arg3.method_5858(arg);
	}

	private boolean method_19105(class_1309 arg, class_1309 arg2) {
		return arg2.method_5858(arg) <= (double)field_18473.get(arg2.method_5864()).floatValue();
	}

	private boolean method_19104(class_1309 arg) {
		return field_18473.containsKey(arg.method_5864());
	}
}
