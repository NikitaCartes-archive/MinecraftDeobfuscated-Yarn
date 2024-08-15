package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

/**
 * An implementation of a mutable registry. All vanilla registries use this (or its
 * subclass, {@link DefaultedRegistry}).
 * 
 * @see Registry
 */
public class SimpleRegistry<T> implements MutableRegistry<T> {
	final RegistryKey<? extends Registry<T>> key;
	private final ObjectList<RegistryEntry.Reference<T>> rawIdToEntry = new ObjectArrayList<>(256);
	private final Reference2IntMap<T> entryToRawId = Util.make(new Reference2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
	private final Map<Identifier, RegistryEntry.Reference<T>> idToEntry = new HashMap();
	private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry = new HashMap();
	private final Map<T, RegistryEntry.Reference<T>> valueToEntry = new IdentityHashMap();
	private final Map<RegistryKey<T>, RegistryEntryInfo> keyToEntryInfo = new IdentityHashMap();
	private Lifecycle lifecycle;
	private final Map<TagKey<T>, RegistryEntryList.Named<T>> tags = new IdentityHashMap();
	SimpleRegistry.TagLookup<T> tagLookup = SimpleRegistry.TagLookup.ofUnbound();
	private boolean frozen;
	@Nullable
	private Map<T, RegistryEntry.Reference<T>> intrusiveValueToEntry;
	private final RegistryWrapper.Impl<T> wrapper = new RegistryWrapper.Impl<T>() {
		@Override
		public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
			return SimpleRegistry.this.key;
		}

		@Override
		public Lifecycle getLifecycle() {
			return SimpleRegistry.this.getLifecycle();
		}

		@Override
		public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
			return SimpleRegistry.this.getEntry(key);
		}

		@Override
		public Stream<RegistryEntry.Reference<T>> streamEntries() {
			return SimpleRegistry.this.streamEntries();
		}

