package net.minecraft.client.util;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {
	boolean test(char c);

	default CharPredicate and(CharPredicate predicate) {
		Objects.requireNonNull(predicate);
		return c -> this.test(c) && predicate.test(c);
	}

	default CharPredicate negate() {
		return c -> !this.test(c);
	}

	default CharPredicate or(CharPredicate predicate) {
		Objects.requireNonNull(predicate);
		return c -> this.test(c) || predicate.test(c);
	}
}
