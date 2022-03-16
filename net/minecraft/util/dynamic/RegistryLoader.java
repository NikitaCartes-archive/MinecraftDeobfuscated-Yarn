/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.dynamic.EntryLoader;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryLoader {
    private final EntryLoader entryLoader;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders = new IdentityHashMap();

    RegistryLoader(EntryLoader entryLoader) {
        this.entryLoader = entryLoader;
    }

    public <E> DataResult<? extends Registry<E>> load(MutableRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, DynamicOps<JsonElement> ops) {
        Map map = this.entryLoader.getKnownEntryPaths(registryRef);
        DataResult<MutableRegistry<Object>> dataResult = DataResult.success(registry, Lifecycle.stable());
        for (Map.Entry entry : map.entrySet()) {
            dataResult = dataResult.flatMap(reg -> this.load((MutableRegistry)reg, registryRef, codec, (RegistryKey)entry.getKey(), Optional.of((EntryLoader.Parseable)entry.getValue()), ops).map(entry -> reg));
        }
        return dataResult.setPartial(registry);
    }

    <E> DataResult<RegistryEntry<E>> load(MutableRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, RegistryKey<E> entryKey, DynamicOps<JsonElement> ops) {
        Optional<EntryLoader.Parseable<E>> optional = this.entryLoader.createParseable(entryKey);
        return this.load(registry, registryRef, codec, entryKey, optional, ops);
    }

    private <E> DataResult<RegistryEntry<E>> load(MutableRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, RegistryKey<E> entryKey, Optional<EntryLoader.Parseable<E>> parseable, DynamicOps<JsonElement> ops) {
        DataResult<RegistryEntry<Object>> dataResult2;
        ValueHolder<E> valueHolder = this.getOrCreateValueHolder(registryRef);
        DataResult dataResult = valueHolder.values.get(entryKey);
        if (dataResult != null) {
            return dataResult;
        }
        RegistryEntry registryEntry = registry.getOrCreateEntry(entryKey);
        valueHolder.values.put(entryKey, DataResult.success(registryEntry));
        if (parseable.isEmpty()) {
            dataResult2 = registry.contains(entryKey) ? DataResult.success(registryEntry, Lifecycle.stable()) : DataResult.error("Missing referenced custom/removed registry entry for registry " + registryRef + " named " + entryKey.getValue());
        } else {
            DataResult<EntryLoader.Entry<E>> dataResult3 = parseable.get().parseElement(ops, codec);
            Optional<EntryLoader.Entry<E>> optional = dataResult3.result();
            if (optional.isPresent()) {
                EntryLoader.Entry<E> entry2 = optional.get();
                registry.replace(entry2.fixedId(), entryKey, entry2.value(), dataResult3.lifecycle());
            }
            dataResult2 = dataResult3.map(entry -> registryEntry);
        }
        valueHolder.values.put(entryKey, dataResult2);
        return dataResult2;
    }

    private <E> ValueHolder<E> getOrCreateValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, ref -> new ValueHolder());
    }

    public LoaderAccess createAccess(DynamicRegistryManager.Mutable dynamicRegistryManager) {
        return new LoaderAccess(dynamicRegistryManager, this);
    }

    static final class ValueHolder<E> {
        final Map<RegistryKey<E>, DataResult<RegistryEntry<E>>> values = Maps.newIdentityHashMap();

        ValueHolder() {
        }
    }

    public record LoaderAccess(DynamicRegistryManager.Mutable dynamicRegistryManager, RegistryLoader loader) {
        public <E> DataResult<? extends Registry<E>> load(RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, DynamicOps<JsonElement> ops) {
            MutableRegistry mutableRegistry = this.dynamicRegistryManager.getMutable(registryRef);
            return this.loader.load(mutableRegistry, registryRef, codec, ops);
        }

        public <E> DataResult<RegistryEntry<E>> load(RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, RegistryKey<E> entryKey, DynamicOps<JsonElement> ops) {
            MutableRegistry mutableRegistry = this.dynamicRegistryManager.getMutable(registryRef);
            return this.loader.load(mutableRegistry, registryRef, codec, entryKey, ops);
        }
    }
}

