/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
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
public final class RegistryElementCodec<E>
implements Codec<Supplier<E>> {
    private final RegistryKey<Registry<E>> registryRef;
    private final MapCodec<E> elementCodec;

    public static <E> RegistryElementCodec<E> of(RegistryKey<Registry<E>> registryRef, MapCodec<E> mapCodec) {
        return new RegistryElementCodec<E>(registryRef, mapCodec);
    }

    private RegistryElementCodec(RegistryKey<Registry<E>> registryRef, MapCodec<E> mapCodec) {
        this.registryRef = registryRef;
        this.elementCodec = mapCodec;
    }

    @Override
    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
        if (dynamicOps instanceof RegistryReadingOps) {
            return ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec);
        }
        return this.elementCodec.codec().encode(supplier.get(), dynamicOps, object);
    }

    @Override
    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
        if (ops instanceof RegistryOps) {
            return ((RegistryOps)ops).decodeOrId(input, this.registryRef, this.elementCodec);
        }
        return this.elementCodec.codec().decode(ops, input).map((? super R pair) -> pair.mapFirst(object -> () -> object));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
    }

    @Override
    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((Supplier)input, ops, prefix);
    }
}

