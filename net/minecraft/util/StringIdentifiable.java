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

    public static <E extends Enum<E>> Codec<E> method_28140(Supplier<E[]> supplier, Function<? super String, ? extends E> function) {
        Enum[] enums = (Enum[])supplier.get();
        return StringIdentifiable.method_28141(Enum::ordinal, i -> enums[i], function);
    }

    public static <E extends StringIdentifiable> Codec<E> method_28141(final ToIntFunction<E> toIntFunction, final IntFunction<E> intFunction, final Function<? super String, ? extends E> function) {
        return new Codec<E>(){

            @Override
            public <T> DataResult<T> encode(E stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(toIntFunction.applyAsInt(stringIdentifiable)));
                }
                return dynamicOps.mergeToPrimitive(object, dynamicOps.createString(stringIdentifiable.asString()));
            }

            @Override
            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(object).flatMap((? super R number) -> Optional.ofNullable(intFunction.apply(number.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element id: " + number))).map((? super R stringIdentifiable) -> Pair.of(stringIdentifiable, dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(object).flatMap((? super R string) -> Optional.ofNullable(function.apply(string)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name: " + string))).map((? super R stringIdentifiable) -> Pair.of(stringIdentifiable, dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + toIntFunction + "]";
            }

            @Override
            public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                return this.encode((E)((StringIdentifiable)object), (DynamicOps<T>)dynamicOps, (T)object2);
            }
        };
    }

    public static Keyable method_28142(final StringIdentifiable[] stringIdentifiables) {
        return new Keyable(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                if (dynamicOps.compressMaps()) {
                    return IntStream.range(0, stringIdentifiables.length).mapToObj(dynamicOps::createInt);
                }
                return Arrays.stream(stringIdentifiables).map(StringIdentifiable::asString).map(dynamicOps::createString);
            }
        };
    }
}

