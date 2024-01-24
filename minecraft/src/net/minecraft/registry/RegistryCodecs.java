package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.entry.RegistryFixedCodec;

/**
 * A utility class for serialization of registries using codecs.
 */
public class RegistryCodecs {
	public static <E> Codec<Registry<E>> createKeyedRegistryCodec(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, Codec<E> elementCodec) {
		Codec<Map<RegistryKey<E>, E>> codec = Codec.unboundedMap(RegistryKey.createCodec(registryRef), elementCodec);
		return codec.xmap(entries -> {
			MutableRegistry<E> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle);
			entries.forEach((key, value) -> mutableRegistry.add(key, (E)value, lifecycle));
			return mutableRegistry.freeze();
		}, registry -> ImmutableMap.copyOf(registry.getEntrySet()));
	}

	public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return entryList(registryRef, elementCodec, false);
	}

	/**
	 * @param alwaysSerializeAsList whether to always serialize the list as a list
	 * instead of serializing as one entry if the length is {@code 0}
	 */
	public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean alwaysSerializeAsList) {
		return RegistryEntryListCodec.create(registryRef, RegistryElementCodec.of(registryRef, elementCodec), alwaysSerializeAsList);
	}

	public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef) {
		return entryList(registryRef, false);
	}

	/**
	 * @param alwaysSerializeAsList whether to always serialize the list as a list
	 * instead of serializing as one entry if the length is {@code 0}
	 */
	public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, boolean alwaysSerializeAsList) {
		return RegistryEntryListCodec.create(registryRef, RegistryFixedCodec.of(registryRef), alwaysSerializeAsList);
	}
}
