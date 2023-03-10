/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

/**
 * A packed array of integers. Introduced in 20w17a to represent the old
 * block state storage format.
 */
public class WordPackedArray {
    private static final int field_29862 = 6;
    private final long[] array;
    private final int unitSize;
    private final long maxValue;
    private final int length;

    /**
     * @param length the length of values
     * @param unitSize the max number of bits a value can use
     */
    public WordPackedArray(int unitSize, int length) {
        this(unitSize, length, new long[MathHelper.roundUpToMultiple(length * unitSize, 64) / 64]);
    }

    public WordPackedArray(int unitSize, int length, long[] array) {
        Validate.inclusiveBetween(1L, 32L, unitSize);
        this.length = length;
        this.unitSize = unitSize;
        this.array = array;
        this.maxValue = (1L << unitSize) - 1L;
        int i = MathHelper.roundUpToMultiple(length * unitSize, 64) / 64;
        if (array.length != i) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + array.length + " but expected: " + i);
        }
    }

    public void set(int index, int value) {
        Validate.inclusiveBetween(0L, this.length - 1, index);
        Validate.inclusiveBetween(0L, this.maxValue, value);
        int i = index * this.unitSize;
        int j = i >> 6;
        int k = (index + 1) * this.unitSize - 1 >> 6;
        int l = i ^ j << 6;
        this.array[j] = this.array[j] & (this.maxValue << l ^ 0xFFFFFFFFFFFFFFFFL) | ((long)value & this.maxValue) << l;
        if (j != k) {
            int m = 64 - l;
            int n = this.unitSize - m;
            this.array[k] = this.array[k] >>> n << n | ((long)value & this.maxValue) >> m;
        }
    }

    public int get(int index) {
        Validate.inclusiveBetween(0L, this.length - 1, index);
        int i = index * this.unitSize;
        int j = i >> 6;
        int k = (index + 1) * this.unitSize - 1 >> 6;
        int l = i ^ j << 6;
        if (j == k) {
            return (int)(this.array[j] >>> l & this.maxValue);
        }
        int m = 64 - l;
        return (int)((this.array[j] >>> l | this.array[k] << m) & this.maxValue);
    }

    public long[] getAlignedArray() {
        return this.array;
    }

    public int getUnitSize() {
        return this.unitSize;
    }
}

