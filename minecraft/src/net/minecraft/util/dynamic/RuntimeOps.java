package net.minecraft.util.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractUniversalBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * A dynamic ops that "serializes" data to a Java runtime representation.
 * This is most useful when combined with another ops.
 * 
 * <p>The objects must be one of: numbers, booleans, string, {@link Map}, {@link List},
 * {@link it.unimi.dsi.fastutil.bytes.ByteList}, {@link
 * it.unimi.dsi.fastutil.ints.IntList}, or {@link it.unimi.dsi.fastutil.longs.LongList}.
 * Arrays are not supported.
 * 
 * @see Codecs#BASIC_OBJECT
 */
public class RuntimeOps implements DynamicOps<Object> {
	public static final RuntimeOps INSTANCE = new RuntimeOps();

	private RuntimeOps() {
	}

	@Override
	public Object empty() {
		return null;
	}

	@Override
	public Object emptyMap() {
		return Map.of();
	}

	@Override
	public Object emptyList() {
		return List.of();
	}

	@Override
	public <U> U convertTo(DynamicOps<U> ops, Object object) {
		if (object == null) {
			return ops.empty();
		} else if (object instanceof Map) {
			return this.convertMap(ops, object);
		} else if (object instanceof ByteList byteList) {
			return ops.createByteList(ByteBuffer.wrap(byteList.toByteArray()));
		} else if (object instanceof IntList intList) {
			return ops.createIntList(intList.intStream());
		} else if (object instanceof LongList longList) {
			return ops.createLongList(longList.longStream());
		} else if (object instanceof List) {
			return this.convertList(ops, object);
		} else if (object instanceof String string) {
			return ops.createString(string);
		} else if (object instanceof Boolean boolean_) {
			return ops.createBoolean(boolean_);
		} else if (object instanceof Byte byte_) {
			return ops.createByte(byte_);
		} else if (object instanceof Short short_) {
			return ops.createShort(short_);
		} else if (object instanceof Integer integer) {
			return ops.createInt(integer);
		} else if (object instanceof Long long_) {
			return ops.createLong(long_);
		} else if (object instanceof Float float_) {
			return ops.createFloat(float_);
		} else if (object instanceof Double double_) {
			return ops.createDouble(double_);
		} else if (object instanceof Number number) {
			return ops.createNumeric(number);
		} else {
			throw new IllegalStateException("Don't know how to convert " + object);
		}
	}

	@Override
	public DataResult<Number> getNumberValue(Object object) {
		return object instanceof Number number ? DataResult.success(number) : DataResult.error(() -> "Not a number: " + object);
	}

	@Override
	public Object createNumeric(Number value) {
		return value;
	}

	@Override
	public Object createByte(byte value) {
		return value;
	}

	@Override
	public Object createShort(short value) {
		return value;
	}

	@Override
	public Object createInt(int value) {
		return value;
	}

	@Override
	public Object createLong(long value) {
		return value;
	}

	@Override
	public Object createFloat(float value) {
		return value;
	}

	@Override
	public Object createDouble(double value) {
		return value;
	}

	@Override
	public DataResult<Boolean> getBooleanValue(Object object) {
		return object instanceof Boolean boolean_ ? DataResult.success(boolean_) : DataResult.error(() -> "Not a boolean: " + object);
	}

	@Override
	public Object createBoolean(boolean value) {
		return value;
	}

	@Override
	public DataResult<String> getStringValue(Object object) {
		return object instanceof String string ? DataResult.success(string) : DataResult.error(() -> "Not a string: " + object);
	}

	@Override
	public Object createString(String value) {
		return value;
	}

	@Override
	public DataResult<Object> mergeToList(Object list, Object value) {
		if (list == this.empty()) {
			return DataResult.success(List.of(value));
		} else if (list instanceof List<?> list2) {
			return list2.isEmpty()
				? DataResult.success(List.of(value))
				: DataResult.success(ImmutableList.<Object>builder().addAll((Iterable<? extends Object>)list2).add(value).build());
		} else {
			return DataResult.error(() -> "Not a list: " + list);
		}
	}

	@Override
	public DataResult<Object> mergeToList(Object list, List<Object> values) {
		if (list == this.empty()) {
			return DataResult.success(values);
		} else if (list instanceof List<?> list2) {
			return list2.isEmpty() ? DataResult.success(values) : DataResult.success(ImmutableList.builder().addAll(list2).addAll(values).build());
		} else {
			return DataResult.error(() -> "Not a list: " + list);
		}
	}

