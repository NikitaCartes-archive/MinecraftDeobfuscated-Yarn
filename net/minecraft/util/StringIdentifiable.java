/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringIdentifiable {
    public String asString();

    /**
     * Creates a codec that serializes an enum implementing this interface either
     * using its ordinals (when compressed) or using its {@link #asString()} method
     * and a given decode function.
     */
    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues, Function<? super String, ? extends E> fromString) {
        Enum[] enums = (Enum[])enumValues.get();
        return StringIdentifiable.createCodec(object -> ((Enum)object).ordinal(), ordinal -> enums[ordinal], fromString);
    }

    /**
     * Creates a codec that serializes a class implementing this interface using either
     * the given toInt and fromInt mapping functions (when compressed output is
     * requested), or its {@link #asString()} method and a given fromString function.
     */
    public static <E extends StringIdentifiable> Codec<E> createCodec(final ToIntFunction<E> compressedEncoder, final IntFunction<E> compressedDecoder, final Function<? super String, ? extends E> decoder) {
        return new Codec<E>(){

            @Override
            public <T> DataResult<T> encode(E stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(compressedEncoder.applyAsInt(stringIdentifiable)));
                }
                return dynamicOps.mergeToPrimitive(object, dynamicOps.createString(stringIdentifiable.asString()));
            }

            @Override
            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(object).flatMap((? super R id) -> Optional.ofNullable((StringIdentifiable)compressedDecoder.apply(id.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element id: " + id))).map((? super R stringIdentifiable) -> Pair.of(stringIdentifiable, dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(object).flatMap((? super R name) -> Optional.ofNullable((StringIdentifiable)decoder.apply(name)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name: " + name))).map((? super R stringIdentifiable) -> Pair.of(stringIdentifiable, dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + compressedEncoder + "]";
            }

            @Override
            public /* synthetic */ DataResult encode(Object value, DynamicOps dynamicOps, Object object) {
                return this.encode((E)((StringIdentifiable)value), (DynamicOps<T>)dynamicOps, (T)object);
            }
        };
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                if (dynamicOps.compressMaps()) {
                    return IntStream.range(0, values.length).mapToObj(dynamicOps::createInt);
                }
                return Arrays.stream(values).map(StringIdentifiable::asString).map(dynamicOps::createString);
            }
        };
    }
}

