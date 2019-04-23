/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import org.jetbrains.annotations.Nullable;

public interface EntityAttribute {
    public String getId();

    public double clamp(double var1);

    public double getDefaultValue();

    public boolean isTracked();

    @Nullable
    public EntityAttribute getParent();
}

