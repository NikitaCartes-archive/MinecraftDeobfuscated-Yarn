/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>It has a few methods to create checkers for {@code Codec.flatXmap} to add
 * extra value validation to encoding and decoding. See the implementation of
 * {@link #nonEmptyList(Codec)}.
 */
public class Codecs {
    public static final Codec<Integer> NONNEGATIVE_INT = Codecs.rangedInt(0, Integer.MAX_VALUE, v -> "Value must be non-negative: " + v);
    public static final Codec<Integer> POSITIVE_INT = Codecs.rangedInt(1, Integer.MAX_VALUE, v -> "Value must be positive: " + v);

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
    public static <F, S> Codec<Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new Xor<F, S>(first, second);
    }

    private static <N extends Number> Function<N, DataResult<N>> createRangeChecker(N min, N max, Function<N, String> messageFactory) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(min) >= 0 && ((Comparable)((Object)value)).compareTo(max) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error((String)messageFactory.apply(value));
        };
    }

    private static Codec<Integer> rangedInt(int min, int max, Function<Integer, String> messageFactory) {
        Function<Integer, DataResult<Integer>> function = Codecs.createRangeChecker(min, max, messageFactory);
        return Codec.INT.flatXmap(function, function);
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

    public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> createPresentValuesChecker() {
        return suppliers -> {
            ArrayList<CallSite> list = Lists.newArrayList();
            for (int i = 0; i < suppliers.size(); ++i) {
                Supplier supplier = (Supplier)suppliers.get(i);
                try {
                    if (supplier.get() != null) continue;
                    list.add((CallSite)((Object)("Missing value [" + i + "] : " + supplier)));
                    continue;
                } catch (Exception exception) {
                    list.add((CallSite)((Object)("Invalid value [" + i + "]: " + supplier + ", message: " + exception.getMessage())));
                }
            }
            if (!list.isEmpty()) {
                return DataResult.error(String.join((CharSequence)"; ", list));
            }
            return DataResult.success(suppliers, Lifecycle.stable());
        };
    }

    public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> createPresentValueChecker() {
        return supplier -> {
            try {
                if (supplier.get() == null) {
                    return DataResult.error("Missing value: " + supplier);
                }
            } catch (Exception exception) {
                return DataResult.error("Invalid value: " + supplier + ", message: " + exception.getMessage());
            }
            return DataResult.success(supplier, Lifecycle.stable());
        };
    }

    static final class Xor<F, S>
    implements Codec<Either<F, S>> {
        private final Codec<F> first;
        private final Codec<S> second;

        public Xor(Codec<F> first, Codec<S> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Either<F, S>, T>> dataResult = this.first.decode(ops, input).map((? super R pair) -> pair.mapFirst(Either::left));
            DataResult<Pair> dataResult2 = this.second.decode(ops, input).map((? super R pair) -> pair.mapFirst(Either::right));
            Optional<Pair> optional = dataResult.result();
            Optional<Pair> optional2 = dataResult2.result();
            if (optional.isPresent() && optional2.isPresent()) {
                return DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(), optional.get());
            }
            return optional.isPresent() ? dataResult : dataResult2;
        }

        @Override
        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
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
            return this.encode((Either)input, ops, prefix);
        }
    }
}

