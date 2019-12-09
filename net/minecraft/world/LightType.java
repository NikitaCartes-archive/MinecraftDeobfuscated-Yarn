/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

public enum LightType {
    SKY(15),
    BLOCK(0);

    public final int value;

    private LightType(int value) {
        this.value = value;
    }
}

