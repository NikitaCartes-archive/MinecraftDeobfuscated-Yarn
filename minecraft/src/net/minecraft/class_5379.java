package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class class_5379<T> implements DynamicOps<T> {
	protected final DynamicOps<T> field_25503;

	protected class_5379(DynamicOps<T> dynamicOps) {
		this.field_25503 = dynamicOps;
	}

	@Override
	public T empty() {
		return this.field_25503.empty();
	}

	@Override
	public <U> U convertTo(DynamicOps<U> dynamicOps, T object) {
		return this.field_25503.convertTo(dynamicOps, object);
	}

	@Override
	public DataResult<Number> getNumberValue(T object) {
		return this.field_25503.getNumberValue(object);
	}

	@Override
	public T createNumeric(Number number) {
		return this.field_25503.createNumeric(number);
	}

	@Override
	public T createByte(byte b) {
		return this.field_25503.createByte(b);
	}

	@Override
	public T createShort(short s) {
		return this.field_25503.createShort(s);
	}

	@Override
	public T createInt(int i) {
		return this.field_25503.createInt(i);
	}

	@Override
	public T createLong(long l) {
		return this.field_25503.createLong(l);
	}

	@Override
	public T createFloat(float f) {
		return this.field_25503.createFloat(f);
	}

	@Override
	public T createDouble(double d) {
		return this.field_25503.createDouble(d);
	}

	@Override
	public DataResult<Boolean> getBooleanValue(T object) {
		return this.field_25503.getBooleanValue(object);
	}

	@Override
	public T createBoolean(boolean bl) {
		return this.field_25503.createBoolean(bl);
	}

	@Override
	public DataResult<String> getStringValue(T object) {
		return this.field_25503.getStringValue(object);
	}

	@Override
	public T createString(String string) {
		return this.field_25503.createString(string);
	}

	@Override
	public DataResult<T> mergeToList(T object, T object2) {
		return this.field_25503.mergeToList(object, object2);
	}

	@Override
	public DataResult<T> mergeToList(T object, List<T> list) {
		return this.field_25503.mergeToList(object, list);
	}

	@Override
	public DataResult<T> mergeToMap(T object, T object2, T object3) {
		return this.field_25503.mergeToMap(object, object2, object3);
	}

	@Override
	public DataResult<T> mergeToMap(T object, MapLike<T> mapLike) {
		return this.field_25503.mergeToMap(object, mapLike);
	}

	@Override
	public DataResult<Stream<Pair<T, T>>> getMapValues(T object) {
		return this.field_25503.getMapValues(object);
	}

	@Override
	public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T object) {
		return this.field_25503.getMapEntries(object);
	}

	@Override
	public T createMap(Stream<Pair<T, T>> stream) {
		return this.field_25503.createMap(stream);
	}

	@Override
	public DataResult<MapLike<T>> getMap(T object) {
		return this.field_25503.getMap(object);
	}

	@Override
	public DataResult<Stream<T>> getStream(T object) {
		return this.field_25503.getStream(object);
	}

	@Override
	public DataResult<Consumer<Consumer<T>>> getList(T object) {
		return this.field_25503.getList(object);
	}

	@Override
	public T createList(Stream<T> stream) {
		return this.field_25503.createList(stream);
	}

	@Override
	public DataResult<ByteBuffer> getByteBuffer(T object) {
		return this.field_25503.getByteBuffer(object);
	}

	@Override
	public T createByteList(ByteBuffer byteBuffer) {
		return this.field_25503.createByteList(byteBuffer);
	}

	@Override
	public DataResult<IntStream> getIntStream(T object) {
		return this.field_25503.getIntStream(object);
	}

	@Override
	public T createIntList(IntStream intStream) {
		return this.field_25503.createIntList(intStream);
	}

	@Override
	public DataResult<LongStream> getLongStream(T object) {
		return this.field_25503.getLongStream(object);
	}

	@Override
	public T createLongList(LongStream longStream) {
		return this.field_25503.createLongList(longStream);
	}

	@Override
	public T remove(T object, String string) {
		return this.field_25503.remove(object, string);
	}

	@Override
	public boolean compressMaps() {
		return this.field_25503.compressMaps();
	}

	@Override
	public ListBuilder<T> listBuilder() {
		return this.field_25503.listBuilder();
	}

	@Override
	public RecordBuilder<T> mapBuilder() {
		return this.field_25503.mapBuilder();
	}
}
