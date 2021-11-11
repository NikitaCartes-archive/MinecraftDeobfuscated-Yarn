package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private final EntryLoader entryLoader;
	private final DynamicRegistryManager registryManager;
	private final Map<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders;
	private final RegistryOps<JsonElement> entryOps;

	public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DynamicRegistryManager registryManager) {
		return ofLoaded(dynamicOps, EntryLoader.resourceBacked(resourceManager), registryManager);
	}

	public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> dynamicOps, EntryLoader entryLoader, DynamicRegistryManager registryManager) {
		RegistryOps<T> registryOps = new RegistryOps<>(dynamicOps, entryLoader, registryManager, Maps.newIdentityHashMap());
		DynamicRegistryManager.load(registryManager, registryOps);
		return registryOps;
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryManager) {
		return of(delegate, EntryLoader.resourceBacked(resourceManager), registryManager);
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, EntryLoader entryLoader, DynamicRegistryManager registryManager) {
		return new RegistryOps<>(delegate, entryLoader, registryManager, Maps.newIdentityHashMap());
	}

	private RegistryOps(
		DynamicOps<T> delegate,
		EntryLoader entryLoader,
		DynamicRegistryManager registryManager,
		IdentityHashMap<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders
	) {
		super(delegate);
		this.entryLoader = entryLoader;
		this.registryManager = registryManager;
		this.valueHolders = valueHolders;
		this.entryOps = delegate == JsonOps.INSTANCE ? this : new RegistryOps<>(JsonOps.INSTANCE, entryLoader, registryManager, valueHolders);
	}

	/**
	 * Encode an id for a registry element than a full object if possible.
	 * 
	 * <p>This method is called by casting an arbitrary dynamic ops to a registry
	 * reading ops.
	 * 
	 * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, Codec)
	 */
	protected <E> DataResult<Pair<Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> key, Codec<E> codec, boolean allowInlineDefinitions) {
		Optional<MutableRegistry<E>> optional = this.registryManager.getOptionalMutable(key);
		if (!optional.isPresent()) {
			return DataResult.error("Unknown registry: " + key);
		} else {
			MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)optional.get();
			DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(this.delegate, object);
			if (!dataResult.result().isPresent()) {
				return !allowInlineDefinitions
					? DataResult.error("Inline definitions not allowed here")
					: codec.decode(this, object).map(pairx -> pairx.mapFirst(objectx -> () -> objectx));
			} else {
				Pair<Identifier, T> pair = (Pair<Identifier, T>)dataResult.result().get();
				RegistryKey<E> registryKey = RegistryKey.of(key, pair.getFirst());
				return this.readSupplier(key, mutableRegistry, codec, registryKey).map(supplier -> Pair.of(supplier, pair.getSecond()));
			}
		}
	}

	/**
	 * Loads elements into a registry just loaded from a decoder.
	 */
	public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> key, Codec<E> codec) {
		Collection<RegistryKey<E>> collection = this.entryLoader.getKnownEntryPaths(key);
		DataResult<SimpleRegistry<E>> dataResult = DataResult.success(registry, Lifecycle.stable());

		for (RegistryKey<E> registryKey : collection) {
			dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(key, simpleRegistry, codec, registryKey).map(supplier -> simpleRegistry));
		}

		return dataResult.setPartial(registry);
	}

	/**
	 * Reads a supplier for a registry element.
	 * 
	 * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.
	 */
	private <E> DataResult<Supplier<E>> readSupplier(
		RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, RegistryKey<E> registryKey2
	) {
		RegistryOps.ValueHolder<E> valueHolder = this.getValueHolder(registryKey);
		DataResult<Supplier<E>> dataResult = (DataResult<Supplier<E>>)valueHolder.values.get(registryKey2);
		if (dataResult != null) {
			return dataResult;
		} else {
			valueHolder.values.put(registryKey2, DataResult.success(method_39744(mutableRegistry, registryKey2)));
			Optional<DataResult<EntryLoader.Entry<E>>> optional = this.entryLoader.load(this.entryOps, registryKey, registryKey2, codec);
			DataResult<Supplier<E>> dataResult2;
			if (optional.isEmpty()) {
				dataResult2 = DataResult.success(method_39743(mutableRegistry, registryKey2), Lifecycle.stable());
			} else {
				DataResult<EntryLoader.Entry<E>> dataResult3 = (DataResult<EntryLoader.Entry<E>>)optional.get();
				Optional<EntryLoader.Entry<E>> optional2 = dataResult3.result();
				if (optional2.isPresent()) {
					EntryLoader.Entry<E> entry = (EntryLoader.Entry<E>)optional2.get();
					mutableRegistry.replace(entry.fixedId(), registryKey2, entry.value(), dataResult3.lifecycle());
				}

				dataResult2 = dataResult3.map(entryx -> method_39743(mutableRegistry, registryKey2));
			}

			valueHolder.values.put(registryKey2, dataResult2);
			return dataResult2;
		}
	}

	private static <E> Supplier<E> method_39744(MutableRegistry<E> mutableRegistry, RegistryKey<E> registryKey) {
		return Suppliers.memoize(() -> {
			E object = mutableRegistry.get(registryKey);
			if (object == null) {
				throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
			} else {
				return (T)object;
			}
		});
	}

	private static <E> Supplier<E> method_39743(Registry<E> registry, RegistryKey<E> registryKey) {
		return new Supplier<E>() {
			public E get() {
				return registry.get(registryKey);
			}

			public String toString() {
				return registryKey.toString();
			}
		};
	}

	private <E> RegistryOps.ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
		return (RegistryOps.ValueHolder<E>)this.valueHolders.computeIfAbsent(registryRef, registryKey -> new RegistryOps.ValueHolder());
	}

	protected <E> DataResult<Registry<E>> getRegistry(RegistryKey<? extends Registry<E>> key) {
		return (DataResult<Registry<E>>)this.registryManager
			.getOptionalMutable(key)
			.map(mutableRegistry -> DataResult.success(mutableRegistry, mutableRegistry.getLifecycle()))
			.orElseGet(() -> DataResult.error("Unknown registry: " + key));
	}

	static final class ValueHolder<E> {
		final Map<RegistryKey<E>, DataResult<Supplier<E>>> values = Maps.<RegistryKey<E>, DataResult<Supplier<E>>>newIdentityHashMap();
	}
}
