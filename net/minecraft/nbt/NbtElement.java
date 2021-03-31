/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
    public static final int field_33246 = 64;
    public static final int field_33247 = 96;
    public static final int field_33248 = 32;
    public static final int field_33249 = 224;
    /**
     * The numeric ID of an NBT null value. Is {@value}.
     * 
     * @see NbtNull
     */
    public static final byte NULL_TYPE = 0;
    /**
     * The numeric ID of an NBT byte value. Is {@value}.
     * 
     * @see NbtByte
     */
    public static final byte BYTE_TYPE = 1;
    /**
     * The numeric ID of an NBT short value. Is {@value}.
     * 
     * @see NbtShort
     */
    public static final byte SHORT_TYPE = 2;
    /**
     * The numeric ID of an NBT integer value. Is {@value}.
     * 
     * @see NbtInt
     */
    public static final byte INT_TYPE = 3;
    /**
     * The numeric ID of an NBT long value. Is {@value}.
     * 
     * @see NbtLong
     */
    public static final byte LONG_TYPE = 4;
    /**
     * The numeric ID of an NBT float value. Is {@value}.
     * 
     * @see NbtFloat
     */
    public static final byte FLOAT_TYPE = 5;
    /**
     * The numeric ID of an NBT double value. Is {@value}.
     * 
     * @see NbtDouble
     */
    public static final byte DOUBLE_TYPE = 6;
    /**
     * The numeric ID of an NBT byte array value. Is {@value}.
     * 
     * @see NbtByteArray
     */
    public static final byte BYTE_ARRAY_TYPE = 7;
    /**
     * The numeric ID of an NBT string value. Is {@value}.
     * 
     * @see NbtString
     */
    public static final byte STRING_TYPE = 8;
    /**
     * The numeric ID of an NBT list value. Is {@value}.
     * 
     * @see NbtList
     */
    public static final byte LIST_TYPE = 9;
    /**
     * The numeric ID of an NBT compound value. Is {@value}.
     * 
     * @see NbtCompound
     */
    public static final byte COMPOUND_TYPE = 10;
    /**
     * The numeric ID of an NBT integer array value. Is {@value}.
     * 
     * @see NbtIntArray
     */
    public static final byte INT_ARRAY_TYPE = 11;
    /**
     * The numeric ID of an NBT long array value. Is {@value}.
     * 
     * @see NbtLongArray
     */
    public static final byte LONG_ARRAY_TYPE = 12;
    /**
     * A wildcard NBT numeric ID that can be used for <i>checking</i> whether an NBT element is an {@link AbstractNbtNumber}. Is {@value}.
     * 
     * @see NbtCompound#getType(String)
     * @see NbtCompound#contains(String, int)
     */
    public static final byte NUMBER_TYPE = 99;
    public static final int field_33264 = 512;

    public void write(DataOutput var1) throws IOException;

    public String toString();

    /**
     * Gets the type of this NBT element.
     * 
     * @return the type
     */
    public byte getType();

    /**
     * Gets the NBT type definition of this NBT element.
     * 
     * @return the element type definition
     */
    public NbtType<?> getNbtType();

    /**
     * Copies this NBT element.
     * 
     * @return the copied element
     */
    public NbtElement copy();

    default public String asString() {
        return new StringNbtWriter().apply(this);
    }

    public void accept(NbtElementVisitor var1);
}

