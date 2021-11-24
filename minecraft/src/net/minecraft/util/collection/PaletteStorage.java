package net.minecraft.util.collection;

import java.util.function.IntConsumer;

/**
 * A storage whose values are raw IDs held by palettes.
 */
public interface PaletteStorage {
	/**
	 * Sets {@code value} to {@code index} and returns the previous value in
	 * this storage.
	 * 
	 * @return the previous value
	 * 
	 * @param index the index
	 * @param value the value to set
	 */
	int swap(int index, int value);

	/**
	 * Sets {@code value} to {@code index} in this storage.
	 * 
	 * @param index the index
	 * @param value the value to set
	 */
	void set(int index, int value);

	/**
	 * {@return the value at {@code index} in this storage}
	 * 
	 * @param index the index
	 */
	int get(int index);

	/**
	 * {@return the backing data of this storage}
	 */
	long[] getData();

	/**
	 * {@return the size of, or the number of elements in, this storage}
	 */
	int getSize();

	/**
	 * {@return the number of bits each element in this storage uses}
	 */
	int getElementBits();

	/**
	 * Executes an {@code action} on all values in this storage, sequentially.
	 */
	void forEach(IntConsumer action);

	void method_39892(int[] is);

	PaletteStorage copy();
}
