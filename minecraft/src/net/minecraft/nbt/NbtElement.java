package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
	Formatting AQUA = Formatting.AQUA;
	Formatting GREEN = Formatting.GREEN;
	Formatting GOLD = Formatting.GOLD;
	Formatting RED = Formatting.RED;

	void write(DataOutput output) throws IOException;

	String toString();

	/**
	 * Gets the type of this NBT element.
	 * 
	 * @return the type
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
		return this.toString();
	}

	default Text toText() {
		return this.toText("", 0);
	}

	Text toText(String indent, int depth);
}
