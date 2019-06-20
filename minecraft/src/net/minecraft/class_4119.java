package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;

public class class_4119 extends class_4097<class_1309> {
	private final Predicate<class_1309> field_18376;
	private final float field_18377;

	public class_4119(class_1311 arg, float f) {
		this(arg2 -> arg.equals(arg2.method_5864().method_5891()), f);
	}

	public class_4119(class_1299<?> arg, float f) {
		this(arg2 -> arg.equals(arg2.method_5864()), f);
	}

	public class_4119(Predicate<class_1309> predicate, float f) {
		super(ImmutableMap.of(class_4140.field_18446, class_4141.field_18457, class_4140.field_18442, class_4141.field_18456));
		this.field_18376 = predicate;
		this.field_18377 = f * f;
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return ((List)arg2.method_18868().method_18904(class_4140.field_18442).get()).stream().anyMatch(this.field_18376);
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(this.field_18376)
						.filter(arg2xx -> arg2xx.method_5858(arg2) <= (double)this.field_18377)
						.findFirst()
						.ifPresent(arg2xx -> lv.method_18878(class_4140.field_18446, new class_4102(arg2xx)))
			);
	}
}
