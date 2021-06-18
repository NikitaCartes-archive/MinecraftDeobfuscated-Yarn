/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.crash;

import org.jetbrains.annotations.Nullable;

public class CrashMemoryReserve {
    @Nullable
    private static byte[] reservedMemory = null;

    public static void reserveMemory() {
        reservedMemory = new byte[0xA00000];
    }

    public static void releaseMemory() {
        reservedMemory = new byte[0];
    }
}

