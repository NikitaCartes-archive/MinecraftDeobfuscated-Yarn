/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import net.minecraft.nbt.Tag;

public abstract class AbstractNumberTag
implements Tag {
    protected AbstractNumberTag() {
    }

    public abstract long getLong();

    public abstract int getInt();

    public abstract short getShort();

    public abstract byte getByte();

    public abstract double getDouble();

    public abstract float getFloat();

    public abstract Number getNumber();
}

