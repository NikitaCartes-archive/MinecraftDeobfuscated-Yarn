package net.minecraft.nbt;

import java.util.AbstractList;

/**
 * Represents an abstraction of a mutable NBT list which holds elements of the same type.
 */
public abstract class AbstractNbtList<T extends NbtElement> extends AbstractList<T> implements NbtElement {
	public abstract T set(int i, T nbtElement);

	public abstract void add(int i, T nbtElement);

	public abstract T remove(int i);

	/**
	 * Sets the element at {@code index} to {@code element}. Does nothing if
	 * the types were incompatible.
	 * 
	 * @return whether the element was actually set
	 */
	public abstract boolean setElement(int index, NbtElement element);

	/**
	 * Inserts {@code element} at {@code index}. Does nothing if the
	 * types were incompatible.
	 * 
	 * @return whether the element was actually added
	 */
	public abstract boolean addElement(int index, NbtElement element);

	/**
	 * Gets the {@linkplain NbtElement#getType type} of element that this list holds.
	 * 
	 * @return the type of element that this list holds
	 */
	public abstract byte getHeldType();
}
