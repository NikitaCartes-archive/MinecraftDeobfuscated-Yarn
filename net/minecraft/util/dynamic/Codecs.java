/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.RegistryEntryList;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
    public static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
    public static final Codec<Integer> NONNEGATIVE_INT = Codecs.rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Integer> POSITIVE_INT = Codecs.rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Float> POSITIVE_FLOAT = Codecs.rangedFloat(0.0f, Float.MAX_VALUE, v -> "Value must be positive: " + v);
    public static final Codec<Pattern> REGULAR_EXPRESSION = Codec.STRING.comapFlatMap(pattern -> {
        try {
            return DataResult.success(Pattern.compile(pattern));
        } catch (PatternSyntaxException patternSyntaxException) {
            return DataResult.error("Invalid regex pattern '" + pattern + "': " + patternSyntaxException.getMessage());
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT = Codecs.method_43532(DateTimeFormatter.ISO_INSTANT);

    /**
     * Returns an exclusive-or codec for {@link Either} instances.
     * 
     * <p>This returned codec fails if both the {@code first} and {@code second} codecs can
     * decode the input, while DFU's {@link com.mojang.serialization.codecs.EitherCodec}
     * will always take the first decoded result when it is available.
     * 
     * <p>Otherwise, this behaves the same as the either codec.
     * 
     * @param <F> the first type
     * @param <S> the second type
     * @return the xor codec for the two codecs
     * @see Codec#either(Codec, Codec)
     * @see com.mojang.serialization.codecs.EitherCodec
     * 
     * @param first the first codec
     * @param second the second codec
     */
    public static <F, S> Codec<com.mojang.datafixers.util.Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new Xor<F, S>(first, second);
    }

    public static <P, I> Codec<I> createCodecForPairObject(Codec<P> codec, String leftFieldName, String rightFieldName, BiFunction<P, P, DataResult<I>> combineFunction, Function<I, P> leftFunction, Function<I, P> rightFunction) {
        Codec<Object> codec2 = Codec.list(codec).comapFlatMap(list2 -> Util.toArray(list2, 2).flatMap(list -> {
            Object object = list.get(0);
            Object object2 = list.get(1);
            return (DataResult)combineFunction.apply(object, object2);
        }), pair -> ImmutableList.of(leftFunction.apply(pair), rightFunction.apply(pair)));
        Codec<Object> codec3 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)codec.fieldOf(leftFieldName)).forGetter(Pair::getFirst), ((MapCodec)codec.fieldOf(rightFieldName)).forGetter(Pair::getSecond)).apply((Applicative<Pair, ?>)instance, Pair::of)).comapFlatMap(pair -> (DataResult)combineFunction.apply(pair.getFirst(), pair.getSecond()), pair -> Pair.of(leftFunction.apply(pair), rightFunction.apply(pair)));
        Codec<Object> codec4 = new Either<Object, Object>(codec2, codec3).xmap(either -> either.map(object -> object, object -> object), com.mojang.datafixers.util.Either::left);
        return Codec.either(codec, codec4).comapFlatMap(either -> either.map(object -> (DataResult)combineFunction.apply(object, object), DataResult::success), pair -> {
            Object object2;
            Object object = leftFunction.apply(pair);
            if (Objects.equals(object, object2 = rightFunction.apply(pair))) {
                return com.mojang.datafixers.util.Either.left(object);
            }
            return com.mojang.datafixers.util.Either.right(pair);
        });
    }

    public static <A> Codec.ResultFunction<A> orElsePartial(final A object) {
        return new Codec.ResultFunction<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> result) {
                MutableObject mutableObject = new MutableObject();
                Optional optional = result.resultOrPartial(mutableObject::setValue);
                if (optional.isPresent()) {
                    return result;
                }
                return DataResult.error("(" + (String)mutableObject.getValue() + " -> using default)", Pair.of(object, input));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> result) {
                return result;
            }

            public String toString() {
                return "OrElsePartial[" + object + "]";
            }
        };
    }

    public static <E> Codec<E> rawIdChecked(ToIntFunction<E> elementToRawId, IntFunction<E> rawIdToElement, int errorRawId) {
        return Codec.INT.flatXmap(rawId -> Optional.ofNullable(rawIdToElement.apply((int)rawId)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element id: " + rawId)), element -> {
            int j = elementToRawId.applyAsInt(element);
            return j == errorRawId ? DataResult.error("Element with unknown id: " + element) : DataResult.success(j);
        });
    }

    public static <E> Codec<E> idChecked(Function<E, String> elementToId, Function<String, E> idToElement) {
        return Codec.STRING.flatXmap(id -> Optional.ofNullable(idToElement.apply((String)id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown element name:" + id)), element -> Optional.ofNullable((String)elementToId.apply(element)).map(DataResult::success).orElseGet(() -> DataResult.error("Element with unknown name: " + element)));
    }

    public static <E> Codec<E> orCompressed(final Codec<E> uncompressedCodec, final Codec<E> compressedCodec) {
        return new Codec<E>(){

            @Override
            public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
                if (ops.compressMaps()) {
                    return compressedCodec.encode(input, ops, prefix);
                }
                return uncompressedCodec.encode(input, ops, prefix);
            }

            @Override
            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
                if (ops.compressMaps()) {
                    return compressedCodec.decode(ops, input);
                }
                return uncompressedCodec.decode(ops, input);
            }

            public String toString() {
                return uncompressedCodec + " orCompressed " + compressedCodec;
            }
        };
    }

    public static <E> Codec<E> withLifecycle(Codec<E> originalCodec, final Function<E, Lifecycle> entryLifecycleGetter, final Function<E, Lifecycle> lifecycleGetter) {
        return originalCodec.mapResult(new Codec.ResultFunction<E>(){

            @Override
            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<E, T>> result) {
                return result.result().map(pair -> result.setLifecycle((Lifecycle)entryLifecycleGetter.apply(pair.getFirst()))).orElse(result);
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, E input, DataResult<T> result) {
                return result.setLifecycle((Lifecycle)lifecycleGetter.apply(input));
            }

            public String toString() {
                return "WithLifecycle[" + entryLifecycleGetter + " " + lifecycleGetter + "]";
            }
        });
    }

    private static <N extends Number> Function<N, DataResult<N>> createIntRangeChecker(N min, N max, Function<N, String> messageFactory) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(min) >= 0 && ((Comparable)((Object)value)).compareTo(max) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error((String)messageFactory.apply(value));
        };
    }

    private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
        Function<Integer, DataResult<Integer>> function = Codecs.createIntRangeChecker(min, max, messageFactory);
        return Codec.INT.flatXmap(function, function);
    }

    private static <N extends Number> Function<N, DataResult<N>> createFloatRangeChecker(N min, N max, Function<N, String> messageFactory) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(min) > 0 && ((Comparable)((Object)value)).compareTo(max) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error((String)messageFactory.apply(value));
        };
    }

    private static Codec<Float> rangedFloat(float min, float max, Function<Float, String> messageFactory) {
        Function<Float, DataResult<Float>> function = Codecs.createFloatRangeChecker(Float.valueOf(min), Float.valueOf(max), messageFactory);
        return Codec.FLOAT.flatXmap(function, function);
    }

    public static <T> Function<List<T>, DataResult<List<T>>> createNonEmptyListChecker() {
        return list -> {
            if (list.isEmpty()) {
                return DataResult.error("List must have contents");
            }
            return DataResult.success(list);
        };
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> originalCodec) {
        return originalCodec.flatXmap(Codecs.createNonEmptyListChecker(), Codecs.createNonEmptyListChecker());
    }

    public static <T> Function<RegistryEntryList<T>, DataResult<RegistryEntryList<T>>> createNonEmptyEntryListChecker() {
        return entries -> {
            if (entries.getStorage().right().filter(List::isEmpty).isPresent()) {
                return DataResult.error("List must have contents");
            }
            return DataResult.success(entries);
        };
    }

    public static <T> Codec<RegistryEntryList<T>> nonEmptyEntryList(Codec<RegistryEntryList<T>> originalCodec) {
        return originalCodec.flatXmap(Codecs.createNonEmptyEntryListChecker(), Codecs.createNonEmptyEntryListChecker());
    }

    public static <A> Codec<A> createLazy(Supplier<Codec<A>> supplier) {
        return new Lazy<A>(supplier);
    }

    public static <E> MapCodec<E> createContextRetrievalCodec(Function<DynamicOps<?>, DataResult<E>> retriever) {
        class ContextRetrievalCodec
        extends MapCodec<E> {
            final /* synthetic */ Function field_36397;

            ContextRetrievalCodec(Function retriever) {
                this.field_36397 = retriever;
            }

            @Override
            public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            }

            @Override
            public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
                return (DataResult)this.field_36397.apply(ops);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + this.field_36397 + "]";
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }
        }
        return new ContextRetrievalCodec(retriever);
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> createEqualTypeChecker(Function<E, T> typeGetter) {
        return collection -> {
            Iterator iterator = collection.iterator();
            if (iterator.hasNext()) {
                Object object = typeGetter.apply(iterator.next());
                while (iterator.hasNext()) {
                    Object object2 = iterator.next();
                    Object object3 = typeGetter.apply(object2);
                    if (object3 == object) continue;
                    return DataResult.error("Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object);
                }
            }
            return DataResult.success(collection, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> exceptionCatching(final Codec<A> codec) {
        return Codec.of(codec, new Decoder<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                try {
                    return codec.decode(ops, input);
                } catch (Exception exception) {
                    return DataResult.error("Cauch exception decoding " + input + ": " + exception.getMessage());
                }
            }
        });
    }

    public static Codec<Instant> method_43532(DateTimeFormatter dateTimeFormatter) {
        return Codec.STRING.comapFlatMap(string -> {
            try {
                return DataResult.success(Instant.from(dateTimeFormatter.parse((CharSequence)string)));
            } catch (Exception exception) {
                return DataResult.error(exception.getMessage());
            }
        }, dateTimeFormatter::format);
    }

    static final class Xor<F, S>
    implements Codec<com.mojang.datafixers.util.Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public Xor(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult = this.first.decode(ops, input).map((? super R pair) -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
            DataResult<Pair> dataResult2 = this.second.decode(ops, input).map((? super R pair) -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
            Optional<Pair> optional = dataResult.result();
            Optional<Pair> optional2 = dataResult2.result();
            if (optional.isPresent() && optional2.isPresent()) {
                return DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(), optional.get());
            }
            return optional.isPresent() ? dataResult : dataResult2;
        }

        @Override
        public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
            return either.map(left -> this.first.encode(left, dynamicOps, object), right -> this.second.encode(right, dynamicOps, object));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Xor xor = (Xor)o;
            return Objects.equals(this.first, xor.first) && Objects.equals(this.second, xor.second);
        }

        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }

        @Override
        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((com.mojang.datafixers.util.Either)input, ops, prefix);
        }
    }

    static final class Either<F, S>
    implements Codec<com.mojang.datafixers.util.Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public Either(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T> DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<com.mojang.datafixers.util.Either<F, Pair>, T>> dataResult = this.first.decode(ops, input).map((? super R pair) -> pair.mapFirst(com.mojang.datafixers.util.Either::left));
            if (!dataResult.error().isPresent()) {
                return dataResult;
            }
            DataResult<Pair<com.mojang.datafixers.util.Either<F, S>, T>> dataResult2 = this.second.decode(ops, input).map((? super R pair) -> pair.mapFirst(com.mojang.datafixers.util.Either::right));
            if (!dataResult2.error().isPresent()) {
                return dataResult2;
            }
            return dataResult.apply2((pair, pair2) -> pair2, dataResult2);
        }

        @Override
        public <T> DataResult<T> encode(com.mojang.datafixers.util.Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
            return either.map(left -> this.first.encode(left, dynamicOps, object), right -> this.second.encode(right, dynamicOps, object));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Either either = (Either)o;
            return Objects.equals(this.first, either.first) && Objects.equals(this.second, either.second);
        }

        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        public String toString() {
            return "EitherCodec[" + this.first + ", " + this.second + "]";
        }

        @Override
        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((com.mojang.datafixers.util.Either)input, ops, prefix);
        }
    }

    record Lazy<A>(Supplier<Codec<A>> delegate) implements Codec<A>
    {
        Lazy {
            supplier = Suppliers.memoize(supplier::get);
        }

        @Override
        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            return this.delegate.get().decode(ops, input);
        }

        @Override
        public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
            return this.delegate.get().encode(input, ops, prefix);
        }
    }
}

