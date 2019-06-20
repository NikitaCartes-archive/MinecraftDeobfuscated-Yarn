package net.minecraft;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
interface class_64 {
	class_64 field_16883 = (arg, consumer) -> false;
	class_64 field_16884 = (arg, consumer) -> true;

	boolean expand(class_47 arg, Consumer<class_82> consumer);

	default class_64 method_16778(class_64 arg) {
		Objects.requireNonNull(arg);
		return (arg2, consumer) -> this.expand(arg2, consumer) && arg.expand(arg2, consumer);
	}

	default class_64 method_385(class_64 arg) {
		Objects.requireNonNull(arg);
		return (arg2, consumer) -> this.expand(arg2, consumer) || arg.expand(arg2, consumer);
	}
}
