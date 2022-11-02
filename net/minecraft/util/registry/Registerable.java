/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryKey;

public interface Registerable<T> {
    public RegistryEntry.Reference<T> register(RegistryKey<T> var1, T var2, Lifecycle var3);

    default public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value) {
        return this.register(key, value, Lifecycle.stable());
    }

    public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> var1);
}

