/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import net.minecraft.world.chunk.SingularPalette;
import org.jetbrains.annotations.Nullable;

/**
 * A paletted container stores objects in 3D voxels as small integer indices,
 * governed by "palettes" that map between these objects and indices.
 * 
 * @see Palette
 */
public class PalettedContainer<T>
implements PaletteResizeListener<T> {
    private static final int field_34557 = 0;
    private final PaletteResizeListener<T> dummyListener = (newSize, added) -> 0;
    private final IndexedIterable<T> idList;
    private volatile Data<T> data;
    private final PaletteProvider paletteProvider;
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
     * @param entryCodec the codec for each entry in the palette
     * @param provider the palette provider that controls how the data are serialized and what
     * types of palette are used for what entry bit sizes
     * @param idList the id list to map between objects and full integer IDs
     */
    public static <T> Codec<PalettedContainer<T>> createCodec(IndexedIterable<T> idList, Codec<T> entryCodec, PaletteProvider provider, T object) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)entryCodec.mapResult(Codecs.method_39028(object)).listOf().fieldOf("palette")).forGetter(Serialized::paletteEntries), Codec.LONG_STREAM.optionalFieldOf("data").forGetter(Serialized::storage)).apply((Applicative<Serialized, ?>)instance, Serialized::new)).comapFlatMap(serialized -> PalettedContainer.read(idList, provider, serialized), container -> container.write(idList, provider));
    }

    public PalettedContainer(IndexedIterable<T> idList, PaletteProvider paletteProvider, DataProvider<T> dataProvider, PaletteStorage storage, List<T> list) {
        this.idList = idList;
        this.paletteProvider = paletteProvider;
        Palette<T> palette = dataProvider.factory().create(dataProvider.bits(), idList, this, list);
        this.data = new Data<T>(dataProvider, storage, palette);
    }

    public PalettedContainer(IndexedIterable<T> idList, T object, PaletteProvider paletteProvider) {
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
     * @param bits the number of bits each entry uses
     * @param previousData the previous data, may be reused if suitable
     */
    private Data<T> getCompatibleData(@Nullable Data<T> previousData, int bits) {
        DataProvider<T> dataProvider = this.paletteProvider.createDataProvider(this.idList, bits);
        if (previousData != null && dataProvider.equals(previousData.configuration())) {
            return previousData;
        }
        return dataProvider.createData(this.idList, this, this.paletteProvider.getContainerSize());
    }

    @Override
    public int onResize(int i, T object) {
        Data<T> data = this.data;
        Data data2 = this.getCompatibleData(data, i);
        data2.importFrom(data.palette, data.storage);
        this.data = data2;
        return data2.palette.index(object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T swap(int x, int y, int z, T value) {
        this.lock();
        try {
            T t = this.swap(this.paletteProvider.computeIndex(x, y, z), value);
            return t;
        } finally {
            this.unlock();
        }
    }

    public T swapUnsafe(int x, int y, int z, T value) {
        return this.swap(this.paletteProvider.computeIndex(x, y, z), value);
    }

    private T swap(int index, T value) {
        int i = this.data.palette.index(value);
        int j = this.data.storage.swap(index, i);
        return this.data.palette.get(j);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
        Data<T> data = this.data;
        return data.palette.get(data.storage.get(index));
    }

    public void method_39793(Consumer<T> consumer) {
        Palette palette = this.data.palette();
        IntArraySet intSet = new IntArraySet();
        this.data.storage.forEach(intSet::add);
        intSet.forEach(id -> consumer.accept(palette.get(id)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    /**
     * Reads data from the packet byte buffer into this container. Previous data
     * in this container is discarded.
     * 
     * @param buf the packet byte buffer
     */
    public void readPacket(PacketByteBuf buf) {
        this.lock();
        try {
            byte i = buf.readByte();
            Data<T> data = this.getCompatibleData(this.data, i);
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

    private static <T> DataResult<PalettedContainer<T>> read(IndexedIterable<T> idList, PaletteProvider provider, Serialized<T> serialized) {
        PaletteStorage paletteStorage;
        List<T> list = serialized.paletteEntries();
        int i2 = provider.getContainerSize();
        int j = provider.getBits(idList, list.size());
        DataProvider<T> dataProvider = provider.createDataProvider(idList, j);
        if (j == 0) {
            paletteStorage = new EmptyPaletteStorage(i2);
        } else {
            Optional<LongStream> optional = serialized.storage();
            if (optional.isEmpty()) {
                return DataResult.error("Missing values for non-zero storage");
            }
            long[] ls = optional.get().toArray();
            try {
                if (dataProvider.factory() == PaletteProvider.ID_LIST) {
                    BiMapPalette<Object> palette = new BiMapPalette<Object>(idList, j, (i, object) -> 0, list);
                    PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i2, ls);
                    int[] is = new int[i2];
                    packedIntegerArray.method_39892(is);
                    PalettedContainer.method_39894(is, i -> idList.getRawId(palette.get(i)));
                    paletteStorage = new PackedIntegerArray(dataProvider.bits(), i2, is);
                } else {
                    paletteStorage = new PackedIntegerArray(dataProvider.bits(), i2, ls);
                }
            } catch (PackedIntegerArray.InvalidLengthException invalidLengthException) {
                return DataResult.error("Failed to read PalettedContainer: " + invalidLengthException.getMessage());
            }
        }
        return DataResult.success(new PalettedContainer<T>(idList, provider, dataProvider, paletteStorage, list));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Serialized<T> write(IndexedIterable<T> idList, PaletteProvider provider) {
        this.lock();
        try {
            Optional<LongStream> optional;
            BiMapPalette<T> biMapPalette = new BiMapPalette<T>(idList, this.data.storage.getElementBits(), this.dummyListener);
            int i2 = provider.getContainerSize();
            int[] is = new int[i2];
            this.data.storage.method_39892(is);
            PalettedContainer.method_39894(is, i -> biMapPalette.index(this.data.palette.get(i)));
            int j = provider.getBits(idList, biMapPalette.getSize());
            if (j != 0) {
                PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i2, is);
                optional = Optional.of(Arrays.stream(packedIntegerArray.getData()));
            } else {
                optional = Optional.empty();
            }
            Serialized<T> serialized = new Serialized<T>(biMapPalette.getElements(), optional);
            return serialized;
        } finally {
            this.unlock();
        }
    }

    private static <T> void method_39894(int[] is, IntUnaryOperator intUnaryOperator) {
        int i = -1;
        int j = -1;
        for (int k = 0; k < is.length; ++k) {
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

    public void count(Counter<T> counter) {
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap();
        this.data.storage.forEach(key -> int2IntMap.put(key, int2IntMap.get(key) + 1));
        int2IntMap.int2IntEntrySet().forEach(entry -> counter.accept(this.data.palette.get(entry.getIntKey()), entry.getIntValue()));
    }

    public static abstract class PaletteProvider {
        public static final Palette.Factory SINGULAR = SingularPalette::create;
        public static final Palette.Factory ARRAY = ArrayPalette::create;
        public static final Palette.Factory BI_MAP = BiMapPalette::create;
        static final Palette.Factory ID_LIST = IdListPalette::create;
        public static final PaletteProvider BLOCK_STATE = new PaletteProvider(4){

            @Override
            public <A> DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
                return switch (bits) {
                    case 0 -> new DataProvider(SINGULAR, bits);
                    case 1, 2, 3, 4 -> new DataProvider(ARRAY, 4);
                    case 5, 6, 7, 8 -> new DataProvider(BI_MAP, bits);
                    default -> new DataProvider(ID_LIST, MathHelper.ceilLog2(idList.size()));
                };
            }
        };
        public static final PaletteProvider BIOME = new PaletteProvider(2){

            @Override
            public <A> DataProvider<A> createDataProvider(IndexedIterable<A> idList, int bits) {
                return switch (bits) {
                    case 0 -> new DataProvider(SINGULAR, bits);
                    case 1, 2, 3 -> new DataProvider(ARRAY, bits);
                    default -> new DataProvider(ID_LIST, MathHelper.ceilLog2(idList.size()));
                };
            }
        };
        private final int edgeBits;

        PaletteProvider(int edgeBits) {
            this.edgeBits = edgeBits;
        }

        public int getContainerSize() {
            return 1 << this.edgeBits * 3;
        }

        public int computeIndex(int x, int y, int z) {
            return (y << this.edgeBits | z) << this.edgeBits | x;
        }

        public abstract <A> DataProvider<A> createDataProvider(IndexedIterable<A> var1, int var2);

        <A> int getBits(IndexedIterable<A> idList, int size) {
            int i = MathHelper.ceilLog2(size);
            DataProvider<A> dataProvider = this.createDataProvider(idList, i);
            return dataProvider.factory() == ID_LIST ? i : dataProvider.bits();
        }
    }

    record DataProvider<T>(Palette.Factory factory, int bits) {
        public Data<T> createData(IndexedIterable<T> idList, PaletteResizeListener<T> listener, int size) {
            PaletteStorage paletteStorage = this.bits == 0 ? new EmptyPaletteStorage(size) : new PackedIntegerArray(this.bits, size);
            Palette<T> palette = this.factory.create(this.bits, idList, listener, List.of());
            return new Data<T>(this, paletteStorage, palette);
        }
    }

    record Data<T>(DataProvider<T> configuration, PaletteStorage storage, Palette<T> palette) {
        public void importFrom(Palette<T> palette, PaletteStorage storage) {
            for (int i = 0; i < storage.getSize(); ++i) {
                T object = palette.get(storage.get(i));
                this.storage.set(i, this.palette.index(object));
            }
        }

        public int getPacketSize() {
            return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntLength(this.storage.getSize()) + this.storage.getData().length * 8;
        }

        public void writePacket(PacketByteBuf buf) {
            buf.writeByte(this.storage.getElementBits());
            this.palette.writePacket(buf);
            buf.writeLongArray(this.storage.getData());
        }
    }

    record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage) {
    }

    @FunctionalInterface
    public static interface Counter<T> {
        public void accept(T var1, int var2);
    }
}

