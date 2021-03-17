package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
	void write(DataOutput output) throws IOException;

	String toString();

	/**
	 * Gets the type of this NBT element.
	 * 
	 * @return the type
	 * 
	 * @see net.fabricmc.yarn.constants.NbtTypeIds a list of valid types
	 */
	byte getType();

	/**
	 * Gets the NBT type definition of this NBT element.
	 * 
	 * @return the element type definition
	 */
	NbtType<?> getNbtType();

	/**
	 * Copies this NBT element.
	 * 
	 * @return the copied element
	 */
	NbtElement copy();

	default String asString() {
		return new StringNbtWriter().apply(this);
	}

	void accept(NbtElementVisitor visitor);
}
