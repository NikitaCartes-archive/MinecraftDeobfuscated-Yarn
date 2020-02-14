/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;

public class class_4801 {
    private final int field_22278;
    private final int field_22279;

    public class_4801(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + i + ", Given max: " + j);
        }
        this.field_22278 = i;
        this.field_22279 = j;
    }

    public static class_4801 method_24502(int i, int j) {
        return new class_4801(i, j);
    }

    public int method_24503(Random random) {
        if (this.field_22278 == this.field_22279) {
            return this.field_22278;
        }
        return random.nextInt(this.field_22279 - this.field_22278 + 1) + this.field_22278;
    }

    public String toString() {
        return "IntRange[" + this.field_22278 + "-" + this.field_22279 + "]";
    }
}

