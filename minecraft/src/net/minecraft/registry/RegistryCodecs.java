package net.minecraft.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.entry.RegistryFixedCodec;

/**
 * A utility class for serialization of registries using codecs.
 */
public class RegistryCodecs {
	private static <T> MapCodec<RegistryCodecs.RegistryManagerEntry<T>> managerEntry(RegistryKey<? extends Registry<T>> registryRef, MapCodec<T> elementCodec) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						RegistryKey.createCodec(registryRef).fieldOf("name").forGetter(RegistryCodecs.RegistryManagerEntry::key),
						Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryManagerEntry::rawId),
						elementCodec.forGetter(RegistryCodecs.RegistryManagerEntry::value)
					)
					.apply(instance, RegistryCodecs.RegistryManagerEntry::new)
		);
	}

	public static <T> Codec<Registry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
		return managerEntry(registryRef, elementCodec.fieldOf("element")).codec().listOf().xmap(entries -> {
			MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle);

			for (RegistryCodecs.RegistryManagerEntry<T> registryManagerEntry : entries) {
				mutableRegistry.set(registryManagerEntry.rawId(), registryManagerEntry.key(), registryManagerEntry.value(), lifecycle);
			}

			return mutableRegistry;
		}, registry -> {
			Builder<RegistryCodecs.RegistryManagerEntry<T>> builder = ImmutableList.builder();

			for (T object : registry) {
				builder.add(new RegistryCodecs.RegistryManagerEntry<>((RegistryKey<T>)registry.getKey(object).get(), registry.getRawId(object), object));
			}

			return builder.build();
		});
	}

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

	static record RegistryManagerEntry<T>(RegistryKey<T> key, int rawId, T value) {
	}
}
