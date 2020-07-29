package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryCodec;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private final ObjectList<T> field_26682 = new ObjectArrayList<>(256);
	private final Object2IntMap<T> field_26683 = new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());
	private final BiMap<Identifier, T> entriesById;
	private final BiMap<RegistryKey<T>, T> entriesByKey;
	protected Object[] randomEntries;
	private int nextId;

	public SimpleRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
		this.field_26683.defaultReturnValue(-1);
		this.entriesById = HashBiMap.create();
		this.entriesByKey = HashBiMap.create();
	}

	public static <T> MapCodec<SimpleRegistry.class_5501<T>> method_30929(RegistryKey<? extends Registry<T>> registryKey, MapCodec<T> mapCodec) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("name").forGetter(arg -> arg.field_26684),
						Codec.INT.fieldOf("id").forGetter(arg -> arg.field_26685),
						mapCodec.forGetter(arg -> arg.field_26686)
					)
					.apply(instance, SimpleRegistry.class_5501::new)
		);
	}

	@Override
	public <V extends T> V set(int rawId, RegistryKey<T> key, V entry) {
		return this.method_31051(rawId, key, entry, true);
	}

	private <V extends T> V method_31051(int i, RegistryKey<T> registryKey, V object, boolean bl) {
		Validate.notNull(registryKey);
		Validate.notNull(object);
		this.field_26682.size(Math.max(this.field_26682.size(), i + 1));
		this.field_26682.set(i, object);
		this.field_26683.put((T)object, i);
		this.randomEntries = null;
		if (bl && this.entriesByKey.containsKey(registryKey)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", registryKey);
		}

		if (this.entriesById.containsValue(object)) {
			LOGGER.error("Adding duplicate value '{}' to registry", object);
		}

		this.entriesById.put(registryKey.getValue(), (T)object);
		this.entriesByKey.put(registryKey, (T)object);
		if (this.nextId <= i) {
			this.nextId = i + 1;
		}

		return object;
	}

	@Override
	public <V extends T> V add(RegistryKey<T> key, V entry) {
		return this.set(this.nextId, key, entry);
	}

	@Override
	public <V extends T> V method_31062(RegistryKey<T> registryKey, V object) {
		Validate.notNull(registryKey);
		Validate.notNull(object);
		T object2 = (T)this.entriesByKey.get(registryKey);
		int i;
		if (object2 == null) {
			i = this.nextId;
		} else {
			i = this.field_26683.getInt(object2);
			this.field_26683.removeInt(object2);
		}

		return this.method_31051(i, registryKey, object, false);
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
	public int getRawId(@Nullable T object) {
		return this.field_26683.getInt(object);
	}

	@Nullable
	@Override
	public T get(@Nullable RegistryKey<T> key) {
		return (T)this.entriesByKey.get(key);
	}

	@Nullable
	@Override
	public T get(int index) {
		return (T)(index >= 0 && index < this.field_26682.size() ? this.field_26682.get(index) : null);
	}

	public Iterator<T> iterator() {
		return Iterators.filter(this.field_26682.iterator(), Objects::nonNull);
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier id) {
		return (T)this.entriesById.get(id);
	}

	@Override
	public Set<Identifier> getIds() {
		return Collections.unmodifiableSet(this.entriesById.keySet());
	}

	@Override
	public Set<Entry<RegistryKey<T>, T>> getEntries() {
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

	public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
		return method_30929(registryKey, codec.fieldOf("element")).codec().listOf().xmap(list -> {
			SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);

			for (SimpleRegistry.class_5501<T> lv : list) {
				simpleRegistry.set(lv.field_26685, lv.field_26684, lv.field_26686);
			}

			return simpleRegistry;
		}, simpleRegistry -> {
			Builder<SimpleRegistry.class_5501<T>> builder = ImmutableList.builder();

			for (T object : simpleRegistry) {
				builder.add(new SimpleRegistry.class_5501<>((RegistryKey<T>)simpleRegistry.getKey(object).get(), simpleRegistry.getRawId(object), object));
			}

			return builder.build();
		});
	}

	public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> codec) {
		return RegistryCodec.of(registryRef, lifecycle, codec);
	}

	public static <T> Codec<SimpleRegistry<T>> method_31059(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> codec) {
		return Codec.unboundedMap(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue), codec).xmap(map -> {
			SimpleRegistry<T> simpleRegistry = new SimpleRegistry<>(registryKey, lifecycle);
			map.forEach(simpleRegistry::add);
			return simpleRegistry;
		}, simpleRegistry -> ImmutableMap.copyOf(simpleRegistry.entriesByKey));
	}

	public static class class_5501<T> {
		public final RegistryKey<T> field_26684;
		public final int field_26685;
		public final T field_26686;

		public class_5501(RegistryKey<T> registryKey, int i, T object) {
			this.field_26684 = registryKey;
			this.field_26685 = i;
			this.field_26686 = object;
		}
	}
}
