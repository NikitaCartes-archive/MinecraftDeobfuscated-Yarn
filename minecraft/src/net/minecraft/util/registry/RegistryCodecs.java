package net.minecraft.util.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;

public class RegistryCodecs {
	private static <T> MapCodec<RegistryCodecs.RegistryManagerEntry<T>> managerEntry(RegistryKey<? extends Registry<T>> registryRef, MapCodec<T> elementCodec) {
		return RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC
							.xmap(RegistryKey.createKeyFactory(registryRef), RegistryKey::getValue)
							.fieldOf("name")
							.forGetter(RegistryCodecs.RegistryManagerEntry::key),
						Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryManagerEntry::rawId),
						elementCodec.forGetter(RegistryCodecs.RegistryManagerEntry::value)
					)
					.apply(instance, RegistryCodecs.RegistryManagerEntry::new)
		);
	}

	public static <T> Codec<Registry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
		return managerEntry(registryRef, elementCodec.fieldOf("element")).codec().listOf().xmap(entries -> {
			MutableRegistry<T> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle, null);

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

	public static <E> Codec<Registry<E>> dynamicRegistry(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, Codec<E> elementCodec) {
		Codec<Map<RegistryKey<E>, E>> codec = registryMap(registryRef, elementCodec);
		Encoder<Registry<E>> encoder = codec.comap(registry -> ImmutableMap.copyOf(registry.getEntrySet()));
		return Codec.of(encoder, createRegistryDecoder(registryRef, elementCodec, codec, lifecycle), "DataPackRegistryCodec for " + registryRef);
	}

	private static <E> Decoder<Registry<E>> createRegistryDecoder(
		RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec, Decoder<Map<RegistryKey<E>, E>> entryMapDecoder, Lifecycle lifecycle
	) {
		final Decoder<MutableRegistry<E>> decoder = entryMapDecoder.map(map -> {
			MutableRegistry<E> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle, null);
			map.forEach((key, value) -> mutableRegistry.add(key, (E)value, lifecycle));
			return mutableRegistry;
		});
		return new Decoder<Registry<E>>() {
			@Override
			public <T> DataResult<Pair<Registry<E>, T>> decode(DynamicOps<T> ops, T input) {
				DataResult<Pair<MutableRegistry<E>, T>> dataResult = decoder.decode(ops, input);
				return ops instanceof RegistryOps<?> registryOps
					? (DataResult)registryOps.getLoaderAccess()
						.map(loaderAccess -> this.load(dataResult, registryOps, loaderAccess.loader()))
						.orElseGet(() -> DataResult.error("Can't load registry with this ops"))
					: dataResult.map(pair -> pair.mapFirst(registry -> registry));
			}

			private <T> DataResult<Pair<Registry<E>, T>> load(DataResult<Pair<MutableRegistry<E>, T>> result, RegistryOps<?> ops, RegistryLoader loader) {
				return result.flatMap(
					pair -> loader.load((MutableRegistry<E>)pair.getFirst(), registryRef, codec, ops.getEntryOps()).map(registry -> Pair.of(registry, pair.getSecond()))
				);
			}
		};
	}

	private static <T> Codec<Map<RegistryKey<T>, T>> registryMap(RegistryKey<? extends Registry<T>> registryRef, Codec<T> elementCodec) {
		return Codec.unboundedMap(Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryRef), RegistryKey::getValue), elementCodec);
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
