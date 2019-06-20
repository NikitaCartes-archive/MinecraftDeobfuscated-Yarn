package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class class_4124 extends class_4097<class_1309> {
	public class_4124() {
		super(
			ImmutableMap.of(
				class_4140.field_18445,
				class_4141.field_18458,
				class_4140.field_18446,
				class_4141.field_18458,
				class_4140.field_18440,
				class_4141.field_18456,
				class_4140.field_18442,
				class_4141.field_18456,
				class_4140.field_18447,
				class_4141.field_18457
			)
		);
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		class_4095<?> lv = arg2.method_18868();
		Optional<class_4208> optional = lv.method_18904(class_4140.field_18440);
		return arg.method_8409().nextInt(100) == 0
			&& optional.isPresent()
			&& Objects.equals(arg.method_8597().method_12460(), ((class_4208)optional.get()).method_19442())
			&& ((class_4208)optional.get()).method_19446().method_19769(arg2.method_19538(), 4.0)
			&& ((List)lv.method_18904(class_4140.field_18442).get()).stream().anyMatch(argx -> class_1299.field_6077.equals(argx.method_5864()));
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(argxx -> class_1299.field_6077.equals(argxx.method_5864()))
						.filter(arg2xx -> arg2xx.method_5858(arg2) <= 32.0)
						.findFirst()
						.ifPresent(arg2xx -> {
							lv.method_18878(class_4140.field_18447, arg2xx);
							lv.method_18878(class_4140.field_18446, new class_4102(arg2xx));
							lv.method_18878(class_4140.field_18445, new class_4142(new class_4102(arg2xx), 0.3F, 1));
						})
			);
	}
}
