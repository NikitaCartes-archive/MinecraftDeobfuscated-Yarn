/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A registry that allows adding or modifying values.
 * Note that in vanilla, all registries are instances of this.
 * 
 * @see Registry
 */
public abstract class MutableRegistry<T>
extends Registry<T> {
    public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    public abstract RegistryEntry<T> set(int var1, RegistryKey<T> var2, T var3, Lifecycle var4);

    public abstract RegistryEntry<T> add(RegistryKey<T> var1, T var2, Lifecycle var3);

    /**
     * {@return whether the registry is empty}
     */
    public abstract boolean isEmpty();
}

