/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4355;

@Environment(value=EnvType.CLIENT)
public class class_4356
extends class_4355 {
    public final int field_19608;

    public class_4356(int i) {
        super(503, "Retry operation", -1, "");
        this.field_19608 = i < 0 || i > 120 ? 5 : i;
    }
}

