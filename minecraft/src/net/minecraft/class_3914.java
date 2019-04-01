package net.minecraft;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface class_3914 {
	class_3914 field_17304 = new class_3914() {
		@Override
		public <T> Optional<T> method_17395(BiFunction<class_1937, class_2338, T> biFunction) {
			return Optional.empty();
		}
	};

	static class_3914 method_17392(class_1937 arg, class_2338 arg2) {
		return new class_3914() {
			@Override
			public <T> Optional<T> method_17395(BiFunction<class_1937, class_2338, T> biFunction) {
				return Optional.of(biFunction.apply(arg, arg2));
			}
		};
	}

	<T> Optional<T> method_17395(BiFunction<class_1937, class_2338, T> biFunction);

	default <T> T method_17396(BiFunction<class_1937, class_2338, T> biFunction, T object) {
		return (T)this.method_17395(biFunction).orElse(object);
	}

	default void method_17393(BiConsumer<class_1937, class_2338> biConsumer) {
		this.method_17395((arg, arg2) -> {
			biConsumer.accept(arg, arg2);
			return Optional.empty();
		});
	}
}
