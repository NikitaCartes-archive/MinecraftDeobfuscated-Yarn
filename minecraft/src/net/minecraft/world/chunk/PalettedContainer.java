package net.minecraft.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;

/**
 * A paletted container stores objects in 3D voxels as small integer indices,
 * governed by "palettes" that map between these objects and indices.
 * 
 * @see Palette
 */
public class PalettedContainer<T> implements PaletteResizeListener<T> {
	private static final int field_34557 = 0;
	private final PaletteResizeListener<T> dummyListener = (newSize, added) -> 0;
	private final IndexedIterable<T> idList;
	private volatile PalettedContainer.Data<T> data;
	private final PalettedContainer.PaletteProvider paletteProvider;
	private final LockHelper field_36300 = new LockHelper("PalettedContainer");

	/**
	 * Acquires the semaphore on this container, and crashes if it cannot be
	 * acquired.
	 */
	public void lock() {
		this.field_36300.method_39935();
	}

	/**
	 * Releases the semaphore on this container.
	 */
	public void unlock() {
		this.field_36300.method_39937();
	}

	/**
	 * Creates a codec for a paletted container with a specific palette provider.
	 * 
	 * @return the created codec
	 * 
	 * @param idList the id list to map between objects and full integer IDs
	 * @param entryCodec the codec for each entry in the palette
	 * @param provider the palette provider that controls how the data are serialized and what
	 * types of palette are used for what entry bit sizes
	 */
	public static <T> Codec<PalettedContainer<T>> createCodec(IndexedIterable<T> idList, Codec<T> entryCodec, PalettedContainer.PaletteProvider provider, T object) {
		return RecordCodecBuilder.create(
				instance -> instance.group(
							entryCodec.mapResult(Codecs.method_39028(object)).listOf().fieldOf("palette").forGetter(PalettedContainer.Serialized::paletteEntries),
							Codec.LONG_STREAM.optionalFieldOf("data").forGetter(PalettedContainer.Serialized::storage)
						)
						.apply(instance, PalettedContainer.Serialized::new)
			)
			.comapFlatMap(serialized -> read(idList, provider, serialized), container -> container.write(idList, provider));
	}

	public PalettedContainer(
		IndexedIterable<T> idList,
		PalettedContainer.PaletteProvider paletteProvider,
		PalettedContainer.DataProvider<T> dataProvider,
		PaletteStorage storage,
		List<T> list
	) {
		this.idList = idList;
		this.paletteProvider = paletteProvider;
		Palette<T> palette = dataProvider.factory().create(dataProvider.bits(), idList, this, list);
		this.data = new PalettedContainer.Data<>(dataProvider, storage, palette);
	}

	public PalettedContainer(IndexedIterable<T> idList, T object, PalettedContainer.PaletteProvider paletteProvider) {
		this.paletteProvider = paletteProvider;
		this.idList = idList;
		this.data = this.getCompatibleData(null, 0);
		this.data.palette.index(object);
	}

	/**
	 * {@return a compatible data object for the given entry {@code bits} size}
	 * This may return a new data object or return {@code previousData} if it
	 * can be reused.
	 * 
	 * @param previousData the previous data, may be reused if suitable
	 * @param bits the number of bits each entry uses
	 */
	private PalettedContainer.Data<T> getCompatibleData(@Nullable PalettedContainer.Data<T> previousData, int bits) {
		PalettedContainer.DataProvider<T> dataProvider = this.paletteProvider.createDataProvider(this.idList, bits);
		return previousData != null && dataProvider.equals(previousData.configuration())
			? previousData
			: dataProvider.createData(this.idList, this, this.paletteProvider.getContainerSize());
	}

	@Override
	public int onResize(int i, T object) {
		PalettedContainer.Data<T> data = this.data;
		PalettedContainer.Data<T> data2 = this.getCompatibleData(data, i);
		data2.importFrom(data.palette, data.storage);
		this.data = data2;
		return data2.palette.index(object);
	}

	public T swap(int x, int y, int z, T value) {
		this.lock();

		Object var5;
		try {
			var5 = this.swap(this.paletteProvider.computeIndex(x, y, z), value);
		} finally {
			this.unlock();
		}

		return (T)var5;
	}

	public T swapUnsafe(int x, int y, int z, T value) {
		return this.swap(this.paletteProvider.computeIndex(x, y, z), value);
	}

	private T swap(int index, T value) {
		int i = this.data.palette.index(value);
		int j = this.data.storage.swap(index, i);
		return this.data.palette.get(j);
	}

	public void set(int x, int y, int z, T value) {
		this.lock();

		try {
			this.set(this.paletteProvider.computeIndex(x, y, z), value);
		} finally {
			this.unlock();
		}
	}

	private void set(int index, T value) {
		int i = this.data.palette.index(value);
		this.data.storage.set(index, i);
	}

	public T get(int x, int y, int z) {
		return this.get(this.paletteProvider.computeIndex(x, y, z));
	}

	protected T get(int index) {
		PalettedContainer.Data<T> data = this.data;
		return data.palette.get(data.storage.get(index));
	}

