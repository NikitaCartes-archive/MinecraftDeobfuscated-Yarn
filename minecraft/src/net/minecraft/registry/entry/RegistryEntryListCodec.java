package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;

public class RegistryEntryListCodec<E> implements Codec<RegistryEntryList<E>> {
	private final RegistryKey<? extends Registry<E>> registry;
	private final Codec<RegistryEntry<E>> entryCodec;
	private final Codec<List<RegistryEntry<E>>> directEntryListCodec;
	private final Codec<Either<TagKey<E>, List<RegistryEntry<E>>>> entryListStorageCodec;

	/**
	 * @param alwaysSerializeAsList whether to always serialize the list as a list
	 * instead of serializing as one entry if the length is {@code 0}
	 */
	private static <E> Codec<List<RegistryEntry<E>>> createDirectEntryListCodec(Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList) {
		Codec<List<RegistryEntry<E>>> codec = Codecs.validate(entryCodec.listOf(), Codecs.createEqualTypeChecker(RegistryEntry::getType));
		return alwaysSerializeAsList
			? codec
			: Codec.either(codec, entryCodec)
				.xmap(
					either -> either.map(entries -> entries, List::of), entries -> entries.size() == 1 ? Either.right((RegistryEntry)entries.get(0)) : Either.left(entries)
				);
	}

	/**
	 * @param alwaysSerializeAsList whether to always serialize the list as a list
	 * instead of serializing as one entry if the length is {@code 0}
	 */
	public static <E> Codec<RegistryEntryList<E>> create(
		RegistryKey<? extends Registry<E>> registryRef, Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList
	) {
		return new RegistryEntryListCodec<>(registryRef, entryCodec, alwaysSerializeAsList);
	}

	/**
	 * @param alwaysSerializeAsList whether to always serialize the list as a list
	 * instead of serializing as one entry if the length is {@code 0}
	 */
	private RegistryEntryListCodec(RegistryKey<? extends Registry<E>> registry, Codec<RegistryEntry<E>> entryCodec, boolean alwaysSerializeAsList) {
		this.registry = registry;
		this.entryCodec = entryCodec;
		this.directEntryListCodec = createDirectEntryListCodec(entryCodec, alwaysSerializeAsList);
		this.entryListStorageCodec = Codec.either(TagKey.codec(registry), this.directEntryListCodec);
	}

	@Override
	public <T> DataResult<Pair<RegistryEntryList<E>, T>> decode(DynamicOps<T> ops, T input) {
		if (ops instanceof RegistryOps<T> registryOps) {
			Optional<RegistryEntryLookup<E>> optional = registryOps.getEntryLookup(this.registry);
			if (optional.isPresent()) {
				RegistryEntryLookup<E> registryEntryLookup = (RegistryEntryLookup<E>)optional.get();
				return this.entryListStorageCodec
					.decode(ops, input)
					.map(pair -> pair.mapFirst(either -> either.map(registryEntryLookup::getOrThrow, RegistryEntryList::of)));
			}
		}

		return this.decodeDirect(ops, input);
	}

	public <T> DataResult<T> encode(RegistryEntryList<E> registryEntryList, DynamicOps<T> dynamicOps, T object) {
		if (dynamicOps instanceof RegistryOps<T> registryOps) {
			Optional<RegistryEntryOwner<E>> optional = registryOps.getOwner(this.registry);
			if (optional.isPresent()) {
				if (!registryEntryList.ownerEquals((RegistryEntryOwner<E>)optional.get())) {
					return DataResult.error(() -> "HolderSet " + registryEntryList + " is not valid in current registry set");
				}

				return this.entryListStorageCodec.encode(registryEntryList.getStorage().mapRight(List::copyOf), dynamicOps, object);
			}
		}

		return this.encodeDirect(registryEntryList, dynamicOps, object);
	}

	private <T> DataResult<Pair<RegistryEntryList<E>, T>> decodeDirect(DynamicOps<T> ops, T input) {
		return this.entryCodec.listOf().decode(ops, input).flatMap(pair -> {
			List<RegistryEntry.Direct<E>> list = new ArrayList();

			for (RegistryEntry<E> registryEntry : (List)pair.getFirst()) {
				if (!(registryEntry instanceof RegistryEntry.Direct<E> direct)) {
					return DataResult.error(() -> "Can't decode element " + registryEntry + " without registry");
				}

				list.add(direct);
			}

			return DataResult.success(new Pair<>(RegistryEntryList.of(list), pair.getSecond()));
		});
	}

	private <T> DataResult<T> encodeDirect(RegistryEntryList<E> entryList, DynamicOps<T> ops, T prefix) {
		return this.directEntryListCodec.encode(entryList.stream().toList(), ops, prefix);
	}
}
