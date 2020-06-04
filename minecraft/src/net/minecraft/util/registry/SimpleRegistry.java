package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_5380;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap<>(256);
	protected final BiMap<Identifier, T> entriesById = HashBiMap.create();
	private final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
	private final Set<RegistryKey<T>> field_25489 = Sets.newIdentityHashSet();
	protected Object[] randomEntries;
	private int nextId;

	public SimpleRegistry(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	@Override
	public <V extends T> V set(int rawId, RegistryKey<T> registryKey, V entry) {
		this.indexedEntries.put((T)entry, rawId);
		Validate.notNull(registryKey);
		Validate.notNull(entry);
		this.randomEntries = null;
		if (this.entriesByKey.containsKey(registryKey)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", registryKey);
		}

		this.entriesById.put(registryKey.getValue(), (T)entry);
		this.entriesByKey.put(registryKey, (T)entry);
		if (this.nextId <= rawId) {
			this.nextId = rawId + 1;
		}

		return entry;
	}

	@Override
	public <V extends T> V add(RegistryKey<T> registryKey, V entry) {
		return this.set(this.nextId, registryKey, entry);
	}

	@Nullable
	@Override
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

	@Nullable
	@Override
	public T get(@Nullable RegistryKey<T> registryKey) {
		return (T)this.entriesByKey.get(registryKey);
	}

	@Nullable
	@Override
	public T get(int index) {
		return this.indexedEntries.get(index);
	}

	public Iterator<T> iterator() {
		return this.indexedEntries.iterator();
	}

	@Nullable
	@Override
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

	public Set<Entry<RegistryKey<T>, T>> method_29722() {
		return Collections.unmodifiableMap(this.entriesByKey).entrySet();
	}

	@Nullable
	public T getRandom(Random random) {
		if (this.randomEntries == null) {
			Collection<?> collection = this.entriesById.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.randomEntries = collection.toArray(new Object[collection.size()]);
		}

		return Util.getRandom((T[])this.randomEntries, random);
	}

	@Override
	public boolean containsId(Identifier id) {
		return this.entriesById.containsKey(id);
	}

	@Override
	public boolean containsId(int id) {
		return this.indexedEntries.containsId(id);
	}

	public boolean method_29723(RegistryKey<T> registryKey) {
		return this.field_25489.contains(registryKey);
	}

	public void method_29725(RegistryKey<T> registryKey) {
		this.field_25489.add(registryKey);
	}

	public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
		return Codec.mapPair(Identifier.field_25139.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("key"), codec.fieldOf("element"))
			.codec()
			.listOf()
			.xmap(list -> {
				SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);

				for (Pair<RegistryKey<T>, T> pair : list) {
					simpleRegistry.add(pair.getFirst(), pair.getSecond());
				}

				return simpleRegistry;
			}, simpleRegistry -> {
				com.google.common.collect.ImmutableList.Builder<Pair<RegistryKey<T>, T>> builder = ImmutableList.builder();

				for (T object : simpleRegistry.indexedEntries) {
					builder.add(Pair.of((RegistryKey<T>)simpleRegistry.getKey(object).get(), object));
				}

				return builder.build();
			});
	}

	public static <T> Codec<SimpleRegistry<T>> method_29721(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
		return class_5380.method_29745(registryKey, lifecycle, codec);
	}

	public static <T> Codec<SimpleRegistry<T>> method_29724(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
		return Codec.unboundedMap(Identifier.field_25139.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue), codec).xmap(map -> {
			SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);
			map.forEach((registryKeyxx, object) -> {
				simpleRegistry.set(simpleRegistry.nextId, registryKeyxx, object);
				simpleRegistry.method_29725(registryKeyxx);
			});
			return simpleRegistry;
		}, simpleRegistry -> {
			Builder<RegistryKey<T>, T> builder = ImmutableMap.builder();
			simpleRegistry.entriesByKey.entrySet().stream().filter(entry -> simpleRegistry.method_29723((RegistryKey<T>)entry.getKey())).forEach(builder::put);
			return builder.build();
		});
	}
}
