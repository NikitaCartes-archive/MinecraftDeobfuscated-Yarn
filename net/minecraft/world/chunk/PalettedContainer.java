/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class PalettedContainer<T>
implements PaletteResizeListener<T> {
    private final Palette<T> fallbackPalette;
    private final PaletteResizeListener<T> noOpPaletteResizeHandler = (i, object) -> 0;
    private final IdList<T> idList;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final T field_12935;
    protected PackedIntegerArray data;
    private Palette<T> palette;
    private int paletteSize;
    private final ReentrantLock writeLock = new ReentrantLock();

    public void lock() {
        if (this.writeLock.isLocked() && !this.writeLock.isHeldByCurrentThread()) {
            String string = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            CrashReport crashReport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
            crashReportSection.add("Thread dumps", string);
            throw new CrashException(crashReport);
        }
        this.writeLock.lock();
    }

    public void unlock() {
        this.writeLock.unlock();
    }

    public PalettedContainer(Palette<T> fallbackPalette, IdList<T> idList, Function<CompoundTag, T> elementDeserializer, Function<T, CompoundTag> elementSerializer, T defaultElement) {
        this.fallbackPalette = fallbackPalette;
        this.idList = idList;
        this.elementDeserializer = elementDeserializer;
        this.elementSerializer = elementSerializer;
        this.field_12935 = defaultElement;
        this.setPaletteSize(4);
    }

    private static int toIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private void setPaletteSize(int size) {
        if (size == this.paletteSize) {
            return;
        }
        this.paletteSize = size;
        if (this.paletteSize <= 4) {
            this.paletteSize = 4;
            this.palette = new ArrayPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer);
        } else if (this.paletteSize < 9) {
            this.palette = new BiMapPalette<T>(this.idList, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
        } else {
            this.palette = this.fallbackPalette;
            this.paletteSize = MathHelper.log2DeBruijn(this.idList.size());
        }
        this.palette.getIndex(this.field_12935);
        this.data = new PackedIntegerArray(this.paletteSize, 4096);
    }

    @Override
    public int onResize(int i, T object) {
        int j;
        this.lock();
        PackedIntegerArray packedIntegerArray = this.data;
        Palette<T> palette = this.palette;
        this.setPaletteSize(i);
        for (j = 0; j < packedIntegerArray.getSize(); ++j) {
            T object2 = palette.getByIndex(packedIntegerArray.get(j));
            if (object2 == null) continue;
            this.set(j, object2);
        }
        j = this.palette.getIndex(object);
        this.unlock();
        return j;
    }

    public T setSync(int x, int y, int z, T value) {
        this.lock();
        T object = this.setAndGetOldValue(PalettedContainer.toIndex(x, y, z), value);
        this.unlock();
        return object;
    }

    public T set(int x, int y, int z, T value) {
        return this.setAndGetOldValue(PalettedContainer.toIndex(x, y, z), value);
    }

    protected T setAndGetOldValue(int index, T value) {
        int i = this.palette.getIndex(value);
        int j = this.data.setAndGetOldValue(index, i);
        T object = this.palette.getByIndex(j);
        return object == null ? this.field_12935 : object;
    }

    protected void set(int i, T object) {
        int j = this.palette.getIndex(object);
        this.data.set(i, j);
    }

    public T get(int x, int y, int z) {
        return this.get(PalettedContainer.toIndex(x, y, z));
    }

    protected T get(int index) {
        T object = this.palette.getByIndex(this.data.get(index));
        return object == null ? this.field_12935 : object;
    }

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf buf) {
        this.lock();
        byte i = buf.readByte();
        if (this.paletteSize != i) {
            this.setPaletteSize(i);
        }
        this.palette.fromPacket(buf);
        buf.readLongArray(this.data.getStorage());
        this.unlock();
    }

    public void toPacket(PacketByteBuf buf) {
        this.lock();
        buf.writeByte(this.paletteSize);
        this.palette.toPacket(buf);
        buf.writeLongArray(this.data.getStorage());
        this.unlock();
    }

    public void read(ListTag paletteTag, long[] data) {
        this.lock();
        int i = Math.max(4, MathHelper.log2DeBruijn(paletteTag.size()));
        if (i != this.paletteSize) {
            this.setPaletteSize(i);
        }
        this.palette.fromTag(paletteTag);
        int j = data.length * 64 / 4096;
        if (this.palette == this.fallbackPalette) {
            BiMapPalette<T> palette = new BiMapPalette<T>(this.idList, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
            palette.fromTag(paletteTag);
            PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096, data);
            for (int k = 0; k < 4096; ++k) {
                this.data.set(k, this.fallbackPalette.getIndex(palette.getByIndex(packedIntegerArray.get(k))));
            }
        } else if (j == this.paletteSize) {
            System.arraycopy(data, 0, this.data.getStorage(), 0, data.length);
        } else {
            PackedIntegerArray packedIntegerArray2 = new PackedIntegerArray(j, 4096, data);
            for (int l = 0; l < 4096; ++l) {
                this.data.set(l, packedIntegerArray2.get(l));
            }
        }
        this.unlock();
    }

    public void write(CompoundTag compoundTag, String string, String string2) {
        this.lock();
        BiMapPalette<T> biMapPalette = new BiMapPalette<T>(this.idList, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
        T object = this.field_12935;
        int i = biMapPalette.getIndex(this.field_12935);
        int[] is = new int[4096];
        for (int j = 0; j < 4096; ++j) {
            T object2 = this.get(j);
            if (object2 != object) {
                object = object2;
                i = biMapPalette.getIndex(object2);
            }
            is[j] = i;
        }
        ListTag listTag = new ListTag();
        biMapPalette.toTag(listTag);
        compoundTag.put(string, listTag);
        int k = Math.max(4, MathHelper.log2DeBruijn(listTag.size()));
        PackedIntegerArray packedIntegerArray = new PackedIntegerArray(k, 4096);
        for (int l = 0; l < is.length; ++l) {
            packedIntegerArray.set(l, is[l]);
        }
        compoundTag.putLongArray(string2, packedIntegerArray.getStorage());
        this.unlock();
    }

    public int getPacketSize() {
        return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntSizeBytes(this.data.getSize()) + this.data.getStorage().length * 8;
    }

    public boolean method_19526(Predicate<T> predicate) {
        return this.palette.accepts(predicate);
    }

    public void count(CountConsumer<T> consumer) {
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap();
        this.data.forEach(i -> int2IntMap.put(i, int2IntMap.get(i) + 1));
        int2IntMap.int2IntEntrySet().forEach(entry -> consumer.accept(this.palette.getByIndex(entry.getIntKey()), entry.getIntValue()));
    }

    @FunctionalInterface
    public static interface CountConsumer<T> {
        public void accept(T var1, int var2);
    }
}

