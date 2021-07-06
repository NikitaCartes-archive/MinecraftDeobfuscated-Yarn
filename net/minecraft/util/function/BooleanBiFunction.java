/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

/**
 * A function that is provided two booleans and returns one boolean.
 */
public interface BooleanBiFunction {
    /**
     * A {@link BooleanBiFunction} that always returns {@code false}.
     */
    public static final BooleanBiFunction FALSE = (a, b) -> false;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if neither argument is {@code true}.
     */
    public static final BooleanBiFunction NOT_OR = (a, b) -> !a && !b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if only the second argument is {@code true}.
     */
    public static final BooleanBiFunction ONLY_SECOND = (a, b) -> b && !a;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code false}.
     */
    public static final BooleanBiFunction NOT_FIRST = (a, b) -> !a;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if only the first argument is {@code true}.
     */
    public static final BooleanBiFunction ONLY_FIRST = (a, b) -> a && !b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the second argument is {@code false}.
     */
    public static final BooleanBiFunction NOT_SECOND = (a, b) -> !b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if one argument is {@code true} and the other is {@code false}.
     */
    public static final BooleanBiFunction NOT_SAME = (a, b) -> a != b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if either argument is {@code false}.
     */
    public static final BooleanBiFunction NOT_AND = (a, b) -> !a || !b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if both arguments are {@code true}.
     */
    public static final BooleanBiFunction AND = (a, b) -> a && b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if both arguments are {@code true} or both arguments are {@code false}.
     */
    public static final BooleanBiFunction SAME = (a, b) -> a == b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the second argument is {@code true}.
     */
    public static final BooleanBiFunction SECOND = (a, b) -> b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code false} or the second argument is {@code true}.
     */
    public static final BooleanBiFunction CAUSES = (a, b) -> !a || b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code true}.
     */
    public static final BooleanBiFunction FIRST = (a, b) -> a;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if the first argument is {@code true} or the second argument is {@code false}.
     */
    public static final BooleanBiFunction CAUSED_BY = (a, b) -> a || !b;
    /**
     * A {@link BooleanBiFunction} that returns {@code true} if either argument is {@code true}.
     */
    public static final BooleanBiFunction OR = (a, b) -> a || b;
    /**
     * A {@link BooleanBiFunction} that always returns {@code true}.
     */
    public static final BooleanBiFunction TRUE = (a, b) -> true;

    public boolean apply(boolean var1, boolean var2);
}

