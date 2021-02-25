/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class LongTag
extends AbstractNumberTag {
    public static final TagReader<LongTag> READER = new TagReader<LongTag>(){

        @Override
        public LongTag read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(128L);
            return LongTag.of(dataInput.readLong());
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
        public /* synthetic */ Tag read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final long value;

    private LongTag(long value) {
        this.value = value;
    }

    public static LongTag of(long value) {
        if (value >= -128L && value <= 1024L) {
            return Cache.VALUES[(int)value - -128];
        }
        return new LongTag(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public byte getType() {
        return 4;
    }

    public TagReader<LongTag> getReader() {
        return READER;
    }

    @Override
    public LongTag copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof LongTag && this.value == ((LongTag)o).value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitLongTag(this);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return (int)(this.value & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFFL);
    }

    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFFL);
    }

    @Override
    public double getDouble() {
        return this.value;
    }

    @Override
    public float getFloat() {
        return this.value;
    }

    @Override
    public Number getNumber() {
        return this.value;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    static class Cache {
        static final LongTag[] VALUES = new LongTag[1153];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new LongTag(-128 + i);
            }
        }
    }
}

