/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * A registry that allows adding or modifying values.
 * Note that in vanilla, all registries are instances of this.
 * 
 * @see Registry
 */
public interface MutableRegistry<T>
extends Registry<T> {
    public RegistryEntry<T> set(int var1, RegistryKey<T> var2, T var3, Lifecycle var4);

    public RegistryEntry.Reference<T> add(RegistryKey<T> var1, T var2, Lifecycle var3);

    /**
     * {@return whether the registry is empty}
     */
    public boolean isEmpty();

    public RegistryEntryLookup<T> createMutableEntryLookup();
}

