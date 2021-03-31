/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {
    public boolean test(char var1);

    default public CharPredicate method_36125(CharPredicate charPredicate) {
        Objects.requireNonNull(charPredicate);
        return c -> this.test(c) && charPredicate.test(c);
    }

    default public CharPredicate method_36123() {
        return c -> !this.test(c);
    }

    default public CharPredicate method_36127(CharPredicate charPredicate) {
        Objects.requireNonNull(charPredicate);
        return c -> this.test(c) || charPredicate.test(c);
    }
}

