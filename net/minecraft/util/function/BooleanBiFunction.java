/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

public interface BooleanBiFunction {
    public static final BooleanBiFunction FALSE = (a, b) -> false;
    public static final BooleanBiFunction NOT_OR = (a, b) -> !a && !b;
    public static final BooleanBiFunction ONLY_SECOND = (a, b) -> b && !a;
    public static final BooleanBiFunction NOT_FIRST = (a, b) -> !a;
    public static final BooleanBiFunction ONLY_FIRST = (a, b) -> a && !b;
    public static final BooleanBiFunction NOT_SECOND = (a, b) -> !b;
    public static final BooleanBiFunction NOT_SAME = (a, b) -> a != b;
    public static final BooleanBiFunction NOT_AND = (a, b) -> !a || !b;
    public static final BooleanBiFunction AND = (a, b) -> a && b;
    public static final BooleanBiFunction SAME = (a, b) -> a == b;
    public static final BooleanBiFunction SECOND = (a, b) -> b;
    public static final BooleanBiFunction CAUSES = (a, b) -> !a || b;
    public static final BooleanBiFunction FIRST = (a, b) -> a;
    public static final BooleanBiFunction CAUSED_BY = (a, b) -> a || !b;
    public static final BooleanBiFunction OR = (a, b) -> a || b;
    public static final BooleanBiFunction TRUE = (a, b) -> true;

    public boolean apply(boolean var1, boolean var2);
}