	@Override
	public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
		if (map == this.empty()) {
			return DataResult.success(Map.of(key, value));
		} else if (map instanceof Map<?, ?> map2) {
			if (map2.isEmpty()) {
				return DataResult.success(Map.of(key, value));
			} else {
				Builder<Object, Object> builder = ImmutableMap.builderWithExpectedSize(map2.size() + 1);
				builder.putAll((Map<? extends Object, ? extends Object>)map2);
				builder.put(key, value);
				return DataResult.success(builder.buildKeepingLast());
			}
		} else {
			return DataResult.error(() -> "Not a map: " + map);
		}
	}

	@Override
	public DataResult<Object> mergeToMap(Object map, Map<Object, Object> map2) {
		if (map == this.empty()) {
			return DataResult.success(map2);
		} else if (map instanceof Map<?, ?> map3) {
			if (map3.isEmpty()) {
				return DataResult.success(map2);
			} else {
				Builder<Object, Object> builder = ImmutableMap.builderWithExpectedSize(map3.size() + map2.size());
				builder.putAll((Map<? extends Object, ? extends Object>)map3);
				builder.putAll(map2);
				return DataResult.success(builder.buildKeepingLast());
			}
		} else {
			return DataResult.error(() -> "Not a map: " + map);
		}
	}

	private static Map<Object, Object> toMap(MapLike<Object> mapLike) {
		return (Map<Object, Object>)mapLike.entries().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
	}

	@Override
	public DataResult<Object> mergeToMap(Object map, MapLike<Object> map2) {
		if (map == this.empty()) {
			return DataResult.success(toMap(map2));
		} else if (map instanceof Map<?, ?> map3) {
			if (map3.isEmpty()) {
				return DataResult.success(toMap(map2));
			} else {
				Builder<Object, Object> builder = ImmutableMap.builderWithExpectedSize(map3.size());
				builder.putAll((Map<? extends Object, ? extends Object>)map3);
				map2.entries().forEach(pair -> builder.put(pair.getFirst(), pair.getSecond()));
				return DataResult.success(builder.buildKeepingLast());
			}
		} else {
			return DataResult.error(() -> "Not a map: " + map);
		}
	}

	static Stream<Pair<Object, Object>> streamEntries(Map<?, ?> map) {
		return map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
	}

	@Override
	public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object map) {
		return map instanceof Map<?, ?> map2 ? DataResult.success(streamEntries(map2)) : DataResult.error(() -> "Not a map: " + map);
	}

	@Override
	public DataResult<Consumer<BiConsumer<Object, Object>>> getMapEntries(Object map) {
		return map instanceof Map<?, ?> map2 ? DataResult.success(map2::forEach) : DataResult.error(() -> "Not a map: " + map);
	}

	@Override
	public Object createMap(Stream<Pair<Object, Object>> entries) {
		return entries.collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
	}

	@Override
	public DataResult<MapLike<Object>> getMap(Object map) {
		return map instanceof Map<?, ?> map2 ? DataResult.success(new MapLike<Object>() {
			@Nullable
			@Override
			public Object get(Object key) {
				return map2.get(key);
			}

			@Nullable
			@Override
			public Object get(String key) {
				return map2.get(key);
			}

			@Override
			public Stream<Pair<Object, Object>> entries() {
				return RuntimeOps.streamEntries(map2);
			}

			public String toString() {
				return "MapLike[" + map2 + "]";
			}
		}) : DataResult.error(() -> "Not a map: " + map);
	}

	@Override
	public Object createMap(Map<Object, Object> map) {
		return map;
	}

	@Override
	public DataResult<Stream<Object>> getStream(Object list) {
		return list instanceof List<?> list2 ? DataResult.success(list2.stream().map(value -> value)) : DataResult.error(() -> "Not an list: " + list);
	}

	@Override
	public DataResult<Consumer<Consumer<Object>>> getList(Object list) {
		return list instanceof List<?> list2 ? DataResult.success(list2::forEach) : DataResult.error(() -> "Not an list: " + list);
	}

	@Override
	public Object createList(Stream<Object> stream) {
		return stream.toList();
	}

	@Override
	public DataResult<ByteBuffer> getByteBuffer(Object byteList) {
		return byteList instanceof ByteList byteList2
			? DataResult.success(ByteBuffer.wrap(byteList2.toByteArray()))
			: DataResult.error(() -> "Not a byte list: " + byteList);
	}

	@Override
	public Object createByteList(ByteBuffer buf) {
		ByteBuffer byteBuffer = buf.duplicate().clear();
		ByteArrayList byteArrayList = new ByteArrayList();
		byteArrayList.size(byteBuffer.capacity());
		byteBuffer.get(0, byteArrayList.elements(), 0, byteArrayList.size());
		return byteArrayList;
	}

	@Override
	public DataResult<IntStream> getIntStream(Object intList) {
		return intList instanceof IntList intList2 ? DataResult.success(intList2.intStream()) : DataResult.error(() -> "Not an int list: " + intList);
	}

	@Override
	public Object createIntList(IntStream stream) {
		return IntArrayList.toList(stream);
	}

	@Override
	public DataResult<LongStream> getLongStream(Object longList) {
		return longList instanceof LongList longList2 ? DataResult.success(longList2.longStream()) : DataResult.error(() -> "Not a long list: " + longList);
	}

	@Override
	public Object createLongList(LongStream stream) {
		return LongArrayList.toList(stream);
	}

	@Override
	public Object remove(Object map, String key) {
		if (map instanceof Map<?, ?> map2) {
			Map<Object, Object> map3 = new LinkedHashMap(map2);
			map3.remove(key);
			return DataResult.success(Map.copyOf(map3));
		} else {
			return DataResult.error(() -> "Not a map: " + map);
		}
	}

	@Override
	public RecordBuilder<Object> mapBuilder() {
		return new RuntimeOps.MapBuilder<>(this);
	}

	public String toString() {
		return "Java";
	}

	static final class MapBuilder<T> extends AbstractUniversalBuilder<T, Builder<T, T>> {
		public MapBuilder(DynamicOps<T> ops) {
			super(ops);
		}

		protected Builder<T, T> initBuilder() {
			return ImmutableMap.builder();
		}

		protected Builder<T, T> append(T object, T object2, Builder<T, T> builder) {
			return builder.put(object, object2);
		}

		protected DataResult<T> build(Builder<T, T> builder, T object) {
			ImmutableMap<T, T> immutableMap;
			try {
				immutableMap = builder.buildOrThrow();
			} catch (IllegalArgumentException var5) {
				return DataResult.error(() -> "Can't build map: " + var5.getMessage());
			}

			return this.ops().mergeToMap(object, immutableMap);
		}
	}
}
