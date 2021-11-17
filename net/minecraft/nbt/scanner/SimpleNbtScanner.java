/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

/**
 * A simple NBT scanner visits all elements shallowly, allowing
 * implementations to override it and perform more actions.
 */
public interface SimpleNbtScanner
extends NbtScanner {
    /**
     * The simple NBT scanner that performs no action.
     */
    public static final SimpleNbtScanner NOOP = new SimpleNbtScanner(){};

    @Override
    default public NbtScanner.Result visitNull() {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitString(String value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitByte(byte value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitShort(short value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitInt(int value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitLong(long value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitFloat(float value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitDouble(double value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitByteArray(byte[] value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitIntArray(int[] value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitLongArray(long[] value) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result visitListMeta(NbtType<?> entryType, int length) {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.NestedResult startListItem(NbtType<?> type, int index) {
        return NbtScanner.NestedResult.SKIP;
    }

    @Override
    default public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
        return NbtScanner.NestedResult.SKIP;
    }

    @Override
    default public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
        return NbtScanner.NestedResult.SKIP;
    }

    @Override
    default public NbtScanner.Result endNested() {
        return NbtScanner.Result.CONTINUE;
    }

    @Override
    default public NbtScanner.Result start(NbtType<?> rootType) {
        return NbtScanner.Result.CONTINUE;
    }
}

