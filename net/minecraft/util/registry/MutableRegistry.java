/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class MutableRegistry<T>
extends Registry<T> {
    public abstract <V extends T> V set(int var1, Identifier var2, V var3);

    public abstract <V extends T> V add(Identifier var1, V var2);

    public abstract boolean isEmpty();
}

