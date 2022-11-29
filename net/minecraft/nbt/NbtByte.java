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

/**
 * Represents an NBT byte. Its type is {@value NbtElement#BYTE_TYPE}.
 * Instances are immutable.
 */
public class NbtByte
extends AbstractNbtNumber {
    private static final int SIZE = 72;
    public static final NbtType<NbtByte> TYPE = new NbtType.OfFixedSize<NbtByte>(){

        @Override
        public NbtByte read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(72L);
            return NbtByte.of(dataInput.readByte());
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
            return visitor.visitByte(input.readByte());
        }

        @Override
        public int getSizeInBytes() {
            return 1;
        }

        @Override
        public String getCrashReportName() {
            return "BYTE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Byte";
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
    /**
     * The NBT byte representing {@code 0}.
     * 
     * @apiNote This is often used to indicate a false boolean value.
     */
    public static final NbtByte ZERO = NbtByte.of((byte)0);
    /**
     * The NBT byte representing {@code 1}.
     * 
     * @apiNote This is often used to indicate a true boolean value.
     */
    public static final NbtByte ONE = NbtByte.of((byte)1);
    private final byte value;

    NbtByte(byte value) {
        this.value = value;
    }

    /**
     * {@return the NBT byte from {@code value}}
     * 
     * @implNote This returns the value from the cache.
     */
    public static NbtByte of(byte value) {
        return Cache.VALUES[128 + value];
    }

    /**
     * {@return the NBT byte representing the boolean {@code value}}
     */
    public static NbtByte of(boolean value) {
        return value ? ONE : ZERO;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public int getSizeInBits() {
        return 72;
    }

    @Override
    public byte getType() {
        return NbtElement.BYTE_TYPE;
    }

    public NbtType<NbtByte> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtByte copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtByte && this.value == ((NbtByte)o).value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitByte(this);
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
        return this.value;
    }

    @Override
    public byte byteValue() {
        return this.value;
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
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitByte(this.value);
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }

    static class Cache {
        static final NbtByte[] VALUES = new NbtByte[256];

        private Cache() {
        }

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new NbtByte((byte)(i - 128));
            }
        }
    }
}

