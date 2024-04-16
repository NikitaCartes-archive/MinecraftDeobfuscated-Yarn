package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private static final String MARKER_KEY = "";

	protected NbtOps() {
	}

	public NbtElement empty() {
		return NbtEnd.INSTANCE;
	}

	public <U> U convertTo(DynamicOps<U> dynamicOps, NbtElement nbtElement) {
		return (U)(switch (nbtElement.getType()) {
			case 0 -> (Object)dynamicOps.empty();
			case 1 -> (Object)dynamicOps.createByte(((AbstractNbtNumber)nbtElement).byteValue());
			case 2 -> (Object)dynamicOps.createShort(((AbstractNbtNumber)nbtElement).shortValue());
			case 3 -> (Object)dynamicOps.createInt(((AbstractNbtNumber)nbtElement).intValue());
			case 4 -> (Object)dynamicOps.createLong(((AbstractNbtNumber)nbtElement).longValue());
			case 5 -> (Object)dynamicOps.createFloat(((AbstractNbtNumber)nbtElement).floatValue());
			case 6 -> (Object)dynamicOps.createDouble(((AbstractNbtNumber)nbtElement).doubleValue());
			case 7 -> (Object)dynamicOps.createByteList(ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()));
			case 8 -> (Object)dynamicOps.createString(nbtElement.asString());
			case 9 -> (Object)this.convertList(dynamicOps, nbtElement);
			case 10 -> (Object)this.convertMap(dynamicOps, nbtElement);
			case 11 -> (Object)dynamicOps.createIntList(Arrays.stream(((NbtIntArray)nbtElement).getIntArray()));
			case 12 -> (Object)dynamicOps.createLongList(Arrays.stream(((NbtLongArray)nbtElement).getLongArray()));
			default -> throw new IllegalStateException("Unknown tag type: " + nbtElement);
		});
	}

	public DataResult<Number> getNumberValue(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtNumber abstractNbtNumber
			? DataResult.success(abstractNbtNumber.numberValue())
			: DataResult.error(() -> "Not a number");
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
		return nbtElement instanceof NbtString nbtString ? DataResult.success(nbtString.asString()) : DataResult.error(() -> "Not a string");
	}

	public NbtElement createString(String string) {
		return NbtString.of(string);
	}

	public DataResult<NbtElement> mergeToList(NbtElement nbtElement, NbtElement nbtElement2) {
		return (DataResult<NbtElement>)createMerger(nbtElement)
			.map(merger -> DataResult.success(merger.merge(nbtElement2).getResult()))
			.orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + nbtElement, nbtElement));
	}

	public DataResult<NbtElement> mergeToList(NbtElement nbtElement, List<NbtElement> list) {
		return (DataResult<NbtElement>)createMerger(nbtElement)
			.map(merger -> DataResult.success(merger.merge(list).getResult()))
			.orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + nbtElement, nbtElement));
	}

	public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
		if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
			return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
		} else if (!(nbtElement2 instanceof NbtString)) {
			return DataResult.error(() -> "key is not a string: " + nbtElement2, nbtElement);
		} else {
			NbtCompound nbtCompound2 = nbtElement instanceof NbtCompound nbtCompound ? nbtCompound.shallowCopy() : new NbtCompound();
			nbtCompound2.put(nbtElement2.asString(), nbtElement3);
			return DataResult.success(nbtCompound2);
		}
	}

	public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, MapLike<NbtElement> mapLike) {
		if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
			return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
		} else {
			NbtCompound nbtCompound2 = nbtElement instanceof NbtCompound nbtCompound ? nbtCompound.shallowCopy() : new NbtCompound();
			List<NbtElement> list = new ArrayList();
			mapLike.entries().forEach(pair -> {
				NbtElement nbtElementx = (NbtElement)pair.getFirst();
				if (!(nbtElementx instanceof NbtString)) {
					list.add(nbtElementx);
				} else {
					nbtCompound2.put(nbtElementx.asString(), (NbtElement)pair.getSecond());
				}
			});
			return !list.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + list, nbtCompound2) : DataResult.success(nbtCompound2);
		}
	}

	public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, Map<NbtElement, NbtElement> map) {
		if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
			return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
		} else {
			NbtCompound nbtCompound2 = nbtElement instanceof NbtCompound nbtCompound ? nbtCompound.shallowCopy() : new NbtCompound();
			List<NbtElement> list = new ArrayList();

			for (Entry<NbtElement, NbtElement> entry : map.entrySet()) {
				NbtElement nbtElement2 = (NbtElement)entry.getKey();
				if (nbtElement2 instanceof NbtString) {
					nbtCompound2.put(nbtElement2.asString(), (NbtElement)entry.getValue());
				} else {
					list.add(nbtElement2);
				}
			}

			return !list.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + list, nbtCompound2) : DataResult.success(nbtCompound2);
		}
	}

	public DataResult<Stream<Pair<NbtElement, NbtElement>>> getMapValues(NbtElement nbtElement) {
		return nbtElement instanceof NbtCompound nbtCompound
			? DataResult.success(nbtCompound.entrySet().stream().map(entry -> Pair.of(this.createString((String)entry.getKey()), (NbtElement)entry.getValue())))
			: DataResult.error(() -> "Not a map: " + nbtElement);
	}

	public DataResult<Consumer<BiConsumer<NbtElement, NbtElement>>> getMapEntries(NbtElement nbtElement) {
		return nbtElement instanceof NbtCompound nbtCompound ? DataResult.success(biConsumer -> {
			for (Entry<String, NbtElement> entry : nbtCompound.entrySet()) {
				biConsumer.accept(this.createString((String)entry.getKey()), (NbtElement)entry.getValue());
			}
		}) : DataResult.error(() -> "Not a map: " + nbtElement);
	}

	public DataResult<MapLike<NbtElement>> getMap(NbtElement nbtElement) {
		return nbtElement instanceof NbtCompound nbtCompound ? DataResult.success(new MapLike<NbtElement>() {
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
				return nbtCompound.entrySet().stream().map(entry -> Pair.of(NbtOps.this.createString((String)entry.getKey()), (NbtElement)entry.getValue()));
			}

			public String toString() {
				return "MapLike[" + nbtCompound + "]";
			}
		}) : DataResult.error(() -> "Not a map: " + nbtElement);
	}

	public NbtElement createMap(Stream<Pair<NbtElement, NbtElement>> stream) {
		NbtCompound nbtCompound = new NbtCompound();
		stream.forEach(entry -> nbtCompound.put(((NbtElement)entry.getFirst()).asString(), (NbtElement)entry.getSecond()));
		return nbtCompound;
	}

	private static NbtElement unpackMarker(NbtCompound nbt) {
		if (nbt.getSize() == 1) {
			NbtElement nbtElement = nbt.get("");
			if (nbtElement != null) {
				return nbtElement;
			}
		}

		return nbt;
	}

	public DataResult<Stream<NbtElement>> getStream(NbtElement nbtElement) {
		if (nbtElement instanceof NbtList nbtList) {
			return nbtList.getHeldType() == NbtElement.COMPOUND_TYPE
				? DataResult.success(nbtList.stream().map(nbt -> unpackMarker((NbtCompound)nbt)))
				: DataResult.success(nbtList.stream());
		} else {
			return nbtElement instanceof AbstractNbtList<?> abstractNbtList
				? DataResult.success(abstractNbtList.stream().map(nbt -> nbt))
				: DataResult.error(() -> "Not a list");
		}
	}

	public DataResult<Consumer<Consumer<NbtElement>>> getList(NbtElement nbtElement) {
		if (nbtElement instanceof NbtList nbtList) {
			return nbtList.getHeldType() == NbtElement.COMPOUND_TYPE ? DataResult.success(consumer -> {
				for (NbtElement nbtElementx : nbtList) {
					consumer.accept(unpackMarker((NbtCompound)nbtElementx));
				}
			}) : DataResult.success(nbtList::forEach);
		} else {
			return nbtElement instanceof AbstractNbtList<?> abstractNbtList
				? DataResult.success(abstractNbtList::forEach)
				: DataResult.error(() -> "Not a list: " + nbtElement);
		}
	}

	public DataResult<ByteBuffer> getByteBuffer(NbtElement nbtElement) {
		return nbtElement instanceof NbtByteArray nbtByteArray
			? DataResult.success(ByteBuffer.wrap(nbtByteArray.getByteArray()))
			: DynamicOps.super.getByteBuffer(nbtElement);
	}

	public NbtElement createByteList(ByteBuffer byteBuffer) {
		ByteBuffer byteBuffer2 = byteBuffer.duplicate().clear();
		byte[] bs = new byte[byteBuffer.capacity()];
		byteBuffer2.get(0, bs, 0, bs.length);
		return new NbtByteArray(bs);
	}

	public DataResult<IntStream> getIntStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtIntArray nbtIntArray
			? DataResult.success(Arrays.stream(nbtIntArray.getIntArray()))
			: DynamicOps.super.getIntStream(nbtElement);
	}

	public NbtElement createIntList(IntStream intStream) {
		return new NbtIntArray(intStream.toArray());
	}

	public DataResult<LongStream> getLongStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtLongArray nbtLongArray
			? DataResult.success(Arrays.stream(nbtLongArray.getLongArray()))
			: DynamicOps.super.getLongStream(nbtElement);
	}

	public NbtElement createLongList(LongStream longStream) {
		return new NbtLongArray(longStream.toArray());
	}

	public NbtElement createList(Stream<NbtElement> stream) {
		return NbtOps.BasicMerger.EMPTY.merge(stream).getResult();
	}

	public NbtElement remove(NbtElement nbtElement, String string) {
		if (nbtElement instanceof NbtCompound nbtCompound) {
			NbtCompound nbtCompound2 = nbtCompound.shallowCopy();
			nbtCompound2.remove(string);
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

	private static Optional<NbtOps.Merger> createMerger(NbtElement nbt) {
		if (nbt instanceof NbtEnd) {
			return Optional.of(NbtOps.BasicMerger.EMPTY);
		} else {
			if (nbt instanceof AbstractNbtList<?> abstractNbtList) {
				if (abstractNbtList.isEmpty()) {
					return Optional.of(NbtOps.BasicMerger.EMPTY);
				}

				if (abstractNbtList instanceof NbtList nbtList) {
					return switch (nbtList.getHeldType()) {
						case 0 -> Optional.of(NbtOps.BasicMerger.EMPTY);
						case 10 -> Optional.of(new NbtOps.CompoundListMerger(nbtList));
						default -> Optional.of(new NbtOps.ListMerger(nbtList));
					};
				}

				if (abstractNbtList instanceof NbtByteArray nbtByteArray) {
					return Optional.of(new NbtOps.ByteArrayMerger(nbtByteArray.getByteArray()));
				}

				if (abstractNbtList instanceof NbtIntArray nbtIntArray) {
					return Optional.of(new NbtOps.IntArrayMerger(nbtIntArray.getIntArray()));
				}

				if (abstractNbtList instanceof NbtLongArray nbtLongArray) {
					return Optional.of(new NbtOps.LongArrayMerger(nbtLongArray.getLongArray()));
				}
			}

			return Optional.empty();
		}
	}

	static class BasicMerger implements NbtOps.Merger {
		public static final NbtOps.BasicMerger EMPTY = new NbtOps.BasicMerger();

		private BasicMerger() {
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			if (nbt instanceof NbtCompound nbtCompound) {
				return new NbtOps.CompoundListMerger().merge(nbtCompound);
			} else if (nbt instanceof NbtByte nbtByte) {
				return new NbtOps.ByteArrayMerger(nbtByte.byteValue());
			} else if (nbt instanceof NbtInt nbtInt) {
				return new NbtOps.IntArrayMerger(nbtInt.intValue());
			} else {
				return (NbtOps.Merger)(nbt instanceof NbtLong nbtLong ? new NbtOps.LongArrayMerger(nbtLong.longValue()) : new NbtOps.ListMerger(nbt));
			}
		}

		@Override
		public NbtElement getResult() {
			return new NbtList();
		}
	}

	static class ByteArrayMerger implements NbtOps.Merger {
		private final ByteArrayList list = new ByteArrayList();

		public ByteArrayMerger(byte value) {
			this.list.add(value);
		}

		public ByteArrayMerger(byte[] values) {
			this.list.addElements(0, values);
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			if (nbt instanceof NbtByte nbtByte) {
				this.list.add(nbtByte.byteValue());
				return this;
			} else {
				return new NbtOps.CompoundListMerger(this.list).merge(nbt);
			}
		}

		@Override
		public NbtElement getResult() {
			return new NbtByteArray(this.list.toByteArray());
		}
	}

	static class CompoundListMerger implements NbtOps.Merger {
		private final NbtList list = new NbtList();

		public CompoundListMerger() {
		}

		public CompoundListMerger(Collection<NbtElement> nbts) {
			this.list.addAll(nbts);
		}

		public CompoundListMerger(IntArrayList list) {
			list.forEach(value -> this.list.add(createMarkerNbt(NbtInt.of(value))));
		}

		public CompoundListMerger(ByteArrayList list) {
			list.forEach(value -> this.list.add(createMarkerNbt(NbtByte.of(value))));
		}

		public CompoundListMerger(LongArrayList list) {
			list.forEach(value -> this.list.add(createMarkerNbt(NbtLong.of(value))));
		}

		private static boolean isMarker(NbtCompound nbt) {
			return nbt.getSize() == 1 && nbt.contains("");
		}

		private static NbtElement makeMarker(NbtElement value) {
			if (value instanceof NbtCompound nbtCompound && !isMarker(nbtCompound)) {
				return nbtCompound;
			}

			return createMarkerNbt(value);
		}

		private static NbtCompound createMarkerNbt(NbtElement value) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.put("", value);
			return nbtCompound;
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			this.list.add(makeMarker(nbt));
			return this;
		}

		@Override
		public NbtElement getResult() {
			return this.list;
		}
	}

	static class IntArrayMerger implements NbtOps.Merger {
		private final IntArrayList list = new IntArrayList();

		public IntArrayMerger(int value) {
			this.list.add(value);
		}

		public IntArrayMerger(int[] values) {
			this.list.addElements(0, values);
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			if (nbt instanceof NbtInt nbtInt) {
				this.list.add(nbtInt.intValue());
				return this;
			} else {
				return new NbtOps.CompoundListMerger(this.list).merge(nbt);
			}
		}

		@Override
		public NbtElement getResult() {
			return new NbtIntArray(this.list.toIntArray());
		}
	}

	static class ListMerger implements NbtOps.Merger {
		private final NbtList list = new NbtList();

		ListMerger(NbtElement nbt) {
			this.list.add(nbt);
		}

		ListMerger(NbtList nbt) {
			this.list.addAll(nbt);
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			if (nbt.getType() != this.list.getHeldType()) {
				return new NbtOps.CompoundListMerger().merge(this.list).merge(nbt);
			} else {
				this.list.add(nbt);
				return this;
			}
		}

		@Override
		public NbtElement getResult() {
			return this.list;
		}
	}

	static class LongArrayMerger implements NbtOps.Merger {
		private final LongArrayList list = new LongArrayList();

		public LongArrayMerger(long value) {
			this.list.add(value);
		}

		public LongArrayMerger(long[] values) {
			this.list.addElements(0, values);
		}

		@Override
		public NbtOps.Merger merge(NbtElement nbt) {
			if (nbt instanceof NbtLong nbtLong) {
				this.list.add(nbtLong.longValue());
				return this;
			} else {
				return new NbtOps.CompoundListMerger(this.list).merge(nbt);
			}
		}

		@Override
		public NbtElement getResult() {
			return new NbtLongArray(this.list.toLongArray());
		}
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
			if (nbtElement == null || nbtElement == NbtEnd.INSTANCE) {
				return DataResult.success(nbtCompound);
			} else if (!(nbtElement instanceof NbtCompound nbtCompound2)) {
				return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
			} else {
				NbtCompound nbtCompound3 = nbtCompound2.shallowCopy();

				for (Entry<String, NbtElement> entry : nbtCompound.entrySet()) {
					nbtCompound3.put((String)entry.getKey(), (NbtElement)entry.getValue());
				}

				return DataResult.success(nbtCompound3);
			}
		}
	}

	interface Merger {
		NbtOps.Merger merge(NbtElement nbt);

		default NbtOps.Merger merge(Iterable<NbtElement> nbts) {
			NbtOps.Merger merger = this;

			for (NbtElement nbtElement : nbts) {
				merger = merger.merge(nbtElement);
			}

			return merger;
		}

		default NbtOps.Merger merge(Stream<NbtElement> nbts) {
			return this.merge(nbts::iterator);
		}

		NbtElement getResult();
	}
}
