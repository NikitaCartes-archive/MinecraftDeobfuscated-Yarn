package net.minecraft.client.util;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {
	boolean test(char c);

	default CharPredicate method_36125(CharPredicate charPredicate) {
		Objects.requireNonNull(charPredicate);
		return c -> this.test(c) && charPredicate.test(c);
	}

	default CharPredicate method_36123() {
		return c -> !this.test(c);
	}

	default CharPredicate method_36127(CharPredicate charPredicate) {
		Objects.requireNonNull(charPredicate);
		return c -> this.test(c) || charPredicate.test(c);
	}
}
