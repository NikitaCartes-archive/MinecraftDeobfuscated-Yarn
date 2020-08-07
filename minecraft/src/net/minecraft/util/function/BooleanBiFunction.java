package net.minecraft.util.function;

public interface BooleanBiFunction {
	BooleanBiFunction FALSE = (bl, bl2) -> false;
	BooleanBiFunction NOT_OR = (bl, bl2) -> !bl && !bl2;
	BooleanBiFunction ONLY_SECOND = (bl, bl2) -> bl2 && !bl;
	BooleanBiFunction NOT_FIRST = (bl, bl2) -> !bl;
	BooleanBiFunction ONLY_FIRST = (bl, bl2) -> bl && !bl2;
	BooleanBiFunction NOT_SECOND = (bl, bl2) -> !bl2;
	BooleanBiFunction NOT_SAME = (bl, bl2) -> bl != bl2;
	BooleanBiFunction NOT_AND = (bl, bl2) -> !bl || !bl2;
	BooleanBiFunction AND = (bl, bl2) -> bl && bl2;
	BooleanBiFunction SAME = (bl, bl2) -> bl == bl2;
	BooleanBiFunction SECOND = (bl, bl2) -> bl2;
	BooleanBiFunction CAUSES = (bl, bl2) -> !bl || bl2;
	BooleanBiFunction FIRST = (bl, bl2) -> bl;
	BooleanBiFunction CAUSED_BY = (bl, bl2) -> bl || !bl2;
	BooleanBiFunction OR = (bl, bl2) -> bl || bl2;
	BooleanBiFunction TRUE = (bl, bl2) -> true;

	boolean apply(boolean bl, boolean bl2);
}