	public void method_39793(Consumer<T> consumer) {
		Palette<T> palette = this.data.palette();
		IntSet intSet = new IntArraySet();
		this.data.storage.forEach(intSet::add);
		intSet.forEach(id -> consumer.accept(palette.get(id)));
	}

	/**
	 * Reads data from the packet byte buffer into this container. Previous data
	 * in this container is discarded.
	 * 
	 * @param buf the packet byte buffer
	 */
	public void readPacket(PacketByteBuf buf) {
		this.lock();

		try {
			int i = buf.readByte();
			PalettedContainer.Data<T> data = this.getCompatibleData(this.data, i);
			data.palette.readPacket(buf);
			buf.readLongArray(data.storage.getData());
			this.data = data;
		} finally {
			this.unlock();
		}
	}

	/**
	 * Writes this container to the packet byte buffer.
	 * 
	 * @param buf the packet byte buffer
	 */
	public void writePacket(PacketByteBuf buf) {
		this.lock();

		try {
			this.data.writePacket(buf);
		} finally {
			this.unlock();
		}
	}

	private static <T> DataResult<PalettedContainer<T>> read(
		IndexedIterable<T> idList, PalettedContainer.PaletteProvider provider, PalettedContainer.Serialized<T> serialized
	) {
		List<T> list = serialized.paletteEntries();
		int i = provider.getContainerSize();
		int j = provider.getBits(idList, list.size());
		PalettedContainer.DataProvider<T> dataProvider = provider.createDataProvider(idList, j);
		PaletteStorage paletteStorage;
		if (j == 0) {
			paletteStorage = new EmptyPaletteStorage(i);
		} else {
			Optional<LongStream> optional = serialized.storage();
			if (optional.isEmpty()) {
				return DataResult.error("Missing values for non-zero storage");
			}

			long[] ls = ((LongStream)optional.get()).toArray();

			try {
				if (dataProvider.factory() == PalettedContainer.PaletteProvider.ID_LIST) {
					Palette<T> palette = new BiMapPalette<>(idList, j, (ix, object) -> 0, list);
					PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i, ls);
					int[] is = new int[i];
					packedIntegerArray.method_39892(is);
					method_39894(is, ix -> idList.getRawId(palette.get(ix)));
					paletteStorage = new PackedIntegerArray(dataProvider.bits(), i, is);
				} else {
					paletteStorage = new PackedIntegerArray(dataProvider.bits(), i, ls);
				}
			} catch (PackedIntegerArray.InvalidLengthException var13) {
				return DataResult.error("Failed to read PalettedContainer: " + var13.getMessage());
			}
		}

