package net.minecraft.util.function;

/**
 * A function that is provided two booleans and returns one boolean.
 */
public interface BooleanBiFunction {
	/**
	 * A {@link BooleanBiFunction} that always returns {@code false}.
	 */
	BooleanBiFunction FALSE = (a, b) -> false;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if neither argument is {@code true}.
	 */
	BooleanBiFunction NOT_OR = (a, b) -> !a && !b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if only the second argument is {@code true}.
	 */
	BooleanBiFunction ONLY_SECOND = (a, b) -> b && !a;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code false}.
	 */
	BooleanBiFunction NOT_FIRST = (a, b) -> !a;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if only the first argument is {@code true}.
	 */
	BooleanBiFunction ONLY_FIRST = (a, b) -> a && !b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the second argument is {@code false}.
	 */
	BooleanBiFunction NOT_SECOND = (a, b) -> !b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if one argument is {@code true} and the other is {@code false}.
	 */
	BooleanBiFunction NOT_SAME = (a, b) -> a != b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if either argument is {@code false}.
	 */
	BooleanBiFunction NOT_AND = (a, b) -> !a || !b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if both arguments are {@code true}.
	 */
	BooleanBiFunction AND = (a, b) -> a && b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if both arguments are {@code true} or both arguments are {@code false}.
	 */
	BooleanBiFunction SAME = (a, b) -> a == b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the second argument is {@code true}.
	 */
	BooleanBiFunction SECOND = (a, b) -> b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code false} or the second argument is {@code true}.
	 */
	BooleanBiFunction CAUSES = (a, b) -> !a || b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code true}.
	 */
	BooleanBiFunction FIRST = (a, b) -> a;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code true} or the second argument is {@code false}.
	 */
	BooleanBiFunction CAUSED_BY = (a, b) -> a || !b;
	/**
	 * A {@link BooleanBiFunction} that returns {@code true} if either argument is {@code true}.
	 */
	BooleanBiFunction OR = (a, b) -> a || b;
	/**
	 * A {@link BooleanBiFunction} that always returns {@code true}.
	 */
	BooleanBiFunction TRUE = (a, b) -> true;

	boolean apply(boolean a, boolean b);
}
