/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public interface RegistryEntryLookup<T> {
    public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> var1);

    default public RegistryEntry.Reference<T> getOrThrow(RegistryKey<T> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing element " + key));
    }

    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> var1);

    default public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
        return this.getOptional(tag).orElseThrow(() -> new IllegalStateException("Missing tag " + tag));
    }

    public static interface RegistryLookup {
        public <T> Optional<RegistryEntryLookup<T>> getOptional(RegistryKey<? extends Registry<? extends T>> var1);

        default public <T> RegistryEntryLookup<T> getOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
            return this.getOptional(registryRef).orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
        }
    }
}

