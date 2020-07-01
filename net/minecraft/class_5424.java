/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public interface class_5424
extends WorldView {
    public long method_30271();

    default public float method_30272() {
        return DimensionType.field_24752[this.getDimension().method_28531(this.method_30271())];
    }

    default public float method_30274(float f) {
        return this.getDimension().method_28528(this.method_30271());
    }

    @Environment(value=EnvType.CLIENT)
    default public int method_30273() {
        return this.getDimension().method_28531(this.method_30271());
    }
}

