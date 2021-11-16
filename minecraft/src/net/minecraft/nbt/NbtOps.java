package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * Used to handle Minecraft NBTs within {@link com.mojang.serialization.Dynamic
 * dynamics} for DataFixerUpper, allowing generalized serialization logic
 * shared across different type of data structures. Use {@link NbtOps#INSTANCE}
 * for the ops singleton.
 * 
 * <p>For instance, dimension data may be stored as JSON in data packs, but
 * they will be transported in packets as NBT. DataFixerUpper allows
 * generalizing the dimension serialization logic to prevent duplicate code,
 * where the NBT ops allow the DataFixerUpper dimension serialization logic
 * to interact with Minecraft NBTs.
 * 
 * @see NbtOps#INSTANCE
 */
public class NbtOps implements DynamicOps<NbtElement> {
	/**
	 * An singleton of the NBT dynamic ops.
	 * 
	 * <p>This ops does not compress maps (replace field name to value pairs
	 * with an ordered list of values in serialization). In fact, since
	 * Minecraft NBT lists can only contain elements of the same type, this op
	 * cannot compress maps.
	 */
	public static final NbtOps INSTANCE = new NbtOps();

	protected NbtOps() {
	}

	public NbtElement empty() {
		return NbtNull.INSTANCE;
	}

	public <U> U convertTo(DynamicOps<U> dynamicOps, NbtElement nbtElement) {
		switch (nbtElement.getType()) {
			case 0:
				return dynamicOps.empty();
			case 1:
				return dynamicOps.createByte(((AbstractNbtNumber)nbtElement).byteValue());
			case 2:
				return dynamicOps.createShort(((AbstractNbtNumber)nbtElement).shortValue());
			case 3:
				return dynamicOps.createInt(((AbstractNbtNumber)nbtElement).intValue());
			case 4:
				return dynamicOps.createLong(((AbstractNbtNumber)nbtElement).longValue());
			case 5:
				return dynamicOps.createFloat(((AbstractNbtNumber)nbtElement).floatValue());
			case 6:
				return dynamicOps.createDouble(((AbstractNbtNumber)nbtElement).doubleValue());
			case 7:
				return dynamicOps.createByteList(ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()));
			case 8:
				return dynamicOps.createString(nbtElement.asString());
			case 9:
				return this.convertList(dynamicOps, nbtElement);
			case 10:
				return this.convertMap(dynamicOps, nbtElement);
			case 11:
				return dynamicOps.createIntList(Arrays.stream(((NbtIntArray)nbtElement).getIntArray()));
			case 12:
				return dynamicOps.createLongList(Arrays.stream(((NbtLongArray)nbtElement).getLongArray()));
			default:
				throw new IllegalStateException("Unknown tag type: " + nbtElement);
		}
	}

	public DataResult<Number> getNumberValue(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtNumber ? DataResult.success(((AbstractNbtNumber)nbtElement).numberValue()) : DataResult.error("Not a number");
	}

	public NbtElement createNumeric(Number number) {
		return NbtDouble.of(number.doubleValue());
	}

	public NbtElement createByte(byte b) {
		return NbtByte.of(b);
	}

	public NbtElement createShort(short s) {
		return NbtShort.of(s);
	}

	public NbtElement createInt(int i) {
		return NbtInt.of(i);
	}

	public NbtElement createLong(long l) {
		return NbtLong.of(l);
	}

	public NbtElement createFloat(float f) {
		return NbtFloat.of(f);
	}

	public NbtElement createDouble(double d) {
		return NbtDouble.of(d);
	}

	public NbtElement createBoolean(boolean bl) {
		return NbtByte.of(bl);
	}

	public DataResult<String> getStringValue(NbtElement nbtElement) {
		return nbtElement instanceof NbtString ? DataResult.success(nbtElement.asString()) : DataResult.error("Not a string");
	}

	public NbtElement createString(String string) {
		return NbtString.of(string);
	}

	private static AbstractNbtList<?> createList(byte knownType, byte valueType) {
		if (isTypeEqual(knownType, valueType, (byte)4)) {
			return new NbtLongArray(new long[0]);
		} else if (isTypeEqual(knownType, valueType, (byte)1)) {
			return new NbtByteArray(new byte[0]);
		} else {
			return (AbstractNbtList<?>)(isTypeEqual(knownType, valueType, (byte)3) ? new NbtIntArray(new int[0]) : new NbtList());
		}
	}

	private static boolean isTypeEqual(byte knownType, byte valueType, byte expectedType) {
		return knownType == expectedType && (valueType == expectedType || valueType == 0);
	}

	private static <T extends NbtElement> void addAll(AbstractNbtList<T> destination, NbtElement source, NbtElement additionalValue) {
		if (source instanceof AbstractNbtList<?> abstractNbtList) {
			abstractNbtList.forEach(nbt -> destination.add(nbt));
		}

		destination.add(additionalValue);
	}

	private static <T extends NbtElement> void addAll(AbstractNbtList<T> destination, NbtElement source, List<NbtElement> additionalValues) {
		if (source instanceof AbstractNbtList<?> abstractNbtList) {
			abstractNbtList.forEach(nbt -> destination.add(nbt));
		}

		additionalValues.forEach(nbt -> destination.add(nbt));
	}

	public DataResult<NbtElement> mergeToList(NbtElement nbtElement, NbtElement nbtElement2) {
		if (!(nbtElement instanceof AbstractNbtList) && !(nbtElement instanceof NbtNull)) {
			return DataResult.error("mergeToList called with not a list: " + nbtElement, nbtElement);
		} else {
			AbstractNbtList<?> abstractNbtList = createList(
				nbtElement instanceof AbstractNbtList ? ((AbstractNbtList)nbtElement).getHeldType() : NbtElement.NULL_TYPE, nbtElement2.getType()
			);
			addAll(abstractNbtList, nbtElement, nbtElement2);
			return DataResult.success(abstractNbtList);
		}
	}

	public DataResult<NbtElement> mergeToList(NbtElement nbtElement, List<NbtElement> list) {
		if (!(nbtElement instanceof AbstractNbtList) && !(nbtElement instanceof NbtNull)) {
			return DataResult.error("mergeToList called with not a list: " + nbtElement, nbtElement);
		} else {
			AbstractNbtList<?> abstractNbtList = createList(
				nbtElement instanceof AbstractNbtList ? ((AbstractNbtList)nbtElement).getHeldType() : NbtElement.NULL_TYPE,
				(Byte)list.stream().findFirst().map(NbtElement::getType).orElse((byte)0)
			);
			addAll(abstractNbtList, nbtElement, list);
			return DataResult.success(abstractNbtList);
		}
	}

	public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
		if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtNull)) {
			return DataResult.error("mergeToMap called with not a map: " + nbtElement, nbtElement);
		} else if (!(nbtElement2 instanceof NbtString)) {
			return DataResult.error("key is not a string: " + nbtElement2, nbtElement);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			if (nbtElement instanceof NbtCompound nbtCompound2) {
				nbtCompound2.getKeys().forEach(key -> nbtCompound.put(key, nbtCompound2.get(key)));
			}

			nbtCompound.put(nbtElement2.asString(), nbtElement3);
			return DataResult.success(nbtCompound);
		}
	}

	public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, MapLike<NbtElement> mapLike) {
		if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtNull)) {
			return DataResult.error("mergeToMap called with not a map: " + nbtElement, nbtElement);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			if (nbtElement instanceof NbtCompound nbtCompound2) {
				nbtCompound2.getKeys().forEach(string -> nbtCompound.put(string, nbtCompound2.get(string)));
			}

			List<NbtElement> list = Lists.<NbtElement>newArrayList();
			mapLike.entries().forEach(pair -> {
				NbtElement nbtElementx = (NbtElement)pair.getFirst();
				if (!(nbtElementx instanceof NbtString)) {
					list.add(nbtElementx);
				} else {
					nbtCompound.put(nbtElementx.asString(), (NbtElement)pair.getSecond());
				}
			});
			return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, nbtCompound) : DataResult.success(nbtCompound);
		}
	}

	public DataResult<Stream<Pair<NbtElement, NbtElement>>> getMapValues(NbtElement nbtElement) {
		return !(nbtElement instanceof NbtCompound nbtCompound)
			? DataResult.error("Not a map: " + nbtElement)
			: DataResult.success(nbtCompound.getKeys().stream().map(key -> Pair.of(this.createString(key), nbtCompound.get(key))));
	}

	public DataResult<Consumer<BiConsumer<NbtElement, NbtElement>>> getMapEntries(NbtElement nbtElement) {
		return !(nbtElement instanceof NbtCompound nbtCompound)
			? DataResult.error("Not a map: " + nbtElement)
			: DataResult.success(entryConsumer -> nbtCompound.getKeys().forEach(key -> entryConsumer.accept(this.createString(key), nbtCompound.get(key))));
	}

	public DataResult<MapLike<NbtElement>> getMap(NbtElement nbtElement) {
		return !(nbtElement instanceof NbtCompound nbtCompound) ? DataResult.error("Not a map: " + nbtElement) : DataResult.success(new MapLike<NbtElement>() {
			@Nullable
			public NbtElement get(NbtElement nbtElement) {
				return nbtCompound.get(nbtElement.asString());
			}

			@Nullable
			public NbtElement get(String string) {
				return nbtCompound.get(string);
			}

			@Override
			public Stream<Pair<NbtElement, NbtElement>> entries() {
				return nbtCompound.getKeys().stream().map(key -> Pair.of(NbtOps.this.createString(key), nbtCompound.get(key)));
			}

			public String toString() {
				return "MapLike[" + nbtCompound + "]";
			}
		});
	}

	public NbtElement createMap(Stream<Pair<NbtElement, NbtElement>> stream) {
		NbtCompound nbtCompound = new NbtCompound();
		stream.forEach(entry -> nbtCompound.put(((NbtElement)entry.getFirst()).asString(), (NbtElement)entry.getSecond()));
		return nbtCompound;
	}

	public DataResult<Stream<NbtElement>> getStream(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtList ? DataResult.success(((AbstractNbtList)nbtElement).stream().map(nbt -> nbt)) : DataResult.error("Not a list");
	}

	public DataResult<Consumer<Consumer<NbtElement>>> getList(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtList<?> abstractNbtList
			? DataResult.success(abstractNbtList::forEach)
			: DataResult.error("Not a list: " + nbtElement);
	}

	public DataResult<ByteBuffer> getByteBuffer(NbtElement nbtElement) {
		return nbtElement instanceof NbtByteArray
			? DataResult.success(ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()))
			: DynamicOps.super.getByteBuffer(nbtElement);
	}

	public NbtElement createByteList(ByteBuffer byteBuffer) {
		return new NbtByteArray(DataFixUtils.toArray(byteBuffer));
	}

	public DataResult<IntStream> getIntStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtIntArray
			? DataResult.success(Arrays.stream(((NbtIntArray)nbtElement).getIntArray()))
			: DynamicOps.super.getIntStream(nbtElement);
	}

	public NbtElement createIntList(IntStream intStream) {
		return new NbtIntArray(intStream.toArray());
	}

	public DataResult<LongStream> getLongStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtLongArray
			? DataResult.success(Arrays.stream(((NbtLongArray)nbtElement).getLongArray()))
			: DynamicOps.super.getLongStream(nbtElement);
	}

	public NbtElement createLongList(LongStream longStream) {
		return new NbtLongArray(longStream.toArray());
	}

	public NbtElement createList(Stream<NbtElement> stream) {
		PeekingIterator<NbtElement> peekingIterator = Iterators.peekingIterator(stream.iterator());
		if (!peekingIterator.hasNext()) {
			return new NbtList();
		} else {
			NbtElement nbtElement = peekingIterator.peek();
			if (nbtElement instanceof NbtByte) {
				List<Byte> list = Lists.<Byte>newArrayList(Iterators.transform(peekingIterator, nbt -> ((NbtByte)nbt).byteValue()));
				return new NbtByteArray(list);
			} else if (nbtElement instanceof NbtInt) {
				List<Integer> list = Lists.<Integer>newArrayList(Iterators.transform(peekingIterator, nbt -> ((NbtInt)nbt).intValue()));
				return new NbtIntArray(list);
			} else if (nbtElement instanceof NbtLong) {
				List<Long> list = Lists.<Long>newArrayList(Iterators.transform(peekingIterator, nbt -> ((NbtLong)nbt).longValue()));
				return new NbtLongArray(list);
			} else {
				NbtList nbtList = new NbtList();

				while (peekingIterator.hasNext()) {
					NbtElement nbtElement2 = peekingIterator.next();
					if (!(nbtElement2 instanceof NbtNull)) {
						nbtList.add(nbtElement2);
					}
				}

				return nbtList;
			}
		}
	}

	public NbtElement remove(NbtElement nbtElement, String string) {
		if (nbtElement instanceof NbtCompound nbtCompound) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound.getKeys().stream().filter(k -> !Objects.equals(k, string)).forEach(k -> nbtCompound2.put(k, nbtCompound.get(k)));
			return nbtCompound2;
		} else {
			return nbtElement;
		}
	}

	public String toString() {
		return "NBT";
	}

	@Override
	public RecordBuilder<NbtElement> mapBuilder() {
		return new NbtOps.MapBuilder();
	}

	class MapBuilder extends AbstractStringBuilder<NbtElement, NbtCompound> {
		protected MapBuilder() {
			super(NbtOps.this);
		}

		protected NbtCompound initBuilder() {
			return new NbtCompound();
		}

		protected NbtCompound append(String string, NbtElement nbtElement, NbtCompound nbtCompound) {
			nbtCompound.put(string, nbtElement);
			return nbtCompound;
		}

		protected DataResult<NbtElement> build(NbtCompound nbtCompound, NbtElement nbtElement) {
			if (nbtElement == null || nbtElement == NbtNull.INSTANCE) {
				return DataResult.success(nbtCompound);
			} else if (!(nbtElement instanceof NbtCompound)) {
				return DataResult.error("mergeToMap called with not a map: " + nbtElement, nbtElement);
			} else {
				NbtCompound nbtCompound2 = new NbtCompound(Maps.<String, NbtElement>newHashMap(((NbtCompound)nbtElement).toMap()));

				for (Entry<String, NbtElement> entry : nbtCompound.toMap().entrySet()) {
					nbtCompound2.put((String)entry.getKey(), (NbtElement)entry.getValue());
				}

				return DataResult.success(nbtCompound2);
			}
		}
	}
}
