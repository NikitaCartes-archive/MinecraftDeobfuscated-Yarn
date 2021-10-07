package net.minecraft.world.chunk;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.AtomicStack;
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
	private final Semaphore semaphore = new Semaphore(1);
	@Nullable
	private final AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack = null;

	/**
	 * Acquires the semaphore on this container, and crashes if it cannot be
	 * acquired.
	 */
	public void lock() {
		if (this.lockStack != null) {
			Thread thread = Thread.currentThread();
			this.lockStack.push(Pair.of(thread, thread.getStackTrace()));
		}

		LockHelper.checkLock(this.semaphore, this.lockStack, "PalettedContainer");
	}

	/**
	 * Releases the semaphore on this container.
	 */
	public void unlock() {
		this.semaphore.release();
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
	public static <T> Codec<PalettedContainer<T>> createCodec(IndexedIterable<T> idList, Codec<T> entryCodec, PalettedContainer.PaletteProvider provider) {
		return RecordCodecBuilder.create(
				instance -> instance.group(
							entryCodec.listOf().fieldOf("palette").forGetter(PalettedContainer.Serialized::paletteEntries),
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
		this.data = new PalettedContainer.Data(dataProvider, storage, palette);
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
			: dataProvider.createData(this.idList, this, this.paletteProvider.getContainerSize(), null);
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
		this.data.set(index, value);
	}

	public T get(int x, int y, int z) {
		return this.get(this.paletteProvider.computeIndex(x, y, z));
	}

	protected T get(int index) {
		PalettedContainer.Data<T> data = this.data;
		return data.palette.get(data.storage.get(index));
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
			if (dataProvider.factory() == PalettedContainer.PaletteProvider.ID_LIST) {
				Palette<T> palette = new BiMapPalette<>(idList, j, (ix, object) -> 0, list);
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, provider.getContainerSize(), ls);
				IntStream intStream = IntStream.range(0, packedIntegerArray.getSize()).map(ix -> idList.getRawId(palette.get(packedIntegerArray.get(ix))));
				paletteStorage = new PackedIntegerArray(dataProvider.bits(), i, intStream);
			} else {
				paletteStorage = new PackedIntegerArray(dataProvider.bits(), i, ls);
			}
		}

		return DataResult.success(new PalettedContainer<>(idList, provider, dataProvider, paletteStorage, list));
	}

	private PalettedContainer.Serialized<T> write(IndexedIterable<T> idList, PalettedContainer.PaletteProvider provider) {
		this.lock();

		PalettedContainer.Serialized var17;
		try {
			BiMapPalette<T> biMapPalette = new BiMapPalette<>(idList, this.data.storage.getElementBits(), this.dummyListener);
			T object = null;
			int i = -1;
			int j = provider.getContainerSize();
			int[] is = new int[j];

			for (int k = 0; k < j; k++) {
				T object2 = this.get(k);
				if (object2 != object) {
					object = object2;
					i = biMapPalette.index(object2);
				}

				is[k] = i;
			}

			int k = provider.getBits(idList, biMapPalette.getSize());
			Optional<LongStream> optional;
			if (k == 0) {
				optional = Optional.empty();
			} else {
				PaletteStorage paletteStorage = new PackedIntegerArray(k, j);

				for (int l = 0; l < is.length; l++) {
					paletteStorage.set(l, is[l]);
				}

				long[] ls = paletteStorage.getData();
				optional = Optional.of(Arrays.stream(ls));
			}

			var17 = new PalettedContainer.Serialized(biMapPalette.getElements(), optional);
		} finally {
			this.unlock();
		}

		return var17;
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
	static record Data() {
		/**
		 * the data provider that derives the palette and storage of this data
		 */
		private final PalettedContainer.DataProvider<T> configuration;
		/**
		 * the data
		 */
		final PaletteStorage storage;
		/**
		 * the palette for the storage
		 */
		final Palette<T> palette;

		Data(PalettedContainer.DataProvider<T> configuration, PaletteStorage storage, Palette<T> palette) {
			this.configuration = configuration;
			this.storage = storage;
			this.palette = palette;
		}

		/**
		 * Imports the data from the other {@code storage} with the other
		 * {@code palette}.
		 * 
		 * @param palette the other palette
		 * @param storage the other storage
		 */
		public void importFrom(Palette<T> palette, PaletteStorage storage) {
			for (int i = 0; i < storage.getSize(); i++) {
				this.set(i, palette.get(storage.get(i)));
			}
		}

		/**
		 * Sets an entry to the storage's given index.
		 * 
		 * @param index the index in the storage
		 * @param value the entry to set
		 */
		public void set(int index, T value) {
			this.storage.set(index, this.palette.index(value));
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
	static record DataProvider<T>() {
		/**
		 * the palette factory
		 */
		private final Palette.Factory factory;
		/**
		 * the number of bits each element use
		 */
		private final int bits;

		DataProvider(Palette.Factory factory, int i) {
			this.factory = factory;
			this.bits = i;
		}

		public PalettedContainer.Data<T> createData(IndexedIterable<T> idList, PaletteResizeListener<T> listener, int size, @Nullable long[] storage) {
			PaletteStorage paletteStorage = (PaletteStorage)(this.bits == 0 ? new EmptyPaletteStorage(size) : new PackedIntegerArray(this.bits, size, storage));
			Palette<T> palette = this.factory.create(this.bits, idList, listener, List.of());
			return new PalettedContainer.Data(this, paletteStorage, palette);
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
					case 1, 2 -> new PalettedContainer.DataProvider(ARRAY, bits);
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
	static record Serialized() {
		/**
		 * the palette
		 */
		private final List<T> paletteEntries;
		/**
		 * the data of the container
		 */
		private final Optional<LongStream> storage;

		Serialized(List<T> list, Optional<LongStream> optional) {
			this.paletteEntries = list;
			this.storage = optional;
		}
	}
}
