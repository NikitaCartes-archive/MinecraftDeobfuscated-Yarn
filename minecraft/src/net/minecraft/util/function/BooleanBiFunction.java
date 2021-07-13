package net.minecraft.util.function;

public interface BooleanBiFunction {
	BooleanBiFunction FALSE = (a, b) -> false;
	BooleanBiFunction NOT_OR = (a, b) -> !a && !b;
	BooleanBiFunction ONLY_SECOND = (a, b) -> b && !a;
	BooleanBiFunction NOT_FIRST = (a, b) -> !a;
	BooleanBiFunction ONLY_FIRST = (a, b) -> a && !b;
	BooleanBiFunction NOT_SECOND = (a, b) -> !b;
	BooleanBiFunction NOT_SAME = (a, b) -> a != b;
	BooleanBiFunction NOT_AND = (a, b) -> !a || !b;
	BooleanBiFunction AND = (a, b) -> a && b;
	BooleanBiFunction SAME = (a, b) -> a == b;
	BooleanBiFunction SECOND = (a, b) -> b;
	BooleanBiFunction CAUSES = (a, b) -> !a || b;
	BooleanBiFunction FIRST = (a, b) -> a;
	BooleanBiFunction CAUSED_BY = (a, b) -> a || !b;
	BooleanBiFunction OR = (a, b) -> a || b;
	BooleanBiFunction TRUE = (a, b) -> true;

	boolean apply(boolean a, boolean b);
}
