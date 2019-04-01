package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class class_4109 extends class_4097<class_1309> {
	private final class_1299<?> field_18363;
	private final int field_18364;
	private final Predicate<class_1309> field_18365;
	private final Predicate<class_1309> field_18366;

	public class_4109(class_1299<?> arg, int i, Predicate<class_1309> predicate, Predicate<class_1309> predicate2) {
		this.field_18363 = arg;
		this.field_18364 = i * i;
		this.field_18365 = predicate2;
		this.field_18366 = predicate;
	}

	public class_4109(class_1299<?> arg, int i) {
		this(arg, i, argx -> true, argx -> true);
	}

	@Override
	public Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(
			Pair.of(class_4140.field_18446, class_4141.field_18458),
			Pair.of(class_4140.field_18447, class_4141.field_18457),
			Pair.of(class_4140.field_18442, class_4141.field_18456)
		);
	}

	@Override
	public boolean method_18919(class_3218 arg, class_1309 arg2) {
		return this.field_18366.test(arg2) && this.method_18959(arg2).stream().anyMatch(this::method_18962);
	}

	@Override
	public void method_18920(class_3218 arg, class_1309 arg2, long l) {
		super.method_18920(arg, arg2, l);
		class_4095<?> lv = arg2.method_18868();
		lv.method_19543(class_4140.field_18442)
			.ifPresent(
				list -> list.stream().filter(arg2xx -> arg2xx.method_5858(arg2) <= (double)this.field_18364).filter(this::method_18962).findFirst().ifPresent(arg2xx -> {
						lv.method_18878(class_4140.field_18447, arg2xx);
						lv.method_18878(class_4140.field_18446, new class_4102(arg2xx));
					})
			);
	}

	private boolean method_18962(class_1309 arg) {
		return this.field_18363.equals(arg.method_5864()) && this.field_18365.test(arg);
	}

	private List<class_1309> method_18959(class_1309 arg) {
		return (List<class_1309>)arg.method_18868().method_19543(class_4140.field_18442).get();
	}
}
