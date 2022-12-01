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
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.math.MathHelper;

/**
 * Represents an NBT 32-bit floating-point number. Its type is {@value NbtElement#FLOAT_TYPE}.
 * Instances are immutable.
 */
public class NbtFloat
extends AbstractNbtNumber {
    private static final int SIZE = 12;
    /**
     * The NBT float representing {@code 0.0f}.
     */
    public static final NbtFloat ZERO = new NbtFloat(0.0f);
    public static final NbtType<NbtFloat> TYPE = new NbtType.OfFixedSize<NbtFloat>(){

        @Override
        public NbtFloat read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(12L);
            return NbtFloat.of(dataInput.readFloat());
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
            return visitor.visitFloat(input.readFloat());
        }

        @Override
        public int getSizeInBytes() {
            return 4;
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

    /**
     * {@return the NBT float from {@code value}}
     */
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
    public int getSizeInBytes() {
        return 12;
    }

    @Override
    public byte getType() {
        return NbtElement.FLOAT_TYPE;
    }

    public NbtType<NbtFloat> getNbtType() {
        return TYPE;
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
    public void accept(NbtElementVisitor visitor) {
        visitor.visitFloat(this);
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
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitFloat(this.value);
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}

