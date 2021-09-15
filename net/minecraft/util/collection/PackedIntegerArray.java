/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.collection;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.minecraft.class_6490;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class PackedIntegerArray
implements class_6490 {
    /**
     * Magic constants for faster integer division by a constant.
     * 
     * <p>This is computed as {@code (n * scale + offset) >> (32 + shift)}. For a divisor n,
     * the constants are stored as such:
     * 
     * <ul>
     * <li>scale at 3 * (n - 1)</li>
     * <li>offset at 3 * (n - 1) + 1</li>
     * <li>shift at 3 * (n - 1) + 2</li>
     * </ul>
     */
    private static final int[] INDEX_PARAMETERS = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 0x55555555, 0x55555555, 0, Integer.MIN_VALUE, 0, 1, 0x33333333, 0x33333333, 0, 0x2AAAAAAA, 0x2AAAAAAA, 0, 0x24924924, 0x24924924, 0, Integer.MIN_VALUE, 0, 2, 0x1C71C71C, 0x1C71C71C, 0, 0x19999999, 0x19999999, 0, 390451572, 390451572, 0, 0x15555555, 0x15555555, 0, 0x13B13B13, 0x13B13B13, 0, 306783378, 306783378, 0, 0x11111111, 0x11111111, 0, Integer.MIN_VALUE, 0, 3, 0xF0F0F0F, 0xF0F0F0F, 0, 0xE38E38E, 0xE38E38E, 0, 226050910, 226050910, 0, 0xCCCCCCC, 0xCCCCCCC, 0, 0xC30C30C, 0xC30C30C, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 0xAAAAAAA, 0xAAAAAAA, 0, 171798691, 171798691, 0, 0x9D89D89, 0x9D89D89, 0, 159072862, 159072862, 0, 0x9249249, 0x9249249, 0, 148102320, 148102320, 0, 0x8888888, 0x8888888, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 0x7878787, 0x7878787, 0, 0x7507507, 0x7507507, 0, 0x71C71C7, 0x71C71C7, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 0x6906906, 0x6906906, 0, 0x6666666, 0x6666666, 0, 104755299, 104755299, 0, 0x6186186, 0x6186186, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 0x5B05B05, 0x5B05B05, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 0x5555555, 0x5555555, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 0x5050505, 0x5050505, 0, 0x4EC4EC4, 0x4EC4EC4, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 0x4924924, 0x4924924, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 0x4444444, 0x4444444, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 0x4104104, 0x4104104, 0, Integer.MIN_VALUE, 0, 5};
    private final long[] storage;
    private final int elementBits;
    private final long maxValue;
    private final int size;
    private final int elementsPerLong;
    private final int indexScale;
    private final int indexOffset;
    private final int indexShift;

    public PackedIntegerArray(int i2, int j, IntStream intStream) {
        this(i2, j);
        MutableInt mutableInt = new MutableInt();
        intStream.forEach(i -> this.set(mutableInt.getAndIncrement(), i));
    }

    public PackedIntegerArray(int elementBits, int size) {
        this(elementBits, size, (long[])null);
    }

    public PackedIntegerArray(int elementBits, int size, @Nullable long[] storage) {
        Validate.inclusiveBetween(1L, 32L, elementBits);
        this.size = size;
        this.elementBits = elementBits;
        this.maxValue = (1L << elementBits) - 1L;
        this.elementsPerLong = (char)(64 / elementBits);
        int i = 3 * (this.elementsPerLong - 1);
        this.indexScale = INDEX_PARAMETERS[i + 0];
        this.indexOffset = INDEX_PARAMETERS[i + 1];
        this.indexShift = INDEX_PARAMETERS[i + 2];
        int j = (size + this.elementsPerLong - 1) / this.elementsPerLong;
        if (storage != null) {
            if (storage.length != j) {
                throw Util.throwOrPause(new RuntimeException("Invalid length given for storage, got: " + storage.length + " but expected: " + j));
            }
            this.storage = storage;
        } else {
            this.storage = new long[j];
        }
    }

    private int getStorageIndex(int index) {
        long l = Integer.toUnsignedLong(this.indexScale);
        long m = Integer.toUnsignedLong(this.indexOffset);
        return (int)((long)index * l + m >> 32 >> this.indexShift);
    }

    @Override
    public int setAndGetOldValue(int i, int j) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        Validate.inclusiveBetween(0L, this.maxValue, j);
        int k = this.getStorageIndex(i);
        long l = this.storage[k];
        int m = (i - k * this.elementsPerLong) * this.elementBits;
        int n = (int)(l >> m & this.maxValue);
        this.storage[k] = l & (this.maxValue << m ^ 0xFFFFFFFFFFFFFFFFL) | ((long)j & this.maxValue) << m;
        return n;
    }

    @Override
    public void set(int i, int j) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        Validate.inclusiveBetween(0L, this.maxValue, j);
        int k = this.getStorageIndex(i);
        long l = this.storage[k];
        int m = (i - k * this.elementsPerLong) * this.elementBits;
        this.storage[k] = l & (this.maxValue << m ^ 0xFFFFFFFFFFFFFFFFL) | ((long)j & this.maxValue) << m;
    }

    @Override
    public int get(int i) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        int j = this.getStorageIndex(i);
        long l = this.storage[j];
        int k = (i - j * this.elementsPerLong) * this.elementBits;
        return (int)(l >> k & this.maxValue);
    }

    @Override
    public long[] getStorage() {
        return this.storage;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getElementBits() {
        return this.elementBits;
    }

    @Override
    public void forEach(IntConsumer intConsumer) {
        int i = 0;
        for (long l : this.storage) {
            for (int j = 0; j < this.elementsPerLong; ++j) {
                intConsumer.accept((int)(l & this.maxValue));
                l >>= this.elementBits;
                if (++i < this.size) continue;
                return;
            }
        }
    }
}

