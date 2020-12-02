package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class class_5699 {
	public static final Codec<DoubleStream> field_28095 = new PrimitiveCodec<DoubleStream>() {
		@Override
		public <T> DataResult<DoubleStream> read(DynamicOps<T> dynamicOps, T object) {
			return class_5699.method_32848(dynamicOps, object);
		}

		public <T> T write(DynamicOps<T> dynamicOps, DoubleStream doubleStream) {
			return class_5699.method_32850(dynamicOps, doubleStream);
		}

		public String toString() {
			return "DoubleStream";
		}
	};

	public static <T> DataResult<DoubleStream> method_32848(DynamicOps<T> dynamicOps, T object) {
		return dynamicOps.getStream(object)
			.flatMap(
				stream -> {
					List<T> list = (List<T>)stream.collect(Collectors.toList());
					return list.stream().allMatch(objectxx -> dynamicOps.getNumberValue((T)objectxx).result().isPresent())
						? DataResult.success(list.stream().mapToDouble(objectxx -> ((Number)dynamicOps.getNumberValue((T)objectxx).result().get()).doubleValue()))
						: DataResult.error("Some elements are not doubles: " + object);
				}
			);
	}

	public static <T> T method_32850(DynamicOps<T> dynamicOps, DoubleStream doubleStream) {
		return dynamicOps.createList(doubleStream.mapToObj(dynamicOps::createDouble));
	}
}
