package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.List;
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
	public static <T> DataResult<DoubleStream> getDoubleStream(DynamicOps<T> ops, T input) {
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
	public static <T> T createDoubleList(DynamicOps<T> ops, DoubleStream input) {
		return ops.createList(input.mapToObj(ops::createDouble));
	}
}
