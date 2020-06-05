/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.dynamic.RegistryCodec;
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
    private final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
    private final Set<RegistryKey<T>> loadedKeys = Sets.newIdentityHashSet();
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(key);
        Validate.notNull(entry);
        this.randomEntries = null;
        if (this.entriesByKey.containsKey(key)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", (Object)key);
        }
        this.entriesById.put(key.getValue(), entry);
        this.entriesByKey.put(key, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        return entry;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> key, V entry) {
        return this.set(this.nextId, key, entry);
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return (Identifier)this.entriesById.inverse().get(entry);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T value) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(value));
    }

    @Override
    public int getRawId(@Nullable T entry) {
        return this.indexedEntries.getId(entry);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return (T)this.entriesByKey.get(key);
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

    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return Collections.unmodifiableMap(this.entriesByKey).entrySet();
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

    /**
     * Returns whether an element corresponding to the key is loaded externally,
     * such as from a decoder.
     * 
     * <p>When an element is loaded externally, it will be encoded when the
     * registry is encoded.</p>
     */
    public boolean isLoaded(RegistryKey<T> key) {
        return this.loadedKeys.contains(key);
    }

    /**
     * Marks an element corresponding to the key as loaded from a decoder.
     * 
     * <p>This will make the element being written by the encoder when the
     * registry is encoded.</p>
     */
    public void markLoaded(RegistryKey<T> key) {
        this.loadedKeys.add(key);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.mapPair(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("key"), codec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(registryKey, lifecycle);
            for (Pair pair : list) {
                simpleRegistry.add((RegistryKey)pair.getFirst(), pair.getSecond());
            }
            return simpleRegistry;
        }, simpleRegistry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : simpleRegistry.indexedEntries) {
                builder.add(Pair.of(simpleRegistry.getKey(object).get(), object));
            }
            return builder.build();
        });
    }

    public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
        return RegistryCodec.of(registryRef, lifecycle, elementCodec);
    }

    public static <T> Codec<SimpleRegistry<T>> createEmptyCodec(RegistryKey<Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
        return Codec.unboundedMap(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryRef), RegistryKey::getValue), elementCodec).xmap(map -> {
            SimpleRegistry simpleRegistry = new SimpleRegistry(registryRef, lifecycle);
            map.forEach((? super K registryKey, ? super V object) -> {
                simpleRegistry.set(simpleRegistry.nextId, (RegistryKey)registryKey, (Object)object);
                simpleRegistry.markLoaded((RegistryKey)registryKey);
            });
            return simpleRegistry;
        }, simpleRegistry -> {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            simpleRegistry.entriesByKey.entrySet().stream().filter(entry -> simpleRegistry.isLoaded((RegistryKey)entry.getKey())).forEach(builder::put);
            return builder.build();
        });
    }
}

