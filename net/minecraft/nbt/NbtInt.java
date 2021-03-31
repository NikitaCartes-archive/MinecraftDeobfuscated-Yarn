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
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 32-bit integer.
 */
public class NbtInt
extends AbstractNbtNumber {
    private static final int field_33196 = 96;
    public static final NbtType<NbtInt> TYPE = new NbtType<NbtInt>(){

        @Override
        public NbtInt read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(96L);
            return NbtInt.of(dataInput.readInt());
        }

        @Override
        public String getCrashReportName() {
            return "INT";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Int";
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
    private final int value;

    private NbtInt(int value) {
        this.value = value;
    }

    public static NbtInt of(int value) {
        if (value >= -128 && value <= 1024) {
            return Cache.VALUES[value - -128];
        }
        return new NbtInt(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value);
    }

    @Override
    public byte getType() {
        return 3;
    }

    public NbtType<NbtInt> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtInt copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtInt && this.value == ((NbtInt)o).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitInt(this);
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public short shortValue() {
        return (short)(this.value & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.value & 0xFF);
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
        private static final int field_33197 = 1024;
        private static final int field_33198 = -128;
        static final NbtInt[] VALUES = new NbtInt[1153];

        private Cache() {
        }

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new NbtInt(-128 + i);
            }
        }
    }
}

