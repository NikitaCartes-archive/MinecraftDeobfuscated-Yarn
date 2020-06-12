package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class NumberCodecs {
	private static Function<Integer, DataResult<Integer>> createRangedDataResultFactory(int min, int max) {
		return integer -> integer >= min && integer <= max
				? DataResult.success(integer)
				: DataResult.error("Value " + integer + " outside of range [" + min + ":" + max + "]", integer);
	}

	public static Codec<Integer> rangedInt(int min, int max) {
		Function<Integer, DataResult<Integer>> function = createRangedDataResultFactory(min, max);
		return Codec.INT.flatXmap(function, function);
	}

	private static Function<Double, DataResult<Double>> createRangedDataResultFactory(double min, double max) {
		return double_ -> double_ >= min && double_ <= max
				? DataResult.success(double_)
				: DataResult.error("Value " + double_ + " outside of range [" + min + ":" + max + "]", double_);
	}

	public static Codec<Double> rangedDouble(double min, double max) {
		Function<Double, DataResult<Double>> function = createRangedDataResultFactory(min, max);
		return Codec.DOUBLE.flatXmap(function, function);
	}

	public static <T> MapCodec<Pair<RegistryKey<T>, T>> method_29906(RegistryKey<Registry<T>> registryKey, MapCodec<T> mapCodec) {
		return Codec.mapPair(Identifier.CODEC.<RegistryKey<T>>xmap(RegistryKey.createKeyFactory(registryKey), RegistryKey::getValue).fieldOf("name"), mapCodec);
	}

	private static <A> MapCodec<A> method_29904(MapCodec<A> mapCodec, NumberCodecs.class_5395<A> arg) {
		return new MapCodec<A>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
				return mapCodec.keys(dynamicOps);
			}

			@Override
			public <T> RecordBuilder<T> encode(A object, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
				return arg.method_29908(dynamicOps, object, mapCodec.encode(object, dynamicOps, recordBuilder));
			}

			@Override
			public <T> DataResult<A> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
				return arg.method_29907(dynamicOps, mapLike, mapCodec.decode(dynamicOps, mapLike));
			}

			public String toString() {
				return mapCodec + "[mapResult " + arg + "]";
			}
		};
	}

	public static <A> MapCodec<A> method_29905(MapCodec<A> mapCodec, Consumer<String> consumer, Supplier<? extends A> supplier) {
		return method_29904(mapCodec, new NumberCodecs.class_5395<A>() {
			@Override
			public <T> DataResult<A> method_29907(DynamicOps<T> dynamicOps, MapLike<T> mapLike, DataResult<A> dataResult) {
				return DataResult.success((A)dataResult.resultOrPartial(consumer).orElseGet(supplier));
			}

			@Override
			public <T> RecordBuilder<T> method_29908(DynamicOps<T> dynamicOps, A object, RecordBuilder<T> recordBuilder) {
				return recordBuilder;
			}

			public String toString() {
				return "WithDefault[" + supplier.get() + "]";
			}
		});
	}

	public static <A> MapCodec<A> method_30018(MapCodec<A> mapCodec, Supplier<A> supplier) {
		return method_29904(mapCodec, new NumberCodecs.class_5395<A>() {
			@Override
			public <T> DataResult<A> method_29907(DynamicOps<T> dynamicOps, MapLike<T> mapLike, DataResult<A> dataResult) {
				return dataResult.setPartial(supplier);
			}

			@Override
			public <T> RecordBuilder<T> method_29908(DynamicOps<T> dynamicOps, A object, RecordBuilder<T> recordBuilder) {
				return recordBuilder;
			}

			public String toString() {
				return "SetPartial[" + supplier + "]";
			}
		});
	}

	interface class_5395<A> {
		<T> DataResult<A> method_29907(DynamicOps<T> dynamicOps, MapLike<T> mapLike, DataResult<A> dataResult);

		<T> RecordBuilder<T> method_29908(DynamicOps<T> dynamicOps, A object, RecordBuilder<T> recordBuilder);
	}
}
