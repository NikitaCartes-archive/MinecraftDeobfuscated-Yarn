/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class IntTag
extends AbstractNumberTag {
    public static final TagReader<IntTag> READER = new TagReader<IntTag>(){

        @Override
        public IntTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(96L);
            return IntTag.of(dataInput.readInt());
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
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final int value;

    private IntTag(int value) {
        this.value = value;
    }

    public static IntTag of(int value) {
        if (value >= -128 && value <= 1024) {
            return Cache.VALUES[value - -128];
        }
        return new IntTag(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value);
    }

    @Override
    public byte getType() {
        return 3;
    }

    public TagReader<IntTag> getReader() {
        return READER;
    }

    @Override
    public IntTag copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof IntTag && this.value == ((IntTag)o).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitIntTag(this);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return this.value;
    }

    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFF);
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
        static final IntTag[] VALUES = new IntTag[1153];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new IntTag(-128 + i);
            }
        }
    }
}

