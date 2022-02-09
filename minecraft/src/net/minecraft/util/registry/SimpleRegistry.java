package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;

public class SimpleRegistry<T> extends MutableRegistry<T> {
	private final ObjectList<RegistryEntry.Reference<T>> rawIdToEntry = new ObjectArrayList<>(256);
	private final Object2IntMap<T> entryToRawId = Util.make(
		new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy()), object2IntOpenCustomHashMap -> object2IntOpenCustomHashMap.defaultReturnValue(-1)
	);
	private final Map<Identifier, RegistryEntry.Reference<T>> idToEntry = new HashMap();
	private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry = new HashMap();
	private final Map<T, RegistryEntry.Reference<T>> valueToEntry = new IdentityHashMap();
	private final Map<T, Lifecycle> entryToLifecycle = new IdentityHashMap();
	private Lifecycle lifecycle;
	private volatile Map<TagKey<T>, RegistryEntryList.Named<T>> tagToEntryList = new IdentityHashMap();
	private boolean frozen;
	@Nullable
	private final Function<T, RegistryEntry.Reference<T>> valueToEntryFunction;
	@Nullable
	private Map<T, RegistryEntry.Reference<T>> unfrozenValueToEntry;
	@Nullable
	private List<RegistryEntry<T>> randomEntries;
	private int nextId;

	public SimpleRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction) {
		super(key, lifecycle);
		this.lifecycle = lifecycle;
		this.valueToEntryFunction = valueToEntryFunction;
		if (valueToEntryFunction != null) {
			this.unfrozenValueToEntry = new IdentityHashMap();
		}
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

	private RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle, boolean checkDuplicateKeys) {
		this.assertNotFrozen(key);
		Validate.notNull(key);
		Validate.notNull(value);
		this.rawIdToEntry.size(Math.max(this.rawIdToEntry.size(), rawId + 1));
		this.entryToRawId.put(value, rawId);
		this.randomEntries = null;
		if (checkDuplicateKeys && this.keyToEntry.containsKey(key)) {
			Util.error("Adding duplicate key '" + key + "' to registry");
		}

		if (this.valueToEntry.containsKey(value)) {
			Util.error("Adding duplicate value '" + value + "' to registry");
		}

		this.entryToLifecycle.put(value, lifecycle);
		this.lifecycle = this.lifecycle.add(lifecycle);
		if (this.nextId <= rawId) {
			this.nextId = rawId + 1;
		}

		RegistryEntry.Reference<T> reference;
		if (this.valueToEntryFunction != null) {
			reference = (RegistryEntry.Reference<T>)this.valueToEntryFunction.apply(value);
			RegistryEntry.Reference<T> reference2 = (RegistryEntry.Reference<T>)this.keyToEntry.put(key, reference);
			if (reference2 != null && reference2 != reference) {
				throw new IllegalStateException("Invalid holder present for key " + key);
			}
		} else {
			reference = (RegistryEntry.Reference<T>)this.keyToEntry.computeIfAbsent(key, keyx -> RegistryEntry.Reference.standAlone(this, keyx));
		}

		this.idToEntry.put(key.getValue(), reference);
		this.valueToEntry.put(value, reference);
		reference.setKeyAndValue(key, value);
		this.rawIdToEntry.set(rawId, reference);
		return reference;
	}

	@Override
	public RegistryEntry<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle) {
		return this.set(this.nextId, key, entry, lifecycle);
	}

	@Override
	public RegistryEntry<T> replace(OptionalInt rawId, RegistryKey<T> key, T newEntry, Lifecycle lifecycle) {
		this.assertNotFrozen(key);
		Validate.notNull(key);
		Validate.notNull(newEntry);
		RegistryEntry<T> registryEntry = (RegistryEntry<T>)this.keyToEntry.get(key);
		T object = registryEntry != null && registryEntry.hasKeyAndValue() ? registryEntry.value() : null;
		int i;
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

	@Nullable
	@Override
	public Identifier getId(T value) {
		RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)this.valueToEntry.get(value);
		return reference != null ? reference.registryKey().getValue() : null;
	}

	@Override
	public Optional<RegistryKey<T>> getKey(T entry) {
		return Optional.ofNullable((RegistryEntry.Reference)this.valueToEntry.get(entry)).map(RegistryEntry.Reference::registryKey);
	}

	@Override
	public int getRawId(@Nullable T value) {
		return this.entryToRawId.getInt(value);
	}

	@Nullable
	@Override
	public T get(@Nullable RegistryKey<T> key) {
		RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)this.keyToEntry.get(key);
		return getValue(reference);
	}

	@Nullable
	@Override
	public T get(int index) {
		return (T)(index >= 0 && index < this.rawIdToEntry.size() ? ((RegistryEntry.Reference)this.rawIdToEntry.get(index)).value() : null);
	}

	@Override
	public Optional<RegistryEntry<T>> getEntry(int rawId) {
		return rawId >= 0 && rawId < this.rawIdToEntry.size() ? Optional.ofNullable((RegistryEntry)this.rawIdToEntry.get(rawId)) : Optional.empty();
	}

	@Override
	public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {
		return Optional.ofNullable((RegistryEntry)this.keyToEntry.get(key));
	}

	@Override
	public RegistryEntry<T> getOrCreateEntry(RegistryKey<T> key) {
		return (RegistryEntry<T>)this.keyToEntry.computeIfAbsent(key, keyx -> {
			if (this.valueToEntryFunction != null) {
				throw new IllegalStateException("This registry can't create new holders without value");
			} else {
				this.assertNotFrozen(keyx);
				return RegistryEntry.Reference.standAlone(this, keyx);
			}
		});
	}

	@Override
	public int size() {
		return this.keyToEntry.size();
	}

	@Override
	public Lifecycle getEntryLifecycle(T entry) {
		return (Lifecycle)this.entryToLifecycle.get(entry);
	}

	@Override
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	public Iterator<T> iterator() {
		return this.rawIdToEntry.stream().mapMulti((entry, valueConsumer) -> {
			if (entry != null) {
				valueConsumer.accept(entry.value());
			}
		}).iterator();
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier id) {
		RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)this.idToEntry.get(id);
		return getValue(reference);
	}

	@Nullable
	private static <T> T getValue(@Nullable RegistryEntry.Reference<T> entry) {
		return entry != null ? entry.value() : null;
	}

	@Override
	public Set<Identifier> getIds() {
		return Collections.unmodifiableSet(this.idToEntry.keySet());
	}

	@Override
	public Set<Entry<RegistryKey<T>, T>> getEntries() {
		return Collections.unmodifiableSet(Maps.transformValues(this.keyToEntry, RegistryEntry::value).entrySet());
	}

	@Override
	public Stream<RegistryEntry.Reference<T>> streamEntries() {
		return this.keyToEntry.values().stream();
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
		RegistryEntryList.Named<T> named = (RegistryEntryList.Named<T>)this.tagToEntryList.get(tag);
		if (named == null) {
			named = new RegistryEntryList.Named<>(tag);
			Map<TagKey<T>, RegistryEntryList.Named<T>> map = new IdentityHashMap(this.tagToEntryList);
			map.put(tag, named);
			this.tagToEntryList = map;
		}

		return named;
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
		if (this.randomEntries == null) {
			this.randomEntries = List.copyOf(this.keyToEntry.values());
		}

		return Util.getRandomOrEmpty(this.randomEntries, random);
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
		List<RegistryKey<T>> list = this.keyToEntry
			.entrySet()
			.stream()
			.filter(entry -> !((RegistryEntry.Reference)entry.getValue()).hasKeyAndValue())
			.map(Entry::getKey)
			.sorted()
			.toList();
		if (!list.isEmpty()) {
			throw new IllegalStateException("Unbound values in registry: " + list);
		} else {
			if (this.unfrozenValueToEntry != null) {
				List<RegistryEntry.Reference<T>> list2 = this.unfrozenValueToEntry.values().stream().filter(entry -> !entry.hasKeyAndValue()).toList();
				if (!list2.isEmpty()) {
					throw new IllegalStateException("Some intrusive holders were not added to registry: " + list2);
				}

				this.unfrozenValueToEntry = null;
			}

			return this;
		}
	}

	@Override
	public RegistryEntry.Reference<T> createEntry(T value) {
		if (this.valueToEntryFunction == null) {
			throw new IllegalStateException("This registry can't create intrusive holders");
		} else if (!this.frozen && this.unfrozenValueToEntry != null) {
			return (RegistryEntry.Reference<T>)this.unfrozenValueToEntry.computeIfAbsent(value, key -> RegistryEntry.Reference.intrusive(this, (T)key));
		} else {
			throw new IllegalStateException("Registry is already frozen");
		}
	}

	@Override
	public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
		return Optional.ofNullable((RegistryEntryList.Named)this.tagToEntryList.get(tag));
	}

	@Override
	public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {
		Map<RegistryEntry.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap();
		this.keyToEntry.values().forEach(entry -> map.put(entry, new ArrayList()));
		tagEntries.forEach((tag, entries) -> {
			for (RegistryEntry<T> registryEntry : entries) {
				if (!registryEntry.setRegistry(this)) {
					throw new IllegalStateException("Can't create named set " + tag + " containing value " + registryEntry + " from outside registry " + this);
				}

				if (!(registryEntry instanceof RegistryEntry.Reference<T> reference)) {
					throw new IllegalStateException("Found direct holder " + registryEntry + " value in tag " + tag);
				}

				((List)map.get(reference)).add(tag);
			}
		});
		Map<TagKey<T>, RegistryEntryList.Named<T>> map2 = new IdentityHashMap(this.tagToEntryList);
		tagEntries.forEach((tag, entries) -> ((RegistryEntryList.Named)map2.computeIfAbsent(tag, RegistryEntryList.Named::new)).copyOf(entries));
		map.forEach(RegistryEntry.Reference::setTags);
		this.tagToEntryList = map2;
	}

	@Override
	public void clearTags() {
		this.tagToEntryList.values().forEach(entryList -> entryList.copyOf(List.of()));
		this.keyToEntry.values().forEach(entry -> entry.setTags(Set.of()));
	}
}
