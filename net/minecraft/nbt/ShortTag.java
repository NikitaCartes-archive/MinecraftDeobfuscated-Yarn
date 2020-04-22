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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ShortTag
extends AbstractNumberTag {
    public static final TagReader<ShortTag> READER = new TagReader<ShortTag>(){

        @Override
        public ShortTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(80L);
            return ShortTag.of(dataInput.readShort());
        }

        @Override
        public String getCrashReportName() {
            return "SHORT";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Short";
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
    private final short value;

    private ShortTag(short value) {
        this.value = value;
    }

    public static ShortTag of(short value) {
        if (value >= -128 && value <= 1024) {
            return Cache.VALUES[value + 128];
        }
        return new ShortTag(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.value);
    }

    @Override
    public byte getType() {
        return 2;
    }

    public TagReader<ShortTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.value + "s";
    }

    @Override
    public ShortTag copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ShortTag && this.value == ((ShortTag)o).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("s").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
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
        return this.value;
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
        static final ShortTag[] VALUES = new ShortTag[1153];

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new ShortTag((short)(-128 + i));
            }
        }
    }
}

