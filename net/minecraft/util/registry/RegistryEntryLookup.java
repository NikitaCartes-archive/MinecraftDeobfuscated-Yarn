/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import java.util.Optional;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

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

