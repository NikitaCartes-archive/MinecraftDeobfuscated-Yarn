/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.border;

public enum WorldBorderStage {
    GROWING(4259712),
    SHRINKING(0xFF3030),
    STATIONARY(2138367);

    private final int color;

    private WorldBorderStage(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}

