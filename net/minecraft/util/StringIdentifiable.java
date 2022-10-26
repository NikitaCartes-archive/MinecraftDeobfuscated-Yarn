/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

/**
 * An interface, implemented by enums, that allows the enum to be serialized
 * by codecs. An instance is identified using a string.
 * 
 * @apiNote To make an enum serializable with codecs, implement this on the enum class,
 * implement {@link #asString} to return a unique ID, and add a {@code static final}
 * field that holds {@linkplain #createCodec the codec for the enum}.
 */
public interface StringIdentifiable {
    public static final int field_38377 = 16;

    /**
     * {@return the unique string representation of the enum, used for serialization}
     */
    public String asString();

    /**
     * Creates a codec that serializes an enum implementing this interface either
     * using its ordinals (when compressed) or using its {@link #asString()} method
     * and a given decode function.
     */
    public static <E extends Enum<E>> Codec<E> createCodec(Supplier<E[]> enumValues) {
        Enum[] enums = (Enum[])enumValues.get();
        if (enums.length > 16) {
            Map<String, Enum> map = Arrays.stream(enums).collect(Collectors.toMap(identifiable -> ((StringIdentifiable)identifiable).asString(), enum_ -> enum_));
            return new Codec(enums, id -> id == null ? null : (Enum)map.get(id));
        }
        return new Codec(enums, id -> {
            for (Enum enum_ : enums) {
                if (!((StringIdentifiable)((Object)enum_)).asString().equals(id)) continue;
                return enum_;
            }
            return null;
        });
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Arrays.stream(values).map(StringIdentifiable::asString).map(ops::createString);
            }
        };
    }

    @Deprecated
    public static class Codec<E extends Enum<E>>
    implements com.mojang.serialization.Codec<E> {
        private final com.mojang.serialization.Codec<E> base;
        private final Function<String, E> idToIdentifiable;

        public Codec(E[] values, Function<String, E> idToIdentifiable) {
            this.base = Codecs.orCompressed(Codecs.idChecked(identifiable -> ((StringIdentifiable)identifiable).asString(), idToIdentifiable), Codecs.rawIdChecked(enum_ -> ((Enum)enum_).ordinal(), ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1));
            this.idToIdentifiable = idToIdentifiable;
        }

        @Override
        public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
            return this.base.decode(ops, input);
        }

        @Override
        public <T> DataResult<T> encode(E enum_, DynamicOps<T> dynamicOps, T object) {
            return this.base.encode(enum_, dynamicOps, object);
        }

        @Nullable
        public E byId(@Nullable String id) {
            return (E)((Enum)this.idToIdentifiable.apply(id));
        }

        @Override
        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((E)((Enum)input), (DynamicOps<T>)ops, (T)prefix);
        }
    }
}

