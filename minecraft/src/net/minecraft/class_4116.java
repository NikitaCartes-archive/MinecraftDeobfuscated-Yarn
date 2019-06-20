package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;

public class class_4116 extends class_4097<class_1314> {
	private final class_4140<class_4208> field_18372;
	private long field_18373;
	private final int field_18374;

	public class_4116(class_4140<class_4208> arg, int i) {
		super(ImmutableMap.of(class_4140.field_18445, class_4141.field_18458, arg, class_4141.field_18456));
		this.field_18372 = arg;
		this.field_18374 = i;
	}

	protected boolean method_18993(class_3218 arg, class_1314 arg2) {
		Optional<class_4208> optional = arg2.method_18868().method_18904(this.field_18372);
		return optional.isPresent()
			&& Objects.equals(arg.method_8597().method_12460(), ((class_4208)optional.get()).method_19442())
			&& ((class_4208)optional.get()).method_19446().method_19769(arg2.method_19538(), (double)this.field_18374);
	}

	protected void method_18994(class_3218 arg, class_1314 arg2, long l) {
		if (l > this.field_18373) {
			Optional<class_243> optional = Optional.ofNullable(class_1414.method_6378(arg2, 8, 6));
			arg2.method_18868().method_18879(class_4140.field_18445, optional.map(argx -> new class_4142(argx, 0.4F, 1)));
			this.field_18373 = l + 180L;
		}
	}
}
