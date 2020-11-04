package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryCodec;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private final ObjectList<T> rawIdToEntry = new ObjectArrayList<>(256);
	private final Object2IntMap<T> entryToRawId = new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());
	private final BiMap<Identifier, T> idToEntry;
	private final BiMap<RegistryKey<T>, T> keyToEntry;
	private final Map<T, Lifecycle> entryToLifecycle;
	private Lifecycle lifecycle;
	protected Object[] randomEntries;
	private int nextId;

	public SimpleRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
		this.entryToRawId.defaultReturnValue(-1);
		this.idToEntry = HashBiMap.create();
		this.keyToEntry = HashBiMap.create();
		this.entryToLifecycle = Maps.<T, Lifecycle>newIdentityHashMap();
		this.lifecycle = lifecycle;
	}

	public static <T> MapCodec<SimpleRegistry.RegistryManagerEntry<T>> createRegistryManagerEntryCodec(
		RegistryKey<? extends Registry<T>> registryKey, MapCodec<T> entryCodec
	) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC
							.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue)
							.fieldOf("name")
							.forGetter(registryManagerEntry -> registryManagerEntry.key),
						Codec.INT.fieldOf("id").forGetter(registryManagerEntry -> registryManagerEntry.rawId),
						entryCodec.forGetter(registryManagerEntry -> registryManagerEntry.entry)
					)
					.apply(instance, SimpleRegistry.RegistryManagerEntry::new)
		);
	}

	@Override
	public <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle) {
		return this.set(rawId, key, entry, lifecycle, true);
	}

	private <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle, boolean checkDuplicateKeys) {
		Validate.notNull(key);
		Validate.notNull(entry);
		this.rawIdToEntry.size(Math.max(this.rawIdToEntry.size(), rawId + 1));
		this.rawIdToEntry.set(rawId, entry);
		this.entryToRawId.put((T)entry, rawId);
		this.randomEntries = null;
		if (checkDuplicateKeys && this.keyToEntry.containsKey(key)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", key);
		}

		if (this.idToEntry.containsValue(entry)) {
			LOGGER.error("Adding duplicate value '{}' to registry", entry);
		}

		this.idToEntry.put(key.getValue(), (T)entry);
		this.keyToEntry.put(key, (T)entry);
		this.entryToLifecycle.put(entry, lifecycle);
		this.lifecycle = this.lifecycle.add(lifecycle);
		if (this.nextId <= rawId) {
			this.nextId = rawId + 1;
		}

		return entry;
	}

	@Override
	public <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle) {
		return this.set(this.nextId, key, entry, lifecycle);
	}

	@Override
	public <V extends T> V replace(OptionalInt rawId, RegistryKey<T> key, V newEntry, Lifecycle lifecycle) {
		Validate.notNull(key);
		Validate.notNull(newEntry);
		T object = (T)this.keyToEntry.get(key);
		int i;
		if (object == null) {
			i = rawId.isPresent() ? rawId.getAsInt() : this.nextId;
		} else {
			i = this.entryToRawId.getInt(object);
			if (rawId.isPresent() && rawId.getAsInt() != i) {
				throw new IllegalStateException("ID mismatch");
			}

			this.entryToRawId.removeInt(object);
			this.entryToLifecycle.remove(object);
		}

		return this.set(i, key, newEntry, lifecycle, false);
	}

	@Nullable
	@Override
	public Identifier getId(T entry) {
		return (Identifier)this.idToEntry.inverse().get(entry);
	}

	@Override
	public Optional<RegistryKey<T>> getKey(T entry) {
		return Optional.ofNullable(this.keyToEntry.inverse().get(entry));
	}

	@Override
	public int getRawId(@Nullable T entry) {
		return this.entryToRawId.getInt(entry);
	}

	@Nullable
	@Override
	public T get(@Nullable RegistryKey<T> key) {
		return (T)this.keyToEntry.get(key);
	}

	@Nullable
	@Override
	public T get(int index) {
		return (T)(index >= 0 && index < this.rawIdToEntry.size() ? this.rawIdToEntry.get(index) : null);
	}

	@Override
	public Lifecycle getEntryLifecycle(T object) {
		return (Lifecycle)this.entryToLifecycle.get(object);
	}

	@Override
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	public Iterator<T> iterator() {
		return Iterators.filter(this.rawIdToEntry.iterator(), Objects::nonNull);
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier id) {
		return (T)this.idToEntry.get(id);
	}

	@Override
	public Set<Identifier> getIds() {
		return Collections.unmodifiableSet(this.idToEntry.keySet());
	}

	@Override
	public Set<Entry<RegistryKey<T>, T>> getEntries() {
		return Collections.unmodifiableMap(this.keyToEntry).entrySet();
	}

	@Nullable
	public T getRandom(Random random) {
		if (this.randomEntries == null) {
			Collection<?> collection = this.idToEntry.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.randomEntries = collection.toArray(new Object[collection.size()]);
		}

		return Util.getRandom((T[])this.randomEntries, random);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean containsId(Identifier id) {
		return this.idToEntry.containsKey(id);
	}

	public static <T> Codec<SimpleRegistry<T>> createRegistryManagerCodec(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> entryCodec) {
		return createRegistryManagerEntryCodec(registryKey, entryCodec.fieldOf("element")).codec().listOf().xmap(list -> {
			SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);

			for (SimpleRegistry.RegistryManagerEntry<T> registryManagerEntry : list) {
				simpleRegistry.set(registryManagerEntry.rawId, registryManagerEntry.key, registryManagerEntry.entry, lifecycle);
			}

			return simpleRegistry;
		}, simpleRegistry -> {
			Builder<SimpleRegistry.RegistryManagerEntry<T>> builder = ImmutableList.builder();

			for (T object : simpleRegistry) {
				builder.add(new SimpleRegistry.RegistryManagerEntry<>((RegistryKey<T>)simpleRegistry.getKey(object).get(), simpleRegistry.getRawId(object), object));
			}

			return builder.build();
		});
	}

	public static <T> Codec<SimpleRegistry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> entryCodec) {
		return RegistryCodec.of(registryRef, lifecycle, entryCodec);
	}

	public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> entryCodec) {
		return Codec.unboundedMap(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue), entryCodec).xmap(map -> {
			SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);
			map.forEach((registryKeyxx, object) -> simpleRegistry.add(registryKeyxx, object, lifecycle));
			return simpleRegistry;
		}, simpleRegistry -> ImmutableMap.copyOf(simpleRegistry.keyToEntry));
	}

	public static class RegistryManagerEntry<T> {
		public final RegistryKey<T> key;
		public final int rawId;
		public final T entry;

		public RegistryManagerEntry(RegistryKey<T> key, int rawId, T entry) {
			this.key = key;
			this.rawId = rawId;
			this.entry = entry;
		}
	}
}
