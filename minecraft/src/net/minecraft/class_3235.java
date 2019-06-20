package net.minecraft;

import java.util.function.Function;
import java.util.stream.Stream;

public class class_3235<T> implements class_1951<T> {
	private final Function<class_2338, class_1951<T>> field_14100;

	public class_3235(Function<class_2338, class_1951<T>> function) {
		this.field_14100 = function;
	}

	@Override
	public boolean method_8674(class_2338 arg, T object) {
		return ((class_1951)this.field_14100.apply(arg)).method_8674(arg, object);
	}

	@Override
	public void method_8675(class_2338 arg, T object, int i, class_1953 arg2) {
		((class_1951)this.field_14100.apply(arg)).method_8675(arg, object, i, arg2);
	}

	@Override
	public boolean method_8677(class_2338 arg, T object) {
		return false;
	}

	@Override
	public void method_20470(Stream<class_1954<T>> stream) {
		stream.forEach(arg -> ((class_1951)this.field_14100.apply(arg.field_9322)).method_20470(Stream.of(arg)));
	}
}
