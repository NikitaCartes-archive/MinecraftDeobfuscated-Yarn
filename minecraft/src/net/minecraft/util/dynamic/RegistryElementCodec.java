package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A codec for registry elements. Will prefer to encode/decode objects as
 * identifiers if they exist in a registry and falls back to full encoding/
 * decoding behavior if it cannot do so.
 * 
 * <p>The codec's saves and loads {@code Supplier<E>} in order to avoid early
 * loading from registry before a registry is fully loaded from a codec.</p>
 * 
 * @param <E> the element type
 * @see RegistryCodec
 * @see RegistryReadingOps
 * @see RegistryOps
 */
public final class RegistryElementCodec<E> implements Codec<Supplier<E>> {
	private final RegistryKey<? extends Registry<E>> registryRef;
	private final Codec<E> elementCodec;

	public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec) {
		return new RegistryElementCodec<>(registryRef, codec);
	}

	private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec) {
		this.registryRef = registryRef;
		this.elementCodec = codec;
	}

	public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
		return dynamicOps instanceof RegistryReadingOps
			? ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec)
			: this.elementCodec.encode((E)supplier.get(), dynamicOps, object);
	}

	@Override
	public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
		return ops instanceof RegistryOps
			? ((RegistryOps)ops).decodeOrId(input, this.registryRef, this.elementCodec)
			: this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(object -> () -> object));
	}

	public String toString() {
		return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
	}
}
