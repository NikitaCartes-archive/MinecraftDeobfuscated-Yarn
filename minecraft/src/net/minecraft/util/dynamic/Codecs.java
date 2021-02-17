package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * A few extensions for {@link Codec} or {@link DynamicOps}.
 * 
 * <p>Expect its removal once Mojang updates DataFixerUpper.
 */
public class Codecs {
	/**
	 * A codec for double streams.
	 * 
	 * @see Codec#INT_STREAM
	 */
	public static final Codec<DoubleStream> DOUBLE_STREAM = new PrimitiveCodec<DoubleStream>() {
		@Override
		public <T> DataResult<DoubleStream> read(DynamicOps<T> ops, T input) {
			return Codecs.getDoubleStream(ops, input);
		}

		public <T> T write(DynamicOps<T> dynamicOps, DoubleStream doubleStream) {
			return Codecs.createDoubleList(dynamicOps, doubleStream);
		}

		public String toString() {
			return "DoubleStream";
		}
	};

	/**
	 * Decodes a double stream from {@code input}.
	 * 
	 * @param <T> the input data type
	 * @see DynamicOps#getIntStream(Object)
	 */
	private static <T> DataResult<DoubleStream> getDoubleStream(DynamicOps<T> ops, T input) {
		return ops.getStream(input)
			.flatMap(
				stream -> {
					List<T> list = (List<T>)stream.collect(Collectors.toList());
					return list.stream().allMatch(element -> ops.getNumberValue((T)element).result().isPresent())
						? DataResult.success(list.stream().mapToDouble(element -> ((Number)ops.getNumberValue((T)element).result().get()).doubleValue()))
						: DataResult.error("Some elements are not doubles: " + input);
				}
			);
	}

	/**
	 * Encodes a double stream to data of type {@code T}.
	 * 
	 * @param <T> the output data type
	 * @see DynamicOps#createIntList(java.util.stream.IntStream)
	 */
	private static <T> T createDoubleList(DynamicOps<T> ops, DoubleStream input) {
		return ops.createList(input.mapToObj(ops::createDouble));
	}

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
