/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

public interface BooleanBiFunction {
    public static final BooleanBiFunction FALSE = (bl, bl2) -> false;
    public static final BooleanBiFunction NOT_OR = (bl, bl2) -> !bl && !bl2;
    public static final BooleanBiFunction ONLY_SECOND = (bl, bl2) -> bl2 && !bl;
    public static final BooleanBiFunction NOT_FIRST = (bl, bl2) -> !bl;
    public static final BooleanBiFunction ONLY_FIRST = (bl, bl2) -> bl && !bl2;
    public static final BooleanBiFunction NOT_SECOND = (bl, bl2) -> !bl2;
    public static final BooleanBiFunction NOT_SAME = (bl, bl2) -> bl != bl2;
    public static final BooleanBiFunction NOT_AND = (bl, bl2) -> !bl || !bl2;
    public static final BooleanBiFunction AND = (bl, bl2) -> bl && bl2;
    public static final BooleanBiFunction SAME = (bl, bl2) -> bl == bl2;
    public static final BooleanBiFunction SECOND = (bl, bl2) -> bl2;
    public static final BooleanBiFunction CAUSES = (bl, bl2) -> !bl || bl2;
    public static final BooleanBiFunction FIRST = (bl, bl2) -> bl;
    public static final BooleanBiFunction CAUSED_BY = (bl, bl2) -> bl || !bl2;
    public static final BooleanBiFunction OR = (bl, bl2) -> bl || bl2;
    public static final BooleanBiFunction TRUE = (bl, bl2) -> true;

    public boolean apply(boolean var1, boolean var2);
}

