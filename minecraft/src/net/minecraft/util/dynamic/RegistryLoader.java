package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryLoader {
	private final EntryLoader entryLoader;
	private final Map<RegistryKey<? extends Registry<?>>, RegistryLoader.ValueHolder<?>> valueHolders = new IdentityHashMap();

	RegistryLoader(EntryLoader entryLoader) {
		this.entryLoader = entryLoader;
	}

	public <E> DataResult<? extends Registry<E>> load(
		MutableRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, DynamicOps<JsonElement> ops
	) {
		Map<RegistryKey<E>, EntryLoader.Parseable<E>> map = this.entryLoader.getKnownEntryPaths(registryRef);
		DataResult<MutableRegistry<E>> dataResult = DataResult.success(registry, Lifecycle.stable());

		for (Entry<RegistryKey<E>, EntryLoader.Parseable<E>> entry : map.entrySet()) {
			dataResult = dataResult.flatMap(
				reg -> this.load(reg, registryRef, codec, (RegistryKey<E>)entry.getKey(), Optional.of((EntryLoader.Parseable)entry.getValue()), ops).map(entryxx -> reg)
			);
		}

		return dataResult.setPartial(registry);
	}

	<E> DataResult<RegistryEntry<E>> load(
		MutableRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, RegistryKey<E> entryKey, DynamicOps<JsonElement> ops
	) {
		Optional<EntryLoader.Parseable<E>> optional = this.entryLoader.createParseable(entryKey);
		return this.load(registry, registryRef, codec, entryKey, optional, ops);
	}

	private <E> DataResult<RegistryEntry<E>> load(
		MutableRegistry<E> registry,
		RegistryKey<? extends Registry<E>> registryRef,
		Codec<E> codec,
		RegistryKey<E> entryKey,
		Optional<EntryLoader.Parseable<E>> parseable,
		DynamicOps<JsonElement> ops
	) {
		RegistryLoader.ValueHolder<E> valueHolder = this.getOrCreateValueHolder(registryRef);
		DataResult<RegistryEntry<E>> dataResult = (DataResult<RegistryEntry<E>>)valueHolder.values.get(entryKey);
		if (dataResult != null) {
			return dataResult;
		} else {
			RegistryEntry<E> registryEntry = registry.getOrCreateEntry(entryKey);
			valueHolder.values.put(entryKey, DataResult.success(registryEntry));
			DataResult<RegistryEntry<E>> dataResult2;
			if (parseable.isEmpty()) {
				if (registry.contains(entryKey)) {
					dataResult2 = DataResult.success(registryEntry, Lifecycle.stable());
				} else {
					dataResult2 = DataResult.error("Missing referenced custom/removed registry entry for registry " + registryRef + " named " + entryKey.getValue());
				}
			} else {
				DataResult<EntryLoader.Entry<E>> dataResult3 = ((EntryLoader.Parseable)parseable.get()).parseElement(ops, codec);
				Optional<EntryLoader.Entry<E>> optional = dataResult3.result();
				if (optional.isPresent()) {
					EntryLoader.Entry<E> entry = (EntryLoader.Entry<E>)optional.get();
					registry.replace(entry.fixedId(), entryKey, entry.value(), dataResult3.lifecycle());
				}

				dataResult2 = dataResult3.map(entryx -> registryEntry);
			}

			valueHolder.values.put(entryKey, dataResult2);
			return dataResult2;
		}
	}

	private <E> RegistryLoader.ValueHolder<E> getOrCreateValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
		return (RegistryLoader.ValueHolder<E>)this.valueHolders.computeIfAbsent(registryRef, ref -> new RegistryLoader.ValueHolder());
	}

	public RegistryLoader.LoaderAccess createAccess(DynamicRegistryManager.Mutable dynamicRegistryManager) {
		return new RegistryLoader.LoaderAccess(dynamicRegistryManager, this);
	}

	public static record LoaderAccess(DynamicRegistryManager.Mutable dynamicRegistryManager, RegistryLoader loader) {
		public <E> DataResult<? extends Registry<E>> load(RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, DynamicOps<JsonElement> ops) {
			MutableRegistry<E> mutableRegistry = this.dynamicRegistryManager.getMutable(registryRef);
			return this.loader.load(mutableRegistry, registryRef, codec, ops);
		}

		public <E> DataResult<RegistryEntry<E>> load(
			RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, RegistryKey<E> entryKey, DynamicOps<JsonElement> ops
		) {
			MutableRegistry<E> mutableRegistry = this.dynamicRegistryManager.getMutable(registryRef);
			return this.loader.load(mutableRegistry, registryRef, codec, entryKey, ops);
		}
	}

	static final class ValueHolder<E> {
		final Map<RegistryKey<E>, DataResult<RegistryEntry<E>>> values = Maps.<RegistryKey<E>, DataResult<RegistryEntry<E>>>newIdentityHashMap();
	}
}
