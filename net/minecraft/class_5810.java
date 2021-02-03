/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.block.Oxidizable;

public interface class_5810
extends Oxidizable<class_5811> {
    @Override
    default public float method_33620() {
        if (this.method_33622() == class_5811.field_28704) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static enum class_5811 {
        field_28704,
        field_28705,
        field_28706,
        field_28707;

    }
}

