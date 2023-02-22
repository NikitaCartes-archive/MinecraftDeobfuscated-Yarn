package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

/**
 * A codec for registry elements. Will prefer to encode/decode objects as
 * identifiers if they exist in a registry and falls back to full encoding/
 * decoding behavior if it cannot do so.
 * 
 * <p>The codec's saves and loads {@code Supplier<E>} in order to avoid early
 * loading from registry before a registry is fully loaded from a codec.
 * 
 * @param <E> the element type
 * @see net.minecraft.registry.RegistryOps
 */
public final class RegistryElementCodec<E> implements Codec<RegistryEntry<E>> {
	private final RegistryKey<? extends Registry<E>> registryRef;
	private final Codec<E> elementCodec;
	private final boolean allowInlineDefinitions;

	public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return of(registryRef, elementCodec, true);
	}

	public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		return new RegistryElementCodec<>(registryRef, elementCodec, allowInlineDefinitions);
	}

	private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		this.registryRef = registryRef;
		this.elementCodec = elementCodec;
		this.allowInlineDefinitions = allowInlineDefinitions;
	}

	public <T> DataResult<T> encode(RegistryEntry<E> registryEntry, DynamicOps<T> dynamicOps, T object) {
		if (dynamicOps instanceof RegistryOps<?> registryOps) {
			Optional<RegistryEntryOwner<E>> optional = registryOps.getOwner(this.registryRef);
			if (optional.isPresent()) {
				if (!registryEntry.ownerEquals((RegistryEntryOwner<E>)optional.get())) {
					return DataResult.error(() -> "Element " + registryEntry + " is not valid in current registry set");
				}

				return registryEntry.getKeyOrValue()
					.map(key -> Identifier.CODEC.encode(key.getValue(), dynamicOps, object), value -> this.elementCodec.encode((E)value, dynamicOps, object));
			}
		}

		return this.elementCodec.encode(registryEntry.value(), dynamicOps, object);
	}

	@Override
	public <T> DataResult<Pair<RegistryEntry<E>, T>> decode(DynamicOps<T> ops, T input) {
		if (ops instanceof RegistryOps<?> registryOps) {
			Optional<RegistryEntryLookup<E>> optional = registryOps.getEntryLookup(this.registryRef);
			if (optional.isEmpty()) {
				return DataResult.error(() -> "Registry does not exist: " + this.registryRef);
			} else {
				RegistryEntryLookup<E> registryEntryLookup = (RegistryEntryLookup<E>)optional.get();
				DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(ops, input);
				if (dataResult.result().isEmpty()) {
					return !this.allowInlineDefinitions
						? DataResult.error(() -> "Inline definitions not allowed here")
						: this.elementCodec.decode(ops, input).map(pairx -> pairx.mapFirst(RegistryEntry::of));
				} else {
					Pair<Identifier, T> pair = (Pair<Identifier, T>)dataResult.result().get();
					RegistryKey<E> registryKey = RegistryKey.of(this.registryRef, pair.getFirst());
					return ((DataResult)registryEntryLookup.getOptional(registryKey)
							.map(DataResult::success)
							.orElseGet(() -> DataResult.error(() -> "Failed to get element " + registryKey)))
						.<Pair<RegistryEntry<E>, T>>map(reference -> Pair.of(reference, pair.getSecond()))
						.setLifecycle(Lifecycle.stable());
				}
			}
		} else {
			return this.elementCodec.decode(ops, input).map(pairx -> pairx.mapFirst(RegistryEntry::of));
		}
	}

	public String toString() {
		return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
	}
}