		@Override
		public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
			return SimpleRegistry.this.getEntryList(tag);
		}

		@Override
		public Stream<RegistryEntryList.Named<T>> streamTags() {
			return SimpleRegistry.this.streamTags();
		}
	};

	public SimpleRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		this(key, lifecycle, false);
	}

	public SimpleRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean intrusive) {
		this.key = key;
		this.lifecycle = lifecycle;
		if (intrusive) {
			this.intrusiveValueToEntry = new IdentityHashMap();
		}
	}

	@Override
	public RegistryKey<? extends Registry<T>> getKey() {
		return this.key;
	}

	public String toString() {
		return "Registry[" + this.key + " (" + this.lifecycle + ")]";
	}

	private void assertNotFrozen() {
		if (this.frozen) {
			throw new IllegalStateException("Registry is already frozen");
		}
	}

	private void assertNotFrozen(RegistryKey<T> key) {
		if (this.frozen) {
			throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
		}
	}

	@Override
	public RegistryEntry.Reference<T> add(RegistryKey<T> key, T value, RegistryEntryInfo info) {
		this.assertNotFrozen(key);
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		if (this.idToEntry.containsKey(key.getValue())) {
			throw (IllegalStateException)Util.getFatalOrPause((T)(new IllegalStateException("Adding duplicate key '" + key + "' to registry")));
		} else if (this.valueToEntry.containsKey(value)) {
			throw (IllegalStateException)Util.getFatalOrPause((T)(new IllegalStateException("Adding duplicate value '" + value + "' to registry")));
		} else {
			RegistryEntry.Reference<T> reference;
			if (this.intrusiveValueToEntry != null) {
				reference = (RegistryEntry.Reference<T>)this.intrusiveValueToEntry.remove(value);
				if (reference == null) {
					throw new AssertionError("Missing intrusive holder for " + key + ":" + value);
				}

				reference.setRegistryKey(key);
			} else {
				reference = (RegistryEntry.Reference<T>)this.keyToEntry.computeIfAbsent(key, k -> RegistryEntry.Reference.standAlone(this.getEntryOwner(), k));
			}

			this.keyToEntry.put(key, reference);
			this.idToEntry.put(key.getValue(), reference);
			this.valueToEntry.put(value, reference);
			int i = this.rawIdToEntry.size();
			this.rawIdToEntry.add(reference);
			this.entryToRawId.put(value, i);
			this.keyToEntryInfo.put(key, info);
			this.lifecycle = this.lifecycle.add(info.lifecycle());
			return reference;
		}
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
		return getValue((RegistryEntry.Reference<T>)this.keyToEntry.get(key));
	}

	@Nullable
	@Override
	public T get(int index) {
		return (T)(index >= 0 && index < this.rawIdToEntry.size() ? ((RegistryEntry.Reference)this.rawIdToEntry.get(index)).value() : null);
	}

	@Override
	public Optional<RegistryEntry.Reference<T>> getEntry(int rawId) {
		return rawId >= 0 && rawId < this.rawIdToEntry.size() ? Optional.ofNullable((RegistryEntry.Reference)this.rawIdToEntry.get(rawId)) : Optional.empty();
	}

	@Override
	public Optional<RegistryEntry.Reference<T>> getEntry(Identifier id) {
		return Optional.ofNullable((RegistryEntry.Reference)this.idToEntry.get(id));
	}

	@Override
	public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {
		return Optional.ofNullable((RegistryEntry.Reference)this.keyToEntry.get(key));
	}

	@Override
	public Optional<RegistryEntry.Reference<T>> getDefaultEntry() {
		return this.rawIdToEntry.isEmpty() ? Optional.empty() : Optional.of((RegistryEntry.Reference)this.rawIdToEntry.getFirst());
	}

	@Override
	public RegistryEntry<T> getEntry(T value) {
		RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)this.valueToEntry.get(value);
		return (RegistryEntry<T>)(reference != null ? reference : RegistryEntry.of(value));
	}

	RegistryEntry.Reference<T> getOrCreateEntry(RegistryKey<T> key) {
		return (RegistryEntry.Reference<T>)this.keyToEntry.computeIfAbsent(key, key2 -> {
			if (this.intrusiveValueToEntry != null) {
				throw new IllegalStateException("This registry can't create new holders without value");
			} else {
				this.assertNotFrozen(key2);
				return RegistryEntry.Reference.standAlone(this.getEntryOwner(), key2);
			}
		});
	}

	@Override
	public int size() {
		return this.keyToEntry.size();
	}

	@Override
	public Optional<RegistryEntryInfo> getEntryInfo(RegistryKey<T> key) {
		return Optional.ofNullable((RegistryEntryInfo)this.keyToEntryInfo.get(key));
	}

	@Override
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	public Iterator<T> iterator() {
		return Iterators.transform(this.rawIdToEntry.iterator(), RegistryEntry::value);
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
	public Set<RegistryKey<T>> getKeys() {
		return Collections.unmodifiableSet(this.keyToEntry.keySet());
	}

	@Override
	public Set<Entry<RegistryKey<T>, T>> getEntrySet() {
		return Collections.unmodifiableSet(Maps.transformValues(this.keyToEntry, RegistryEntry::value).entrySet());
	}

	@Override
	public Stream<RegistryEntry.Reference<T>> streamEntries() {
		return this.rawIdToEntry.stream();
	}

	@Override
	public Stream<RegistryEntryList.Named<T>> streamTags() {
		return this.tagLookup.stream();
	}

	RegistryEntryList.Named<T> getTag(TagKey<T> key) {
		return (RegistryEntryList.Named<T>)this.tags.computeIfAbsent(key, this::createNamedEntryList);
	}

	private RegistryEntryList.Named<T> createNamedEntryList(TagKey<T> tag) {
		return new RegistryEntryList.Named<>(this.getEntryOwner(), tag);
	}

	@Override
	public boolean isEmpty() {
		return this.keyToEntry.isEmpty();
	}

	@Override
	public Optional<RegistryEntry.Reference<T>> getRandom(Random random) {
		return Util.getRandomOrEmpty(this.rawIdToEntry, random);
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
		if (this.frozen) {
			return this;
		} else {
			this.frozen = true;
			this.valueToEntry.forEach((value, entry) -> entry.setValue(value));
			List<Identifier> list = this.keyToEntry
				.entrySet()
				.stream()
				.filter(entry -> !((RegistryEntry.Reference)entry.getValue()).hasKeyAndValue())
				.map(entry -> ((RegistryKey)entry.getKey()).getValue())
				.sorted()
				.toList();
			if (!list.isEmpty()) {
				throw new IllegalStateException("Unbound values in registry " + this.getKey() + ": " + list);
			} else {
				if (this.intrusiveValueToEntry != null) {
					if (!this.intrusiveValueToEntry.isEmpty()) {
						throw new IllegalStateException("Some intrusive holders were not registered: " + this.intrusiveValueToEntry.values());
					}

					this.intrusiveValueToEntry = null;
				}

				if (this.tagLookup.isBound()) {
					throw new IllegalStateException("Tags already present before freezing");
				} else {
					List<Identifier> list2 = this.tags
						.entrySet()
						.stream()
						.filter(entry -> !((RegistryEntryList.Named)entry.getValue()).isBound())
						.map(entry -> ((TagKey)entry.getKey()).id())
						.sorted()
						.toList();
					if (!list2.isEmpty()) {
						throw new IllegalStateException("Unbound tags in registry " + this.getKey() + ": " + list2);
					} else {
						this.tagLookup = SimpleRegistry.TagLookup.fromMap(this.tags);
						this.refreshTags();
						return this;
					}
				}
			}
		}
	}

	@Override
	public RegistryEntry.Reference<T> createEntry(T value) {
		if (this.intrusiveValueToEntry == null) {
			throw new IllegalStateException("This registry can't create intrusive holders");
		} else {
			this.assertNotFrozen();
			return (RegistryEntry.Reference<T>)this.intrusiveValueToEntry
				.computeIfAbsent(value, valuex -> RegistryEntry.Reference.intrusive(this.getReadOnlyWrapper(), (T)valuex));
		}
	}

	@Override
	public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
		return this.tagLookup.getOptional(tag);
	}

	private RegistryEntry.Reference<T> ensureTagable(TagKey<T> key, RegistryEntry<T> entry) {
		if (!entry.ownerEquals(this.getEntryOwner())) {
			throw new IllegalStateException("Can't create named set " + key + " containing value " + entry + " from outside registry " + this);
		} else if (entry instanceof RegistryEntry.Reference) {
			return (RegistryEntry.Reference<T>)entry;
		} else {
			throw new IllegalStateException("Found direct holder " + entry + " value in tag " + key);
		}
	}

	@Override
	public void setEntries(TagKey<T> tag, List<RegistryEntry<T>> entries) {
		this.assertNotFrozen();
		this.getTag(tag).setEntries(entries);
	}

	void refreshTags() {
		Map<RegistryEntry.Reference<T>, List<TagKey<T>>> map = new IdentityHashMap();
		this.keyToEntry.values().forEach(key -> map.put(key, new ArrayList()));
		this.tagLookup.forEach((key, value) -> {
			for (RegistryEntry<T> registryEntry : value) {
				RegistryEntry.Reference<T> reference = this.ensureTagable(key, registryEntry);
				((List)map.get(reference)).add(key);
			}
		});
		map.forEach(RegistryEntry.Reference::setTags);
	}

	public void resetTagEntries() {
		this.assertNotFrozen();
		this.tags.values().forEach(tag -> tag.setEntries(List.of()));
	}

	@Override
	public RegistryEntryLookup<T> createMutableEntryLookup() {
		this.assertNotFrozen();
		return new RegistryEntryLookup<T>() {
			@Override
			public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return Optional.of(this.getOrThrow(key));
			}

			@Override
			public RegistryEntry.Reference<T> getOrThrow(RegistryKey<T> key) {
				return SimpleRegistry.this.getOrCreateEntry(key);
			}

			@Override
			public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
				return Optional.of(this.getOrThrow(tag));
			}

			@Override
			public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
				return SimpleRegistry.this.getTag(tag);
			}
		};
	}

	@Override
	public RegistryEntryOwner<T> getEntryOwner() {
		return this.wrapper;
	}

	@Override
	public RegistryWrapper.Impl<T> getReadOnlyWrapper() {
		return this.wrapper;
	}

	@Override
	public Registry.PendingTagLoad<T> startTagReload(TagGroupLoader.RegistryTags<T> tags) {
		if (!this.frozen) {
			throw new IllegalStateException("Invalid method used for tag loading");
		} else {
			Builder<TagKey<T>, RegistryEntryList.Named<T>> builder = ImmutableMap.builder();
			final Map<TagKey<T>, List<RegistryEntry<T>>> map = new HashMap();
			tags.tags().forEach((key, values) -> {
				RegistryEntryList.Named<T> named = (RegistryEntryList.Named<T>)this.tags.get(key);
				if (named == null) {
					named = this.createNamedEntryList(key);
				}

				builder.put(key, named);
				map.put(key, List.copyOf(values));
			});
			final ImmutableMap<TagKey<T>, RegistryEntryList.Named<T>> immutableMap = builder.build();
			final RegistryWrapper.Impl<T> impl = new RegistryWrapper.Impl.Delegating<T>() {
				@Override
				public RegistryWrapper.Impl<T> getBase() {
					return SimpleRegistry.this.getReadOnlyWrapper();
				}

				@Override
				public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
					return Optional.ofNullable(immutableMap.get(tag));
				}

				@Override
				public Stream<RegistryEntryList.Named<T>> streamTags() {
					return immutableMap.values().stream();
				}
			};
			return new Registry.PendingTagLoad<T>() {
				@Override
				public RegistryKey<? extends Registry<? extends T>> getKey() {
					return SimpleRegistry.this.getKey();
				}

				@Override
				public RegistryWrapper.Impl<T> getLookup() {
					return impl;
				}

				@Override
				public void apply() {
					immutableMap.forEach((key, value) -> {
						List<RegistryEntry<T>> list = (List<RegistryEntry<T>>)map.getOrDefault(key, List.of());
						value.setEntries(list);
					});
					SimpleRegistry.this.tagLookup = SimpleRegistry.TagLookup.fromMap(immutableMap);
					SimpleRegistry.this.refreshTags();
				}
			};
		}
	}

	interface TagLookup<T> {
		static <T> SimpleRegistry.TagLookup<T> ofUnbound() {
			return new SimpleRegistry.TagLookup<T>() {
				@Override
				public boolean isBound() {
					return false;
				}

				@Override
				public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key) {
					throw new IllegalStateException("Tags not bound, trying to access " + key);
				}

				@Override
				public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer) {
					throw new IllegalStateException("Tags not bound");
				}

				@Override
				public Stream<RegistryEntryList.Named<T>> stream() {
					throw new IllegalStateException("Tags not bound");
				}
			};
		}

		static <T> SimpleRegistry.TagLookup<T> fromMap(Map<TagKey<T>, RegistryEntryList.Named<T>> map) {
			return new SimpleRegistry.TagLookup<T>() {
				@Override
				public boolean isBound() {
					return true;
				}

				@Override
				public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key) {
					return Optional.ofNullable((RegistryEntryList.Named)map.get(key));
				}

				@Override
				public void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer) {
					map.forEach(consumer);
				}

				@Override
				public Stream<RegistryEntryList.Named<T>> stream() {
					return map.values().stream();
				}
			};
		}

		boolean isBound();

		Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> key);

		void forEach(BiConsumer<? super TagKey<T>, ? super RegistryEntryList.Named<T>> consumer);

		Stream<RegistryEntryList.Named<T>> stream();
	}
}
