/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import net.minecraft.util.math.IntRange;

public class Durations {
    public static IntRange betweenSeconds(int min, int max) {
        return new IntRange(min * 20, max * 20);
    }
}

