/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap(256);
    protected final BiMap<Identifier, T> entriesById = HashBiMap.create();
    protected final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> registryKey, V entry) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(registryKey);
        Validate.notNull(entry);
        this.randomEntries = null;
        if (this.entriesByKey.containsKey(registryKey)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", (Object)registryKey);
        }
        this.entriesById.put(registryKey.getValue(), entry);
        this.entriesByKey.put(registryKey, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        return entry;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> registryKey, V entry) {
        return this.set(this.nextId, registryKey, entry);
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return (Identifier)this.entriesById.inverse().get(entry);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Optional<RegistryKey<T>> getKey(T value) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(value));
    }

    @Override
    public int getRawId(@Nullable T entry) {
        return this.indexedEntries.getId(entry);
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public T get(@Nullable RegistryKey<T> registryKey) {
        return (T)this.entriesByKey.get(registryKey);
    }

    @Override
    @Nullable
    public T get(int index) {
        return this.indexedEntries.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier id) {
        return (T)this.entriesById.get(id);
    }

    @Override
    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(this.entriesById.get(id));
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entriesById.keySet());
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Collection collection = this.entriesById.values();
            if (collection.isEmpty()) {
                return null;
            }
            this.randomEntries = collection.toArray(new Object[collection.size()]);
        }
        return (T)Util.getRandom(this.randomEntries, random);
    }

    @Override
    public boolean containsId(Identifier id) {
        return this.entriesById.containsKey(id);
    }

    @Override
    public boolean containsId(int id) {
        return this.indexedEntries.containsId(id);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.mapPair(Identifier.field_25139.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("key"), codec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(registryKey, lifecycle);
            for (Pair pair : list) {
                simpleRegistry.add((RegistryKey)pair.getFirst(), pair.getSecond());
            }
            return simpleRegistry;
        }, simpleRegistry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Map.Entry entry : simpleRegistry.entriesByKey.entrySet()) {
                builder.add(Pair.of(entry.getKey(), entry.getValue()));
            }
            return builder.build();
        });
    }
}

