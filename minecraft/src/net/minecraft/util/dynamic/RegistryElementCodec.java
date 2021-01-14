package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A codec for registry elements. Will prefer to encode/decode objects as
 * identifiers if they exist in a registry and falls back to full encoding/
 * decoding behavior if it cannot do so.
 * 
 * <p>The codec's saves and loads {@code Supplier<E>} in order to avoid early
 * loading from registry before a registry is fully loaded from a codec.
 * 
 * @param <E> the element type
 * @see RegistryCodec
 * @see RegistryReadingOps
 * @see RegistryOps
 */
public final class RegistryElementCodec<E> implements Codec<Supplier<E>> {
	private final RegistryKey<? extends Registry<E>> registryRef;
	private final Codec<E> elementCodec;
	private final boolean field_26758;

	public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return method_31192(registryRef, elementCodec, true);
	}

	public static <E> Codec<List<Supplier<E>>> method_31194(RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
		return Codec.either(method_31192(registryKey, codec, false).listOf(), codec.xmap(object -> () -> object, Supplier::get).listOf())
			.xmap(either -> either.map(list -> list, list -> list), Either::left);
	}

	private static <E> RegistryElementCodec<E> method_31192(RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec, boolean bl) {
		return new RegistryElementCodec<>(registryKey, codec, bl);
	}

	private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		this.registryRef = registryRef;
		this.elementCodec = elementCodec;
		this.field_26758 = allowInlineDefinitions;
	}

	public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
		return dynamicOps instanceof RegistryReadingOps
			? ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec)
			: this.elementCodec.encode((E)supplier.get(), dynamicOps, object);
	}

	@Override
	public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
		return ops instanceof RegistryOps
			? ((RegistryOps)ops).decodeOrId(input, this.registryRef, this.elementCodec, this.field_26758)
			: this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(object -> () -> object));
	}

	public String toString() {
		return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
	}
}
