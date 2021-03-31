/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>Expect its removal once Mojang updates DataFixerUpper.
 */
public class Codecs {
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
            return "XorCodec[" + this.first + ", " + this.second + ']';
        }

        @Override
        public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((Either)input, ops, prefix);
        }
    }
}

