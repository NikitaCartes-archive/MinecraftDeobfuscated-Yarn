/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4592;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public abstract class class_4593<E extends Entity>
extends class_4592<E> {
    private float field_20923 = 1.0f;
    private float field_20924 = 1.0f;
    private float field_20925 = 1.0f;

    public void method_22955(float f, float g, float h) {
        this.field_20923 = f;
        this.field_20924 = g;
        this.field_20925 = h;
    }

    @Override
    public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
        super.method_17116(arg, arg2, i, this.field_20923 * f, this.field_20924 * g, this.field_20925 * h);
    }
}

