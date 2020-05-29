package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	protected final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
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

	@Environment(EnvType.CLIENT)
	@Override
	public Optional<RegistryKey<T>> getKey(T value) {
		return Optional.ofNullable(this.entriesByKey.inverse().get(value));
	}

	@Override
	public int getRawId(@Nullable T entry) {
		return this.indexedEntries.getId(entry);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
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
				Builder<Pair<RegistryKey<T>, T>> builder = ImmutableList.builder();

				for (Entry<RegistryKey<T>, T> entry : simpleRegistry.entriesByKey.entrySet()) {
					builder.add(Pair.of((RegistryKey<T>)entry.getKey(), (T)entry.getValue()));
				}

				return builder.build();
			});
	}
}
