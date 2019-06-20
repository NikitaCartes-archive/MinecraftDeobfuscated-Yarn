package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;

public class class_4106<E extends class_1309, T extends class_1309> extends class_4097<E> {
	private final int field_18355;
	private final float field_18356;
	private final class_1299<? extends T> field_18357;
	private final int field_18358;
	private final Predicate<T> field_18359;
	private final Predicate<E> field_18360;
	private final class_4140<T> field_18361;

	public class_4106(class_1299<? extends T> arg, int i, Predicate<E> predicate, Predicate<T> predicate2, class_4140<T> arg2, float f, int j) {
		super(
			ImmutableMap.of(
				class_4140.field_18446,
				class_4141.field_18458,
				class_4140.field_18445,
				class_4141.field_18457,
				arg2,
				class_4141.field_18457,
				class_4140.field_18442,
				class_4141.field_18456
			)
		);
		this.field_18357 = arg;
		this.field_18356 = f;
		this.field_18358 = i * i;
		this.field_18355 = j;
		this.field_18359 = predicate2;
		this.field_18360 = predicate;
		this.field_18361 = arg2;
	}

	public static <T extends class_1309> class_4106<class_1309, T> method_18941(class_1299<? extends T> arg, int i, class_4140<T> arg2, float f, int j) {
		return new class_4106<>(arg, i, argx -> true, argx -> true, arg2, f, j);
	}

	@Override
	protected boolean method_18919(class_3218 arg, E arg2) {
		return this.field_18360.test(arg2)
			&& ((List)arg2.method_18868().method_18904(class_4140.field_18442).get())
				.stream()
				.anyMatch(argx -> this.field_18357.equals(argx.method_5864()) && this.field_18359.test(argx));
	}

	@Override
	protected void method_18920(class_3218 arg, E arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		lv.method_18904(class_4140.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(argxx -> this.field_18357.equals(argxx.method_5864()))
						.map(argxx -> argxx)
						.filter(arg2xx -> arg2xx.method_5858(arg2) <= (double)this.field_18358)
						.filter(this.field_18359)
						.findFirst()
						.ifPresent(arg2xx -> {
							lv.method_18878(this.field_18361, (T)arg2xx);
							lv.method_18878(class_4140.field_18446, new class_4102(arg2xx));
							lv.method_18878(class_4140.field_18445, new class_4142(new class_4102(arg2xx), this.field_18356, this.field_18355));
						})
			);
	}
}
