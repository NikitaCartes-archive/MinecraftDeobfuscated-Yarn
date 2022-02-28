/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    private static final Logger field_36635 = LogUtils.getLogger();
    private final ObjectList<RegistryEntry.Reference<T>> rawIdToEntry = new ObjectArrayList<RegistryEntry.Reference<T>>(256);
    private final Object2IntMap<T> entryToRawId = Util.make(new Object2IntOpenCustomHashMap(Util.identityHashStrategy()), object2IntOpenCustomHashMap -> object2IntOpenCustomHashMap.defaultReturnValue(-1));
    private final Map<Identifier, RegistryEntry.Reference<T>> idToEntry = new HashMap<Identifier, RegistryEntry.Reference<T>>();
    private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry = new HashMap<RegistryKey<T>, RegistryEntry.Reference<T>>();
    private final Map<T, RegistryEntry.Reference<T>> valueToEntry = new IdentityHashMap<T, RegistryEntry.Reference<T>>();
    private final Map<T, Lifecycle> entryToLifecycle = new IdentityHashMap<T, Lifecycle>();
    private Lifecycle lifecycle;
    private volatile Map<TagKey<T>, RegistryEntryList.Named<T>> tagToEntryList = new IdentityHashMap<TagKey<T>, RegistryEntryList.Named<T>>();
    private boolean frozen;
    @Nullable
    private final Function<T, RegistryEntry.Reference<T>> valueToEntryFunction;
    @Nullable
    private Map<T, RegistryEntry.Reference<T>> unfrozenValueToEntry;
    @Nullable
    private List<RegistryEntry.Reference<T>> cachedEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction) {
        super(key, lifecycle);
        this.lifecycle = lifecycle;
        this.valueToEntryFunction = valueToEntryFunction;
        if (valueToEntryFunction != null) {
            this.unfrozenValueToEntry = new IdentityHashMap<T, RegistryEntry.Reference<T>>();
        }
    }

    private List<RegistryEntry.Reference<T>> getEntries() {
        if (this.cachedEntries == null) {
            this.cachedEntries = this.rawIdToEntry.stream().filter(Objects::nonNull).toList();
        }
        return this.cachedEntries;
    }

    private void assertNotFrozen(RegistryKey<T> key) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
        }
    }

    @Override
    public RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle) {
        return this.set(rawId, key, value, lifecycle, true);
    }

    private RegistryEntry<T> set(int rawId, RegistryKey<T> key2, T value, Lifecycle lifecycle, boolean checkDuplicateKeys) {
        RegistryEntry.Reference reference;
        this.assertNotFrozen(key2);
        Validate.notNull(key2);
        Validate.notNull(value);
        this.rawIdToEntry.size(Math.max(this.rawIdToEntry.size(), rawId + 1));
        this.entryToRawId.put(value, rawId);
        this.cachedEntries = null;
        if (checkDuplicateKeys && this.keyToEntry.containsKey(key2)) {
            Util.error("Adding duplicate key '" + key2 + "' to registry");
        }
        if (this.valueToEntry.containsKey(value)) {
            Util.error("Adding duplicate value '" + value + "' to registry");
        }
        this.entryToLifecycle.put(value, lifecycle);
        this.lifecycle = this.lifecycle.add(lifecycle);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        if (this.valueToEntryFunction != null) {
            reference = this.valueToEntryFunction.apply(value);
            RegistryEntry.Reference reference2 = this.keyToEntry.put(key2, reference);
            if (reference2 != null && reference2 != reference) {
                throw new IllegalStateException("Invalid holder present for key " + key2);
            }
        } else {
            reference = this.keyToEntry.computeIfAbsent(key2, key -> RegistryEntry.Reference.standAlone(this, key));
        }
        this.idToEntry.put(key2.getValue(), reference);
        this.valueToEntry.put(value, reference);
        reference.setKeyAndValue(key2, value);
        this.rawIdToEntry.set(rawId, reference);
        return reference;
    }

    @Override
    public RegistryEntry<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public RegistryEntry<T> replace(OptionalInt rawId, RegistryKey<T> key, T newEntry, Lifecycle lifecycle) {
        int i;
        Object object;
        this.assertNotFrozen(key);
        Validate.notNull(key);
        Validate.notNull(newEntry);
        RegistryEntry registryEntry = this.keyToEntry.get(key);
        Object v0 = object = registryEntry != null && registryEntry.hasKeyAndValue() ? registryEntry.value() : null;
        if (object == null) {
            i = rawId.orElse(this.nextId);
        } else {
            i = this.entryToRawId.getInt(object);
            if (rawId.isPresent() && rawId.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }
            this.entryToLifecycle.remove(object);
            this.entryToRawId.removeInt(object);
            this.valueToEntry.remove(object);
        }
        return this.set(i, key, newEntry, lifecycle, false);
    }

    @Override
    @Nullable
    public Identifier getId(T value) {
        RegistryEntry.Reference<T> reference = this.valueToEntry.get(value);
        return reference != null ? reference.registryKey().getValue() : null;
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T entry) {
        return Optional.ofNullable(this.valueToEntry.get(entry)).map(RegistryEntry.Reference::registryKey);
    }

    @Override
    public int getRawId(@Nullable T value) {
        return this.entryToRawId.getInt(value);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return SimpleRegistry.getValue(this.keyToEntry.get(key));
    }

    @Override
    @Nullable
    public T get(int index) {
        if (index < 0 || index >= this.rawIdToEntry.size()) {
            return null;
        }
        return SimpleRegistry.getValue((RegistryEntry.Reference)this.rawIdToEntry.get(index));
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(int rawId) {
        if (rawId < 0 || rawId >= this.rawIdToEntry.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable((RegistryEntry)this.rawIdToEntry.get(rawId));
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {
        return Optional.ofNullable((RegistryEntry)this.keyToEntry.get(key));
    }

    @Override
    public RegistryEntry<T> getOrCreateEntry(RegistryKey<T> key2) {
        return this.keyToEntry.computeIfAbsent(key2, key -> {
            if (this.valueToEntryFunction != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            }
            this.assertNotFrozen((RegistryKey<T>)key);
            return RegistryEntry.Reference.standAlone(this, key);
        });
    }

    @Override
    public int size() {
        return this.keyToEntry.size();
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return this.entryToLifecycle.get(entry);
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.transform(this.getEntries().iterator(), RegistryEntry::value);
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier id) {
        RegistryEntry.Reference<T> reference = this.idToEntry.get(id);
        return SimpleRegistry.getValue(reference);
    }

    @Nullable
    private static <T> T getValue(@Nullable RegistryEntry.Reference<T> entry) {
        return entry != null ? (T)entry.value() : null;
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.idToEntry.keySet());
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntrySet() {
        return Collections.unmodifiableSet(Maps.transformValues(this.keyToEntry, RegistryEntry::value).entrySet());
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return this.getEntries().stream();
    }

    @Override
    public boolean containsTag(TagKey<T> tag) {
        return this.tagToEntryList.containsKey(tag);
    }

    @Override
    public Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries() {
        return this.tagToEntryList.entrySet().stream().map(entry -> Pair.of((TagKey)entry.getKey(), (RegistryEntryList.Named)entry.getValue()));
    }

    @Override
    public RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag) {
        RegistryEntryList.Named<T> named = this.tagToEntryList.get(tag);
        if (named == null) {
            named = this.createNamedEntryList(tag);
            IdentityHashMap<TagKey<T>, RegistryEntryList.Named<T>> map = new IdentityHashMap<TagKey<T>, RegistryEntryList.Named<T>>(this.tagToEntryList);
            map.put(tag, named);
            this.tagToEntryList = map;
        }
        return named;
    }

    private RegistryEntryList.Named<T> createNamedEntryList(TagKey<T> tag) {
        return new RegistryEntryList.Named<T>(this, tag);
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return this.tagToEntryList.keySet().stream();
    }

    @Override
    public boolean isEmpty() {
        return this.keyToEntry.isEmpty();
    }

    @Override
    public Optional<RegistryEntry<T>> getRandom(Random random) {
        return Util.getRandomOrEmpty(this.getEntries(), random).map(RegistryEntry::upcast);
    }

    @Override
    public boolean containsId(Identifier id) {
        return this.idToEntry.containsKey(id);
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        return this.keyToEntry.containsKey(key);
    }

    @Override
    public Registry<T> freeze() {
        this.frozen = true;
        List<Identifier> list = this.keyToEntry.entrySet().stream().filter(entry -> !((RegistryEntry.Reference)entry.getValue()).hasKeyAndValue()).map(entry -> ((RegistryKey)entry.getKey()).getValue()).sorted().toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + this.getKey() + ": " + list);
        }
        if (this.unfrozenValueToEntry != null) {
            List<RegistryEntry.Reference> list2 = this.unfrozenValueToEntry.values().stream().filter(entry -> !entry.hasKeyAndValue()).toList();
            if (!list2.isEmpty()) {
                throw new IllegalStateException("Some intrusive holders were not added to registry: " + list2);
            }
            this.unfrozenValueToEntry = null;
        }
        return this;
    }

    @Override
    public RegistryEntry.Reference<T> createEntry(T value) {
        if (this.valueToEntryFunction == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        }
        if (this.frozen || this.unfrozenValueToEntry == null) {
            throw new IllegalStateException("Registry is already frozen");
        }
        return this.unfrozenValueToEntry.computeIfAbsent(value, key -> RegistryEntry.Reference.intrusive(this, key));
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
        return Optional.ofNullable(this.tagToEntryList.get(tag));
    }

    @Override
    public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {
        IdentityHashMap<RegistryEntry.Reference, List> map = new IdentityHashMap<RegistryEntry.Reference, List>();
        this.keyToEntry.values().forEach(entry -> map.put((RegistryEntry.Reference)entry, new ArrayList()));
        tagEntries.forEach((? super K tag, ? super V entries) -> {
            for (RegistryEntry registryEntry : entries) {
                if (!registryEntry.matchesRegistry(this)) {
                    throw new IllegalStateException("Can't create named set " + tag + " containing value " + registryEntry + " from outside registry " + this);
                }
                if (registryEntry instanceof RegistryEntry.Reference) {
                    RegistryEntry.Reference reference = (RegistryEntry.Reference)registryEntry;
                    ((List)map.get(reference)).add(tag);
                    continue;
                }
                throw new IllegalStateException("Found direct holder " + registryEntry + " value in tag " + tag);
            }
        });
        Sets.SetView<TagKey<T>> set = Sets.difference(this.tagToEntryList.keySet(), tagEntries.keySet());
        if (!set.isEmpty()) {
            field_36635.warn("Not all defined tags for registry {} are present in data pack: {}", (Object)this.getKey(), (Object)set.stream().map(tag -> tag.id().toString()).sorted().collect(Collectors.joining(", ")));
        }
        IdentityHashMap<TagKey<T>, RegistryEntryList.Named<T>> map2 = new IdentityHashMap<TagKey<T>, RegistryEntryList.Named<T>>(this.tagToEntryList);
        tagEntries.forEach((? super K tag, ? super V entries) -> map2.computeIfAbsent((TagKey<T>)tag, this::createNamedEntryList).copyOf(entries));
        map.forEach(RegistryEntry.Reference::setTags);
        this.tagToEntryList = map2;
    }

    @Override
    public void clearTags() {
        this.tagToEntryList.values().forEach(entryList -> entryList.copyOf(List.of()));
        this.keyToEntry.values().forEach(entry -> entry.setTags(Set.of()));
    }
}

