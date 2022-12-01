package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
	int field_33246 = 8;
	int field_33247 = 12;
	int field_33248 = 4;
	int field_33249 = 28;
	/**
	 * The numeric ID of an NBT end value. Is {@value}.
	 * 
	 * @see NbtEnd
	 */
	byte END_TYPE = 0;
	/**
	 * The numeric ID of an NBT byte value. Is {@value}.
	 * 
	 * @see NbtByte
	 */
	byte BYTE_TYPE = 1;
	/**
	 * The numeric ID of an NBT short value. Is {@value}.
	 * 
	 * @see NbtShort
	 */
	byte SHORT_TYPE = 2;
	/**
	 * The numeric ID of an NBT integer value. Is {@value}.
	 * 
	 * @see NbtInt
	 */
	byte INT_TYPE = 3;
	/**
	 * The numeric ID of an NBT long value. Is {@value}.
	 * 
	 * @see NbtLong
	 */
	byte LONG_TYPE = 4;
	/**
	 * The numeric ID of an NBT float value. Is {@value}.
	 * 
	 * @see NbtFloat
	 */
	byte FLOAT_TYPE = 5;
	/**
	 * The numeric ID of an NBT double value. Is {@value}.
	 * 
	 * @see NbtDouble
	 */
	byte DOUBLE_TYPE = 6;
	/**
	 * The numeric ID of an NBT byte array value. Is {@value}.
	 * 
	 * @see NbtByteArray
	 */
	byte BYTE_ARRAY_TYPE = 7;
	/**
	 * The numeric ID of an NBT string value. Is {@value}.
	 * 
	 * @see NbtString
	 */
	byte STRING_TYPE = 8;
	/**
	 * The numeric ID of an NBT list value. Is {@value}.
	 * 
	 * @see NbtList
	 */
	byte LIST_TYPE = 9;
	/**
	 * The numeric ID of an NBT compound value. Is {@value}.
	 * 
	 * @see NbtCompound
	 */
	byte COMPOUND_TYPE = 10;
	/**
	 * The numeric ID of an NBT integer array value. Is {@value}.
	 * 
	 * @see NbtIntArray
	 */
	byte INT_ARRAY_TYPE = 11;
	/**
	 * The numeric ID of an NBT long array value. Is {@value}.
	 * 
	 * @see NbtLongArray
	 */
	byte LONG_ARRAY_TYPE = 12;
	/**
	 * A wildcard NBT numeric ID that can be used for <i>checking</i> whether an NBT element is an {@link AbstractNbtNumber}. Is {@value}.
	 * 
	 * @see NbtCompound#getType(String)
	 * @see NbtCompound#contains(String, int)
	 */
	byte NUMBER_TYPE = 99;
	int MAX_DEPTH = 512;

	/**
	 * Writes the NBT element to {@code output}.
	 * 
	 * @apiNote This is a low-level method for serializing NBT elements; consider using
	 * {@link NbtIo}, {@link NbtOps}, or {@link net.minecraft.network.PacketByteBuf#writeNbt}
	 * instead.
	 */
	void write(DataOutput output) throws IOException;

	String toString();

	/**
	 * {@return the type of this NBT element}
	 */
	byte getType();

	/**
	 * {@return the NBT type definition of this NBT element}
	 */
	NbtType<?> getNbtType();

	/**
	 * {@return an NBT element of equal value that won't change with this element}
	 */
	NbtElement copy();

	int getSizeInBytes();

	/**
	 * {@return the NBT's string representation}
	 * 
	 * @implNote By default, this returns the same result as {@link
	 * net.minecraft.nbt.visitor.StringNbtWriter}. {@link NbtString} will return its
	 * string value instead.
	 */
	default String asString() {
		return new StringNbtWriter().apply(this);
	}

	void accept(NbtElementVisitor visitor);

	NbtScanner.Result doAccept(NbtScanner visitor);

	default void accept(NbtScanner visitor) {
		NbtScanner.Result result = visitor.start(this.getNbtType());
		if (result == NbtScanner.Result.CONTINUE) {
			this.doAccept(visitor);
		}
	}
}
