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
		return new Codecs.Xor<>(first, second);
	}

	/**
	 * @see com.mojang.serialization.codecs.EitherCodec
	 */
	static final class Xor<F, S> implements Codec<Either<F, S>> {
		private final Codec<F> first;
		private final Codec<S> second;

		public Xor(Codec<F> first, Codec<S> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
			DataResult<Pair<Either<F, S>, T>> dataResult = this.first.decode(ops, input).map(pair -> pair.mapFirst(Either::left));
			DataResult<Pair<Either<F, S>, T>> dataResult2 = this.second.decode(ops, input).map(pair -> pair.mapFirst(Either::right));
			Optional<Pair<Either<F, S>, T>> optional = dataResult.result();
			Optional<Pair<Either<F, S>, T>> optional2 = dataResult2.result();
			if (optional.isPresent() && optional2.isPresent()) {
				return DataResult.error(
					"Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(),
					(Pair<Either<F, S>, T>)optional.get()
				);
			} else {
				return optional.isPresent() ? dataResult : dataResult2;
			}
		}

		public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicOps, T object) {
			return either.map(left -> this.first.encode((F)left, dynamicOps, object), right -> this.second.encode((S)right, dynamicOps, object));
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Codecs.Xor<?, ?> xor = (Codecs.Xor<?, ?>)o;
				return Objects.equals(this.first, xor.first) && Objects.equals(this.second, xor.second);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.first, this.second});
		}

		public String toString() {
			return "XorCodec[" + this.first + ", " + this.second + ']';
		}
	}
}
