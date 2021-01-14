/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Represents an NBT 64-bit integer.
 */
public class NbtLong
extends AbstractNbtNumber {
    public static final NbtType<NbtLong> TYPE = new NbtType<NbtLong>(){

        @Override
        public NbtLong read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(128L);
            return NbtLong.of(dataInput.readLong());
        }

        @Override
        public String getCrashReportName() {
            return "LONG";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Long";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final long value;

    private NbtLong(long value) {
        this.value = value;
    }

    public static NbtLong of(long value) {
        if (value >= -128L && value <= 1024L) {
            return Cache.VALUES[(int)value + 128];
        }
        return new NbtLong(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public byte getType() {
        return 4;
    }

    public NbtType<NbtLong> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.value + "L";
    }

    @Override
    public NbtLong copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtLong && this.value == ((NbtLong)o).value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("L").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return (int)(this.value & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short shortValue() {
        return (short)(this.value & 0xFFFFL);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.value & 0xFFL);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }

    static class Cache {
        static final NbtLong[] VALUES = new NbtLong[1153];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new NbtLong(-128 + i);
            }
        }
    }
}

