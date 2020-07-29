/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public abstract class MutableRegistry<T>
extends Registry<T> {
    public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    public abstract <V extends T> V set(int var1, RegistryKey<T> var2, V var3);

    public abstract <V extends T> V add(RegistryKey<T> var1, V var2);

    public abstract <V extends T> V method_31062(RegistryKey<T> var1, V var2);
}

