/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import net.minecraft.class_6490;
import net.minecraft.class_6502;
import net.minecraft.class_6564;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.AtomicStack;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.jetbrains.annotations.Nullable;

public class PalettedContainer<T>
implements PaletteResizeListener<T> {
    private static final int field_34557 = 0;
    private final PaletteResizeListener<T> field_34558 = (newSize, added) -> 0;
    private final IndexedIterable<T> field_34559;
    private volatile class_6561<T> field_34560;
    private final class_6563 field_34561;
    private final Semaphore field_34562 = new Semaphore(1);
    @Nullable
    private final AtomicStack<Pair<Thread, StackTraceElement[]>> field_34563 = null;

    public void lock() {
        if (this.field_34563 != null) {
            Thread thread = Thread.currentThread();
            this.field_34563.push(Pair.of(thread, thread.getStackTrace()));
        }
        LockHelper.checkLock(this.field_34562, this.field_34563, "PalettedContainer");
    }

    public void unlock() {
        this.field_34562.release();
    }

    public static <T> Codec<PalettedContainer<T>> method_38298(IndexedIterable<T> indexedIterable, Codec<T> codec, class_6563 arg) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)codec.listOf().fieldOf("palette")).forGetter(class_6562::comp_75), Codec.LONG_STREAM.optionalFieldOf("data").forGetter(class_6562::comp_76)).apply((Applicative<class_6562, ?>)instance, class_6562::new)).comapFlatMap(arg2 -> PalettedContainer.read(indexedIterable, arg, arg2), palettedContainer -> palettedContainer.write(indexedIterable, arg));
    }

    public PalettedContainer(IndexedIterable<T> indexedIterable, class_6563 arg, class_6560<T> arg2, class_6490 arg3, List<T> list) {
        this.field_34559 = indexedIterable;
        this.field_34561 = arg;
        Palette<T> palette = arg2.comp_72().create(arg2.comp_73(), indexedIterable, this);
        list.forEach(palette::getIndex);
        this.field_34560 = new class_6561<T>(arg2, arg3, palette);
    }

    public PalettedContainer(IndexedIterable<T> indexedIterable, T object, class_6563 arg) {
        this.field_34561 = arg;
        this.field_34559 = indexedIterable;
        this.field_34560 = this.method_38297(null, 0);
        this.field_34560.field_34565.getIndex(object);
    }

    private class_6561<T> method_38297(@Nullable class_6561<T> arg, int i) {
        class_6560<T> lv = this.field_34561.method_38314(this.field_34559, i);
        if (arg != null && lv.equals(arg.comp_74())) {
            return arg;
        }
        return lv.method_38305(this.field_34559, this, this.field_34561.method_38312(), null);
    }

    @Override
    public int onResize(int i, T object) {
        class_6561<T> lv = this.field_34560;
        class_6561 lv2 = this.method_38297(lv, i);
        lv2.method_38308(lv.field_34565, lv.field_34564);
        this.field_34560 = lv2;
        return lv2.field_34565.getIndex(object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T setSync(int x, int y, int z, T value) {
        this.lock();
        try {
            T t = this.setAndGetOldValue(this.field_34561.method_38313(x, y, z), value);
            return t;
        } finally {
            this.unlock();
        }
    }

    public T set(int x, int y, int z, T value) {
        return this.setAndGetOldValue(this.field_34561.method_38313(x, y, z), value);
    }

    private T setAndGetOldValue(int index, T value) {
        int i = this.field_34560.field_34565.getIndex(value);
        int j = this.field_34560.field_34564.setAndGetOldValue(index, i);
        return this.field_34560.field_34565.getByIndex(j);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void method_35321(int i, int j, int k, T object) {
        this.lock();
        try {
            this.set(this.field_34561.method_38313(i, j, k), object);
        } finally {
            this.unlock();
        }
    }

    private void set(int index, T object) {
        this.field_34560.method_38307(index, object);
    }

    public T get(int x, int y, int z) {
        return this.get(this.field_34561.method_38313(x, y, z));
    }

    protected T get(int index) {
        class_6561<T> lv = this.field_34560;
        return lv.field_34565.getByIndex(lv.field_34564.get(index));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fromPacket(PacketByteBuf buf) {
        this.lock();
        try {
            byte i = buf.readByte();
            class_6561<T> lv = this.method_38297(this.field_34560, i);
            lv.field_34565.fromPacket(buf);
            buf.readLongArray(lv.field_34564.getStorage());
            this.field_34560 = lv;
        } finally {
            this.unlock();
        }
    }

    public void toPacket(PacketByteBuf packetByteBuf) {
        this.lock();
        try {
            this.field_34560.method_38309(packetByteBuf);
        } finally {
            this.unlock();
        }
    }

    private static <T> DataResult<PalettedContainer<T>> read(IndexedIterable<T> indexedIterable, class_6563 arg, class_6562<T> arg2) {
        class_6490 lv2;
        List<T> list = arg2.comp_75();
        int i2 = arg.method_38312();
        int j = arg.method_38315(indexedIterable, list.size());
        class_6560<T> lv = arg.method_38314(indexedIterable, j);
        if (j == 0) {
            lv2 = new class_6502(i2);
        } else {
            Optional<LongStream> optional = arg2.comp_76();
            if (optional.isEmpty()) {
                return DataResult.error("Missing values for non-zero storage");
            }
            long[] ls = optional.get().toArray();
            if (lv.comp_72() == class_6563.field_34571) {
                BiMapPalette<Object> palette = new BiMapPalette<Object>(indexedIterable, j, (i, object) -> 0, list);
                PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, arg.method_38312(), ls);
                IntStream intStream = IntStream.range(0, packedIntegerArray.getSize()).map(i -> indexedIterable.getRawId(palette.getByIndex(packedIntegerArray.get(i))));
                lv2 = new PackedIntegerArray(lv.comp_73(), i2, intStream);
            } else {
                lv2 = new PackedIntegerArray(lv.comp_73(), i2, ls);
            }
        }
        return DataResult.success(new PalettedContainer<T>(indexedIterable, arg, lv, lv2, list));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private class_6562<T> write(IndexedIterable<T> indexedIterable, class_6563 arg) {
        this.lock();
        try {
            Optional<LongStream> optional;
            int k;
            BiMapPalette<T> biMapPalette = new BiMapPalette<T>(indexedIterable, this.field_34560.field_34564.getElementBits(), this.field_34558);
            Object object = null;
            int i = -1;
            int j = arg.method_38312();
            int[] is = new int[j];
            for (k = 0; k < j; ++k) {
                T object2 = this.get(k);
                if (object2 != object) {
                    object = object2;
                    i = biMapPalette.getIndex(object2);
                }
                is[k] = i;
            }
            k = arg.method_38315(indexedIterable, biMapPalette.getIndexBits());
            if (k != 0) {
                PackedIntegerArray lv = new PackedIntegerArray(k, j);
                for (int l = 0; l < is.length; ++l) {
                    lv.set(l, is[l]);
                }
                long[] ls = lv.getStorage();
                optional = Optional.of(Arrays.stream(ls));
            } else {
                optional = Optional.empty();
            }
            class_6562<T> class_65622 = new class_6562<T>(biMapPalette.method_38288(), optional);
            return class_65622;
        } finally {
            this.unlock();
        }
    }

    public int getPacketSize() {
        return this.field_34560.method_38306();
    }

    public boolean hasAny(Predicate<T> predicate) {
        return this.field_34560.field_34565.accepts(predicate);
    }

    public void count(CountConsumer<T> consumer) {
        Int2IntOpenHashMap int2IntMap = new Int2IntOpenHashMap();
        this.field_34560.field_34564.forEach(i -> int2IntMap.put(i, int2IntMap.get(i) + 1));
        int2IntMap.int2IntEntrySet().forEach(entry -> consumer.accept(this.field_34560.field_34565.getByIndex(entry.getIntKey()), entry.getIntValue()));
    }

    public static abstract class class_6563 {
        public static final Palette.class_6559 field_34566 = class_6564::method_38316;
        public static final Palette.class_6559 field_34567 = ArrayPalette::method_38295;
        public static final Palette.class_6559 field_34568 = BiMapPalette::method_38287;
        static final Palette.class_6559 field_34571 = IdListPalette::method_38286;
        public static final class_6563 field_34569 = new class_6563(4){

            @Override
            public <A> class_6560<A> method_38314(IndexedIterable<A> indexedIterable, int i) {
                return switch (i) {
                    case 0 -> new class_6560(field_34566, i);
                    case 1, 2, 3, 4 -> new class_6560(field_34567, 4);
                    case 5, 6, 7, 8 -> new class_6560(field_34568, i);
                    default -> new class_6560(field_34571, MathHelper.log2DeBruijn(indexedIterable.size()));
                };
            }
        };
        public static final class_6563 field_34570 = new class_6563(2){

            @Override
            public <A> class_6560<A> method_38314(IndexedIterable<A> indexedIterable, int i) {
                return switch (i) {
                    case 0 -> new class_6560(field_34566, i);
                    case 1, 2 -> new class_6560(field_34567, i);
                    default -> new class_6560(field_34571, MathHelper.log2DeBruijn(indexedIterable.size()));
                };
            }
        };
        private final int field_34572;

        class_6563(int i) {
            this.field_34572 = i;
        }

        public int method_38312() {
            return 1 << this.field_34572 * 3;
        }

        public int method_38313(int i, int j, int k) {
            return (j << this.field_34572 | k) << this.field_34572 | i;
        }

        public abstract <A> class_6560<A> method_38314(IndexedIterable<A> var1, int var2);

        <A> int method_38315(IndexedIterable<A> indexedIterable, int i) {
            int j = MathHelper.log2DeBruijn(i);
            class_6560<A> lv = this.method_38314(indexedIterable, j);
            return lv.comp_72() == field_34571 ? j : lv.comp_73();
        }
    }

    record class_6560<T>(Palette.class_6559 comp_72, int comp_73) {
        public class_6561<T> method_38305(IndexedIterable<T> indexedIterable, PaletteResizeListener<T> paletteResizeListener, int i, @Nullable long[] ls) {
            class_6490 lv = this.comp_73 == 0 ? new class_6502(i) : new PackedIntegerArray(this.comp_73, i, ls);
            Palette<T> palette = this.comp_72.create(this.comp_73, indexedIterable, paletteResizeListener);
            return new class_6561<T>(this, lv, palette);
        }
    }

    static final class class_6561<T>
    extends Record {
        private final class_6560<T> comp_74;
        final class_6490 field_34564;
        final Palette<T> field_34565;

        class_6561(class_6560<T> arg, class_6490 arg2, Palette<T> palette) {
            this.comp_74 = arg;
            this.field_34564 = arg2;
            this.field_34565 = palette;
        }

        public void method_38308(Palette<T> palette, class_6490 arg) {
            for (int i = 0; i < arg.getSize(); ++i) {
                this.method_38307(i, palette.getByIndex(arg.get(i)));
            }
        }

        public void method_38307(int i, T object) {
            this.field_34564.set(i, this.field_34565.getIndex(object));
        }

        public int method_38306() {
            return 1 + this.field_34565.getPacketSize() + PacketByteBuf.getVarIntLength(this.field_34564.getSize()) + this.field_34564.getStorage().length * 8;
        }

        public void method_38309(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeByte(this.field_34564.getElementBits());
            this.field_34565.toPacket(packetByteBuf);
            packetByteBuf.writeLongArray(this.field_34564.getStorage());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{class_6561.class, "configuration;storage;palette", "comp_74", "field_34564", "field_34565"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{class_6561.class, "configuration;storage;palette", "comp_74", "field_34564", "field_34565"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{class_6561.class, "configuration;storage;palette", "comp_74", "field_34564", "field_34565"}, this, object);
        }

        public class_6560<T> comp_74() {
            return this.comp_74;
        }

        public class_6490 method_38310() {
            return this.field_34564;
        }

        public Palette<T> method_38311() {
            return this.field_34565;
        }
    }

    record class_6562<T>(List<T> comp_75, Optional<LongStream> comp_76) {
    }

    @FunctionalInterface
    public static interface CountConsumer<T> {
        public void accept(T var1, int var2);
    }
}

