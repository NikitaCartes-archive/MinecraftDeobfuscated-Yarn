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
import net.minecraft.util.math.MathHelper;

public class FloatTag
extends AbstractNumberTag {
    public static final FloatTag ZERO = new FloatTag(0.0f);
    public static final TagReader<FloatTag> READER = new TagReader<FloatTag>(){

        @Override
        public FloatTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(96L);
            return FloatTag.of(dataInput.readFloat());
        }

        @Override
        public String getCrashReportName() {
            return "FLOAT";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Float";
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
    private final float value;

    private FloatTag(float value) {
        this.value = value;
    }

    public static FloatTag of(float value) {
        if (value == 0.0f) {
            return ZERO;
        }
        return new FloatTag(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
    }

    @Override
    public byte getType() {
        return 5;
    }

    public TagReader<FloatTag> getReader() {
        return READER;
    }

    @Override
    public FloatTag copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof FloatTag && this.value == ((FloatTag)o).value;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitFloatTag(this);
    }

    @Override
    public long getLong() {
        return (long)this.value;
    }

    @Override
    public int getInt() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
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
        return Float.valueOf(this.value);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

