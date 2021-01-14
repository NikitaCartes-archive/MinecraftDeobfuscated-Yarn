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
import net.minecraft.util.math.MathHelper;

/**
 * Represents an NBT 32-bit floating-point number.
 */
public class NbtFloat
extends AbstractNbtNumber {
    public static final NbtFloat ZERO = new NbtFloat(0.0f);
    public static final NbtType<NbtFloat> TYPE = new NbtType<NbtFloat>(){

        @Override
        public NbtFloat read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(96L);
            return NbtFloat.of(dataInput.readFloat());
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
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final float value;

    private NbtFloat(float value) {
        this.value = value;
    }

    public static NbtFloat of(float value) {
        if (value == 0.0f) {
            return ZERO;
        }
        return new NbtFloat(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
    }

    @Override
    public byte getType() {
        return 5;
    }

    public NbtType<NbtFloat> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.value + "f";
    }

    @Override
    public NbtFloat copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtFloat && this.value == ((NbtFloat)o).value;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("f").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long longValue() {
        return (long)this.value;
    }

    @Override
    public int intValue() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short shortValue() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
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
        return Float.valueOf(this.value);
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}

