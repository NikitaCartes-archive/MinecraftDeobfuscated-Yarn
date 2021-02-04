/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Degradable;

public interface Oxidizable
extends Degradable<OxidizationLevel> {
    @Override
    default public float getDegradationChanceMultiplier() {
        if (this.getDegradationLevel() == OxidizationLevel.UNAFFECTED) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static enum OxidizationLevel {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        OXIDIZED;

    }
}