		return DataResult.success(new PalettedContainer<>(idList, provider, dataProvider, paletteStorage, list));
	}

	private PalettedContainer.Serialized<T> write(IndexedIterable<T> idList, PalettedContainer.PaletteProvider provider) {
		this.lock();

		PalettedContainer.Serialized var12;
		try {
			BiMapPalette<T> biMapPalette = new BiMapPalette<>(idList, this.data.storage.getElementBits(), this.dummyListener);
			int i = provider.getContainerSize();
			int[] is = new int[i];
			this.data.storage.method_39892(is);
			method_39894(is, ix -> biMapPalette.index(this.data.palette.get(ix)));
			int j = provider.getBits(idList, biMapPalette.getSize());
			Optional<LongStream> optional;
			if (j != 0) {
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i, is);
				optional = Optional.of(Arrays.stream(packedIntegerArray.getData()));
			} else {
				optional = Optional.empty();
			}

			var12 = new PalettedContainer.Serialized(biMapPalette.getElements(), optional);
		} finally {
			this.unlock();
		}

		return var12;
	}

	private static <T> void method_39894(int[] is, IntUnaryOperator intUnaryOperator) {
		int i = -1;
		int j = -1;

		for (int k = 0; k < is.length; k++) {
			int l = is[k];
			if (l != i) {
				i = l;
				j = intUnaryOperator.applyAsInt(l);
			}

			is[k] = j;
		}
	}

	public int getPacketSize() {
		return this.data.getPacketSize();
	}

	/**
	 * {@return {@code true} if any object in this container's palette matches
	 * this predicate}
	 */
	public boolean hasAny(Predicate<T> predicate) {
		return this.data.palette.hasAny(predicate);
	}

	public void count(PalettedContainer.Counter<T> counter) {
		Int2IntMap int2IntMap = new Int2IntOpenHashMap();
		this.data.storage.forEach(key -> int2IntMap.put(key, int2IntMap.get(key) + 1));
		int2IntMap.int2IntEntrySet().forEach(entry -> counter.accept(this.data.palette.get(entry.getIntKey()), entry.getIntValue()));
	}

	/**
	 * A counter that receives a palette entry and its number of occurences
	 * in the container.
	 */
	@FunctionalInterface
	public interface Counter<T> {
		/**
		 * @param object the palette entry
		 * @param count the entry's number of occurence
		 */
		void accept(T object, int count);
	}

	/**
	 * Runtime representation of data in a paletted container.
	 */
	static record Data<T>(PalettedContainer.DataProvider<T> configuration, PaletteStorage storage, Palette<T> palette) {

		/**
		 * Imports the data from the other {@code storage} with the other
		 * {@code palette}.
		 */
		public void importFrom(Palette<T> palette, PaletteStorage storage) {
			for (int i = 0; i < storage.getSize(); i++) {
				T object = palette.get(storage.get(i));
				this.storage.set(i, this.palette.index(object));
			}
		}

		/**
		 * {@return the size of this data, in bytes, when written to a packet}
		 * 
		 * @see #writePacket(PacketByteBuf)
		 */
		public int getPacketSize() {
			return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntLength(this.storage.getSize()) + this.storage.getData().length * 8;
		}

		public void writePacket(PacketByteBuf buf) {
			buf.writeByte(this.storage.getElementBits());
			this.palette.writePacket(buf);
			buf.writeLongArray(this.storage.getData());
		}
	}

	/**
	 * A palette data provider constructs an empty data for a paletted
	 * container given a palette provider and a desired entry size in bits.
	 */
	static record DataProvider<T>(Palette.Factory factory, int bits) {
		public PalettedContainer.Data<T> createData(IndexedIterable<T> idList, PaletteResizeListener<T> listener, int size) {
			PaletteStorage paletteStorage = (PaletteStorage)(this.bits == 0 ? new EmptyPaletteStorage(size) : new PackedIntegerArray(this.bits, size));
			Palette<T> palette = this.factory.create(this.bits, idList, listener, List.of());
			return new PalettedContainer.Data<>(this, paletteStorage, palette);
		}
	}

	/**
	 * A palette provider determines what type of palette to choose given the
	 * bits used to represent each element. In addition, it controls how the
	 * data in the serialized container is read based on the palette given.
	 */
	public abstract static class PaletteProvider {
		public static final Palette.Factory SINGULAR = SingularPalette::create;
		public static final Palette.Factory ARRAY = ArrayPalette::create;
		public static final Palette.Factory BI_MAP = BiMapPalette::create;
		static final Palette.Factory ID_LIST = IdListPalette::create;
		/**
		 * A palette provider that stores {@code 4096} objects in a container.
		 * Used in vanilla by block states in a chunk section.
		 */
		public static final PalettedContainer.PaletteProvider BLOCK_STATE = new PalettedContainer.PaletteProvider(4) {
			@Override
			public <A> PalettedContainer.DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
				return switch (bits) {
					case 0 -> new PalettedContainer.DataProvider(SINGULAR, bits);
					case 1, 2, 3, 4 -> new PalettedContainer.DataProvider(ARRAY, 4);
					case 5, 6, 7, 8 -> new PalettedContainer.DataProvider(BI_MAP, bits);
					default -> new PalettedContainer.DataProvider(PalettedContainer.PaletteProvider.ID_LIST, MathHelper.ceilLog2(idList.size()));
				};
			}
		};
		/**
		 * A palette provider that stores {@code 64} objects in a container.
		 * Used in vanilla by biomes in a chunk section.
		 */
		public static final PalettedContainer.PaletteProvider BIOME = new PalettedContainer.PaletteProvider(2) {
			@Override
			public <A> PalettedContainer.DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
				return switch (bits) {
					case 0 -> new PalettedContainer.DataProvider(SINGULAR, bits);
					case 1, 2, 3 -> new PalettedContainer.DataProvider(ARRAY, bits);
					default -> new PalettedContainer.DataProvider(PalettedContainer.PaletteProvider.ID_LIST, MathHelper.ceilLog2(idList.size()));
				};
			}
		};
		private final int edgeBits;

		PaletteProvider(int edgeBits) {
			this.edgeBits = edgeBits;
		}

		/**
		 * {@return the size of the container's data desired by this provider}
		 */
		public int getContainerSize() {
			return 1 << this.edgeBits * 3;
		}

		/**
		 * {@return the index of an object in the storage given its x, y, z coordinates}
		 * 
		 * @param x the x coordinate
		 * @param y the y coordinate
		 * @param z the z coordinate
		 */
		public int computeIndex(int x, int y, int z) {
			return (y << this.edgeBits | z) << this.edgeBits | x;
		}

		/**
		 * Creates a data provider that is suitable to represent objects with
		 * {@code bits} size in the storage.
		 * 
		 * @return the data provider
		 * 
		 * @param idList the id list that maps between objects and full integer IDs
		 * @param bits the number of bits needed to represent all palette entries
		 */
		public abstract <A> PalettedContainer.DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits);

		<A> int getBits(IndexedIterable<A> idList, int size) {
			int i = MathHelper.ceilLog2(size);
			PalettedContainer.DataProvider<A> dataProvider = this.createDataProvider(idList, i);
			return dataProvider.factory() == ID_LIST ? i : dataProvider.bits();
		}
	}

	/**
	 * The storage form of the paletted container in the {@linkplain
	 * PalettedContainer#createCodec codec}. The {@code palette} is the entries
	 * in the palette, but the interpretation of data depends on the palette
	 * provider specified for the codec.
	 * 
	 * @see PalettedContainer#createCodec
	 */
	static record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage) {
	}
}
